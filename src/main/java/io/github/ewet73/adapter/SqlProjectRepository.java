package io.github.ewet73.adapter;

import io.github.ewet73.model.Project;
import io.github.ewet73.model.ProjectRepository;
import io.github.ewet73.model.ProjectStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface SqlProjectRepository extends ProjectRepository, JpaRepository<Project, Integer> {
    @Override
    @Query("select distinct p from Project p join fetch p.steps")
    List<Project> findAll();

    @Override
    @Query("select distinct s from ProjectStep s join fetch s.project")
    List<ProjectStep> findAllProjectSteps();

    @Override
    @Query("select distinct s from ProjectStep s join fetch s.project p WHERE p.id = :projectId")
    List<ProjectStep> findProjectStepsByProjectId(int projectId);

    @Override
    @Query("select distinct s from ProjectStep s WHERE s.id = :projectStepId")
    ProjectStep findByStepId(Integer projectStepId);

    @Override
    @Query("select distinct p from Project p WHERE p.id = :projectId")
    Project findByProjectId(Integer projectId);
}
