package org.example.util;

public class TokenWithLine {
    private final String token;
    private final int line;

    public TokenWithLine(String token, int line) {
        this.token = token;
        this.line = line;
    }

    public String getToken() {
        return token;
    }

    public int getLine() {
        return line;
    }

    @Override
    public String toString() {
        return "Token{" +
                "token='" + token + '\'' +
                ", line=" + line +
                '}';
    }
}
