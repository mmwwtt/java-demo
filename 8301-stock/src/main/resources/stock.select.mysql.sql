use stock;


select count(*)
from stock_t;

select count(*)
from stock_detail_t;


select count(*)
from stock_strategy_result_t;

select count(*)
from stock_strategy_result_t
where level = 1;

select count(*)
from stock_strategy_result_t
where level = 2;

select count(*)
from stock_strategy_result_t
where level = 3;

select count(*)
from stock_strategy_result_t
where level = 4;

select count(*)
from stock_strategy_result_t
where level = 5;

select count(*)
from stock_strategy_result_t
where level = 6;

select count(*)
from stock_strategy_result_t
where level = 7;

select count(*)
from stock_strategy_result_t
where level = 8;

select count(*)
from stock_strategy_result_t
where level = 9;

select count(*)
from stock_strategy_result_t
where level = 10;

select count(*)
from stock_strategy_result_t
where level = 11;



select *
from stock_strategy_win_t
where level = '2';

select *
from stock_strategy_result_t
where stock_code = '000933.SZ';

delete
from stock_strategy_result_t
where level = 4;



select *
from stock_detail_t
where stock_code = '002083.SZ'
order by deal_date desc;

select *
from stock_strategy_result_t
where strategy_code = '1026 1032 1084 11 12 13';

delete
from stock_strategy_result_t
where level != 1;
delete
from stock_strategy_win_t
where level != 1;


select *
from stock_strategy_win_t t
where one_perc_rate < two_perc_rate
  and two_perc_rate < three_perc_rate
  and three_perc_rate < four_perc_rate
  and four_perc_rate< five_perc_rate
and five_perc_rate > 0.06
