DROP DATABASE IF EXISTS stock;
CREATE DATABASE stock;

use stock;

DROP TABLE IF EXISTS stock_t;
CREATE TABLE stock_t
(
    stock_Id INT(11)     NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    name     VARCHAR(64) NOT NULL COMMENT '股票名称',
    code     VARCHAR(64) NOT NULL COMMENT '股票编码'

) COMMENT '股票表';


DROP TABLE IF EXISTS stock_Detail_t;
CREATE TABLE stock_Detail_t
(
    stock_detail_Id        INT(11)     NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    stock_code             VARCHAR(64) NOT NULL COMMENT '股票代码',
    deal_date              VARCHAR(64) NOT NULL COMMENT '交易日期',
    start_price            DOUBLE      NOT NULL COMMENT '开盘价',
    high_price             DOUBLE      NOT NULL COMMENT '最高价',
    low_price              DOUBLE      NOT NULL COMMENT '最低价',
    end_price              DOUBLE      NOT NULL COMMENT '收盘价',
    all_deal_quantity      DOUBLE      NOT NULL COMMENT '成交量',
    all_deal_price         DOUBLE      NOT NULL COMMENT '成交额',
    last_price             DOUBLE      NOT NULL COMMENT '前收盘价',
    price_pert             DOUBLE      NOT NULL COMMENT '涨跌幅',
    up_shadow_len          DOUBLE COMMENT '上影线长度',
    up_shadow_pert         DOUBLE COMMENT '上影线站总长的百分比',
    low_shadow_len         DOUBLE COMMENT '下影线长度',
    low_shadow_pert        DOUBLE COMMENT '下影线站总长的百分比',
    entity_len             DOUBLE COMMENT '实体长度',
    entity_pert            DOUBLE COMMENT '实体占总长的百分比',
    all_len                DOUBLE COMMENT '总长',
    five_day_line          DOUBLE COMMENT '5日线',
    ten_day_line           DOUBLE COMMENT '10日线',
    twenty_day_line        DOUBLE COMMENT '20日线',
    sixty_day_line         DOUBLE COMMENT '60日线',
    pert_division_quentity DOUBLE COMMENT '涨跌成交比',
    is_up                  BOOLEAN COMMENT '是否为阳线(收盘价高于开盘价，  可能是-9  -> -1  也是阳线)',
    is_down                BOOLEAN COMMENT '是否为阴线',
    is_balance             BOOLEAN COMMENT '开盘价是否等于收盘价',
    is_ten_star            BOOLEAN COMMENT '是否为十字星'
) COMMENT '股票详情表';

create index stock_code on stock_detail_t (stock_code);

create index stock_code_deal_date on stock_detail_t (stock_code, deal_date);



DROP TABLE IF EXISTS stock_calculation_result_t;
CREATE TABLE stock_calculation_result_t
(
    calc_res_id int(11)      NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    strategy    VARCHAR(256) COMMENT '策略描述',
    win_rate    DOUBLE COMMENT '胜率',
    perc_rate   DOUBLE COMMENT '百分比叠加后的结果',
    create_date DATETIME(64) NOT NULL COMMENT '创建日期'
) COMMENT '股票计算结果表';