
#慢查询系统参数配置(打开on/关闭off)
show global variables like 'slow_query_log';
set global slow_query_log=on;

#定位慢查询的时间阈值(默认10s)
show global variables like 'long_query_time';
set global long_query_time=1;

#慢查询日志存储路径
show global variables like 'slow_query_log_file';


#查找、设置慢查询保存方式(默认File)
#FILE：保存在文件，通过慢日志查询存储路径命令查询保存位置
#TABLE：保存在数据库，会保存到mysql库的mysql.slow_log表中

show variables like '%log_output%';
set global log_output='FILE,TABLE';

#查询有多少条慢SQL
show global status like '%Slow_queries%';

#查询、设置未使用索引的SQL记录(默认OFF)full index scan也会被记录为慢查询
show variables like 'log_queries_not_using_indexes';
set global log_queries_not_using_indexes=on;

#查询慢sql日志
SELECT SLEEP(30);
select * from mysql.slow_log;





#mysql中的条件判断 case-when
select (case
            when sex = '0' then '男'
            when sex = '1' then '女'
            else '不男不女' end) sex
from student_t;

#mysql中的条件判断 if
select  if(sex = '0', '男', '女') sex
from student_t;









-- 递归查询父节点-包含自己
WITH RECURSIVE area_tree AS (
    SELECT t1.*, 4 level
    FROM area_t T1
    WHERE T1.id='120113'
    UNION ALL
    SELECT t2.*, t3.level - 1
    FROM area_t T2, area_tree T3
    WHERE T2.id=T3.parent_id
)
SELECT * FROM area_tree T;



-- 查询子节点  包含自己
WITH RECURSIVE area_tree AS (
    SELECT t1.*, 4 level
    FROM area_t T1
    WHERE T1.id='120113'
    UNION ALL
    SELECT t2.*, t3.level + 1
    FROM area_t T2, area_tree T3
    WHERE T2.parent_id=T3.id
)
SELECT * FROM area_tree T;






#两个表根据主键插入/更新数据
replace INTO student_tmp_t
SELECT *
FROM student_t;

# insert + update 实现merge into
           insert into student_tmp_t(student_id, student_number)
select t.student_id, t.student_number
from student_t t
         left join student_tmp_t ti on t.student_id = ti.student_id
where ti.student_id is null;

update student_t t,student_tmp_t ti
SET t.student_id=ti.student_id,
    t.student_number = ti.student_number
where t.student_id = ti.student_id
