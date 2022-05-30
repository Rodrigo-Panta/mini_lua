package interpreter.command;

import interpreter.expr.ConstExpr;
import interpreter.expr.Expr;
import interpreter.expr.Variable;
import interpreter.util.Utils;
import interpreter.value.NumberValue;
import interpreter.value.Value;

public class NumericForCommand extends Command {
    private Variable var;
    private Expr expr1;
    private Expr expr2;
    private Expr expr3;
    private Command cmds;

    public NumericForCommand(int line, Variable var, Expr expr1, Expr expr2, Expr expr3, Command cmds) {
        super(line);
        this.var = var;
        this.expr1 = expr1;
        this.expr2 = expr2;
        this.expr3 = expr3;
        this.cmds = cmds;

    }

    public void execute() {
        double initialValue = Utils.getAsNumberValue(expr1).value();
        double stopValue = Utils.getAsNumberValue(expr2).value();
        if(expr3==null) {
            expr3 = new ConstExpr(expr2.getLine(), new NumberValue(1.));
        }
        double iterationValue = Utils.getAsNumberValue(expr3).value();

        var.setValue(new NumberValue(initialValue));  
        
        int iterationDirection = 0; //Zero if loop has a positive iteration, One otherwise
        if(stopValue - initialValue < 0)
            iterationDirection = 1;
        while(true) {
            //Stop if stopValue is hit
            if((iterationDirection==0 && ((double)var.expr().value())>stopValue)||
            (iterationDirection==1 && ((double)var.expr().value())<stopValue)){
                break;
            }
            //Stop in case of infinite loop 
            if(iterationDirection==0 && iterationValue<0) break;
            if(iterationDirection==1 && iterationValue>0) break;
            if(iterationValue==0)break;
       
            cmds.execute();
            var.setValue(new NumberValue((double)var.expr().value()+iterationValue));
        }
        
    }

}

