package interpreter.command;

import java.util.Map;
import java.util.Map.Entry;

import javax.swing.text.TabExpander;
import javax.swing.text.Utilities;

import interpreter.expr.Expr;
import interpreter.expr.Variable;
import interpreter.util.Utils;
import interpreter.value.TableValue;
import interpreter.value.Value;

public class GenericForCommand extends Command {

    private Variable var1;
    private Variable var2;
    private Expr expr;
    private Command cmds;

    public GenericForCommand(int line, Variable var1, Variable var2, Expr expr, Command cmds) {
        super(line);
        this.var1=var1;
        this.var2=var2;
        this.expr=expr;
        this.cmds=cmds;
    }

    public void execute() {
        Value<?> tableV = expr.expr();
        if(!(tableV instanceof TableValue)) {
            Utils.abort(super.getLine());
        }

        Map<Value<?>, Value<?>> table = ((TableValue) tableV).value();

        for(Entry entry: table.entrySet()) {
            var1.setValue((Value<?>) entry.getKey());
            if(var2!=null) {
                var2.setValue((Value<?>) entry.getValue());
            }
            cmds.execute();
        }
    }
    

}