use stock;

select *
from strategy_tmp_t;



select down_shadow_len
from detail_t
order by down_shadow_len
limit 110000,1;


select strategy_id,strategy_code,name,type,cnt,rise1_middle,rise1_max_middle,rise2_middle,rise3_middle,
       rise4_middle,rise5_middle,rise10_middle,rise5_max_middle,
       rise10_max_middle
from strategy_l1_t
where name like 'T5%'
order by rise5_max_middle desc


select * from strategy_t
where predict_rise3_avg <0
