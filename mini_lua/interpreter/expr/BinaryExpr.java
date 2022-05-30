package interpreter.expr;


import interpreter.util.Utils;
import interpreter.value.BooleanValue;
import interpreter.value.NumberValue;
import interpreter.value.StringValue;
import interpreter.value.Value;

public class BinaryExpr extends Expr {
    private Expr left;
    private BinaryOp op;
    private Expr right;

    public BinaryExpr(int line, Expr left, BinaryOp op, Expr right) {
        super(line);
        this.left = left;
        this.op = op;
        this.right = right;
    }

    @Override
    public Value<?> expr() {
        
        Value<?> ret = null;
      
        switch(op) {
            case And:
                ret = andOp();
                break;
             case Or:
                ret = orOp();
                break;
             case Equal:
                ret = equalOp();
                break;
             case NotEqual:
                ret = new BooleanValue(!equalOp().value());
                break;
             case LowerThan:
                ret = lowerThanOp();
                break;
             case LowerEqual:
                ret = lowerEqualOp();
                break;
             case GreaterThan:
                ret = new BooleanValue(!lowerEqualOp().value());
                break;
             case GreaterEqual:
                ret = new BooleanValue(!lowerThanOp().value());
                break;
             case Concat:
                ret = concatOp();
                break;
             case Add:
                ret = addOp();
                break;
             case Sub:
                ret = subOp();
                break;
             case Mul:
                ret = mulOp();
                break;
             case Div:
                ret = divOp();
                break;
             case Mod:
                ret = modOp();
                break;
        }

        return ret;
    }

    private Value<?> andOp() {
        if(left.expr() == null || !left.expr().eval()) {
            return left.expr();
        } else {
            return right.expr();
        }
    }    

    private Value<?> orOp() {
        if(left.expr() == null || !left.expr().eval()) {
            return right.expr();
        } else {
            return left.expr();
        }
    }    

    private BooleanValue lowerThanOp() {
        Value<?> lv = Utils.getAsNumberValue(left);
        Value<?> rv = Utils.getAsNumberValue(right);
        return new BooleanValue(((double)lv.value())<((double)rv.value()));
    }


    private BooleanValue lowerEqualOp() {
        Value<?> lv = Utils.getAsNumberValue(left);
        Value<?> rv = Utils.getAsNumberValue(right);
        return new BooleanValue(((double)lv.value())<=((double)rv.value()));
    }
    
    private BooleanValue equalOp() {
        if(left.expr() == null) {
            if(right.expr()==null){
                return new BooleanValue(true);
            }
            return new BooleanValue(false);
        }
        return new BooleanValue(left.expr().equals(right.expr()));
    }      

    private Value<?> concatOp() {
        Value<?> lv = Utils.getAsStringValue(left);
        Value<?> rv = Utils.getAsStringValue(right);
        return new StringValue((String) lv.value() + (String) rv.value());
    }   

    private Value<?> addOp() {
        Value<?> lv = Utils.getAsNumberValue(left);
        Value<?> rv = Utils.getAsNumberValue(right);
        return new NumberValue(((double)lv.value())+((double)rv.value()));
    }

    private Value<?> subOp() {
        Value<?> lv = Utils.getAsNumberValue(left);
        Value<?> rv = Utils.getAsNumberValue(right);
        return new NumberValue(((double)lv.value())-((double)rv.value()));
    }

    private Value<?> mulOp() {
        Value<?> lv = Utils.getAsNumberValue(left);
        Value<?> rv = Utils.getAsNumberValue(right);
        return new NumberValue(((double)lv.value())*((double)rv.value()));

    } 

    private Value<?> divOp() {
        Value<?> lv = Utils.getAsNumberValue(left);
        Value<?> rv = Utils.getAsNumberValue(right);
        if((double) rv.value()==0) Utils.abort(right.getLine());
        return new NumberValue(((double)lv.value())/((double)rv.value()));

    } 


    private Value<?> modOp() {
        Value<?> lv = Utils.getAsNumberValue(left);
        Value<?> rv = Utils.getAsNumberValue(right);
        if((double) rv.value()==0) Utils.abort(right.getLine());
        return new NumberValue(((double)lv.value())%((double)rv.value()));

    } 


    
}
