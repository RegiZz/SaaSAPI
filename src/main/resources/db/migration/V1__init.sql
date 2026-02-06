create table users (
    id bigserial primary key,
    email varchar(255) not null unique,
    created_at timestamp not null
);

create table plans (
    id bigserial primary key,
    code varchar(64) not null unique,
    price numeric(19, 2),
    billing_period varchar(16),
    max_users integer,
    max_projects integer,
    active boolean not null
);

create table subscriptions (
    id bigserial primary key,
    user_id bigint not null,
    plan_id bigint not null,
    status varchar(16) not null,
    start_date timestamp,
    end_date timestamp,
    trial_end_date timestamp,
    auto_renew boolean not null,
    pending_plan_id bigint,
    pending_plan_change_date timestamp,
    constraint fk_subscription_user foreign key (user_id) references users(id),
    constraint fk_subscription_plan foreign key (plan_id) references plans(id),
    constraint fk_subscription_pending_plan foreign key (pending_plan_id) references plans(id)
);

create table subscription_events (
    id bigserial primary key,
    subscription_id bigint not null,
    type varchar(32) not null,
    occurred_at timestamp not null,
    constraint fk_event_subscription foreign key (subscription_id) references subscriptions(id)
);