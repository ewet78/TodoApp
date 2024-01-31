package io.github.ewet73.model.projection;

import io.github.ewet73.model.Task;
import io.github.ewet73.model.TaskGroup;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class GroupTaskWriteModel {

    @NotBlank(message = "Task's description must not be empty")
    private String description;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime deadline;

    @Valid
    private TaskGroup groupId;

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

    public Task toTask(final TaskGroup group) {
        return new Task(description, deadline, group);
    }

    public TaskGroup getGroupId() {
        return groupId;
    }

    public void setGroupId(TaskGroup groupId) {
        this.groupId = groupId;
    }
}
