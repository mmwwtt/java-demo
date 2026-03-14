use stock;

delete
from stock_strategy_win_t
where level != 1;


select * from stock.stock_strategy_win_t
    where five_max_perc_rate > 0.11
      and five_max_middle_perc_rate>0.09
      and five_min_perc_rate > -0.01
        and five_perc_rate > 0.015;




select count(*) from stock.stock_strategy_win_t;
