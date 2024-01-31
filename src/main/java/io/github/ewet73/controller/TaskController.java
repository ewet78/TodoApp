package io.github.ewet73.controller;

import io.github.ewet73.logic.TaskGroupService;
import io.github.ewet73.logic.TaskService;
import io.github.ewet73.model.Task;
import io.github.ewet73.model.TaskRepository;
import io.github.ewet73.model.projection.GroupReadModel;
import io.github.ewet73.model.projection.GroupTaskWriteModel;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@IllegalExceptionProcessing
@RequestMapping("/tasks")
class TaskController {
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    private final ApplicationEventPublisher eventPublisher;

    private final TaskRepository repository;
    private final TaskService service;
    private final TaskGroupService groupService;

    TaskController(ApplicationEventPublisher eventPublisher, final TaskRepository repository, TaskService service, TaskGroupService groupService) {
        this.eventPublisher = eventPublisher;
        this.repository = repository;
        this.service = service;
        this.groupService = groupService;
    }

    @PostMapping(value = "/{groupId}", produces = MediaType.TEXT_HTML_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    String createTask(
            @ModelAttribute("task") @Valid GroupTaskWriteModel current,
            @PathVariable int groupId) {
        service.createTask(current, groupId);
        return "redirect:/tasks";
    }

    @GetMapping(params = {"!sort", "!page", "!size"})
    String readAllTasks(Model model) {
        logger.warn("Exposing all the tasks!");
        List<Task> tasks = service.findAll();
        model.addAttribute("tasks", tasks);
        model.addAttribute("task", new GroupTaskWriteModel());
        model.addAttribute("groups", getGroups());
        return "tasks";
    }

    @GetMapping
    ResponseEntity<List<Task>> readAllTasks(Pageable page) {
        logger.info("Custom pageable");
        return ResponseEntity.ok(repository.findAll(page).getContent());
    }

    @GetMapping("/{id}")
    ResponseEntity<Task> readTask(@PathVariable int id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search/done")
    ResponseEntity<List<Task>> readDoneTasks(@RequestParam(defaultValue = "true") boolean state){
        return ResponseEntity.ok(
                repository.findByDone(state)
        );
    }

    @PutMapping("/{id}")
    ResponseEntity<?> updateTask(@PathVariable int id, @RequestBody @Valid Task toUpdate) {
        if(!repository.existsById(id)) {
           return ResponseEntity.notFound().build();
        }
        repository.findById(id)
                .ifPresent(task -> {
                    task.updateFrom(toUpdate);
                    repository.save(task);
                });
        return ResponseEntity.noContent().build();
    }

    @Transactional
    @PatchMapping("/{id}")
    public String toggleTask(@PathVariable int id) {
        Optional<Task> optionalTask = repository.findById(id);
        if(optionalTask.isPresent()) {
            Task task = optionalTask.get();
            task.toggle();
            repository.save(task);
        }
        return "tasks";
    }

    @ModelAttribute("groups")
    List<GroupReadModel> getGroups() {
        return groupService.readAll();
    }


}
