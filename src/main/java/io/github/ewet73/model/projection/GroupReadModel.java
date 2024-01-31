package io.github.ewet73.model.projection;

import io.github.ewet73.model.Project;
import io.github.ewet73.model.ProjectStep;
import io.github.ewet73.model.Task;
import io.github.ewet73.model.TaskGroup;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GroupReadModel {

    private int id;
    private String description;
    /**
     * Deadline from the latest task in group.
     */
    private LocalDateTime deadline;

    private List<GroupTaskReadModel> tasks;

    private ProjectStep projectStepId;

    private Project projectId;

    public GroupReadModel(TaskGroup source) {
        id = source.getId();
        description = source.getDescription();
        source.getTasks().stream()
                .map(Task::getDeadline)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .ifPresent(date -> deadline = date);
        tasks = source.getTasks().stream()
                .map(GroupTaskReadModel::new)
                .collect(Collectors.toList());
        projectStepId = source.getProjectStepId();
        projectId = source.getProject();


    }

    public Project getProject() {
        return projectId;
    }

    public ProjectStep getProjectStepId() {
        return projectStepId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public List<GroupTaskReadModel> getTasks() {
        return tasks;
    }

    public void setTasks(List<GroupTaskReadModel> tasks) {
        this.tasks = tasks;
    }
}
