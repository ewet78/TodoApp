package io.github.ewet73.controller;

import io.github.ewet73.logic.ProjectService;
import io.github.ewet73.logic.TaskGroupService;
import io.github.ewet73.model.Project;
import io.github.ewet73.model.ProjectStep;
import io.github.ewet73.model.Task;
import io.github.ewet73.model.TaskRepository;
import io.github.ewet73.model.projection.GroupReadModel;
import io.github.ewet73.model.projection.GroupTaskWriteModel;
import io.github.ewet73.model.projection.GroupWriteModel;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URI;
import java.util.List;

@Controller
@IllegalExceptionProcessing
@RequestMapping("/groups")
class TaskGroupController {
    private static final Logger logger = LoggerFactory.getLogger(TaskGroupController.class);
    private final TaskGroupService service;
    private final ProjectService projectService;
    private final TaskRepository repository;

    TaskGroupController(final TaskGroupService service, ProjectService projectService, final TaskRepository repository) {
        this.service = service;
        this.projectService = projectService;
        this.repository = repository;
    }

    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    String showGroups(Model model) {
        List<Project> projects = projectService.readAll();
        model.addAttribute("group", new GroupWriteModel());
        model.addAttribute("groups", getGroups());
        model.addAttribute("projects", projects);
        List<ProjectStep> projectSteps = projectService.readAllProjectSteps();
        model.addAttribute("projectSteps", projectSteps);
        return "groups";
    }

    @GetMapping(value = "/{projectId}", produces = MediaType.TEXT_HTML_VALUE)
    String showGroups(Model model, @PathVariable int projectId) {
        List<Project> projects = projectService.readAll();
        model.addAttribute("group", new GroupWriteModel());
        model.addAttribute("projects", projects);
        model.addAttribute("projectSteps", getProjectStepsByProjectId(projectId));
        return "groups";
    }

    private ProjectStep getProjectStepByItsId(int projectStepId) {
        return projectService.readProjectStepByItsId(projectStepId);
    }

    private List<ProjectStep> getProjectStepsByProjectId(int projectId) {
        return projectService.readProjectStepsByProjectId(projectId);
    }

    private List<ProjectStep> getAllProjectSteps() {
        return projectService.readAllProjectSteps();
    }

    @PostMapping(value = {"/{projectId}/{projectStepId}"}, produces = MediaType.TEXT_HTML_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    String addGroup(
            @ModelAttribute("group") @Valid GroupWriteModel current,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes,
            @PathVariable int projectId,
            @PathVariable int projectStepId
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult);
            List<Project> projects = projectService.readAll();
            model.addAttribute("projects", projects);
            model.addAttribute("project", getProjectByItsId(projectId));
            model.addAttribute("projectSteps", getProjectStepsByProjectId(projectId));
            model.addAttribute("projectStep", getProjectStepByItsId(projectStepId));
            return "groups";
        }
        service.createGroup(current, projectId, projectStepId);
        List<Project> projects = projectService.readAll();
        model.addAttribute("projects", projects);
        model.addAttribute("project", getProjectByItsId(projectStepId));
        model.addAttribute("group", new GroupWriteModel(current.getProjectStepId(), current.getProject()));
        model.addAttribute("projectSteps", getProjectStepsByProjectId(projectId));
        model.addAttribute("projectStep", getProjectStepByItsId(projectStepId));
        model.addAttribute("groups", getGroups());
        redirectAttributes.addFlashAttribute("message", "Dodano grupę!");
        return "redirect:/groups";
    }

    @PostMapping(value ="/{projectId}/", produces = MediaType.TEXT_HTML_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    String addGroup(
            @ModelAttribute("group") @Valid GroupWriteModel current,
            BindingResult bindingResult,
            Model model,
            @PathVariable int projectId
    ) {
        if (current.getProjectStepId() == null) {
            bindingResult.rejectValue("projectStepId", "step.not.selected", "You should choose project step");
        }

        if (bindingResult.hasErrors()) {
            List<Project> projects = projectService.readAll();
            model.addAttribute("projects", projects);
            model.addAttribute("project", getProjectByItsId(projectId));
            model.addAttribute("projectSteps", getProjectStepsByProjectId(projectId));
            return "groups";
        }
        return "groups";
    }

    private Project getProjectByItsId(int projectId) {
        return  projectService.readProjectByItsId(projectId);

    }

    @PostMapping(value = {"/{projectId}/", "/{projectId}/{projectStepId}"},params = "addTask", produces = MediaType.TEXT_HTML_VALUE)
    String addGroupTask(@ModelAttribute("group") GroupWriteModel current, Model model,  @PathVariable int projectId) {
        current.getTasks().add(new GroupTaskWriteModel());
        List<Project> projects = projectService.readAll();
        model.addAttribute("projects", projects);
        model.addAttribute("project", getProjectByItsId(projectId));
        model.addAttribute("projectSteps", getProjectStepsByProjectId(projectId));
        return "groups";
    }

    @PostMapping(value = {"/{projectId}/", "/{projectId}/{projectStepId}"}, params = "removeTask")
    String removeGroupTask(@ModelAttribute("group") GroupWriteModel current, @RequestParam("removeTask") int index, @PathVariable int projectId, Model model) {
        model.addAttribute("tasks", current.removeTaskFromGroup(index));
        model.addAttribute("group", current);
        List<Project> projects = projectService.readAll();
        model.addAttribute("projects", projects);
        model.addAttribute("projectSteps", getProjectStepsByProjectId(projectId));
        if (current.getTasks().size() == 1) {
            return "redirect:/groups";
        }
        return "groups";
    }

    @ResponseBody
    @PostMapping(produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<GroupReadModel> createGroup(@RequestBody @Valid GroupWriteModel toCreate) {
        GroupReadModel result = service.createGroup(toCreate);
        return ResponseEntity.created(URI.create("/" + result.getId())).body(result);
    }

    @ResponseBody
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<GroupReadModel>> readAllGroups() {
        return ResponseEntity.ok(service.readAll());
    }

    @ResponseBody
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<Task>> readAllTasksFromGroup(@PathVariable int id) {
        return ResponseEntity.ok(repository.findAllByGroup_Id(id));
    }

    @ResponseBody
    @Transactional
    @PatchMapping("/{id}")
    public ResponseEntity<?> toggleGroup(@PathVariable int id) {
        service.toggleGroup(id);
        return ResponseEntity.noContent().build();
    }

    @ModelAttribute("groups")
    List<GroupReadModel> getGroups() {
        return service.readAll();
    }
}
