use stock;

select *
from strategy_tmp_t;



select dif from detail_t order by  dif limit 110000,1;



-- 自动生成基础策略枚举  start--------------------------------------
SET @field = 'wr';
SET @getter = 'getWr()';
SET @code = '313';

WITH split_points AS (SELECT row_num,
                             wr as cut_val
                      FROM (SELECT wr,
                                   ROW_NUMBER() OVER (ORDER BY wr ASC) AS row_num
                            FROM detail_t
                            WHERE wr IS NOT NULL) AS ranked_data
                      WHERE row_num IN (
                                        55000, 110000, 165000, 220000, 275000,
                                        330000, 385000, 440000, 495000, 550000,
                                        605000, 660000, 715000, 770000, 825000
                          )),
     ranges AS (

         SELECT 1 AS idx, NULL AS min_val, (SELECT cut_val FROM split_points ORDER BY row_num LIMIT 1) AS max_val
         UNION ALL

         SELECT ROW_NUMBER() OVER (ORDER BY row_num) + 1 AS idx, cut_val AS min_val, LEAD(cut_val) OVER (ORDER BY row_num) AS max_val
         FROM split_points
     )
SELECT CONCAT(
               'new StrategyEnum("', @code, LPAD(idx, 2, '0'), '", "',
               CASE
                   WHEN min_val IS NULL THEN CONCAT(@field, '<', ROUND(max_val, 4))
                   WHEN max_val IS NULL THEN CONCAT(ROUND(min_val, 4), '<', @field)
                   ELSE CONCAT(ROUND(min_val, 4), '<', @field, '<', ROUND(max_val, 4))
                   END,
               '", "', @field, '", (Detail t0) -> ',
               CASE
                   WHEN min_val IS NULL THEN CONCAT('lessThan(t0.', @getter, ', ', ROUND(max_val, 4), ')')
                   WHEN max_val IS NULL THEN CONCAT('moreThan(t0.', @getter, ', ', ROUND(min_val, 4), ')')
                   ELSE CONCAT('isInRange(t0.', @getter, ', ', ROUND(min_val, 4), ', ', ROUND(max_val, 4), ')')
                   END,
               '),'
       ) AS java_code
FROM ranges
ORDER BY idx;

-- 自动生成基础策略枚举  end--------------------------------------