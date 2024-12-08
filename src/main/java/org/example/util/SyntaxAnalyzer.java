package org.example.util;

import java.util.*;

public class SyntaxAnalyzer {
    private final List<TokenWithLine> tokens; // Токены с привязкой к строкам
    private int currentTokenIndex = 0; // Индекс текущего токена
    private final List<TreeNode> parseTree = new ArrayList<>(); // Дерево разбора

    public SyntaxAnalyzer(List<TokenWithLine> tokens) {
        this.tokens = tokens;
    }

    public TreeNode analyze() {
        TreeNode programNode = parseProgram();
        return programNode; // Корень дерева разбора
    }

    private TreeNode parseProgram() {
        TreeNode programNode = new TreeNode("Program");

        if (!match("VAR")) {
            throwError("Программа должна начинаться с ключевого слова VAR.");
        }
        programNode.addChild(parseVariableSection());

        if (!match("BEGIN")) {
            throwError("Пропущено ключевое слово BEGIN.");
        }
        programNode.addChild(parseStatements());

        if (!match("END")) {
            throwError("Пропущено ключевое слово END.");
        }

        if (currentTokenIndex < tokens.size()) {
            throwError("Код после END недопустим.");
        }

        return programNode;
    }

    private TreeNode parseVariableSection() {
        TreeNode variableSectionNode = new TreeNode("VariableSection");

        variableSectionNode.addChild(parseVariableList());

        if (!match(":") || !match("LOGICAL")) {
            throwError("Ожидалось ': LOGICAL' после списка переменных.");
        }

        if (!match(";")) {
            throwError("Пропущен ';' после объявления переменных.");
        }

        return variableSectionNode;
    }

    private TreeNode parseVariableList() {
        TreeNode variableListNode = new TreeNode("VariableList");

        while (true) {
            String variable = currentToken();
            if (!isIdentifier(variable)) {
                throwError("Некорректное имя переменной: " + variable);
            }

            variableListNode.addChild(new TreeNode("Variable", variable));
            advance();

            if (!match(",")) {
                break;
            }
        }

        return variableListNode;
    }

    private TreeNode parseStatements() {
        TreeNode statementsNode = new TreeNode("Statements");

        while (!currentToken().equals("END") && !currentToken().equals("UNTIL")) {
            if (isIdentifier(currentToken()) && !isKeyword(currentToken())) {
                statementsNode.addChild(parseAssignment());
                if (!match(";")) {
                    throwError("Пропущен ';' после оператора присваивания.");
                }
            } else if (match("READ")) {
                statementsNode.addChild(parseRead());
                if (!match(";")) {
                    throwError("Пропущен ';' после READ.");
                }
            } else if (match("WRITE")) {
                statementsNode.addChild(parseWrite());
                if (!match(";")) {
                    throwError("Пропущен ';' после WRITE.");
                }
            } else if (match("REPEAT")) {
                statementsNode.addChild(parseRepeat());
            } else {
                throwError("Неизвестная команда: " + currentToken());
            }
        }

        return statementsNode;
    }


    private TreeNode parseAssignment() {
        TreeNode assignmentNode = new TreeNode("Assignment");

        String variable = currentToken();
        if (!isIdentifier(variable)) {
            throwError("Ожидалась переменная.");
        }
        assignmentNode.addChild(new TreeNode("Variable", variable));
        advance();

        if (!match("=")) {
            throwError("Ожидалось '='.");
        }
        assignmentNode.addChild(parseLogicalExpression());

        return assignmentNode;
    }

    private TreeNode parseRead() {
        TreeNode readNode = new TreeNode("Read");

        if (!match("(")) {
            throwError("Ожидалась '(' после READ.");
        }

        String variable = currentToken();
        if (!isIdentifier(variable)) {
            throwError("Ожидалась переменная.");
        }
        readNode.addChild(new TreeNode("Variable", variable));
        advance();

        if (!match(")")) {
            throwError("Ожидалась ')' после переменной.");
        }

        return readNode;
    }

    private TreeNode parseWrite() {
        TreeNode writeNode = new TreeNode("Write");

        if (!match("(")) {
            throwError("Ожидалась '(' после WRITE.");
        }

        String variable = currentToken();
        if (!isIdentifier(variable)) {
            throwError("Ожидалась переменная.");
        }
        writeNode.addChild(new TreeNode("Variable", variable));
        advance();

        if (!match(")")) {
            throwError("Ожидалась ')' после переменной.");
        }

        return writeNode;
    }

    private TreeNode parseRepeat() {
        TreeNode repeatNode = new TreeNode("Repeat");

        repeatNode.addChild(parseStatementsInsideRepeat());

        if (!match("UNTIL")) {
            throwError("Ожидалась UNTIL после REPEAT.");
        }
        repeatNode.addChild(parseLogicalExpression());

        if (!match(";")) {
            throwError("Ожидался ';' после UNTIL.");
        }

        return repeatNode;
    }

    private TreeNode parseStatementsInsideRepeat() {
        TreeNode statementsNode = new TreeNode("RepeatStatements");

        while (!currentToken().equals("UNTIL")) {
            if (currentToken() == null) {
                throwError("Ожидалось ключевое слово UNTIL в конце блока REPEAT.");
            }
            statementsNode.addChild(parseStatements());
        }

        return statementsNode;
    }

    private TreeNode parseLogicalExpression() {
        TreeNode logicalExpressionNode = new TreeNode("LogicalExpression");

        logicalExpressionNode.addChild(parseTerm());

        while (currentToken() != null && (currentToken().equals(".IMP.") || currentToken().equals(".OR.") || currentToken().equals(".AND."))) {
            String operator = currentToken();
            advance();
            logicalExpressionNode.addChild(new TreeNode("Operator", operator));
            logicalExpressionNode.addChild(parseTerm());
        }

        return logicalExpressionNode;
    }

    private TreeNode parseTerm() {
        if (match(".NOT.")) {
            TreeNode notNode = new TreeNode("Not");
            notNode.addChild(parseTerm());
            return notNode;
        }

        if (match("(")) {
            TreeNode expressionNode = parseLogicalExpression();
            if (!match(")")) {
                throwError("Ожидалась закрывающая скобка ')'.");
            }
            return expressionNode;
        }

        String token = currentToken();
        advance();
        return new TreeNode("Term", token);
    }

    private String currentToken() {
        return currentTokenIndex < tokens.size() ? tokens.get(currentTokenIndex).getToken() : null;
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
        return Set.of("BEGIN", "END", "VAR", "LOGICAL", "READ", "REPEAT", "WRITE", "UNTIL").contains(token);
    }

    private void throwError(String message) {
        throw new RuntimeException(message + " (токен: '" + currentToken() + "')");
    }
}

