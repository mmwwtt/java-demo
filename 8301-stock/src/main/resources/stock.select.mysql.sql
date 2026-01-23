

use stock;


select count(*)
from stock_t;

select count(*)
from stock_detail_t;

select count(*)
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
order by win_rate desc;


select *
from stock_calculation_result_t
where type = 1
order by calc_res_id desc;


select *
from stock_detail_t
where stock_code = '603107.SH'
order by deal_date desc;


# 设置最大连接数为500
SET GLOBAL max_connections = 300;
set global innodb_flush_log_at_trx_commit = 0;
set global sync_binlog = 100000;
SET GLOBAL innodb_buffer_pool_size = 8 * 1024 * 1024 * 1024; -- 4 GB
SET GLOBAL innodb_log_buffer_size = 128 * 1024 * 1024; -- 16 MB
SET GLOBAL thread_cache_size = 50;

#已连接的线程
SHOW STATUS LIKE 'Threads_connected';
#最大线程
SHOW VARIABLES LIKE 'max_connections';
#正在执行sql的线程
SHOW STATUS LIKE 'Threads_running';