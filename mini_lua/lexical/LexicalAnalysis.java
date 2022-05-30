package lexical;

import java.io.FileInputStream;
import java.io.PushbackInputStream;

public class LexicalAnalysis implements AutoCloseable {
    
    private int line;
    private SymbolTable symbolTable;
    private PushbackInputStream input;

    public LexicalAnalysis(String filename) {
        try {
            input = new PushbackInputStream(new FileInputStream(filename));
        } catch (Exception e) {
            throw new LexicalException("Unable to open file: " + filename);
        }
        line = 1;
        symbolTable = new SymbolTable();
    }

    public void close() {
		try {
			input.close();
		} catch (Exception e) {
			throw new LexicalException("Unable to close file");
		}
	}

    public Lexeme nextToken () {
        Lexeme lexeme = new Lexeme("", TokenType.END_OF_FILE);

        int state = 1;

        while(state != 17 && state != 18) {
            int c = getc();

            switch(state){
                case 1:
                    if (c == ' ' || c == '\t' || c == '\r') continue;    //Stay on 1
                    else if (c == '\n') line++;   //Stay on 1     
                    else if (Character.isDigit(c)) {
                        lexeme.append(c);
                        state = 15;
                    } else if (c == '"') state = 14;
                    else if (Character.isAlphabetic(c) || c == '_') {
                        lexeme.append(c);
                        state = 13;
                    } else if (c == ';' || c == ',' || c == '+' || c == '*' || c == '/' || 
                                c == '%' || c == '#' || c == '(' || c == ')' || c == '[' || 
                                c == ']' || c == '{' || c == '}') {
                                    lexeme.append(c);
                                    state = 17;
                                }
                    else if (c == '.') {
                        lexeme.append(c);
                        state = 12;
                    } else if (c == '~') {
                        lexeme.append(c);
                        state = 11;
                    } else if (c == '=' || c == '<' || c == '>') {
                        lexeme.append(c);
                        state = 10;
                    } else if (c == '-') {
                        state = 2;
                        // '-' will be added to the lexeme on state 2 instead                   
                    } else if(c == -1) {
                        // The token is an actual EOF
                        state = 18;
                    } else {
                        lexeme.append(c);
                        lexeme.type = TokenType.INVALID_TOKEN;
                        state = 18;
                    }
                    break;
                case 2:
                    if (c == '-') {
                        state = 3;
                    } else {
                        ungetc(c);
                        lexeme.append('-');
                        state = 17;
                    }
                    break;
                case 3:
                    if (c == '\n') {
                        state = 1;
                        line++;
                    } else if (c == '[') state = 4;
                    else {
                        state = 9;
                    }
                    break;
                case 4:
                    if (c == '\n') {
                        state = 1;
                        line++;
                    } else if (c == '[') {
                        state = 5;
                    } else {
                        state = 9;
                    }
                    break;
                case 5:
                    if (c == '-') state = 6;
                    else if(c == '\n') line++;
                    else if (c == -1){
                        state = 18;
                        lexeme.type = TokenType.UNEXPECTED_EOF;
                    } 
                    // otherwise Stay on 5
                    break;
                case 6:
                    if (c == '-') state = 7;
                    else {
                        ungetc(c);
                        state = 5;
                    }                 
                    break;
                case 7:
                    if (c == '-') state = 7;
                    else if (c == ']'){
                        state = 8;
                    } else {
                        ungetc(c);
                        state = 5;
                    }                  
                    break;
                case 8:
                    if (c == ']'){
                        state = 1;
                    } else if (c == '-') {
                        state = 6; 
                    }   else {
                        ungetc(c);
                        state = 5;
                    }
                    break;
                case 9:
                    if (c == '\n') {
                        state = 1; line++;
                    } else if (c == -1) state = 18;
                    //otherwise Stay on 9
                    break;
                case 10:
                    if (c == '=') lexeme.append(c);
                    else ungetc(c);
                    state = 17;                
                    break;
                case 11:
                    if (c == '='){
                        lexeme.append(c);
                        state = 17;
                    } else if (c == -1){
                        lexeme.type = TokenType.UNEXPECTED_EOF;
                        state = 18;
                    } else {
                        lexeme.append(c);
                        lexeme.type = TokenType.INVALID_TOKEN;
                        state = 18;
                    }
                    break;
                case 12:
                    if (c == '.'){
                        lexeme.append(c);
                    } else {
                        ungetc(c);
                    }    
                    state = 17;
                    break;
                case 13:
                    if(c == '_' || Character.isLetterOrDigit((char) c)){
                        lexeme.append(c);       //Stay on 13
                    } else {
                        ungetc(c);
                        state = 17;
                    }         
                    break;
                case 14:
                    if (c == -1) {
                        lexeme.type = TokenType.UNEXPECTED_EOF;
                        state = 18;
                    } else if(c == '\n'){
                        line++;
                        lexeme.type = TokenType.INVALID_TOKEN;
                        state = 18;
                    } else if (c != '"') lexeme.append(c); //Stay on 14
                    else {
                        lexeme.type = TokenType.STRING;
                        state = 18;
                    }
                    break;
                case 15:
                    if (Character.isDigit(c)) lexeme.append(c);     //Stay on 15
                    else if (c == '.'){
                        lexeme.append(c);
                        state = 16;
                    } else {
                        ungetc(c);
                        lexeme.type = TokenType.NUMBER;
                        state = 18;
                    }
                    break;
                case 16:
                    if (Character.isDigit(c)) lexeme.append(c); //Stay on 16
                    else {
                        ungetc(c);
                        lexeme.type = TokenType.NUMBER;
                        state = 18;
                    }
                    break;
                default:
                    throw new LexicalException("Invalid state when building token");
            }
        }

        if (state == 17) {
            lexeme.type = symbolTable.find(lexeme.token);
        }

        return lexeme;
    }

    private int getc() {
        try{
            int c = input.read();
            return c;
        }catch(Exception e){
            throw new LexicalException("Unable to read next character");
        }
    }

    private void ungetc(int c) {
        try {
            if (c!=-1) input.unread(c); 
        } catch (Exception e) {
            throw new LexicalException("Unable to ungetc");
        }
    }

    public int getLine() {
        return line;
    }

}
