package io.github.ewet73.reports;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface PersistedTaskEventRepository extends JpaRepository<PersistedTaskEvent, Integer> {
    List<PersistedTaskEvent> findByTaskId(int taskId);
}
