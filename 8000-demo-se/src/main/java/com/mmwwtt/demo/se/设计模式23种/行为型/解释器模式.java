package com.mmwwtt.demo.se.设计模式23种.行为型;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Slf4j
public class 解释器模式 {

    @Test
    @DisplayName("测试解释器模式")
    public void test() {
        Expression isMale = getMaleExpression();
        Expression isMarriedWoman = getMarriedWomanExpression();

        log.info("John is male? {}", isMale.interpret("John"));
        log.info("Julie is a married women? {}", isMarriedWoman.interpret("Married Julie"));
    }


    public Expression getMaleExpression(){
        Expression robert = new TerminalExpression("Robert");
        Expression john = new TerminalExpression("John");
        return new OrExpression(robert, john);
    }

    //规则：Julie 是一个已婚的女性
    public  Expression getMarriedWomanExpression(){
        Expression julie = new TerminalExpression("Julie");
        Expression married = new TerminalExpression("Married");
        return new AndExpression(julie, married);
    }

    abstract class Expression {
        public abstract boolean interpret(String context);
    }

    class TerminalExpression extends Expression {

        private String data;

        public TerminalExpression(String data){
            this.data = data;
        }

        @Override
        public boolean interpret(String context) {
            if(context.contains(data)){
                return true;
            }
            return false;
        }
    }

    class OrExpression extends Expression {

        private Expression expr1 = null;
        private Expression expr2 = null;

        public OrExpression(Expression expr1, Expression expr2) {
            this.expr1 = expr1;
            this.expr2 = expr2;
        }

        @Override
        public boolean interpret(String context) {
            return expr1.interpret(context) || expr2.interpret(context);
        }
    }

    class AndExpression extends Expression {

        private Expression expr1 = null;
        private Expression expr2 = null;

        public AndExpression(Expression expr1, Expression expr2) {
            this.expr1 = expr1;
            this.expr2 = expr2;
        }

        @Override
        public boolean interpret(String context) {
            return expr1.interpret(context) && expr2.interpret(context);
        }
    }
}
