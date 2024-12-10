package org.example.util;

import java.util.*;

public class Interpreter {
    private final Map<String, Integer> variables = new HashMap<>(); // Переменные и их значения
    private final List<TokenWithLine> tokensWithLines;
    private int currentTokenIndex = 0; // Индекс текущего токена

    public Interpreter(List<TokenWithLine> tokensWithLines) {
        this.tokensWithLines = tokensWithLines;
    }

    public void run() {
        try {
            parseProgram();
            System.out.println("Код выполнен успешно.");
        } catch (Exception ex) {
            System.out.println("Ошибка: " + ex.getMessage());
        }
    }

    private void parseProgram() {
        if (!match("VAR"))
            throwError("Программа должна начинаться с секции VAR.");

        parseVariableSection();

        if (!match("BEGIN"))
            throwError("Пропущено ключевое слово BEGIN.");

        parseStatements();

        if (!match("END"))
            throwError("Пропущено ключевое слово END.");

        if (currentTokenIndex < tokensWithLines.size())
            throwError("Код после END недопустим.");
    }

    private void parseVariableSection() {
        parseVariableList();

        if (!match(":") || !match("LOGICAL"))
            throwError("Ожидалось ': LOGICAL' после списка переменных.");

        if (!match(";"))
            throwError("Пропущен ';' после объявления переменных.");
    }

    private void parseVariableList() {
        while (true) {
            String variable = currentToken();
            if (!isIdentifier(variable))
                throwError("Некорректное имя переменной '" + variable + "'. Переменные должны содержать только буквы.");

            if (variable.length() > 8)
                throwError("Имя переменной '" + variable + "' превышает допустимую длину в 8 символов.");

            if (variables.containsKey(variable))
                throwError("Переменная '" + variable + "' уже была объявлена.");

            variables.put(variable, null); // Инициализируем переменную значением null
            advance();

            if (!match(","))
                break;
        }
    }

    private void parseStatements() {
        while (!currentToken().equals("END")) {
            if (isIdentifier(currentToken()) && !isKeyword(currentToken())) {
                parseAssignment();
                if (!match(";"))
                    throwError("Пропущен ';' после оператора присваивания.");
            } else if (match("READ")) {
                parseRead();
                if (!match(";"))
                    throwError("Пропущен ';' после READ.");
            } else if (match("WRITE")) {
                parseWrite();
                if (!match(";"))
                    throwError("Пропущен ';' после WRITE.");
            } else if (match("REPEAT")) {
                parseRepeat();
            } else {
                throwError("Неизвестная команда: " + currentToken() + ".");
            }
        }
    }

    private void parseStatementsInsideRepeat() {
        while (true) {
            // Смотрим текущий токен и проверяем, не UNTIL ли это
            String current = currentToken();
            if ("UNTIL".equals(current)) {
                break; // Если встретили UNTIL, выходим из цикла
            }

            // Если это не UNTIL, то продолжаем обрабатывать команды, как обычно
            if (isIdentifier(current) && !isKeyword(current)) {
                parseAssignment();
                if (!match(";"))
                    throwError("Пропущен ';' после оператора присваивания.");
            } else if (match("READ")) {
                parseRead();
                if (!match(";"))
                    throwError("Пропущен ';' после READ.");
            } else if (match("WRITE")) {
                parseWrite();
                if (!match(";"))
                    throwError("Пропущен ';' после WRITE.");
            } else {
                throwError("Неизвестная команда: " + currentToken() + ".");
            }
        }
    }


    private void parseAssignment() {
        String variable = currentToken();
        if (!variables.containsKey(variable))
            throwError("Переменная '" + variable + "' не объявлена.");

        advance();

        if (!match("="))
            throwError("Ожидалось '=' в операторе присваивания.");

        int value = parseLogicalExpression();

        if (value != 0 && value != 1) {
            throwError("Ожидалось логическое значение (0 или 1), но получено: " + value);
        }

        variables.put(variable, value);
    }

    private void parseRead() {
        if (!match("("))
            throwError("Ожидалась '(' после READ.");

        String variable = currentToken();
        if (!variables.containsKey(variable))
            throwError("Переменная '" + variable + "' не объявлена.");

        advance();

        if (!match(")"))
            throwError("Ожидалась ')' после имени переменной в READ.");

        System.out.print("Введите значение для " + variable + " (0 или 1): ");
        Scanner scanner = new Scanner(System.in);

        // Проверяем, что введенное значение является целым числом
        if (!scanner.hasNextInt())
            throwError("Ожидалось числовое значение.");

        int value = scanner.nextInt();

        // Проверяем, что значение 0 или 1
        if (value != 0 && value != 1) {
            throwError("Ожидалось значение 0 или 1.");
        }

        variables.put(variable, value);
    }


    private void parseWrite() {
        if (!match("("))
            throwError("Ожидалась '(' после WRITE.");

        String variable = currentToken();
        if (!variables.containsKey(variable))
            throwError("Переменная '" + variable + "' не объявлена.");

        if(variables.get(variable)==null){
            throwError("Переменная '" + variable + "' не инициализирована.");
        }
        advance();

        if (!match(")"))
            throwError("Ожидалась ')' после имени переменной в WRITE.");

        System.out.println("Значение " + variable + ": " + variables.get(variable));
    }

    private void parseRepeat() {

        // Сохраняем позицию начала блока REPEAT
        int startPosition = currentTokenIndex;

        while (true) {
            // Выполняем действия внутри блока REPEAT
            parseStatementsInsideRepeat();

            // Проверяем наличие UNTIL
            if (!match("UNTIL")) {
                throwError("Ожидалась UNTIL после списка действий в REPEAT.");
            }

            // Парсим логическое условие после UNTIL
            int condition = parseLogicalExpression();

            // Проверяем наличие ';' после условия UNTIL
            if (!match(";")) {
                throwError("Ожидался ';' после условия UNTIL.");
            }

            // Если условие истинно (1), выходим из цикла
            if (condition == 1) {
                break;
            }

            // Если условие ложно (0), возвращаемся к началу блока REPEAT
            resetToPosition(startPosition);
        }
    }

    private void resetToPosition(int position) {
        currentTokenIndex = position;
    }

    private int parseLogicalExpression() {
        int value = parseTerm();  // Начинаем с терма (логического значения)

        while (currentToken() != null && (currentToken().equals(".IMP.") || currentToken().equals(".OR.") || currentToken().equals(".AND."))) {
            String operator = currentToken();
            advance();

            int nextValue = parseTerm();  // Получаем следующее логическое значение

            switch (operator) {
                case ".IMP." -> value = (value == 0 || nextValue != 0) ? 1 : 0;  // Импликация
                case ".OR." -> value = (value != 0 || nextValue != 0) ? 1 : 0;   // Логическое ИЛИ
                case ".AND." -> value = (value != 0 && nextValue != 0) ? 1 : 0;   // Логическое И
                default -> throwError("Неизвестный логический оператор '" + operator + "'.");
            }
        }

        return value;
    }

    private int parseTerm() {
        // Логические операции обрабатываются в parseLogicalExpression,
        // здесь нужно только учитывать унарный оператор .NOT.
        if (match(".NOT.")) {
            int value = parseTerm();  // Следующее логическое значение
            return value == 0 ? 1 : 0;  // Логическое отрицание
        }

        // Обработка выражений в скобках
        if (match("(")) {
            int value = parseLogicalExpression();  // Выражение внутри скобок
            if (!match(")"))
                throwError("Ожидалась закрывающая скобка ')'.");
            return value;
        }

        // Если это переменная или константа, возвращаем ее значение
        String token = currentToken();

        if (isIdentifier(token)) {
            if (!variables.containsKey(token))
                throwError("Переменная '" + token + "' не объявлена.");

            Integer variableValue = variables.get(token);
            if (variableValue == null)
                throwError("Переменная '" + token + "' не была инициализирована.");

            advance();
            return variables.get(token);
        }

        try {
            int parsedValue = Integer.parseInt(token);  // Если это константа (0 или 1)
            advance();
            return parsedValue;
        } catch (NumberFormatException e) {
            throwError("Некорректное выражение: " + token);
            return 0;  // Невозможно достичь
        }
    }

    private String currentToken() {
        return currentTokenIndex < tokensWithLines.size() ? tokensWithLines.get(currentTokenIndex).getToken() : null;
    }

    private void advance() {
        currentTokenIndex++;
    }

    private boolean match(String expected) {
        if (currentToken() != null && currentToken().equalsIgnoreCase(expected)) {
            advance();
            return true;
        }
        return false;
    }

    private boolean isIdentifier(String token) {
        return token.matches("[A-Za-z]+");
    }

    private boolean isKeyword(String token) {
        return token.equals("BEGIN") || token.equals("END") || token.equals("VAR") || token.equals("LOGICAL") ||
                token.equals("READ") || token.equals("REPEAT") || token.equals("WRITE") ||
                token.equals("UNTIL");
    }

    private void throwError(String message) {
        throw new RuntimeException(message + " Строка " + tokensWithLines.get(currentTokenIndex).getLine() + ". Токен '" + tokensWithLines.get(currentTokenIndex).getToken() + "'");
    }
}
