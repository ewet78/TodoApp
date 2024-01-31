package io.github.ewet73.model;

import java.util.List;
import java.util.Optional;

public interface TaskGroupRepository {
    List<TaskGroup> findAll();

    TaskGroup findByCustomId(Integer id);

    Optional<TaskGroup> findByProjectId(Integer id);

    TaskGroup save(TaskGroup entity);

    boolean existsByDoneIsFalseAndProject_Id(Integer projectId);

    boolean existsByDescription(String description);
}
