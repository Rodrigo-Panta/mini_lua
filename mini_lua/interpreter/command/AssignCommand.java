package interpreter.command;

import java.util.ArrayList;
import java.util.Vector;

import interpreter.expr.Expr;
import interpreter.expr.SetExpr;
import interpreter.value.Value;

public class AssignCommand extends Command {

    private ArrayList<SetExpr> lhs;
    private ArrayList<Expr> rhs;

    public AssignCommand(int line, ArrayList<SetExpr> lhs, ArrayList<Expr> rhs) {
        super(line);
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public void execute() {
        for (int i = 0; i < lhs.size(); i++) {
            Value<?> v = null;
            try {
                v = rhs.get(i).expr();
            } catch (IndexOutOfBoundsException e) {
                //This means there are more elements on the left side than in the right
                // v keeps null
            } catch (NullPointerException e) {
                //v also keeps null if nil was passed in the rhs
            }
            //Sets v as the variable in lhs 
            lhs.get(i).setValue(v);
        }    
    }

}
