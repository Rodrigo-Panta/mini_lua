package lexical;

public class Lexeme {
    public String token;
    public TokenType type;

    public Lexeme(String token, TokenType type) {
        this.token = token;
        this.type = type;
    }

    //A method for appending the converted character c to the lexeme's token
    public void append(int c){
        this.token += (char) c;
    }


}
