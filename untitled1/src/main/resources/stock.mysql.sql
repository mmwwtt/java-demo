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
    stock_detail_Id   INT(11)     NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    stock_code        VARCHAR(64) NOT NULL COMMENT '股票代码',
    deal_date         VARCHAR(64) NOT NULL COMMENT '交易日期',
    start_price       float       NOT NULL COMMENT '开盘价',
    high_price        float       NOT NULL COMMENT '最高价',
    low_price         float       NOT NULL COMMENT '最低价',
    end_price         float       NOT NULL COMMENT '收盘价',
    all_deal_quantity float       NOT NULL COMMENT '成交量',
    all_deal_price    float       NOT NULL COMMENT '成交额',
    last_price         float       NOT NULL COMMENT '前收盘价',
    price_pert         float       NOT NULL COMMENT '涨跌幅'
) COMMENT '股票详情表';

create index stock_code on stock_detail_t(stock_code);

create index stock_code_deal_date on stock_detail_t(stock_code,deal_date);