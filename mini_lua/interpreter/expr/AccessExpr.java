package interpreter.expr;

import interpreter.util.Memory;
import interpreter.util.Utils;
import interpreter.value.StringValue;
import interpreter.value.TableValue;
import interpreter.value.Value;

public class AccessExpr extends SetExpr {
    
    Expr base;
    Expr index;

    public AccessExpr (int line, Expr base, Expr index) {
        super(line);
        if(base==null) Utils.abort(line);
        this.base = base;
        this.index = index;
    }

    @Override
    public Value<?> expr() {
        if(base.expr()==null) Utils.abort(super.getLine());
        Value<?> memValue = Memory.read(((Variable) base).getName());
        if(!(memValue instanceof TableValue)) {
            Utils.abort(this.getLine());    //No such table 
        }
        Value<?> indexValue = (index instanceof Variable && index.expr()==null) ? 
                    new StringValue(((Variable)index).getName()) : index.expr();
        if(indexValue == null) Utils.abort(index.getLine());      
        return ((TableValue) memValue).value().get(indexValue);
    }

    @Override
    public void setValue(Value<?> value) {
        Value<?> memValue = Memory.read(((Variable) base).getName());
        if(!(memValue instanceof TableValue)) {
            Utils.abort(this.getLine());    //No such table 
        }
        if(index.expr()!=null)
            ((TableValue) memValue).value().put(index.expr(), value);

    }



}
