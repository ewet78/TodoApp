package io.github.ewet73.model.projection;

import io.github.ewet73.model.Task;
import io.github.ewet73.model.TaskGroup;
import jakarta.validation.Valid;

public class GroupTaskReadModel {
    private int id;
    private String description;
    private boolean done;

    @Valid
    private TaskGroup groupId;

    public GroupTaskReadModel(Task source) {
        id = source.getId();
        description = source.getDescription();
        done = source.isDone();
        groupId = source.getGroup();
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

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public TaskGroup getGroupId() {
        return groupId;
    }

    public void setGroupId(TaskGroup groupId) {
        this.groupId = groupId;
    }
}
