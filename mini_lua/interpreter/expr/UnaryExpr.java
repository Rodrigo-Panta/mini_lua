package interpreter.expr;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

import interpreter.util.Utils;
import interpreter.value.BooleanValue;
import interpreter.value.NumberValue;
import interpreter.value.StringValue;
import interpreter.value.TableValue;
import interpreter.value.Value;

public class UnaryExpr extends Expr {
    
    private Expr expr;
    private UnaryOp op;

    public UnaryExpr(int line, Expr expr, UnaryOp op) {
        super(line);
        this.expr = expr;
        this.op = op;
    }

    @Override
    public Value<?> expr() { 
        Value<?> v = expr.expr();

        Value<?> ret = null;
        switch (op) {
            case Neg:
                ret = negOp(v);
                break;
            case Size:
                ret = sizeOp(v);
                break;
            case Not:
                ret = notOp(v);
                break;
            case Read:
                ret = readOp(v);
                break;
            case ToNumber:
                ret = toNumberOp(v);
                break;
            case ToString:
                ret = toStringOp(v);
                break;
            default:
                Utils.abort(super.getLine());
        }

        return ret;
    }

    public Value<?> negOp(Value<?> v) {
        Value<?> ret = null;
        if (v instanceof NumberValue) {
            NumberValue nv = (NumberValue) v;
            Double d = -nv.value();
            
            ret = new NumberValue(d);
        } else if (v instanceof StringValue) {
            StringValue sv = (StringValue) v;
            String s = sv.value();

            try {
                Double d = -Double.valueOf(s);
                ret = new NumberValue(d);
            } catch (Exception e) {
                Utils.abort(super.getLine());
            }
        } else {
            Utils.abort(super.getLine());
        }

        return ret;
    }

    private Value<?> sizeOp(Value<?> v){
        Value<?> ret = null;

        if(v instanceof StringValue) {
            ret = new NumberValue((double) ((String)v.value()).length());
        } else if(v instanceof TableValue) {
            ret = new NumberValue((double) ((Map)v.value()).size());
        }  else Utils.abort(super.getLine());
        
        return ret;
    }

    private Value<?> notOp(Value<?> v){
        boolean b = (v==null||!v.eval());        
        return new BooleanValue(b);
    }

    private Value<?> readOp(Value<?> v){
        Scanner scanner = new Scanner(System.in);
        
        if(v!=null) System.out.println(v.value().toString());
        
        String str;
        try {
            str = scanner.nextLine();
        } catch (NoSuchElementException e) {
            str = "";
        }
        
        return new StringValue(str);
    }

    private Value<?> toNumberOp(Value<?> v){
        Value<?> ret = null;
        if (v instanceof NumberValue) {
            ret = (NumberValue) v;
        
        } else if (v instanceof StringValue) {
            StringValue sv = (StringValue) v;
            String s = sv.value();
            try {
                Double d = Double.valueOf(s);
                ret = new NumberValue(d);
            } catch (Exception e) {
                //ret keeps null
            }
        } else {
            //ret also keeps null
        }

        return ret;    
    }

    private Value<?> toStringOp(Value<?> v){
        Value<?> ret = null;
        if (v instanceof NumberValue) {
            ret = new StringValue(v.value().toString());
        } else if (v instanceof StringValue) {
            ret = (StringValue) v;
        } else if (v instanceof BooleanValue){
            ret = new StringValue(((BooleanValue)v).toString());
        } else {
            ret = null;
        }

        return ret;    
    }
}
