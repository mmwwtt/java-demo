DROP DATABASE IF EXISTS pms;
CREATE DATABASE pms;

use pms;

DROP TABLE IF EXISTS province_t;
CREATE TABLE province_t(
                           province_id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '省份id',
                           province_name VARCHAR(64) NOT NULL COMMENT '省份名称',
                           STATUS INT(2) NOT NULL COMMENT '状态(1启用,2禁用)',
                           create_by_id INT(8) COMMENT '创建人id',
                           create_by_name INT(8) COMMENT '创建人',
                           create_date DATETIME COMMENT '创建时间',
                           last_update_by_id INT(8) COMMENT '最后更新人id',
                           last_update_by_name INT(8) COMMENT '最后更新人',
                           last_update_date DATETIME COMMENT '最后更新时间',
                           delete_flag char(1) default 'N' not NULL comment '是否删除标识'
) COMMENT '省份表';

DROP TABLE IF EXISTS city_t;
CREATE TABLE city_t(
                       city_id INT(11)NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '城市id',
                       province_id INT(11) NOT NULL COMMENT '省份id',
                       city_name VARCHAR(64) NOT NULL COMMENT '城市名称',
                       STATUS INT(2) NOT NULL COMMENT '状态(1启用,2禁用)',
                       create_by_id INT(8) COMMENT '创建人id',
                       create_by_name INT(8) COMMENT '创建人',
                       create_date DATETIME COMMENT '创建时间',
                       last_update_by_id INT(8) COMMENT '最后更新人id',
                       last_update_by_name INT(8) COMMENT '最后更新人',
                       last_update_date DATETIME COMMENT '最后更新时间',
                       delete_flag char(1) default 'N' not NULL comment '是否删除标识'
) COMMENT '城市表';

DROP TABLE IF EXISTS zone_t;
CREATE TABLE zone_t(
                       zone_id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '地区id',
                       city_id INT(11) NOT NULL  COMMENT '城市id',
                       zone_name VARCHAR(64) NOT NULL COMMENT '地区名称',
                       STATUS INT(2) NOT NULL COMMENT '状态(1启用,2禁用)',
                       create_by_id INT(8) COMMENT '创建人id',
                       create_by_name INT(8) COMMENT '创建人',
                       create_date DATETIME COMMENT '创建时间',
                       last_update_by_id INT(8) COMMENT '最后更新人id',
                       last_update_by_name INT(8) COMMENT '最后更新人',
                       last_update_date DATETIME COMMENT '最后更新时间',
                       delete_flag char(1) default 'N' not NULL comment '是否删除标识'
) COMMENT '地区表';

DROP TABLE IF EXISTS user_t;
CREATE TABLE user_t (
                        user_id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '用户id',
                        user_name VARCHAR(64) NOT NULL  COMMENT '用户名',
                        PASSWORD VARCHAR(64) NOT NULL COMMENT '密码',
                        NAME VARCHAR(64) NOT NULL COMMENT '姓名',
                        avator VARCHAR(65) COMMENT '头像',
                        phone_number INT(11) COMMENT '手机号',
                        STATUS INT(2) COMMENT '状态',
                        create_by_id INT(8) COMMENT '创建人id',
                        create_by_name INT(8) COMMENT '创建人',
                        create_date DATETIME COMMENT '创建时间',
                        last_update_by_id INT(8) COMMENT '最后更新人id',
                        last_update_by_name INT(8) COMMENT '最后更新人',
                        last_update_date DATETIME COMMENT '最后更新时间',
                        delete_flag char(1) default 'N' not NULL comment '是否删除标识'
)COMMENT '用户表';

DROP TABLE IF EXISTS address_t;
CREATE TABLE address_t(
                          address_id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '地址id',
                          user_id INT(11) NOT NULL COMMENT '用户id',
                          province_id INT(11) NOT NULL COMMENT '省份id',
                          city_id  INT(11) NOT NULL COMMENT '城市id',
                          zone_id INT(11) NOT NULL COMMENT '地区id',
                          detail_address VARCHAR(512)COMMENT '详细地址',
                          create_by_id INT(8) COMMENT '创建人id',
                          create_by_name INT(8) COMMENT '创建人',
                          create_date DATETIME COMMENT '创建时间',
                          last_update_by_id INT(8) COMMENT '最后更新人id',
                          last_update_by_name INT(8) COMMENT '最后更新人',
                          last_update_date DATETIME COMMENT '最后更新时间',
                          delete_flag char(1) default 'N' not NULL comment '是否删除标识'
)COMMENT '用户地址表';

DROP TABLE IF EXISTS role_t;
CREATE TABLE role_t(
                       role_id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '角色id',
                       role_name VARCHAR(64) NOT NULL COMMENT'角色名称',
                       note VARCHAR(512) COMMENT'备注',
                       create_by_id INT(8) COMMENT '创建人id',
                       create_by_name INT(8) COMMENT '创建人',
                       create_date DATETIME COMMENT '创建时间',
                       last_update_by_id INT(8) COMMENT '最后更新人id',
                       last_update_by_name INT(8) COMMENT '最后更新人',
                       last_update_date DATETIME COMMENT '最后更新时间',
                       delete_flag char(1) default 'N' not NULL comment '是否删除标识'
) COMMENT '角色表';

DROP TABLE IF EXISTS user_role_t;
CREATE TABLE user_role_t(
                            user_role_id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键id',
                            user_id INT(11) NOT NULL COMMENT '用户id',
                            role_id INT(11) NOT NULL COMMENT '角色id',
                            create_by_id INT(8) COMMENT '创建人id',
                            create_by_name INT(8) COMMENT '创建人',
                            create_date DATETIME COMMENT '创建时间',
                            last_update_by_id INT(8) COMMENT '最后更新人id',
                            last_update_by_name INT(8) COMMENT '最后更新人',
                            last_update_date DATETIME COMMENT '最后更新时间',
                            delete_flag char(1) default 'N' not NULL comment '是否删除标识'
) COMMENT '用户角色表';

DROP TABLE IF EXISTS pet_t;
CREATE TABLE pet_t(
                      pet_id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '宠物id',
                      pet_name VARCHAR(64) NOT NULL COMMENT '宠物名',
                      pet_image_id INT(11) NOT NULL COMMENT '宠物封面图像id',
                      sex INT(2) NOT NULL COMMENT '性别(1雄/2雌)',
                      birth_date DATE COMMENT '出生日期',
                      sort INT(2) COMMENT '分类',
                      hair_color VARCHAR(64) COMMENT '毛色',
                      buy_price  DOUBLE COMMENT '进价',
                      sell_price DOUBLE COMMENT '售价',
                      source VARCHAR(64) COMMENT '来源',
                      note VARCHAR(256) COMMENT '备注(注意事项)',
                      vacciness_status INT(2) COMMENT '疫苗状态',
                      health_status INT(2) COMMENT'健康状态(1健康/2微恙/3生病/4重症/5死亡)',
                      sell_status INT(2) COMMENT '售卖状态(1已售/2未售)',
                      user_id INT(11) COMMENT '买家id',
                      create_by_id INT(8) COMMENT '创建人id',
                      create_by_name INT(8) COMMENT '创建人',
                      create_date DATETIME COMMENT '创建时间',
                      last_update_by_id INT(8) COMMENT '最后更新人id',
                      last_update_by_name INT(8) COMMENT '最后更新人',
                      last_update_date DATETIME COMMENT '最后更新时间',
                      delete_flag char(1) default 'N' not NULL comment '是否删除标识'
) COMMENT '宠物表';

DROP TABLE IF EXISTS pet_image_t;
CREATE TABLE pet_image_t(
                            pet_image_id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键id',
                            pet_id INT(11) NOT NULL COMMENT '宠物id',
                            image_name VARCHAR(64) NOT NULL COMMENT '图片文件名',
                            image_path VARCHAR(256) NOT NULL COMMENT '图片存储路径',
                            STATUS INT(2) NOT NULL COMMENT '状态(1启用/2禁用)',
                            create_by_id INT(8) COMMENT '创建人id',
                            create_by_name INT(8) COMMENT '创建人',
                            create_date DATETIME COMMENT '创建时间',
                            last_update_by_id INT(8) COMMENT '最后更新人id',
                            last_update_by_name INT(8) COMMENT '最后更新人',
                            last_update_date DATETIME COMMENT '最后更新时间',
                            delete_flag char(1) default 'N' not NULL comment '是否删除标识'
) COMMENT '宠物图片表';

DROP TABLE IF EXISTS food_t;
CREATE TABLE food_t(
                       food_id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '食品id',
                       image_name VARCHAR(64) COMMENT '食品图片名',
                       image_path VARCHAR(256) COMMENT '食品图片存储路径',
                       food_name VARCHAR(64) COMMENT '食品名',
                       component VARCHAR(256) COMMENT '成分',
                       STATUS INT(2) COMMENT '状态(1启用/2禁用)',
                       stock INT(11) COMMENT '库存',
                       buy_price DOUBLE COMMENT '进价',
                       sell_price DOUBLE COMMENT '售价',
                       source VARCHAR(64) COMMENT '食品来源(进货商)',
                       note VARCHAR(512) COMMENT '备注(注意事项)',
                       create_by_id INT(8) COMMENT '创建人id',
                       create_by_name INT(8) COMMENT '创建人',
                       create_date DATETIME COMMENT '创建时间',
                       last_update_by_id INT(8) COMMENT '最后更新人id',
                       last_update_by_name INT(8) COMMENT '最后更新人',
                       last_update_date DATETIME COMMENT '最后更新时间',
                       delete_flag char(1) default 'N' not NULL comment '是否删除标识'
) COMMENT '食品表';


DROP TABLE IF EXISTS pet_food_t;
CREATE TABLE pet_food_t(
                           pet_food_id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键id',
                           pet_id INT(11) NOT NULL COMMENT '宠物id',
                           food_id INT(11) NOT NULL COMMENT '食品id',
                           create_by_id INT(8) COMMENT '创建人id',
                           create_by_name INT(8) COMMENT '创建人',
                           create_date DATETIME COMMENT '创建时间',
                           last_update_by_id INT(8) COMMENT '最后更新人id',
                           last_update_by_name INT(8) COMMENT '最后更新人',
                           last_update_date DATETIME COMMENT '最后更新时间',
                           delete_flag char(1) default 'N' not NULL comment '是否删除标识'
) COMMENT '宠物食品表';



DROP TABLE IF EXISTS vacciness_t;
CREATE TABLE vacciness_t(
                            vacciness_id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '疫苗id',
                            image_name VARCHAR(64) COMMENT '疫苗图片名',
                            image_path VARCHAR(256) COMMENT '疫苗图片存储路径',
                            vacciness_name VARCHAR(64) COMMENT '疫苗名称',
                            sort INT(2) COMMENT '疫苗类型',
                            ACTION VARCHAR(256) COMMENT '作用',
                            manufacturer VARCHAR(256) COMMENT '生产商',
                            STATUS INT(2) COMMENT '状态(1启用/2禁用)',
                            note VARCHAR(512) COMMENT '备注(注意事项)',
                            create_by_id INT(8) COMMENT '创建人id',
                            create_by_name INT(8) COMMENT '创建人',
                            create_date DATETIME COMMENT '创建时间',
                            last_update_by_id INT(8) COMMENT '最后更新人id',
                            last_update_by_name INT(8) COMMENT '最后更新人',
                            last_update_date DATETIME COMMENT '最后更新时间',
                            delete_flag char(1) default 'N' not NULL comment '是否删除标识'
) COMMENT '疫苗表';


DROP TABLE IF EXISTS pet_vacciness_t;
CREATE TABLE pet_vacciness_t(
                                pet_vacciness_id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键id',
                                vacciness_id INT(11) NOT NULL COMMENT '疫苗id',
                                create_by_id INT(8) COMMENT '创建人id',
                                create_by_name INT(8) COMMENT '创建人',
                                create_date DATETIME COMMENT '创建时间',
                                last_update_by_id INT(8) COMMENT '最后更新人id',
                                last_update_by_name INT(8) COMMENT '最后更新人',
                                last_update_date DATETIME COMMENT '最后更新时间',
                                delete_flag char(1) default 'N' not NULL comment '是否删除标识'
) COMMENT'宠物疫苗表';

DROP TABLE IF EXISTS lookup_type_t;
CREATE TABLE lookup_type_t(
                              lookup_type_id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键id',
                              type varchar(64) comment 'lookup 类型',
                              name_cn varchar(64) comment '中文名称',
                              name_en varchar(64) comment '英文名称',
                              note varchar(256) comment '备注描述',
                              create_by_id INT(8) COMMENT '创建人id',
                              create_by_name varchar(64) COMMENT '创建人',
                              create_date DATETIME COMMENT '创建时间',
                              last_update_by_id INT(8) COMMENT '最后更新人id',
                              last_update_by_name varchar(64) COMMENT '最后更新人',
                              last_update_date DATETIME COMMENT '最后更新时间',
                              delete_flag char(1) default 'N' not NULL comment '是否删除标识'
) COMMENT'lookup类型查找表';

DROP TABLE IF EXISTS lookup_t;
CREATE TABLE lookup_t(
                         lookup_id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键id',
                         lookup_type_id int(11) comment 'lookupId',
                         code varchar(64) comment '编码',
                         name_cn varchar(64) comment '中文名称',
                         name_en varchar(64) comment '英文名称',
                         note varchar(256) comment '备注描述',
                         create_by_id INT(8) COMMENT '创建人id',
                         create_by_name varchar(64) COMMENT '创建人',
                         create_date DATETIME COMMENT '创建时间',
                         last_update_by_id INT(8) COMMENT '最后更新人id',
                         last_update_by_name varchar(64) COMMENT '最后更新人',
                         last_update_date DATETIME COMMENT '最后更新时间',
                         delete_flag char(1) default 'N' not NULL comment '是否删除标识'
) COMMENT'lookup查找表';








