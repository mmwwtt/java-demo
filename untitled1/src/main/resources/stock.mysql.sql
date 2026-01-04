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
    stock_detail_Id         INT(11)        NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    stock_code              VARCHAR(64)    NOT NULL COMMENT '股票代码',
    deal_date               VARCHAR(64)    NOT NULL COMMENT '交易日期',
    start_price             decimal(10, 4) NOT NULL COMMENT '开盘价',
    high_price              decimal(10, 4) NOT NULL COMMENT '最高价',
    low_price               decimal(10, 4) NOT NULL COMMENT '最低价',
    end_price               decimal(10, 4) NOT NULL COMMENT '收盘价',
    deal_quantity           decimal(20, 4) NOT NULL COMMENT '成交量',
    deal_price              decimal(20, 4) NOT NULL COMMENT '成交额',
    last_price              decimal(10, 4) NOT NULL COMMENT '前收盘价',
    price_pert              decimal(10, 4) NOT NULL COMMENT '涨跌幅',
    up_shadow_len           decimal(10, 4) COMMENT '上影线长度',
    up_shadow_pert          decimal(10, 4) COMMENT '上影线站总长的百分比',
    low_shadow_len          decimal(10, 4) COMMENT '下影线长度',
    low_shadow_pert         decimal(10, 4) COMMENT '下影线站总长的百分比',
    entity_len              decimal(10, 4) COMMENT '实体长度',
    entity_pert             decimal(10, 4) COMMENT '实体占总长的百分比',
    all_len                 decimal(10, 4) COMMENT '总长',
    five_day_line           decimal(10, 4) COMMENT '5日线',
    five_day_high           decimal(10, 4) COMMENT '5日最高',
    five_day_low            decimal(10, 4) COMMENT '5日最低',
    five_day_deal_quantity   decimal(20, 4) COMMENT '5日均量',
    ten_day_line            decimal(10, 4) COMMENT '10日线',
    ten_day_high            decimal(10, 4) COMMENT '10日最高',
    ten_day_low             decimal(10, 4) COMMENT '10日最低',
    ten_day_deal_quantity    decimal(20, 4) COMMENT '10日均量',
    twenty_day_line         decimal(10, 4) COMMENT '20日线',
    twenty_day_high         decimal(10, 4) COMMENT '20日最高',
    twenty_day_low          decimal(10, 4) COMMENT '20日最低',
    twenty_day_deal_quantity decimal(20, 4) COMMENT '20日均量',
    sixty_day_line          decimal(10, 4) COMMENT '60日线',
    sixty_day_high          decimal(10, 4) COMMENT '60日最高',
    sixty_day_low           decimal(10, 4) COMMENT '60日最低',
    sixty_day_deal_quantity  decimal(20, 4) COMMENT '60日均量',
    pert_division_quantity  decimal(20, 15) COMMENT '涨跌成交比',
    is_up                   boolean COMMENT '是否为阳线(收盘价高于开盘价，  可能是-9  -> -1  也是阳线)',
    is_down                 boolean COMMENT '是否为阴线',
    is_balance              boolean COMMENT '开盘价是否等于收盘价',
    is_ten_star             boolean COMMENT '是否为十字星'
) COMMENT '股票详情表';

create index stock_code on stock_detail_t (stock_code);

create index stock_code_deal_date on stock_detail_t (stock_code, deal_date);



DROP TABLE IF EXISTS stock_calculation_result_t;
CREATE TABLE stock_calculation_result_t
(
    calc_res_id   INT(11)  NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    type          VARCHAR(64) COMMENT '策略类型',
    strategy_desc VARCHAR(256) COMMENT '策略描述',
    win_rate      DECIMAL(8, 4) COMMENT '预测后的平均涨幅',
    all_cnt       INT(11) COMMENT '符合条件的数据量',
    perc_rate     DECIMAL(8, 4) COMMENT '百分比叠加后的结果',
    create_date   DATETIME NOT NULL COMMENT '创建日期'
) COMMENT '股票计算结果表';


select count(*)
from stock_t;

select count(*)
from stock_detail_t;

select count(*)
from stock_calculation_result_t;

select *
from stock_t;

select *
from stock_detail_t;

select *
from stock_calculation_result_t;



select *
from stock_calculation_result_t
where create_date = (select max(create_date) from stock_calculation_result_t where type = '0')
  and type = 0
  and all_cnt > 300
  and win_rate > 0.6
order by win_rate desc, all_cnt desc;


select *
from stock_calculation_result_t
where type = 1
  and create_date = (select max(create_date) from stock_calculation_result_t where type = '1')
order by win_rate desc, all_cnt desc;


select count(*)
from stock_calculation_result_t
where create_date = (select max(create_date) from stock_calculation_result_t);




