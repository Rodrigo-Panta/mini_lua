package interpreter.command;

import interpreter.expr.Expr;

public class RepeatCommand extends Command {

    private Command cmds;
    private Expr expr;

    public RepeatCommand(int line, Command cmds, Expr expr) {
        super(line);
        this.cmds = cmds;
        this.expr = expr;
    }

    public void execute() {
        do {
            cmds.execute();
        } while ((expr.expr()!=null) && !expr.expr().eval());
    }

    
}
