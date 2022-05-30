package syntatic;

import java.util.ArrayList;

import interpreter.expr.Expr;
import interpreter.command.AssignCommand;
import interpreter.command.BlocksCommand;
import interpreter.command.Command;
import interpreter.command.GenericForCommand;
import interpreter.command.IfCommand;
import interpreter.command.NumericForCommand;
import interpreter.command.PrintCommand;
import interpreter.command.RepeatCommand;
import interpreter.command.WhileCommand;
import interpreter.expr.AccessExpr;
import interpreter.expr.BinaryExpr;
import interpreter.expr.BinaryOp;
import interpreter.expr.ConstExpr;
import interpreter.expr.SetExpr;
import interpreter.expr.TableEntry;
import interpreter.expr.TableExpr;
import interpreter.expr.UnaryExpr;
import interpreter.expr.UnaryOp;
import interpreter.expr.Variable;
import interpreter.util.Memory;
import interpreter.value.Value;
import interpreter.value.BooleanValue;
import interpreter.value.NumberValue;
import interpreter.value.StringValue;
import interpreter.value.TableValue;
import lexical.Lexeme;
import lexical.LexicalAnalysis;
import lexical.TokenType;

public class SyntaticAnalysis {
    
    private LexicalAnalysis lex;
    private Lexeme current;

    public SyntaticAnalysis(LexicalAnalysis lex) {
        this.lex = lex;
        current = lex.nextToken();
    }

    public void start(){
        BlocksCommand command = procCode();
        command.execute();
        eat(TokenType.END_OF_FILE);
    }

    // <code> ::= { <cmd> }
    private BlocksCommand procCode() {
        int line = lex.getLine();
        ArrayList<Command> commands = new ArrayList<>();
        while(current.type == TokenType.IF ||
                current.type == TokenType.WHILE ||
                current.type == TokenType.REPEAT ||
                current.type == TokenType.FOR ||
                current.type == TokenType.PRINT ||
                current.type == TokenType.VAR) {
                    commands.add(procCmd());
                }
        return new BlocksCommand(line, commands);
    }

    // <cmd> ::= (<if> | <while> | <repeat> | <for> | <print> | <assign>) [;]
    private Command procCmd() {
        Command command = null;
        
        switch(current.type){
            case IF:
                command = procIf();
                break;
            case WHILE:
                command = procWhile();
                break;
            case REPEAT:
                command = procRepeat();
                break;
            case FOR:
                 command = procFor();
                break;
            case PRINT:
                command = procPrint();
                break;
            case VAR:
                command = procAssign();
                break;
            default:
                showError();
                break;
        }

        if(current.type == TokenType.SEMICOLON) {
            advance();
        }

        return command;
    }
    
    // <if> ::= if <expr> then <code> { elseif <expr> then <code> } [ else <code> ] end
    private IfCommand procIf() {
        eat(TokenType.IF);
        int line = lex.getLine();
        Expr expr = procExpr();
        eat(TokenType.THEN);
        Command thenCmds = procCode();
        IfCommand ifCommand = new IfCommand(line, expr, thenCmds);
        IfCommand ret = ifCommand;
        while(current.type == TokenType.ELSEIF) {
            advance();
            int elseifLine = lex.getLine();
            Expr elseifExpr = procExpr();
            eat(TokenType.THEN);
            Command elseifCmds = procCode();
            IfCommand elseifCommand = new IfCommand(elseifLine, elseifExpr, elseifCmds);
            ifCommand.setElseCommands(elseifCommand);
            ifCommand=elseifCommand;
        }
        if(current.type==TokenType.ELSE){
            advance();
            Command elseCmds = procCode();
            ifCommand.setElseCommands(elseCmds);
        }
        eat(TokenType.END);
        return ret;
    }

    // <while> ::= while <expr> do <code> end
    private WhileCommand procWhile() {
        eat(TokenType.WHILE);
        int line = lex.getLine();
        
        Expr expr = procExpr();
        eat(TokenType.DO);
        Command commands = procCode();
        eat(TokenType.END);

        return new WhileCommand(line, expr, commands);

    }

    // <repeat> ::= repeat <code> until <expr>
    private RepeatCommand procRepeat() {
        eat(TokenType.REPEAT);
        int line = lex.getLine();
        Command cmds = procCode();
        eat(TokenType.UNTIL);
        Expr expr = procExpr();
        return new RepeatCommand(line, cmds, expr);
    }     

    // <for> ::= for <name> (('=' <expr> ',' <expr> [ ',' <expr> ]) | ([',' name] in <expr>)) do <code> end
    private Command procFor() {
        eat(TokenType.FOR);
        int forLine = lex.getLine();
        String name = current.token;
        eat(TokenType.VAR);
        int varLine = lex.getLine();
        if(current.type == TokenType.ASSIGN) {
            advance();
            Expr expr1 = procExpr();
            eat(TokenType.COMMA);
            Expr expr2 = procExpr();
            Expr expr3 = null;
            if(current.type==TokenType.COMMA){
                advance();
                expr3 = procExpr();
            }
            Variable var = new Variable(varLine, name);

            eat(TokenType.DO);
            Command cmds = procCode();
            eat(TokenType.END);    
            return new NumericForCommand(forLine, var, expr1, expr2, expr3, cmds);
        } else {
            Variable var1 = new Variable(varLine, name);
            Variable var2 = null;
            if(current.type==TokenType.COMMA){
                advance();
                int line = lex.getLine();
                String var2Value = current.token;
                var2 = new Variable(line, var2Value);
                eat(TokenType.VAR);
            }
            eat(TokenType.IN);
            Expr expr = procExpr();
            eat(TokenType.DO);
            Command cmds = procCode();
            eat(TokenType.END);
            return new GenericForCommand(forLine, var1, var2, expr, cmds);
        }
    }

    // <print>   ::= print '(' [ <expr> ] ')'
    private PrintCommand procPrint() {
        Expr expr = null;
        
        eat(TokenType.PRINT);
        eat(TokenType.OPEN_BRACKET);
        if(current.type == TokenType.CLOSE_BRACKET) {
            advance();
        } else {
            expr = procExpr();
            eat(TokenType.CLOSE_BRACKET);
        }
        PrintCommand command = new PrintCommand(lex.getLine(), expr);
        return command;
    }

    // <assign> ::= <lvalue> { ',' <lvalue> } '=' <expr> { ',' <expr> }
    private AssignCommand procAssign() {
        ArrayList<SetExpr> lhs = new ArrayList<SetExpr>();
        ArrayList <Expr> rhs = new ArrayList<Expr>();
        
        lhs.add(procLValue());
        while(current.type==TokenType.COMMA){
            advance();
            lhs.add(procLValue());
        }
        eat(TokenType.ASSIGN);
        rhs.add(procExpr());
        while(current.type==TokenType.COMMA) {
            advance();
            rhs.add(procExpr());
        }
        return new AssignCommand(lhs.get(0).getLine(), lhs, rhs);
    }

    // <expr> ::= <rel> { (and | or) <rel> }
    private Expr procExpr() {
        Expr expr = procRel();
        BinaryOp op = null;
        while(current.type==TokenType.AND || current.type==TokenType.OR) {
            if(current.type==TokenType.AND) op = BinaryOp.And;
            else op = BinaryOp.Or;
            advance();
            int line = lex.getLine();
            Expr right = procRel();
            expr = new BinaryExpr(line, expr, op, right);
        }
        return expr;
    }

    // <rel> ::= <concat> [ ('<' | '>' | '<=' | '>=' | '~=' | '==') <concat> ]
    private Expr procRel() {
        Expr expr = procConcat();
        BinaryOp op = null;
        switch(current.type) {
            case LOWER:
                op = BinaryOp.LowerThan;
                break;    
            case GREATER:  
                op = BinaryOp.GreaterThan;
                break;
            case LOWER_EQUAL:
                op = BinaryOp.LowerEqual;
                break;
            case GREATER_EQUAL:
                op = BinaryOp.GreaterEqual;
                break;
            case NOT_EQUAL:
                op = BinaryOp.NotEqual;
                break;
            case EQUAL:
                op = BinaryOp.Equal;
                break;
            default:
                break;
        }
        
        if(op!=null) {
            advance();
            int line = lex.getLine();
            Expr right = procConcat();
            expr = new BinaryExpr(line, expr, op, right);
        }
        

        return expr;
    }

    // <concat> ::= <arith> { '..' <arith> }
    private Expr procConcat() {
        Expr expr = procArith();
        int line = lex.getLine();
        while(current.type == TokenType.DOT_DOT){
            advance();
            Expr right = procArith();
            expr = new BinaryExpr(line, expr, BinaryOp.Concat, right);
        }
        return expr;
    }

    // <arith> ::= <term> { ('+' | '-') <term> }
    private Expr procArith() {
        Expr expr = procTerm(); 
        int line = expr.getLine();
        BinaryOp op = null;
        while(current.type == TokenType.ADD || current.type == TokenType.SUB) {
            if(current.type==TokenType.ADD) op = BinaryOp.Add;
            else op = BinaryOp.Sub;
            advance();
            Expr right = procTerm();
            expr = new BinaryExpr(line, expr, op, right);
        }
        return expr;
    }

    // <term> ::= <factor> { ('*' | '/' | '%') <factor> }
    private Expr procTerm() {
        Expr expr = procFactor();
        int line = expr.getLine();
        BinaryOp op = null;
        while(current.type == TokenType.MUL || 
                current.type == TokenType.DIV ||
                current.type == TokenType.MOD) {
                    if(current.type==TokenType.MUL) op = BinaryOp.Mul;
                    else if(current.type==TokenType.DIV) op = BinaryOp.Div;
                    else op = BinaryOp.Mod;
                    advance();
                    Expr right = procFactor();
                    expr = new BinaryExpr(line, expr, op, right);
                }
        return expr;
    }

    //  <factor>   ::= '(' <expr> ')' | [ '-' | '#' | not] <rvalue>
    private Expr procFactor() {
        Expr expr = null;
        if(current.type == TokenType.OPEN_BRACKET) {
            advance();
            expr = procExpr();
            eat(TokenType.CLOSE_BRACKET);
        } else {
            UnaryOp op = null;
            if(current.type == TokenType.SUB) {
                advance();
                op = UnaryOp.Neg;
            } else if(current.type == TokenType.HASH) {
                advance();
                op = UnaryOp.Size;
            } else if(current.type == TokenType.NOT) {
                advance();
                op = UnaryOp.Not;
            }
            expr = procRValue();
            int line = lex.getLine();

            if(op != null) {
                expr = new UnaryExpr(line, expr, op);
            }

        }
        return expr;
    }
    // <lvalue>   ::= <name> { '.' <name> | '[' <expr> ']' }
    private SetExpr procLValue() {
        Expr expr = procName();     //Variable being accessed
        Expr index = null;          //Index if it's a table
        while(true) {
            if(current.type == TokenType.DOT) {
                advance();
                index = procName();
                expr = new AccessExpr(lex.getLine(), expr, index);
            }else if(current.type == TokenType.OPEN_SQR_BRACKET) {
                advance();
                index = procExpr();
                expr = new AccessExpr(lex.getLine(), expr, index);
                eat(TokenType.CLOSE_SQR_BRACKET);
            }else {
                break;
            }                
        }
        return (SetExpr) expr;
    }
    
    // <rvalue>   ::= <const> | <function> | <table> | <lvalue>
    private Expr procRValue() {
        Expr expr = null;
        if(current.type == TokenType.NUMBER ||
            current.type == TokenType.STRING ||
            current.type == TokenType.FALSE ||
            current.type == TokenType.TRUE ||
            current.type == TokenType.NIL) {
                expr = procConst();
            }

        else if(current.type == TokenType.READ ||
            current.type == TokenType.TONUMBER ||
            current.type == TokenType.TOSTRING) {
                expr = procFunction();
        }
        else if(current.type == TokenType.OPEN_BRACES){
           expr = procTable();
        } else if(current.type == TokenType.VAR) {
            expr = procLValue();
        } else {
            showError();
        }        

        return expr;
    }

    // <const>     ::= <number> | <string> | false | true | nil
    private ConstExpr procConst() {
        Value<?> v = null;
        switch(current.type) {
            case NUMBER:
                v = procNumber();
                break;
            case STRING:
                v = procString();
                break; 
            case FALSE:
                v = new BooleanValue(false);
                advance();
                break;
            case TRUE:
                v = new BooleanValue(true);
                advance();
                break;
            case NIL:
                v = null;
                advance();
                break;
            default:            
                showError();    
                break;
        } 
        int line = lex.getLine();
        return new ConstExpr(line, v);
    }
    
    // <function> ::= (read | tonumber | tostring) '(' [ <expr> ] ')'
    private UnaryExpr procFunction() {
        UnaryOp op = null;
        if(current.type == TokenType.READ) {
            op = UnaryOp.Read;
        } else if (current.type == TokenType.TONUMBER){
            op = UnaryOp.ToNumber;
        }
        else if(current.type == TokenType.TOSTRING) {
            op = UnaryOp.ToString;
        } else {
            showError();
        }
        advance();
        int line = lex.getLine();
        eat(TokenType.OPEN_BRACKET);
        
        Expr expr = new ConstExpr(line, null);
        if(current.type == TokenType.CLOSE_BRACKET) {
            advance();
        } else {
            expr = procExpr();
            eat(TokenType.CLOSE_BRACKET);
        }
        return new UnaryExpr(line, expr, op);
    }

    // <table> ::= '{' [ <elem> { ',' <elem> } ] '}'
    private TableExpr procTable() {
        eat(TokenType.OPEN_BRACES);

        TableExpr expr = new TableExpr(lex.getLine());

        if(current.type == TokenType.CLOSE_BRACES){
            advance(); 
            return expr;
        }   
        expr.addEntry(procElem());
        while(current.type == TokenType.COMMA){
            advance();
            expr.addEntry(procElem());
        }     
        eat(TokenType.CLOSE_BRACES);

        return expr;
    }
    
    // <elem>     ::= [ '[' <expr> ']' '=' ] <expr> 
    private TableEntry procElem() {
        Expr key = null;
        Expr value = null;

        if(current.type == TokenType.OPEN_SQR_BRACKET) {
            advance();
            key = procExpr();
            eat(TokenType.CLOSE_SQR_BRACKET);
            eat(TokenType.ASSIGN);
        }
        value = procExpr();
        return new TableEntry(key, value);
        
    }

    // <name> ::= var
    private Variable procName() {
        String name = current.token;
        eat(TokenType.VAR);
        int line = lex.getLine();
        return new Variable(line, name);
    }

    // str 
    private StringValue procString() {
        String str = current.token;
        eat(TokenType.STRING);
        return new StringValue(str);
    }

    // number
    private NumberValue procNumber() {
        String tmp = current.token;
        eat(TokenType.NUMBER);
        return new NumberValue(Double.valueOf(tmp));
    }


    //Methods related to token checking
    private void advance(){
        // System.out.println("Advanced (\"" + current.token + "\", " +
        //     current.type + ")");
        current = lex.nextToken();
    }

    private void eat(TokenType type){
        // System.out.println("Expected (..., " + type + "), found (\"" + 
        //     current.token + "\", " + current.type + ")");
        if(type == current.type){
            advance();
        } else {
            showError();
        }
    }

    private void showError() {
        System.out.printf("%02d: ", lex.getLine());
        switch(current.type){
            case INVALID_TOKEN:
                System.out.println("Lexema inválido: " + current.token);
                break;    
            case END_OF_FILE:
            case UNEXPECTED_EOF:
                System.out.println("Fim de arquivo inesperado");
                break;
            default:
                System.out.println("Lexema não não esperado: " + current.token);
                break;
        }
        System.exit(1);
    }


}
