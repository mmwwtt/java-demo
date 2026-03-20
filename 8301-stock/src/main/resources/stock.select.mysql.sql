use stock;

select *
from strategy_tmp_t;



select dif from detail_t order by  dif limit 110000,1;



-- 自动生成基础策略枚举  start--------------------------------------
SET @field = 'dif';
SET @getter = 'getDif()';
SET @code = '310';

WITH split_points AS (SELECT row_num,
                             dif as cut_val
                      FROM (SELECT dif,
                                   ROW_NUMBER() OVER (ORDER BY dif ASC) AS row_num
                            FROM detail_t
                            WHERE dif IS NOT NULL) AS ranked_data
                      WHERE row_num IN (
                                        55000, 110000, 165000, 220000, 275000,
                                        330000, 385000, 440000, 495000, 550000,
                                        605000, 660000, 715000, 770000, 825000
                          )),
     sp_with_idx AS (SELECT ROW_NUMBER() OVER (ORDER BY row_num) AS rn, cut_val FROM split_points),
     ranges AS (
         SELECT 1 AS idx, NULL AS min_val, (SELECT cut_val FROM sp_with_idx WHERE rn = 1) AS max_val
         UNION ALL
         SELECT a.rn + 1 AS idx, a.cut_val AS min_val, b.cut_val AS max_val
         FROM sp_with_idx a
         JOIN sp_with_idx b ON b.rn = a.rn + 2
         UNION ALL
         SELECT 15 AS idx, (SELECT cut_val FROM sp_with_idx WHERE rn = 14) AS min_val, (SELECT cut_val FROM sp_with_idx WHERE rn = 15) AS max_val
         UNION ALL
         SELECT 16 AS idx, (SELECT cut_val FROM sp_with_idx WHERE rn = 15) AS min_val, NULL AS max_val
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