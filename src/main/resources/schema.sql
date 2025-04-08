create table if not exists member
(
    id          bigint auto_increment primary key,
    name        varchar(32)  not null,
    email       varchar(32)  not null,
    password    varchar(128) not null,
    role        varchar(8)   not null,
    delete_type varchar(6)   not null,
    updated_at  timestamp    not null,
    created_at  timestamp    not null
);

create table if not exists team
(
    id          bigint auto_increment primary key,
    name        varchar(32) not null,
    delete_type varchar(6)  not null,
    updated_at  timestamp   not null,
    created_at  timestamp   not null
);

create table if not exists member_access_history
(
    id                              bigint auto_increment primary key,
    result_type                     varchar(8) not null,
    member_access_history_member_id bigint     not null,
    failure_type                    varchar(32),
    access_at                       timestamp  not null
);
