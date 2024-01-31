package io.github.ewet73.logic;

import io.github.ewet73.TaskConfigurationProperties;
import io.github.ewet73.model.Project;
import io.github.ewet73.model.ProjectRepository;
import io.github.ewet73.model.ProjectStep;
import io.github.ewet73.model.TaskGroupRepository;
import io.github.ewet73.model.projection.GroupReadModel;
import io.github.ewet73.model.projection.GroupTaskWriteModel;
import io.github.ewet73.model.projection.GroupWriteModel;
import io.github.ewet73.model.projection.ProjectWriteModel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectService {
    private ProjectRepository repository;
    private TaskGroupRepository taskGroupRepository;
    private TaskGroupService taskGroupService;

    private TaskConfigurationProperties config;

    public ProjectService(final ProjectRepository repository, final TaskGroupRepository taskGroupRepository, TaskGroupService taskGroupService, final TaskConfigurationProperties config) {
        this.repository = repository;
        this.taskGroupRepository = taskGroupRepository;
        this.taskGroupService = taskGroupService;
        this.config = config;
    }

    public List<Project> readAll() {
        return repository.findAll();
    }

    public Project readProjectByItsId(int projectId) {
        return repository.findByProjectId(projectId);
    }

    public List<ProjectStep> readAllProjectSteps() {
        return repository.findAllProjectSteps();
    }

    public List<ProjectStep> readProjectStepsByProjectId(int projectId) {
        return repository.findProjectStepsByProjectId(projectId);
    }

    public ProjectStep readProjectStepByItsId(int projectStepId) {
        return repository.findByStepId(projectStepId);
    }

    public Project save(final ProjectWriteModel toSave) {
        return repository.save(toSave.toProject());
    }

    public GroupReadModel createGroup(LocalDateTime deadline, int projectId, int projectStepId) {
        if (!config.getTemplate().isAllowMultipleTasks() && taskGroupRepository.existsByDoneIsFalseAndProject_Id(projectId)) {
            throw new IllegalStateException("Only one undone group from project is allowed");
        }
        Project myProject = repository.findByProjectId(projectId);

        if (myProject == null) {
            throw new IllegalArgumentException("Project with given id not found");
        }
        GroupWriteModel targetGroup = new GroupWriteModel();
        targetGroup.setDescription(myProject.getDescription());
        targetGroup.setTasks(myProject.getSteps().stream()
                .map(projectStep -> {
                    GroupTaskWriteModel task = new GroupTaskWriteModel();
                    task.setDescription(projectStep.getDescription());
                    task.setDeadline(deadline.plusDays(projectStep.getDaysToDeadline()));
                    return task;
                })
                .collect(Collectors.toList())
        );

        return taskGroupService.createGroup(targetGroup, projectId, projectStepId);
    }
}
