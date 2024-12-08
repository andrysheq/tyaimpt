//package org.example.util;
//
//import java.util.List;
//import java.util.Stack;
//
//public class BottomUpSyntaxAnalyzer {
//    private List<String> tokens;
//    private int currentTokenIndex;
//    private Stack<SyntaxTreeNodeDeprecated> stack;
//
//    public BottomUpSyntaxAnalyzer(List<String> tokens) {
//        this.tokens = tokens;
//        this.currentTokenIndex = 0;
//        this.stack = new Stack<>();
//    }
//
//    public SyntaxTreeNodeDeprecated parse() {
//        while (currentTokenIndex < tokens.size()) {
//            String token = tokens.get(currentTokenIndex);
//
//            if (LexicalAnalyzerDeprecated.classifyToken(token).equals("Неизвестная лексема")){
//                throw new RuntimeException("Разбор невозможен: присутствует неизвестная лексема.");
//            }
//
//            // Сдвиг: добавляем токен в стек
//            stack.push(new SyntaxTreeNodeDeprecated(token));
//            currentTokenIndex++;
//        }
//
//        applyReductions();
//
//        // Проверка на одиночное корневое дерево
//        if (stack.size() != 1) {
//            System.out.println("Ошибка: Ожидалось одно корневое дерево.");
//            for (SyntaxTreeNodeDeprecated node : stack) {
//                System.out.println(node.getValue());
//            }
//            throw new RuntimeException("Ошибка в синтаксическом анализе. Ожидалось одно корневое дерево.");
//        }
//        return stack.pop();
//    }
//
//    private void applyReductions() {
//        // Сначала свертка всех логических операторов
//        reduceLogicalExpression();
//        reduceExpressions();
//        reduceNotExpression();
//        reduceUnaryNotOperator();
//        reduceAssignment();
//        reduceRepeatUntil();
//        reduceWriteStatement();
//        reduceBlock();
//        reduceVarDeclaration();
//        reduceProgram();
//    }
//
//    private boolean reduceAssignment() {
//        if (stack.size() >= 3) {
//            for (int i = 0; i < stack.size() - 2; i++) {
//                SyntaxTreeNodeDeprecated top = stack.get(i + 2);
//                SyntaxTreeNodeDeprecated operator = stack.get(i + 1);
//                SyntaxTreeNodeDeprecated left = stack.get(i);
//
//                if (isIdentifier(left) && isAssignmentOperator(operator) && isExpression(top)) {
//                    SyntaxTreeNodeDeprecated assignmentNode = new SyntaxTreeNodeDeprecated("ASSIGNMENT");
//                    assignmentNode.addChild(left);
//                    assignmentNode.addChild(operator);
//                    assignmentNode.addChild(top);
//                    stack.set(i, assignmentNode);  // Заменяем старые элементы на новый узел
//                    stack.subList(i + 1, i + 3).clear();  // Удаляем старые элементы
//                }
//            }
//        }
//        return false;
//    }
//
//    private boolean reduceExpressions() {
//        if (stack.size() >= 3) {
//            for (int i = 0; i < stack.size() - 2; i++) {
//                SyntaxTreeNodeDeprecated top = stack.get(i + 2);
//                SyntaxTreeNodeDeprecated operator = stack.get(i + 1);
//                SyntaxTreeNodeDeprecated left = stack.get(i);
//
//                if (left.getValue().equals("(") && isExpression(operator) && top.getValue().equals(")")) {
//                    SyntaxTreeNodeDeprecated assignmentNode = new SyntaxTreeNodeDeprecated("EXPRESSION");
//                    assignmentNode.addChild(left);
//                    assignmentNode.addChild(operator);
//                    assignmentNode.addChild(top);
//                    stack.set(i, assignmentNode);  // Заменяем старые элементы на новый узел
//                    stack.subList(i + 1, i + 3).clear();  // Удаляем старые элементы
//                }
//            }
//        }
//        return false;
//    }
//
//    private boolean reduceLogicalExpression() {
//        if (stack.size() >= 3) {
//            for (int i = 0; i < stack.size() - 2; i++) {
//                SyntaxTreeNodeDeprecated left = stack.get(i);
//                SyntaxTreeNodeDeprecated operator = stack.get(i + 1);
//                SyntaxTreeNodeDeprecated right = stack.get(i + 2);
//
//                if (isExpression(left) && isLogicalOperator(operator) && isExpression(right)) {
//                    SyntaxTreeNodeDeprecated logicalNode = new SyntaxTreeNodeDeprecated(operator.getValue());
//                    logicalNode.addChild(left);
//                    logicalNode.addChild(right);
//                    stack.set(i, logicalNode);  // Заменяем старые элементы на новый узел
//                    stack.subList(i + 1, i + 3).clear();  // Удаляем старые элементы
//                }
//            }
//        }
//        return false;
//    }
//
//    private boolean reduceWriteStatement() {
//        if (stack.size() >= 2) {
//            for (int i = 0; i < stack.size() - 1; i++) {
//                SyntaxTreeNodeDeprecated argument = stack.get(i + 1);
//                SyntaxTreeNodeDeprecated writeKeyword = stack.get(i);
//
//                if (writeKeyword.getValue().equals("WRITE") && isExpression(argument)) {
//                    SyntaxTreeNodeDeprecated writeNode = new SyntaxTreeNodeDeprecated("WRITE");
//                    writeNode.addChild(writeKeyword);
//                    writeNode.addChild(argument);
//                    stack.set(i,writeNode);
//                    stack.subList(i + 1, i + 2).clear();
//                    System.out.println("Применено правило записи: WRITE");
//                }
//            }
//        }
//        return false;
//    }
//
//    private boolean reduceVarDeclaration() {
//        if (stack.size() >= 2) {
//            for (int i = 0; i < stack.size() - 1; i++) {
//                SyntaxTreeNodeDeprecated varNode = stack.get(i);
//
//                if (varNode.getValue().equals("VAR")){
//                    for (int j = i + 1; j < stack.size(); j++) {
//                        SyntaxTreeNodeDeprecated endNode = stack.get(j);
//                        if (endNode.getValue().equals("LOGICAL")) {
//                            SyntaxTreeNodeDeprecated blockNode = new SyntaxTreeNodeDeprecated("VAR_DECLARATION_BLOCK");
//                            for (int x = i+1; x < j; x++) {
//                                SyntaxTreeNodeDeprecated childNode = stack.get(x);
//                                if(LexicalAnalyzerDeprecated.classifyToken(childNode.getValue()).equals("Идентификатор")) {
//                                    blockNode.addChild(childNode);
//                                } else if (LexicalAnalyzerDeprecated.classifyToken(childNode.getValue()).equals("Разделитель")) {
//
//                                } else{
//                                    throw new RuntimeException("Ошибка во время разбора блока VAR");
//                                }
//                            }
//                            //blockNode.addChild(endNode);
//                            stack.set(i, blockNode);
//                            stack.subList(i + 1, j + 1).clear();
//                            System.out.println("Применено правило блока: VAR_DECLARATION");
//                            return true;
//                        }
//                    }
//                }
//            }
//        }
//        return false;
//    }
//
//    private boolean reduceProgram() {
//        if (stack.size() >= 2) {
//            for (int i = 0; i < stack.size() - 1; i++) {
//                SyntaxTreeNodeDeprecated varNode = stack.get(i);
//
//                if (varNode.getValue().equals("VAR_DECLARATION_BLOCK")){
//                    for (int j = i + 1; j < stack.size(); j++) {
//                        SyntaxTreeNodeDeprecated endNode = stack.get(j);
//                        if (endNode.getValue().equals("BLOCK_BEGIN_END")) {
//                            SyntaxTreeNodeDeprecated blockNode = new SyntaxTreeNodeDeprecated("PROGRAM");
//                            blockNode.addChild(varNode);
//                            blockNode.addChild(endNode);
//                            stack.set(i, blockNode);
//                            stack.subList(i + 1, j + 1).clear();
//                            System.out.println("Применено правило блока: PROGRAM");
//                            return true;
//                        }
//                    }
//                }
//            }
//        }
//        return false;
//    }
//
//    private boolean reduceBlock() {
//        if (stack.size() >= 2) {
//            for (int i = 0; i < stack.size() - 1; i++) {
//                //SyntaxTreeNodeDeprecated endNode = stack.get(i + 1);
//                SyntaxTreeNodeDeprecated beginNode = stack.get(i);
//
//                if (beginNode.getValue().equals("BEGIN")){ //&& endNode.getValue().equals("END")) {
//                    for (int j = i; j < stack.size(); j++) {
//                        SyntaxTreeNodeDeprecated endNode = stack.get(j);
//                        if (endNode.getValue().equals("END")) {
//                            SyntaxTreeNodeDeprecated blockNode = new SyntaxTreeNodeDeprecated("BLOCK_BEGIN_END");
//                            //blockNode.addChild(beginNode);
//                            for (int x = i+1; x < j; x++) {
//                                SyntaxTreeNodeDeprecated childNode = stack.get(x);
//                                if(childNode.getValue().equals("ASSIGNMENT")) {
//                                    blockNode.addChild(childNode);
//                                }
//                            }
//                            //blockNode.addChild(endNode);
//                            stack.set(i, blockNode);
//                            stack.subList(i + 1, j + 1).clear();
//                            System.out.println("Применено правило блока: BLOCK");
//                            return true;
//                        }
//                    }
//                }
//            }
//        }
//        return false;
//    }
//
//    private boolean reduceNotExpression() {
//        if (stack.size() >= 3) {
//            for (int i = 0; i < stack.size() - 2; i++) {
//                SyntaxTreeNodeDeprecated closeParen = stack.get(i + 2);
//                SyntaxTreeNodeDeprecated expression = stack.get(i + 1);
//                SyntaxTreeNodeDeprecated openParen = stack.get(i);
//
//                if (openParen.getValue().equals("(") && closeParen.getValue().equals(")") && isUnaryOperator(expression)) {
//                    SyntaxTreeNodeDeprecated notNode = new SyntaxTreeNodeDeprecated("NOT_EXPRESSION");
//                    notNode.addChild(openParen);
//                    notNode.addChild(expression);
//                    notNode.addChild(closeParen);
//                    stack.set(i,notNode);
//                    stack.subList(i + 1, i + 3).clear();
//                    System.out.println("Применено правило унарного выражения: NOT_EXPRESSION");
//                }
//            }
//        }
//        return false;
//    }
//
//    private boolean reduceRepeatUntil() {
//        if (stack.size() >= 3) {
//            for (int i = 0; i < stack.size() - 2; i++) {
//                SyntaxTreeNodeDeprecated condition = stack.get(i + 2);
//                SyntaxTreeNodeDeprecated untilNode = stack.get(i + 1);
//                SyntaxTreeNodeDeprecated repeatNode = stack.get(i);
//
//                if (repeatNode.getValue().equals("REPEAT") && untilNode.getValue().equals("UNTIL") && isExpression(condition)) {
//                    SyntaxTreeNodeDeprecated repeatUntilNode = new SyntaxTreeNodeDeprecated("REPEAT_UNTIL");
//                    repeatUntilNode.addChild(repeatNode);
//                    repeatUntilNode.addChild(untilNode);
//                    repeatUntilNode.addChild(condition);
//                    stack.set(i,repeatUntilNode);
//                    stack.subList(i + 1, i + 3).clear();
//                    System.out.println("Применено правило цикла: REPEAT_UNTIL");
//                }
//            }
//        }
//        return false;
//    }
//
//    private boolean reduceUnaryNotOperator() {
//        if (stack.size() >= 2) {
//            for (int i = 0; i < stack.size() - 1; i++) {
//                SyntaxTreeNodeDeprecated operand = stack.get(i + 1);
//                SyntaxTreeNodeDeprecated notOperator = stack.get(i);
//
//                if (notOperator.getValue().equals(".NOT.") && isExpression(operand)) {
//                    SyntaxTreeNodeDeprecated notNode = new SyntaxTreeNodeDeprecated("NOT");
//                    notNode.addChild(operand);
//                    stack.set(i,notNode);
//                    stack.subList(i + 1, i + 2).clear();
//                    System.out.println("Применено правило унарного оператора: NOT");
//                }
//            }
//        }
//        return false;
//    }
//
//    // Методы для классификации токенов
//    private boolean isIdentifier(SyntaxTreeNodeDeprecated node) {
//        return LexicalAnalyzerDeprecated.classifyToken(node.getValue()).equals("Идентификатор");
//    }
//
//    private boolean isAssignmentOperator(SyntaxTreeNodeDeprecated node) {
//        return node.getValue().equals("=");
//    }
//
//        private boolean isLogicalOperator(SyntaxTreeNodeDeprecated node) {
//        return LexicalAnalyzerDeprecated.classifyToken(node.getValue()).equals("Логический оператор");
//    }
//
//    private boolean isUnaryOperator(SyntaxTreeNodeDeprecated node) {
//        return LexicalAnalyzerDeprecated.classifyToken(node.getValue()).equals("Унарный оператор");
//    }
//
//        private boolean isExpression(SyntaxTreeNodeDeprecated node) {
//        String type = LexicalAnalyzerDeprecated.classifyToken(node.getValue());
//        return type.equals("Идентификатор") || type.equals("Константа") || type.equals("Логический оператор") || node.getValue().equals("EXPRESSION");
//    }
//}
