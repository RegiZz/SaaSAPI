alter table plans drop column max_users;
alter table plans drop column max_projects;

create table plan_limits (
    plan_id bigint not null,
    limit_name varchar(64) not null,
    limit_value integer not null,
    primary key (plan_id, limit_name),
    constraint fk_plan_limits_plan foreign key (plan_id) references plans(id)
);
