

use stock;


select count(*)
from stock_t;

select count(*)
from stock_detail_t;

select count(*)
from stock_calculation_result_t;

select count(*)
from stock_strategy_result_t;

select * from stock_strategy_win_t where level = '2';

select * from stock_strategy_result_t where stock_code = '000933.SZ';

delete
from stock_strategy_result_t
where level = 4;

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
order by win_rate desc;


select *
from stock_calculation_result_t
where type = 1
order by calc_res_id desc;


select *
from stock_detail_t
where stock_code = '002083.SZ'
order by deal_date desc;





delete  from stock_strategy_result_t where level =2