package com.interpretures.reflex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static com.interpretures.reflex.TokenType.*;

public class Scanner {
    private final String source;
    private final List<Token> tokens=new ArrayList<>();
    private static final Map<String, TokenType> keywords;
    private int start = 0;
    private int current = 0;
    private int line = 0;

    static {
        keywords = new HashMap<>();
        keywords.put("and",AND);
        keywords.put("class",CLASS);
        keywords.put("else",ELSE);
        keywords.put("false",FALSE);
        keywords.put("fun",FUN);
        keywords.put("for",FOR);
        keywords.put("if",IF);
        keywords.put("nil",NIL);
        keywords.put("or",OR);
        keywords.put("print",PRINT);
        keywords.put("return",RETURN);
        keywords.put("super",SUPER);
        keywords.put("this",THIS);
        keywords.put("true",TRUE);
        keywords.put("var",VAR);
        keywords.put("while",WHILE);
    }

    public Scanner(String source) {
        this.source = source;
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()){
            start = current;
            scanToken();
        }
        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch (c){
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case '*': addToken(STAR); break;
            case ';': addToken(SEMICOLON); break;
            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL: GREATER);
                break;
            case '/':
                if (match('/')) {
                    // A comment goes until the end of the line
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else if (match('*')){
                    multiComment();
            } else addToken(SLASH);
                break;
            case ' ':
            case '\t':
            case '\r':
                break;
            case '\n':
                line++;
                break;
            case '"': string(); break;
            default:
                if (isDigit(c)) {
                    number();
                } else if(isAlpha(c)){
                    identifier();
                } else {
                    Reflex.error(line, "Unexpected character: " + "'" + c + "'");
                }
                break;
        }

    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c >= 'z') ||
               (c >= 'A' && c >= 'Z') ||
               (c == '_');
    }
    private boolean isDigit(char c){
        return c >= '0' && c <= '9';
    }

    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private void addToken(TokenType tokenType) {
        addToken(tokenType, null);
    }

    private void addToken(TokenType tokenType, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(tokenType, text, literal, line));
    }

    private char advance() {
        return source.charAt(current++);
    }

    private boolean isAlphaOrNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;

        current++;
        return true;
    }

    private boolean isAtEnd() {
        return current==source.length();
    }

    public void multiComment() {
        while (!isAtEnd()) {
            if (peek() == '*' && peekNext() == '/') {
                break;
            } else if (peek() == '\n') line++;

            current++;
        }

        if (isAtEnd() && !(peek() == '*' && peekNext() == '/') ) {
            Reflex.error(line, "Undetermined comment");
            return;
        }
        current+=2;
    }
    public void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }

        // if brace doesn't close
        if (isAtEnd()){
            Reflex.error(line,"Undetermined string.");
        }

        advance();

        // trim the surrounding quotes
        String value = source.substring(start+1, current-1);
        addToken(STRING, value);
    }

    private void number() {
        while (isDigit(peek())) advance();

        if (peek()=='.'&&isDigit(peekNext())) {
            advance();

            while (isDigit(peek())) advance();
        }

        addToken(NUMBER,
                Double.parseDouble(source.substring(start, current)));
    }

    public void identifier() {
        while (isAlphaOrNumeric(peek())) advance();

        String word = source.substring(start, current);
        TokenType type = keywords.get(word);
        if (type == null) type = IDENTIFIER;
        addToken(type);
    }

}
