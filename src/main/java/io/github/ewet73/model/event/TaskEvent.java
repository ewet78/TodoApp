package io.github.ewet73.model.event;

import io.github.ewet73.model.Task;

import java.time.Clock;
import java.time.Instant;

public abstract class TaskEvent {

    public static TaskEvent changed(Task source) {
        return source.isDone() ? new TaskDone(source) : new TaskUndone(source);
    }

    private int taskId;

    private Instant occurrence;

    TaskEvent(final int taskId, Clock clock) {
        this.taskId = taskId;
        this.occurrence = Instant.now();
    }

    public int getTaskId() {
        return taskId;
    }

    public Instant getOccurrence() {
        return occurrence;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "taskId=" + taskId +
                ", occurrence=" + occurrence +
                '}';
    }
}
