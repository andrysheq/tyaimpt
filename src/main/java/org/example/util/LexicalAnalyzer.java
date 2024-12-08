package org.example.util;

import java.util.*;
import java.util.regex.*;

public class LexicalAnalyzer {
    // Список ключевых слов
    private static final Set<String> Keywords = new HashSet<>(Arrays.asList(
            "BEGIN", "END", "VAR", "LOGICAL", "READ", "REPEAT", "WRITE", "0", "1", ".NOT.", ".AND.", ".OR.", ".IMP."
    ));

    // Список разделителей и операторов
    private static final Set<Character> Separators = new HashSet<>(Arrays.asList(';', ':', ',', '(', ')'));
    private static final Set<Character> Operators = new HashSet<>(Arrays.asList('+', '-', '/', '='));

    // Регулярные выражения для идентификаторов (переменных) и чисел
    private static final Pattern IdentifierPattern = Pattern.compile("^[A-Za-z]{1,8}$");
    private static final Pattern NumberPattern = Pattern.compile("^\\d+$");

    // Метод для токенизации текста
    public List<String> tokenize(String code) {
        List<String> tokens = new ArrayList<>();
        StringBuilder token = new StringBuilder();

        for (char c : code.toCharArray()) {
            if (Character.isWhitespace(c)) {
                addToken(token.toString(), tokens);
                token.setLength(0);
            } else if (Separators.contains(c) || Operators.contains(c)) {
                addToken(token.toString(), tokens);
                tokens.add(String.valueOf(c)); // Добавляем оператор/разделитель как отдельную лексему
                token.setLength(0);
            } else {
                token.append(c);
            }
        }

        addToken(token.toString(), tokens); // Добавляем последний токен, если есть
        return tokens;
    }

    // Метод для классификации лексемы
    public String classifyToken(String token) {
        if (Keywords.contains(token)) {
            return "Ключевое слово";
        } else if (token.length() == 1 && Separators.contains(token.charAt(0))) {
            return "Разделитель";
        } else if (token.length() == 1 && Operators.contains(token.charAt(0))) {
            return "Оператор";
        } else if (IdentifierPattern.matcher(token).matches()) {
            return "Переменная";
        } else if (NumberPattern.matcher(token).matches()) {
            return "Операнд";
        } else {
            return "Неизвестная лексема";
        }
    }

    // Вспомогательный метод для добавления токена в список, если он не пустой
    private void addToken(String token, List<String> tokens) {
        if (!token.isEmpty()) {
            tokens.add(token);
        }
    }

    // Метод для токенизации с учетом номеров строк
    public List<TokenWithLine> tokenizeWithLines(String code) {
        List<TokenWithLine> tokensWithLines = new ArrayList<>();
        StringBuilder token = new StringBuilder();
        int lineNumber = 1;

        for (char c : code.toCharArray()) {
            if (c == '\n') {
                lineNumber++;
            }

            if (Character.isWhitespace(c)) {
                addTokenWithLine(token.toString(), lineNumber, tokensWithLines);
                token.setLength(0);
            } else if (Separators.contains(c) || Operators.contains(c)) {
                addTokenWithLine(token.toString(), lineNumber, tokensWithLines);
                tokensWithLines.add(new TokenWithLine(String.valueOf(c), lineNumber));
                token.setLength(0);
            } else {
                token.append(c);
            }
        }

        addTokenWithLine(token.toString(), lineNumber, tokensWithLines); // Добавляем последний токен, если есть
        return tokensWithLines;
    }

    private void addTokenWithLine(String token, int lineNumber, List<TokenWithLine> tokensWithLines) {
        if (!token.isEmpty()) {
            tokensWithLines.add(new TokenWithLine(token, lineNumber));
        }
    }

}

