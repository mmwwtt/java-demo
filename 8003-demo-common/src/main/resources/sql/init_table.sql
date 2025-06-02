create database if not exists demo;
use demo;
create table if not exists user_t
(
    user_id           bigint AUTO_INCREMENT not null comment '主键id'
        primary key,
    name              varchar(30)           null comment '姓名',
    sex_code          varchar(2)            null comment '性别',
    height            double                null comment '身高',
    birth_date        date                  null comment '出生年月日',
    birth_date_time   datetime              null comment '出生年月日 时间',
    birth_time        time                  null comment '出生时间',
    created_date      datetime              null comment '创建日期',
    created_by        bigint                null comment '创建人',
    last_updated_date datetime              null comment '最后更新时间',
    last_updated_by   bigint                null comment '最后更新人'
) AUTO_INCREMENT = 1000 COMMENT '用户表';
insert into user_t(user_id, name, sex_code, birth_date, birth_date_time, birth_time)
values (1, '小张', 1, '2000-04-07', '2010-04-07 11:12:40', '11:12:40');


create table if not exists family_t
(
    family_id bigint AUTO_INCREMENT not null comment '主键id'
        primary key,
    name      varchar(30)           null comment '姓名',
    user_id   bigint                null comment '用户id'
) AUTO_INCREMENT = 1000 COMMENT '家庭成员表';
insert into family_t(family_id, name, user_id)
values (1, '1爸', 1),
       (2, '2爸', 1);

create table if not exists contact_t
(
    contact_id  bigint AUTO_INCREMENT not null comment '主键id'
        primary key,
    email       varchar(30)           null comment '邮箱',
    phoneNumber varchar(30)           null comment '手机号码',
    address     varchar(50)           null comment '地址',
    user_id     bigint                null comment '用户id'
) AUTO_INCREMENT = 1000 COMMENT '联系方式表';
insert into contact_t(contact_id, email, phoneNumber, address, user_id)
values (1, 'qq@qq.com', '15988487590', '滨江区', 1);
