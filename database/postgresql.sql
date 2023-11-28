create table if not exists xn_user
(
    id              serial
        primary key,
    name            varchar(100)                           not null
        unique,
    password        varchar(64)                            not null,
    create_time     timestamp    default CURRENT_TIMESTAMP not null,
    last_login_time timestamp,
    last_login_ip   varchar(100) default NULL::character varying,
    remark          text
);

comment on table xn_user is '任务调度平台用户表';

comment on column xn_user.id is '用户Id,雪花算法生成';

comment on column xn_user.name is '用户名称，唯一';

comment on column xn_user.password is '用户密码，md5加密';

comment on column xn_user.create_time is '用户的创建时间';

comment on column xn_user.last_login_time is '用户的最后一次登录时间';

comment on column xn_user.last_login_ip is '用户最后一次登录的IP地址';

comment on column xn_user.remark is '用户备注';

alter table xn_user
    owner to postgres;

create table if not exists xn_task_info
(
    id           serial
        primary key,
    name         varchar(200)                        not null,
    remark       text,
    task_type    integer                             not null,
    handler_type integer                             not null,
    handler_name varchar(200)                        not null,
    params       text,
    create_time  timestamp default CURRENT_TIMESTAMP not null,
    update_time  timestamp,
    status       integer   default 0                 not null
);

comment on table xn_task_info is '任务管理表';

comment on column xn_task_info.id is '任务ID,主键，自动生成';

comment on column xn_task_info.name is '任务名称';

comment on column xn_task_info.remark is '任务备注';

comment on column xn_task_info.task_type is '任务类型 0：单机执行 1：广播执行';

comment on column xn_task_info.handler_type is '任务的处理器类型 0：SpringBean 1：自定义注解 2：方法返回名称';

comment on column xn_task_info.handler_name is '任务处理器名称';

comment on column xn_task_info.params is '任务的执行参数';

comment on column xn_task_info.create_time is '任务的创建时间';

comment on column xn_task_info.update_time is '任务的最后更新时间';

comment on column xn_task_info.status is '任务状态 0：正常 1：禁用';

alter table xn_task_info
    owner to postgres;

create table if not exists xn_task_record
(
    id              serial
        primary key,
    task_id         integer                             not null,
    execute_count   integer   default 0                 not null,
    execute_name    varchar(200)                        not null,
    execute_address varchar(100)                        not null,
    execute_params  text,
    create_time     timestamp default CURRENT_TIMESTAMP not null,
    runner_time     timestamp,
    stop_time       timestamp,
    status          integer   default 1                 not null,
    result_content  text
);

comment on table xn_task_record is '任务运行记录表';

comment on column xn_task_record.id is '记录Id,主键，自动生成';

comment on column xn_task_record.task_id is '任务Id,非空';

comment on column xn_task_record.execute_count is '当前任务记录的执行次数';

comment on column xn_task_record.execute_name is '当前任务记录执行的实例名称';

comment on column xn_task_record.execute_address is '当前任务记录执行的实例地址';

comment on column xn_task_record.execute_params is '任务运行时的运行参数';

comment on column xn_task_record.create_time is '任务记录的创建时间';

comment on column xn_task_record.runner_time is '任务的运行时间';

comment on column xn_task_record.stop_time is '任务的结束时间';

comment on column xn_task_record.status is '运行记录状态 0：成功 1：等待中 2：运行中 3：失败';

comment on column xn_task_record.result_content is '任务运行结果详情';

alter table xn_task_record
    owner to postgres;

create table if not exists xn_task_logs
(
    id          serial
        primary key,
    task_id     integer                             not null,
    record_id   integer                             not null,
    content     text,
    create_time timestamp default CURRENT_TIMESTAMP not null
);

comment on table xn_task_logs is '任务运行记录日志表';

comment on column xn_task_logs.id is '日志Id,主键 自动生成';

comment on column xn_task_logs.task_id is '任务id';

comment on column xn_task_logs.record_id is '任务记录Id';

comment on column xn_task_logs.content is '日志内容';

comment on column xn_task_logs.create_time is '日志的创建时间';

alter table xn_task_logs
    owner to postgres;

