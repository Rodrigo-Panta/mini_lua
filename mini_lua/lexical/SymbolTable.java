package lexical;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    
    private final Map<String, TokenType> symbolTable;
 
    public SymbolTable() {
        symbolTable = new HashMap<>();

         // Symbols
        symbolTable.put("=", TokenType.ASSIGN);             
        symbolTable.put(",", TokenType.COMMA);             
        symbolTable.put(";", TokenType.SEMICOLON);             
        symbolTable.put("{", TokenType.OPEN_BRACES);             
        symbolTable.put("}", TokenType.CLOSE_BRACES);             
        symbolTable.put("(", TokenType.OPEN_BRACKET);             
        symbolTable.put(")", TokenType.CLOSE_BRACKET);             
        symbolTable.put("[", TokenType.OPEN_SQR_BRACKET);             
        symbolTable.put("]", TokenType.CLOSE_SQR_BRACKET);             

        // Logic operators
        symbolTable.put("==", TokenType.EQUAL);             
        symbolTable.put("~=", TokenType.NOT_EQUAL);             
        symbolTable.put("<", TokenType.LOWER);             
        symbolTable.put("<=", TokenType.LOWER_EQUAL);             
        symbolTable.put(">=", TokenType.GREATER_EQUAL);             
        symbolTable.put(">", TokenType.GREATER);             

        // Arithmetic operators
        symbolTable.put("+", TokenType.ADD);             
        symbolTable.put("-", TokenType.SUB);             
        symbolTable.put("*", TokenType.MUL);             
        symbolTable.put("/", TokenType.DIV);             
        symbolTable.put("%", TokenType.MOD);             

        // Other Operators
        symbolTable.put("#", TokenType.HASH);             
        symbolTable.put(".", TokenType.DOT);             
        symbolTable.put("..", TokenType.DOT_DOT);             

        // Keywords
        symbolTable.put("if", TokenType.IF);             
        symbolTable.put("then", TokenType.THEN);             
        symbolTable.put("elseif", TokenType.ELSEIF);             
        symbolTable.put("else", TokenType.ELSE);             
        symbolTable.put("while", TokenType.WHILE);             
        symbolTable.put("do", TokenType.DO);             
        symbolTable.put("repeat", TokenType.REPEAT);             
        symbolTable.put("until", TokenType.UNTIL);             
        symbolTable.put("for", TokenType.FOR);             
        symbolTable.put("in", TokenType.IN);             
        symbolTable.put("end", TokenType.END);             
        symbolTable.put("nil", TokenType.NIL);             
        symbolTable.put("true", TokenType.TRUE);             
        symbolTable.put("false", TokenType.FALSE);             
        symbolTable.put("not", TokenType.NOT);             
        symbolTable.put("and", TokenType.AND);             
        symbolTable.put("or", TokenType.OR);             

        // Functions
        symbolTable.put("print", TokenType.PRINT);             
        symbolTable.put("read", TokenType.READ);             
        symbolTable.put("tonumber", TokenType.TONUMBER);             
        symbolTable.put("tostring", TokenType.TOSTRING);             
    }
    
    public boolean contains(String token) {
		return symbolTable.containsKey(token);
	}

	public TokenType find(String token) {
		return this.contains(token) ?
					symbolTable.get(token) : TokenType.VAR;
	}
    

}
