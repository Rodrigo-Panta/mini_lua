package interpreter.util;

import interpreter.expr.Expr;
import interpreter.value.NumberValue;
import interpreter.value.StringValue;
import interpreter.value.Value;

public class Utils {

    private Utils() {
    }

    //A function that checks if a value can be turned into a number, and if it can, returns that value
    public static NumberValue getAsNumberValue(Expr expr) {
        Value<?> v = expr.expr();
        NumberValue ret = null;
        if(v instanceof NumberValue) {
            ret = (NumberValue) v;
        } else if(v instanceof StringValue) {
            StringValue sv = (StringValue) v;
            String s = sv.value();
            try {
                Double d = Double.valueOf(s);
                ret = new NumberValue(d);
            } catch (Exception e) {
                Utils.abort(expr.getLine());
            }

        } else {
            Utils.abort(expr.getLine());
        }
        return ret;
    }

    //Returns a StringValue for the value in the expression if it's a StringValue or a NumberValue 
    public static StringValue getAsStringValue(Expr expr) {
        Value<?> v = expr.expr();
        StringValue ret = null;
        if(v instanceof StringValue) {
            ret = (StringValue) v;
        } else if(v instanceof NumberValue) {
            ret = new StringValue(v.toString());
        } else {
            Utils.abort(expr.getLine());
        }
        return ret;
    }

    public static void abort(int line) {
        System.out.printf("%02d: Operação inválida\n", line);
        System.exit(1);
    }

}