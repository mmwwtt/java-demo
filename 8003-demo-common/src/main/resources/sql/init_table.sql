create table base_info_t
(
    base_info_id      bigint      not null comment '主键id'
        primary key,
    name              varchar(30) null comment '姓名',
    sex_code          varchar(2)  null comment '性别',
    height            double      null comment '身高',
    birth_date        date        null comment '出生年月日',
    created_date      datetime    null comment '创建日期',
    created_by        varchar(30) null comment '创建人',
    last_updated_date datetime    null comment '最后更新时间',
    last_updated_by   varchar(30) null comment '最后更新人'
);

