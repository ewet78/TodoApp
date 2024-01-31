package io.github.ewet73.logic;

import io.github.ewet73.model.*;
import io.github.ewet73.model.projection.GroupReadModel;
import io.github.ewet73.model.projection.GroupWriteModel;

import java.util.List;
import java.util.stream.Collectors;

public class TaskGroupService {
    private TaskGroupRepository repository;

    private ProjectRepository projectRepository;

    private TaskRepository taskRepository;

    TaskGroupService(final TaskGroupRepository repository, final TaskRepository taskRepository, final ProjectRepository projectRepository) {
        this.repository = repository;
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }
    public TaskGroupRepository getRepository() {
        return repository;
    }
    public GroupReadModel createGroup(final GroupWriteModel source) {
        return createGroup(source, source.getProject().getId(), source.getProjectStepId().getId());
    }

    public GroupReadModel createGroup(GroupWriteModel source, int projectId, int projectStepId) {
        Project project = projectRepository.findByProjectId(projectId);
        ProjectStep step = projectRepository.findByStepId(projectStepId);
        TaskGroup result = repository.save(source.toGroup(project, step));
        return new GroupReadModel(result);
    }

    public List<GroupReadModel> readAll() {
        return repository.findAll().stream()
                .map(GroupReadModel::new)
                .collect(Collectors.toList());
    }

    public void toggleGroup(int groupId) {
        if (taskRepository.existsByDoneIsFalseAndGroup_Id(groupId)) {
            throw new IllegalStateException("Group has undone tasks. Done all the tasks first.");
        }
        TaskGroup result = repository.findByCustomId(groupId);
        if (result != null){
            throw (new IllegalArgumentException("TaskGroup with given id not found."));
        }
        result.setDone(!result.isDone());
        repository.save(result);
    }
}
