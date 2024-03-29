package io.github.ewet73.adapter;

import io.github.ewet73.model.TaskGroup;
import io.github.ewet73.model.TaskGroupRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface SqlTaskGroupRepository extends TaskGroupRepository, JpaRepository<TaskGroup, Integer> {
    @Override
    @Query("select distinct g from TaskGroup  g join fetch g.tasks")
    List<TaskGroup> findAll();

    @Override
    @Query("select distinct g from TaskGroup g WHERE g.id=:id")
    TaskGroup findByCustomId(Integer id);

    @Override
    boolean existsByDoneIsFalseAndProject_Id(Integer projectId);
}
