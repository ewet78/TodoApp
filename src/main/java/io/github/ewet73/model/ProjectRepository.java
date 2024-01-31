package io.github.ewet73.model;

import java.util.List;

public interface ProjectRepository {
    List<Project> findAll();
    List<ProjectStep> findAllProjectSteps();
    List<ProjectStep> findProjectStepsByProjectId(int projectId);
    ProjectStep findByStepId(Integer id);
    Project findByProjectId(Integer id);

    Project save(Project entity);
}
