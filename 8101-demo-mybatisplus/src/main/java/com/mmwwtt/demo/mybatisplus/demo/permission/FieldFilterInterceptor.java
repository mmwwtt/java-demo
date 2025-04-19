package com.mmwwtt.demo.mybatisplus.demo.permission;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.sql.Connection;
import java.util.Properties;

@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class FieldFilterInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler)invocation.getTarget();
        // 获取原始 SQL
        String originalSql = statementHandler.getBoundSql().getSql();
        // 修改 SQL，过滤字段
        String modifiedSql = modifySql(originalSql);
        // 使用 MetaObject 修改 BoundSql 中的 SQL
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
        metaObject.setValue("boundSql.oracle语法.txt", modifiedSql);
        // 执行修改后的 SQL
        return invocation.proceed();
    }

    private String modifySql(String sql) {
        // 使用 JSqlParser 解析 SQL
        net.sf.jsqlparser.statement.Statement statement;
        try {
            statement = CCJSqlParserUtil.parse(sql);
        } catch (JSQLParserException e) {
            throw new RuntimeException("SQL 解析异常：" + sql, e);
        }
        // 如果是 SELECT 语句，过滤字段
        if (statement instanceof Select) {
            Select select = (Select) statement;
            PlainSelect selectBody = (PlainSelect) select.getSelectBody();
            // 假设只保留 id 和 name 字段
            selectBody.getSelectItems().removeIf(item -> {
                if (item instanceof SelectItem ) {
                    Expression expression = ((SelectItem) item).getExpression();
                    if (expression instanceof Column) {
                        String columnName = ((Column) expression).getColumnName();
                        return !"base_info_id".equals(columnName) && !"name".equals(columnName);
                    }
                }
                return false;
            });
            return select.toString();
        }
        return sql; // 对于非 SELECT 语句，不修改
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // 可选：设置属性
    }
}