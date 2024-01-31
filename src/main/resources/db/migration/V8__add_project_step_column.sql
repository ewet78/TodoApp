alter table task_groups add column project_step_id int null;
alter table task_groups
    add foreign key (project_step_id) references project_steps (id);