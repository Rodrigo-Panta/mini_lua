import lexical.Lexeme;
import lexical.LexicalAnalysis;
import lexical.TokenType;
import syntatic.SyntaticAnalysis;


public class MiniLuaI {
    public static void main(String[] args) {
        if (args.length != 1) {
			System.out.println("Usage: java MiniLuaI [source file]");
			return;
		}

        try (LexicalAnalysis l = new LexicalAnalysis(args[0])) {
			SyntaticAnalysis s = new SyntaticAnalysis(l);
			s.start();
			// Lexeme lex = null;
			// while (lex == null || (lex.type != TokenType.END_OF_FILE && lex.type != TokenType.UNEXPECTED_EOF) ){
			// 	lex = l.nextToken();
			// 	System.out.println("Token: " + lex.token + "\t\tType: " + lex.type + "\t\tLine: " + l.getLine());
			// } 
		} catch (Exception e) {
			System.err.println("Internal error: " + e.getMessage());
			//e.printStackTrace();
		}
    }
}