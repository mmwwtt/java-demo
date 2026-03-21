DROP DATABASE IF EXISTS stock;
CREATE DATABASE stock;

use stock;


# 设置最大连接数为500
SET GLOBAL max_connections = 500;
set global innodb_flush_log_at_trx_commit = 0;
set global sync_binlog = 100000;
SET GLOBAL innodb_buffer_pool_size = 8 * 1024 * 1024 * 1024; -- 8 GB
SET GLOBAL innodb_log_buffer_size = 128 * 1024 * 1024; -- 128 MB
SET GLOBAL thread_cache_size = 50;
set GLOBAL sort_buffer_size = 1 * 1024 * 1024 * 1024;


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


DROP TABLE IF EXISTS detail_t;
CREATE TABLE detail_t
(
    detail_Id                INT(8)         NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    stock_code               VARCHAR(16)    NOT NULL COMMENT '股票代码',
    deal_date                VARCHAR(16)    NOT NULL COMMENT '交易日期',
    start_price              decimal(10, 4) NOT NULL COMMENT '开盘价',
    high_price               decimal(10, 4) NOT NULL COMMENT '最高价',
    low_price                decimal(10, 4) NOT NULL COMMENT '最低价',
    end_price                decimal(10, 4) NOT NULL COMMENT '收盘价',
    deal_quantity            decimal(20, 4) NOT NULL COMMENT '成交量',
    deal_price               decimal(20, 4) NOT NULL COMMENT '成交额',
    last_price               decimal(10, 4) NOT NULL COMMENT '前收盘价',
    up_shadow_len            decimal(10, 4) COMMENT '上影线长度',
    up_shadow_pert           decimal(10, 4) COMMENT '上影线站总长的百分比',
    down_shadow_len          decimal(10, 4) COMMENT '下影线长度',
    down_shadow_pert         decimal(10, 4) COMMENT '下影线站总长的百分比',
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
    rise0                    decimal(10, 4) NOT NULL COMMENT '当天涨跌幅',
    rise1                    decimal(10, 4) COMMENT '当天收盘到1天后的涨幅',
    rise2                    decimal(10, 4) COMMENT '当天收盘到2天后的涨幅',
    rise3                    decimal(10, 4) COMMENT '当天收盘到3天后的涨幅',
    rise4                    decimal(10, 4) COMMENT '当天收盘到4天后的涨幅',
    rise5                    decimal(10, 4) COMMENT '当天收盘到5天后的涨幅',
    rise5_max                decimal(10, 4) COMMENT '当天收盘到5天内最高的涨幅',
    rise5_min                decimal(10, 4) COMMENT '当天收盘到5天内最低的涨幅(回调)',
    rise10                   decimal(10, 4) COMMENT '当天收盘到10天后的涨幅',
    rise10_max               decimal(10, 4) COMMENT '当天收盘到10天内最高的涨幅',
    rise10_min               decimal(10, 4) COMMENT '当天收盘到10天内最低的涨幅(回调)',
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
    position60               decimal(10, 4) comment '60天内位置',
    rsi                      decimal(15, 4) comment 'RSI相对强弱指标(14日)',
    atr14                    decimal(15, 4) comment 'ATR平均真实波幅(14日)',
    bias5                    decimal(10, 4) comment '5日乖离率',
    bias10                   decimal(10, 4) comment '10日乖离率',
    bias20                   decimal(10, 4) comment ' 20日乖离率',
    ma_align_bull_score      INT4 comment ' 均线多头排列强度(0~4)',
    ma_align_bear_score      INT4 comment '均线空头排列强度(0~4)',
    boll_position            decimal(10, 4) comment ' 20日均线斜率(日变化率)',
    ma20_slope               decimal(10, 4) comment 'RSI相对强弱指标(14日)',
    volatility20             decimal(10, 4) comment '20日波动率',
    volume_price_divergence  INT4 comment '量价背离信号'
) COMMENT '股票详情表';
create index stockCode on detail_t (stock_code);


DROP TABLE IF EXISTS strategy_l1_t;
CREATE TABLE strategy_l1_t
(
    strategy_id       INT(8) NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    strategy_code     VARCHAR(200) COMMENT '策略编码',
    name              VARCHAR(200) COMMENT '策略描述',
    type              VARCHAR(200) COMMENT '策略类型',
    cnt               INT(8) COMMENT '符合条件的id数量',
    rise1_avg         DECIMAL(8, 4) COMMENT '1日平均涨幅',
    rise2_avg         DECIMAL(8, 4) COMMENT '2日平均涨幅',
    rise3_avg         DECIMAL(8, 4) COMMENT '3日平均涨幅',
    rise4_avg         DECIMAL(8, 4) COMMENT '4日平均涨幅',
    rise5_avg         DECIMAL(8, 4) COMMENT '5日平均涨幅',
    rise10_avg        DECIMAL(8, 4) COMMENT '10日平均涨幅',
    rise5_max_avg     DECIMAL(8, 4) COMMENT '5日最大平均涨幅',
    rise10_max_avg    DECIMAL(8, 4) COMMENT '10日最大平均涨幅',
    rise1_middle      DECIMAL(8, 4) COMMENT '1日中位数涨幅',
    rise2_middle      DECIMAL(8, 4) COMMENT '2日中位数涨幅',
    rise3_middle      DECIMAL(8, 4) COMMENT '3日中位数涨幅',
    rise4_middle      DECIMAL(8, 4) COMMENT '4日中位数涨幅',
    rise5_middle      DECIMAL(8, 4) COMMENT '5日中位数涨幅',
    rise10_middle     DECIMAL(8, 4) COMMENT '10日中位数涨幅',
    rise5_max_middle  DECIMAL(8, 4) COMMENT '5日最大中位数涨幅',
    rise10_max_middle DECIMAL(8, 4) COMMENT '10日最大中位数涨幅',
    detail_ids        JSON COMMENT '符合条件的详情id列表'
) COMMENT '1层策略结果表';


DROP TABLE IF EXISTS strategy_tmp_t;
CREATE TABLE strategy_tmp_t
(
    strategy_id   INT(8) NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    strategy_code VARCHAR(200) COMMENT '策略编码',
    date_cnt      INT(8) COMMENT '有符合数据的日期天数',
    detail_cnt    INT(8) COMMENT '有符合数据的详情数',
    pert          DECIMAL(8, 4) COMMENT '用于过滤判断的临时属性',
    level         INT(4) comment '策略层数'
) COMMENT '策略中间表';


DROP TABLE IF EXISTS strategy_t;
CREATE TABLE strategy_t
(
    strategy_id       INT(8) NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    strategy_code     VARCHAR(200) COMMENT '策略编码',
    name              VARCHAR(200) COMMENT '策略描述',
    detail_ids        JSON COMMENT '符合条件的详情id列表',
    detail_cnt        INT(8) COMMENT '符合条件的详情数量',
    date_cnt          INT(8) COMMENT '有符合数据的日期天数',
    is_active         boolean comment '策略是否有效(如何和其他重复度高则根据 规则决定是否失效)',
    rise1_avg         DECIMAL(8, 4) COMMENT '1日平均涨幅',
    rise1_middle      DECIMAL(8, 4) COMMENT '1日中位数涨幅',
    rise2_avg         DECIMAL(8, 4) COMMENT '2日平均涨幅',
    rise2_middle      DECIMAL(8, 4) COMMENT '2日中位数涨幅',
    rise3_avg         DECIMAL(8, 4) COMMENT '3日平均涨幅',
    rise3_middle      DECIMAL(8, 4) COMMENT '3日中位数涨幅',
    rise4_avg         DECIMAL(8, 4) COMMENT '4日平均涨幅',
    rise4_middle      DECIMAL(8, 4) COMMENT '4日中位数涨幅',
    rise5_avg         DECIMAL(8, 4) COMMENT '5日平均涨幅',
    rise10_avg        DECIMAL(8, 4) COMMENT '10日平均涨幅',
    rise5_middle      DECIMAL(8, 4) COMMENT '5日中位数涨幅',
    rise5_max_avg     DECIMAL(8, 4) COMMENT '5日最大平均涨幅',
    rise5_max_middle  DECIMAL(8, 4) COMMENT '5日最大中位数涨幅',
    rise10_max_avg    DECIMAL(8, 4) COMMENT '10日最大平均涨幅',
    rise10_middle     DECIMAL(8, 4) COMMENT '10日中位数涨幅',
    rise10_max_middle DECIMAL(8, 4) COMMENT '10日最大中位数涨幅',
    level             INT(4) comment '策略层数'
) COMMENT '策略结果表';