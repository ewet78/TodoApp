package io.github.ewet73.reports;

import io.github.ewet73.model.event.TaskEvent;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "task_events")
class PersistedTaskEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    int taskId;

    String name;

    LocalDateTime occurrence;

    public PersistedTaskEvent() {
    }

    PersistedTaskEvent(TaskEvent source) {
        taskId = source.getTaskId();
        name = source.getClass().getSimpleName();
        occurrence = LocalDateTime.ofInstant(source.getOccurrence(), ZoneId.systemDefault());
    }

}
