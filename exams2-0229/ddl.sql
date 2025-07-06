-- DDL sql 记录到这里

create table moment_img_t
(
    moment_img_id    bigint auto_increment comment '主键'
        primary key,
    moment_id        bigint                 not null,
    img_path         varchar(60)            not null comment '图片路径',
    row_num          int                    not null comment '顺序',
    create_date      datetime               not null comment '创建时间',
    last_update_date datetime               not null comment '最后更新时间',
    delete_flag      varchar(2) default 'N' null comment '软删除标识 删除(Y) 未删除(N)'
)
    comment '动态-图片关系表';

create index moment_img_t__momentid_rownumindex
    on moment_img_t (moment_id, row_num)
    comment '(动态id,顺序号)';

create table moment_t
(
    moment_id        bigint auto_increment comment '主键'
        primary key,
    user_id          bigint                 not null comment '用户id',
    content          varchar(600)           null comment '内容',
    position         varchar(200)           null comment '位置',
    create_date      datetime               not null comment '创建时间',
    last_update_date datetime               not null comment '最后更新时间',
    delete_flag      varchar(2) default 'N' null comment '软删除标识 删除(Y) 未删除(N)'
)
    comment '朋友圈动态表';

create index moment_t_userid_index
    on moment_t (user_id)
    comment '(用户id)';

create table user_friend_t
(
    user_friend_id   bigint auto_increment comment '主键'
        primary key,
    user_id          bigint                 not null comment '用户id',
    friend_id        bigint                 not null comment '朋友的用户id',
    create_date      datetime               not null comment '创建时间',
    last_update_date datetime               not null comment '最后更新时间',
    delete_flag      varchar(2) default 'N' null comment '软删除标识 删除(Y) 未删除(N)'
)
    comment '用户朋友关系表';

create index user_friend_t_userid_index
    on user_friend_t (user_id)
    comment '(userId)';

create table user_t
(
    user_id          bigint auto_increment comment '用户表主键'
        primary key,
    password         varchar(64)            null comment '密码',
    user_name        varchar(30)            null comment '用户昵称',
    phone_number     varchar(20)            null comment '手机号',
    picture_path     varchar(60)            null comment '头像路径',
    create_date      datetime               not null comment '创建日期',
    last_update_date datetime               not null comment '最后更新日期',
    delete_flag      varchar(2) default 'N' null comment '软删除标识 删除(Y) 未删除(N)'
)
    comment '用户表';

