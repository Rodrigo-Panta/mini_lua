package interpreter.command;

import interpreter.expr.Expr;

public class IfCommand extends Command {

    private Expr expr;
    private Command thenCmds;
    private Command elseCmds;

    public IfCommand(int line, Expr expr, Command thenCmds) {
        super(line);
        this.expr = expr;
        this.thenCmds = thenCmds;
        elseCmds = null;
    }

    public void setElseCommands(Command elseCmds) {
        this.elseCmds = elseCmds;
    }

    public void execute() {
        if(!(expr.expr()==null) && expr.expr().eval()) {
            thenCmds.execute();
        } else {
            if(elseCmds!=null)
                elseCmds.execute();
        }
    }
    
}
