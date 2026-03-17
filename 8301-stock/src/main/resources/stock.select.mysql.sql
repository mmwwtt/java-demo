use stock;

select *
from stock.strategy_tmp_t;



select count(*)
from stock.strategy_tmp_t;




SELECT strategy_id,
       strategy_code, `desc`, detail_ids, rise1_avg, rise2_avg, rise3_avg, rise4_avg, rise5_avg, rise10_avg, rise5_max_avg, rise10_max_avg, rise1_middle, rise2_middle, rise3_middle, rise4_middle, rise5_middle, rise10_middle, rise5_max_middle, rise10_max_middle
FROM strategy_l1_t