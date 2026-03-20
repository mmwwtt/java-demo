use stock;

select *
from strategy_tmp_t;



select count(*)
from strategy_tmp_t;


select strategy_id from strategy_l1_t;


select count(*) from detail_t where macd >-1  and macd <0;

select count(*) from detail_t where macd >0  and macd <1;

select count(*) from detail_t where macd >1;


select macd from detail_t order by  macd limit 110000,1;
select macd from detail_t order by  macd limit 220000,1;
select macd from detail_t order by  macd limit 330000,1;
select macd from detail_t order by  macd limit 440000,1;
select macd from detail_t order by  macd limit 550000,1;
select macd from detail_t order by  macd limit 660000,1;
select macd from detail_t order by  macd limit 770000,1;