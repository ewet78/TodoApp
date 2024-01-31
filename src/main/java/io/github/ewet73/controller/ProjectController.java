package io.github.ewet73.controller;

import io.github.ewet73.logic.ProjectService;
import io.github.ewet73.model.Project;
import io.github.ewet73.model.ProjectStep;
import io.github.ewet73.model.projection.ProjectWriteModel;
import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService service;

    ProjectController(ProjectService service) {
        this.service = service;
    }

    @GetMapping
    String showProjects(Model model) {
        model.addAttribute("projects", getProjects());
        model.addAttribute("project", new ProjectWriteModel());
        model.addAttribute("steps", new ProjectStep());
        return "projects";
    }
    @GetMapping(value = "/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<ProjectStep>> showProjectStepsOfSelectedProject(@PathVariable int projectId) {
        try {
            List<ProjectStep> projectSteps = service.readProjectStepsByProjectId(projectId);
            return ResponseEntity.ok(projectSteps);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    String addProject(
            @ModelAttribute("project") @Valid ProjectWriteModel current,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            if (current.getSteps().size() == 1) {
                model.addAttribute("steps", current.getSteps().remove(0));
            } else {
                model.addAttribute("steps", current.getSteps());
            }
            model.addAttribute("project", current);
            model.addAttribute("projects", getProjects());
            return "projects";
        }
        service.save(current);
        model.addAttribute("project", new ProjectWriteModel());
        model.addAttribute("projects", getProjects());
        model.addAttribute("message", "Dodano projekt!");
        return "projects";
    }

    @PostMapping(params = "addStep")
    String addProjectStep(@ModelAttribute("project") ProjectWriteModel current, Model model) {
        current.getSteps().add(new ProjectStep());
        model.addAttribute("steps", current.getSteps());
        return "projects";
    }

    @PostMapping(value = {"/{projectId}/", ""}, params = "removeStep")
    String removeProjectStep(@ModelAttribute("project") ProjectWriteModel current, @RequestParam("removeStep") int index, Model model, @PathVariable(required = false) Integer projectId) {
        model.addAttribute("steps", current.removeStepFromProject(index));
        model.addAttribute("project", current);
        if (current.getSteps().size() == 1) {
            return "redirect:/projects";
        }
        return "projects";
    }

    @Timed(value = "project.create.group", histogram = true, percentiles = {0.5, 0.95, 0.99})
    @PostMapping("/{id}")
    String createGroup(
//            @ModelAttribute("project") ProjectWriteModel current,
//            Model model,
//            @PathVariable int id,
//            @RequestParam("description") String description,
//            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime deadline
    ) {
//        try {
//            service.createGroup(deadline, id);
//            model.addAttribute("message", "Dodano grupę!");
//        } catch (IllegalStateException | IllegalArgumentException e) {
//            model.addAttribute("message", "Błąd podczas tworzenia grupy!");
//        }
        return "redirect:/groups";
    }


    @ModelAttribute("projects")
    List<Project> getProjects() {
        return service.readAll();
    }
}
