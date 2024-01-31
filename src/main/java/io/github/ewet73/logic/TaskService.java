package io.github.ewet73.logic;

import io.github.ewet73.model.Task;
import io.github.ewet73.model.TaskGroup;
import io.github.ewet73.model.TaskGroupRepository;
import io.github.ewet73.model.TaskRepository;
import io.github.ewet73.model.projection.GroupTaskReadModel;
import io.github.ewet73.model.projection.GroupTaskWriteModel;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class TaskService {
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);
    private final TaskRepository repository;

    private TaskGroupRepository groupRepository;

    TaskService(final TaskRepository repository, final TaskGroupRepository groupRepository){
        this.repository = repository;
        this.groupRepository = groupRepository;
    }

    @Transactional
    public List<Task> findAll() {
        logger.info("Supply sync!");
        List<Task> tasks =  repository.findAll();
        tasks.forEach(task -> {
            TaskGroup group = task.getGroup();
            if (group != null) {
                Hibernate.initialize(group.getTasks());
            }
        });
        return tasks;
    }
    @Async
    @Transactional
    public CompletableFuture<List<Task>> findAllAsync(){
        logger.info("Supply async!");
        return CompletableFuture.supplyAsync(this::findAll);
    }

    public List<Task> findForTodayAndOverdue(){
        LocalDateTime today = LocalDateTime.now();
        return repository.findByDone(false).stream()
                .filter(task -> task.getDeadline().isBefore(today.plusDays(2)))
                .collect(Collectors.toList());

    }

    public GroupTaskReadModel createTask(GroupTaskWriteModel source, int groupId) {
        TaskGroup taskGroupId = groupRepository.findByCustomId(groupId);
        Task result = repository.save(source.toTask(taskGroupId));
        return new GroupTaskReadModel(result);
    }


}