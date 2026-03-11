use stock;

delete
from stock_strategy_result_t
where level != 1;
delete
from stock_strategy_win_t
where level != 1;


