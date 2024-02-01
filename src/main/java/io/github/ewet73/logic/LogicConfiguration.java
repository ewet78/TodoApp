package io.github.ewet73.logic;

import io.github.ewet73.TaskConfigurationProperties;
import io.github.ewet73.model.ProjectRepository;
import io.github.ewet73.model.TaskGroupRepository;
import io.github.ewet73.model.TaskRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class LogicConfiguration {
    @Bean
    ProjectService projectService(
            final ProjectRepository repository,
            final TaskGroupRepository taskGroupRepository,
            final TaskGroupService taskGroupService,
            final TaskConfigurationProperties config
    ){
        return new ProjectService(repository, taskGroupRepository, taskGroupService, config);
    }

    @Bean
    TaskGroupService taskGroupService(
            final TaskGroupRepository taskGroupRepository,
            final TaskRepository taskRepository,
            final ProjectRepository projectRepository
    ) {
        return new TaskGroupService(taskGroupRepository, taskRepository, projectRepository);
    }

    @Bean
    TaskService taskService(
            final TaskRepository taskRepository,
            final TaskGroupRepository taskGroupRepository
    ){
        return new TaskService(taskRepository, taskGroupRepository);
    }
}
