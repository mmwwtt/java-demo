DROP DATABASE IF EXISTS stock;
CREATE DATABASE stock;

use stock;


# 设置最大连接数为500
SET GLOBAL max_connections = 200;
set global innodb_flush_log_at_trx_commit = 0;
set global sync_binlog = 100000;
SET GLOBAL innodb_buffer_pool_size = 8 * 1024 * 1024 * 1024; -- 8 GB
SET GLOBAL innodb_log_buffer_size = 128 * 1024 * 1024; -- 128 MB
SET GLOBAL thread_cache_size = 50;

#已连接的线程
SHOW STATUS LIKE 'Threads_connected';
#最大线程
SHOW VARIABLES LIKE 'max_connections';
#正在执行sql的线程
SHOW STATUS LIKE 'Threads_running';
#查询正在执行的sql
SHOW FULL PROCESSLIST;

DROP TABLE IF EXISTS stock_t;
CREATE TABLE stock_t
(
    stock_Id INT(8)      NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    name     VARCHAR(16) NOT NULL COMMENT '股票名称',
    code     VARCHAR(16) NOT NULL COMMENT '股票编码'

) COMMENT '股票表';


DROP TABLE IF EXISTS stock_detail_t;
CREATE TABLE stock_detail_t
(
    stock_detail_Id          INT(8)         NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    stock_code               VARCHAR(16)    NOT NULL COMMENT '股票代码',
    deal_date                VARCHAR(16)    NOT NULL COMMENT '交易日期',
    start_price              decimal(10, 4) NOT NULL COMMENT '开盘价',
    high_price               decimal(10, 4) NOT NULL COMMENT '最高价',
    low_price                decimal(10, 4) NOT NULL COMMENT '最低价',
    end_price                decimal(10, 4) NOT NULL COMMENT '收盘价',
    deal_quantity            decimal(20, 4) NOT NULL COMMENT '成交量',
    deal_price               decimal(20, 4) NOT NULL COMMENT '成交额',
    last_price               decimal(10, 4) NOT NULL COMMENT '前收盘价',
    price_pert               decimal(10, 4) NOT NULL COMMENT '涨跌幅',
    up_shadow_len            decimal(10, 4) COMMENT '上影线长度',
    up_shadow_pert           decimal(10, 4) COMMENT '上影线站总长的百分比',
    low_shadow_len           decimal(10, 4) COMMENT '下影线长度',
    low_shadow_pert          decimal(10, 4) COMMENT '下影线站总长的百分比',
    entity_len               decimal(10, 4) COMMENT '实体长度',
    entity_pert              decimal(10, 4) COMMENT '实体占总长的百分比',
    all_len                  decimal(10, 4) COMMENT '总长',
    five_day_line            decimal(10, 4) COMMENT '5日线',
    five_high                decimal(10, 4) COMMENT '5日最高',
    five_low                 decimal(10, 4) COMMENT '5日最低',
    five_day_deal_quantity   decimal(20, 4) COMMENT '5日均量',
    five_high_date           varchar(64) COMMENT '5日最高日期',
    five_low_date            varchar(64) COMMENT '5日最低日期',
    five_is_up               boolean COMMENT '是否处于5日中的上涨',
    ten_day_line             decimal(10, 4) COMMENT '10日线',
    ten_high                 decimal(10, 4) COMMENT '10日最高',
    ten_low                  decimal(10, 4) COMMENT '10日最低',
    ten_day_deal_quantity    decimal(20, 4) COMMENT '10日均量',
    ten_high_date            varchar(64) COMMENT '10日最高日期',
    ten_low_date             varchar(64) COMMENT '10日最低日期',
    ten_is_up                boolean COMMENT '是否处于10日中的上涨',
    twenty_day_line          decimal(10, 4) COMMENT '20日线',
    twenty_high              decimal(10, 4) COMMENT '20日最高',
    twenty_low               decimal(10, 4) COMMENT '20日最低',
    twenty_high_date         varchar(64) COMMENT '20日最高日期',
    twenty_low_date          varchar(64) COMMENT '20日最低日期',
    twenty_is_up             boolean COMMENT '是否处于20日中的上涨',
    twenty_day_deal_quantity decimal(20, 4) COMMENT '20日均量',
    forty_day_line           decimal(10, 4) COMMENT '40日线',
    forty_high               decimal(10, 4) COMMENT '40日最高',
    forty_low                decimal(10, 4) COMMENT '40日最低',
    forty_high_date          varchar(64) COMMENT '40日最高日期',
    forty_low_date           varchar(64) COMMENT '40日最低日期',
    forty_is_up              boolean COMMENT '是否处于40日中的上涨',
    forty_day_deal_quantity  decimal(20, 4) COMMENT '40日均量',
    sixty_day_line           decimal(10, 4) COMMENT '60日线',
    sixty_high               decimal(10, 4) COMMENT '60日最高',
    sixty_low                decimal(10, 4) COMMENT '60日最低',
    sixty_high_date          varchar(64) COMMENT '60日最高日期',
    sixty_low_date           varchar(64) COMMENT '60日最低日期',
    sixty_is_up              boolean COMMENT '是否处于60日中的上涨',
    sixty_day_deal_quantity  decimal(20, 4) COMMENT '60日均量',
    pert_division_quantity   decimal(20, 15) COMMENT '涨跌成交比',
    is_up                    boolean COMMENT '是否上涨',
    is_down                  boolean COMMENT '是否下跌',
    is_red                   boolean COMMENT '是否为阳线',
    is_green                 boolean COMMENT '是否为阴线',
    is_balance               boolean COMMENT '开盘价是否等于收盘价',
    is_ten_star              boolean COMMENT '是否为十字星',
    next2_price_pert         decimal(10, 4) COMMENT '当天到2天后的涨幅',
    next3_price_pert         decimal(10, 4) COMMENT '当天到3天后的涨幅',
    next4_price_pert         decimal(10, 4) COMMENT '当天到4天后的涨幅',
    next5_price_pert         decimal(10, 4) COMMENT '当天到5天后的涨幅',
    next10_price_pert        decimal(10, 4) COMMENT '当天到10天后的涨幅',
    next5_max_price_pert     decimal(10, 4) COMMENT '当天到5天内最高的涨幅',
    next10_max_price_pert    decimal(10, 4) COMMENT '当天到10天内最高的涨幅',
    wr                       decimal(10, 4) COMMENT '威廉指标',
    ema12                    decimal(10, 4) COMMENT 'MACD相关指标',
    ema26                    decimal(10, 4) COMMENT 'MACD相关指标',
    dif                      decimal(10, 4) COMMENT 'MACD相关指标',
    dea                      decimal(10, 4) COMMENT 'MACD相关指标',
    macd                     decimal(10, 4) COMMENT 'MACD相关指标',
    position5                decimal(10, 4) comment '5天内位置',
    position10               decimal(10, 4) comment '10天内位置',
    position20               decimal(10, 4) COMMENT '在20日中的位置  (收盘价- 20日最低) / (20日最高- 20日最低)   大于80%是高位   小于20%是低位',
    position40               decimal(10, 4) comment '40天内位置',
    position60               decimal(10, 4) comment '60天内位置'

) COMMENT '股票详情表';
create index stock_code_deal_date on stock_detail_t (stock_code, deal_date desc);



DROP TABLE IF EXISTS stock_calculation_result_t;
CREATE TABLE stock_calculation_result_t
(
    calc_res_id        INT(11)  NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    type               VARCHAR(64) COMMENT '策略类型',
    strategy_desc      VARCHAR(256) COMMENT '策略描述',
    win_rate           DECIMAL(8, 4) COMMENT '预测胜率',
    all_cnt            INT(11) COMMENT '符合条件的数据量',
    perc_rate          DECIMAL(8, 4) COMMENT '百分比叠加后的结果',
    win_perc_rate      DECIMAL(8, 4) COMMENT '预测对的百分比叠加后的结果',
    two_perc_rate      DECIMAL(8, 4) COMMENT '2天后百分比叠加后的结果',
    three_perc_rate    DECIMAL(8, 4) COMMENT '3天后百分比叠加后的结果',
    four_perc_rate     DECIMAL(8, 4) COMMENT '4天后百分比叠加后的结果',
    five_perc_rate     DECIMAL(8, 4) COMMENT '5天后百分比叠加后的结果',
    five_max_perc_rate DECIMAL(8, 4) COMMENT '5天内最高价 百分比叠加后的结果',
    ten_perc_rate      DECIMAL(8, 4) COMMENT '10天后百分比叠加后的结果',
    ten_max_perc_rate  DECIMAL(8, 4) COMMENT '10天内最高价 百分比叠加后的结果',
    create_date        DATETIME NOT NULL COMMENT '创建日期'
) COMMENT '股票计算结果表';


DROP TABLE IF EXISTS stock_strategy_result_t;
CREATE TABLE stock_strategy_result_t
(
    strategy_result_id   INT(11)  NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    strategy_code        VARCHAR(200) COMMENT '策略编码',
    stock_code           VARCHAR(16) COMMENT '股票代码',
    stock_detail_id_list JSON COMMENT '预测的股票详情id列表',
    level                INT(4) comment '条件层数',
    create_date          DATETIME NOT NULL COMMENT '创建日期'
) COMMENT '策略预测表';
create index strategy_code on stock_strategy_result_t (strategy_code);
create index stock_code on stock_strategy_result_t (stock_code);


DROP TABLE IF EXISTS stock_strategy_win_t;
CREATE TABLE stock_strategy_win_t
(
    strategy_win_id    INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    strategy_code      VARCHAR(200) COMMENT '策略编码',
    strategy_name      VARCHAR(200) COMMENT '策略名称',
    cnt                INT(8) COMMENT '总数',
    win_rate           DECIMAL(8, 4) COMMENT '胜率',
    win_perc_rate      DECIMAL(8, 4) COMMENT '预测对的平均涨幅',
    one_perc_rate      DECIMAL(8, 4) COMMENT '1天后的平均涨幅',
    two_perc_rate      DECIMAL(8, 4) COMMENT '2天后百分比叠加后的结果',
    three_perc_rate    DECIMAL(8, 4) COMMENT '3天后百分比叠加后的结果',
    four_perc_rate     DECIMAL(8, 4) COMMENT '4天后百分比叠加后的结果',
    five_perc_rate     DECIMAL(8, 4) COMMENT '5天后百分比叠加后的结果',
    ten_perc_rate      DECIMAL(8, 4) COMMENT '10天后百分比叠加后的结果',
    five_max_perc_rate DECIMAL(8, 4) COMMENT '5天内最高价 百分比叠加后的结果',
    ten_max_perc_rate  DECIMAL(8, 4) COMMENT '10天内最高价 百分比叠加后的结果',
    level              INT(4) comment '条件层数',
    create_date        DATETIME COMMENT '创建日期'
) COMMENT '策略胜率表';
