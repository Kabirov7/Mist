package com.interpretures.mist;

class Interpreter implements Expr.Visitor<Object> {

    public void interpret(Expr expression){
        try {
            Object value = evaluate(expression);
            System.out.println(stringify(value));
        } catch (RuntimeError error){
            Mist.runtimeError(error);
        }
    }

    private String stringify(Object object) {
        if (object == null) return "nill";

        if (object instanceof Double) {
            String txt = object.toString();
            if (txt.endsWith(".0")) {
                txt = txt.substring(0, txt.length() - 2);
            }
            return txt;
        }

        return object.toString();
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);
        int compareResult;

        switch (expr.operator.type){
            case MINUS:
                checkNumberOperands(expr.operator, left, right);
                return (double) left - (double) right;
            case PLUS:
                if (left instanceof Double && right instanceof Double){
                    return (double) left + (double) right;
                }
                if (left instanceof String || right instanceof String){
                    return stringify(left) + stringify(right);
                }
                throw new RuntimeError(expr.operator, "Operands must be two numbers or two strings");
            case SLASH:
                checkNumberOperands(expr.operator, left, right);
                if ((double) right == 0.)
                    throw new RuntimeError(expr.operator, "Division by zero");
                return (double) left / (double) right;
            case STAR:
                checkNumberOperands(expr.operator, left, right);
                return (double) left * (double) right;
            // Conditionals
            case GREATER:
                checkNumberOrStringOperands(expr.operator, left, right);
                if (left instanceof Double) return (double) left > (double) right;
                compareResult = ((String) left).compareTo((String) right);
                return compareResult > 0;
            case GREATER_EQUAL:
                checkNumberOrStringOperands(expr.operator, left, right);
                if (left instanceof Double)
                    return (double) left >= (double) right;
                compareResult = ((String) left).compareTo((String) right);
                return compareResult >= 0;
            case LESS:
                checkNumberOrStringOperands(expr.operator, left, right);
                if (left instanceof Double)
                    return (double) left < (double) right;
                compareResult = ((String) left).compareTo((String) right);
                return compareResult < 0;
            case LESS_EQUAL:
                checkNumberOrStringOperands(expr.operator, left, right);
                if (left instanceof Double)
                    return (double) left <= (double) right;
                compareResult = ((String) left).compareTo((String) right);
                return compareResult <= 0;
            case EQUAL_EQUAL:
                return isEqual(left, right);
            case BANG_EQUAL:
                return !isEqual(left, right);

        }
        return null;
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.right);

        switch (expr.operator.type){
            case BANG:
                return !isTruthy(right);
            case MINUS:
                checkNumberOperand(expr.operator, right);
                return -(double) right;
        }

        // Unreachable
        return null;
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double)return;
        throw new RuntimeError(operator, "Operand must be a number");
    }

    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) return;
        throw new RuntimeError(operator, "Operands must be a numbers");
    }

    private void checkNumberOrStringOperands(Token operator, Object a, Object b){
        if ((a instanceof Double && b instanceof Double) || (a instanceof String && b instanceof String)) return;
        throw new RuntimeError(operator, "Operands must be two numbers or strings");
    }

    private boolean isTruthy(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean) return (boolean) object;
        return true;
    }

    private boolean isEqual(Object a, Object b){
        if (a == null && b == null) return true;
        if (a == null) return false;

        return a.equals(b);
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }
}
