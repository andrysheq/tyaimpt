package org.example.util;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.*;

public class LexicalAnalyzer {

    // Терминальные символы (ключевые слова и логические операторы)
    private static final Set<String> keywords = new HashSet<>(Arrays.asList(
            "BEGIN", "END", "VAR", "LOGICAL", "READ", "REPEAT", "WRITE"
    ));

    // Логические операторы
    private static final Set<String> logicalOperators = new HashSet<>(Arrays.asList(
            ".AND.", ".OR.", ".IMP."
    ));

    private static final Set<String> unOperators = new HashSet<>(List.of(
            ".NOT."
    ));

    // Символы для констант (0 и 1)
    private static final Set<String> constants = new HashSet<>(Arrays.asList("0", "1"));

    // Регулярное выражение для идентификаторов (буквы A-z)
    private static final Pattern identifierPattern = Pattern.compile("^[A-Za-z]+$");

    // Символы для разделителей (скобки, запятые и т.п.)
    private static final Set<Character> separators = new HashSet<>(Arrays.asList('(', ')', ',', ';', ':'));

    private static final Set<Character> operators = new HashSet<>(List.of('='));

    // Метод для токенизации текста
    public static List<String> tokenize(String code) {
        List<String> tokens = new ArrayList<>();
        StringBuilder token = new StringBuilder();

        for (int i = 0; i < code.length(); i++) {
            char c = code.charAt(i);

            // Обработка пробелов
            if (Character.isWhitespace(c)) {
                addToken(token.toString(), tokens);
                token.setLength(0);  // Очистка строки
            }
            // Обработка разделителей
            else if (separators.contains(c)) {
                addToken(token.toString(), tokens);
                tokens.add(String.valueOf(c));  // Добавляем разделитель как отдельную лексему
                token.setLength(0);
            }
            // Обработка операторов
            else if (operators.contains(c)) {
                addToken(token.toString(), tokens);
                tokens.add(String.valueOf(c));  // Добавляем оператор как отдельную лексему
                token.setLength(0);
            }
            // Обработка логических операторов
            else if (i + 4 <= code.length() && logicalOperators.contains(code.substring(i, i + 4))) {
                addToken(token.toString(), tokens);
                tokens.add(code.substring(i, i + 4));  // Добавляем оператор как отдельную лексему
                i += 3;  // Продвигаем указатель для обработки оператора
                token.setLength(0);
            }
            // Обработка унарных операторов
            else if (i + 4 <= code.length() && unOperators.contains(code.substring(i, i + 4))) {
                addToken(token.toString(), tokens);
                tokens.add(code.substring(i, i + 4));  // Добавляем оператор как отдельную лексему
                i += 3;  // Продвигаем указатель для обработки оператора
                token.setLength(0);
            } else {
                token.append(c);
            }
        }
        addToken(token.toString(), tokens);  // Добавляем последний токен
        return tokens;
    }

    // Метод для добавления токена в список
    public static void addToken(String token, List<String> tokens) {
        if (!token.isEmpty()) {
            tokens.add(token);
        }
    }

    // Метод для классификации лексем
    public static String classifyToken(String token) {
        if (keywords.contains(token)) {
            return "Ключевое слово";
        } else if (logicalOperators.contains(token)) {
            return "Логический оператор";
        }
        else if (unOperators.contains(token)) {
            return "Унарный оператор";
        }
        else if (token.length() == 1 && operators.contains(token.charAt(0))) {
            return "Оператор";
        }
        else if (constants.contains(token)) {
            return "Константа";
        } else if (identifierPattern.matcher(token).matches()) {
            return "Идентификатор";
        } else if (token.length() == 1 && separators.contains(token.charAt(0))) {
            return "Разделитель";
        } else {
            return "Неизвестная лексема";
        }
    }
}
