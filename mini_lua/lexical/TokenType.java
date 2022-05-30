package lexical;

public enum TokenType {
    //Special
    UNEXPECTED_EOF,
    INVALID_TOKEN,
    END_OF_FILE,
    
    // Symbols
	ASSIGN,             // =
    COMMA,              // ,
    SEMICOLON,          // ;
    OPEN_BRACES,        // {
    CLOSE_BRACES,       // }
    OPEN_BRACKET,       // (
    CLOSE_BRACKET,      // )
    OPEN_SQR_BRACKET,   // [
    CLOSE_SQR_BRACKET,  // ]

	// Logic operators
	EQUAL,              // ==
	NOT_EQUAL,          // ~=
	LOWER,              // <
	LOWER_EQUAL,        // <=
	GREATER_EQUAL,      // >=
	GREATER,            // >

	// Arithmetic operators
	ADD,                // +
	SUB,                // -
	MUL,                // *
	DIV,                // /
	MOD,                // %

    // Other Operators
    HASH,               // #
    DOT,                // .
    DOT_DOT,            // ..

	// Keywords
    IF,                 // if
	THEN,               // then
    ELSEIF,             // elseif
	ELSE,               // else
	WHILE,              // while
	DO,                 // do
    REPEAT,             // repeat
    UNTIL,              // until
    FOR,                // for
    IN,                 // in
    END,                // end 
    NIL,                // nil
    TRUE,               // true
	FALSE,              // false
	NOT,                // not
    AND,                // and
    OR,                 // or

    // Functions
    PRINT,              // print()
    READ,               // read()
    TONUMBER,           //tonumber()
    TOSTRING,           //tostring()

	// Others
	NUMBER,             // number
    STRING,             // "..."
	VAR                 // variable

    ;

}
