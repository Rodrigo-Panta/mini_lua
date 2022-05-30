package interpreter.command;

import interpreter.expr.Expr;
import interpreter.value.Value;

public class PrintCommand extends Command {

    private Expr expr;

    public PrintCommand(int line) {
        this(line, null);
    }

    public PrintCommand(int line, Expr expr) {
        super(line);
        this.expr = expr;
    }


    @Override
    public void execute() {
        if(expr == null) {
            System.out.println();
        } else {
            Value<?> v = expr == null ? null : expr.expr();
            System.out.println(v == null ? "nil" : v.toString());
        }
    }

}
