package org.example.util;

import java.util.List;
import java.util.Stack;

public class SyntaxAnalyzer {
    private List<String> tokens;
    private int currentTokenIndex;

    public SyntaxAnalyzer(List<String> tokens) {
        this.tokens = tokens;
        this.currentTokenIndex = 0;
    }

    public SyntaxTreeNode parse() {
        SyntaxTreeNode root = program();
        if (currentTokenIndex < tokens.size()) {
            throw new RuntimeException("Unexpected token: " + tokens.get(currentTokenIndex));
        }
        return root;
    }

    private SyntaxTreeNode program() {
        SyntaxTreeNode programNode = new SyntaxTreeNode("PROGRAM");
        while (currentTokenIndex < tokens.size()) {
            programNode.addChild(statement());
        }
        return programNode;
    }

    
    private SyntaxTreeNode statement() {
        String currentToken = tokens.get(currentTokenIndex);
        SyntaxTreeNode statementNode;
    
        if (currentToken.equals("BEGIN")) {
            statementNode = new SyntaxTreeNode("BEGIN");
            currentTokenIndex++; // consume 'BEGIN'
            
            // Process statements inside the BEGIN...END block
            while (!tokens.get(currentTokenIndex).equals("END")) {
                statementNode.addChild(statement());
            }
            currentTokenIndex++; // consume 'END'
            
        } else if (currentToken.equals("VAR")) {
            statementNode = new SyntaxTreeNode("VAR");
            currentTokenIndex++; // consume 'VAR'
            
            // Process variable declarations with commas and types
            while (true) {
                String variableName = tokens.get(currentTokenIndex++);
                if (LexicalAnalyzer.classifyToken(variableName).equals("Идентификатор")) {
                    statementNode.addChild(new SyntaxTreeNode(variableName));
                } else {
                    throw new RuntimeException("Invalid identifier in variable declaration: " + variableName);
                }
                
                // Check for comma, type, or assignment
                String nextToken = tokens.get(currentTokenIndex);
                if (nextToken.equals(",")) {
                    currentTokenIndex++; // consume ','
                } else if (nextToken.equals(":")) {
                    currentTokenIndex++; // consume ':'
                    String type = tokens.get(currentTokenIndex++);
                    if (!LexicalAnalyzer.classifyToken(type).equals("Тип переменной")) {
                        throw new RuntimeException("Expected variable type, found: " + type);
                    }
                    statementNode.addChild(new SyntaxTreeNode(type));
                    break;
                } else {
                    throw new RuntimeException("Unexpected token in variable declaration: " + nextToken);
                }
            }
    
        } else if (LexicalAnalyzer.classifyToken(currentToken).equals("Идентификатор")) {
            // Handle identifier followed by possible assignment
            statementNode = new SyntaxTreeNode("ASSIGNMENT");
            statementNode.addChild(new SyntaxTreeNode(currentToken)); // variable name
            currentTokenIndex++; // consume identifier
    
            // Check for '=' as assignment operator
            currentToken = tokens.get(currentTokenIndex);
            if (currentToken.equals("=")) {
                statementNode.addChild(new SyntaxTreeNode("=")); // Add '=' as part of the assignment node
                currentTokenIndex++; // consume '='
                statementNode.addChild(expression()); // parse expression to the right of '='
            } else {
                throw new RuntimeException("Expected '=', found: " + currentToken);
            }
            
        } else if (currentToken.equals("WRITE")) {
            statementNode = new SyntaxTreeNode("WRITE");
            currentTokenIndex++; // consume 'WRITE'
    
            // Expect '(' next
            if (tokens.get(currentTokenIndex).equals("(")) {
                currentTokenIndex++; // consume '('
                statementNode.addChild(expression()); // Process the expression inside WRITE
    
                // Expect ')' to close the WRITE expression
                if (tokens.get(currentTokenIndex).equals(")")) {
                    currentTokenIndex++; // consume ')'
                } else {
                    throw new RuntimeException("Expected ')', found: " + tokens.get(currentTokenIndex));
                }
            } else {
                throw new RuntimeException("Expected '(', found: " + tokens.get(currentTokenIndex));
            }
    
        } else if (currentToken.equals("REPEAT")) {
            statementNode = new SyntaxTreeNode("REPEAT");
            currentTokenIndex++; // consume 'REPEAT'
            
            // Process the loop body
            while (!tokens.get(currentTokenIndex).equals("UNTIL")) {statementNode.addChild(statement());
            }
            currentTokenIndex++; // consume 'UNTIL'
            
            // Add the condition after UNTIL
            statementNode.addChild(expression());
            
        } else if (currentToken.equals(";")) {
            // Ignore ';' as it's just a separator
            currentTokenIndex++; // consume ';'
            return statement(); // Continue parsing the next statement
    
        } else {
            throw new RuntimeException("Unexpected token in statement: " + currentToken);
        }
    
        // Check for a trailing ';' after a complete statement
        if (currentTokenIndex < tokens.size() && tokens.get(currentTokenIndex).equals(";")) {
            currentTokenIndex++; // consume ';'
        }
    
        return statementNode;
    }
    
    private SyntaxTreeNode expression() {
        SyntaxTreeNode expressionNode = new SyntaxTreeNode("EXPRESSION");
    
        // Начинаем с текущего токена
        String currentToken = tokens.get(currentTokenIndex);
    
        // Обработка унарного оператора (например, .NOT.)
        if (LexicalAnalyzer.classifyToken(currentToken).equals("Унарный оператор")) {
            SyntaxTreeNode unaryNode = new SyntaxTreeNode(currentToken); // Create a node for the unary operator
            currentTokenIndex++; // consume the unary operator
            
            // Вложенное выражение для унарного оператора
            unaryNode.addChild(expression()); 
            return unaryNode;
    
        } else if (currentToken.equals("(")) {
            currentTokenIndex++; // consume '('
            expressionNode.addChild(expression()); // Process the expression inside parentheses
    
            // Expect ')'
            if (tokens.get(currentTokenIndex).equals(")")) {
                currentTokenIndex++; // consume ')'
            } else {
                throw new RuntimeException("Expected ')', found: " + tokens.get(currentTokenIndex));
            }
    
        } else if (LexicalAnalyzer.classifyToken(currentToken).equals("Идентификатор") || 
                   LexicalAnalyzer.classifyToken(currentToken).equals("Константа")) {
            // Если это идентификатор или константа, обрабатываем как простое выражение
            expressionNode.addChild(new SyntaxTreeNode(currentToken));
            currentTokenIndex++; // consume identifier or constant
    
        } else {
            throw new RuntimeException("Unexpected token in expression: " + currentToken);
        }
    
        // Проверка на наличие логических операторов и операторов присваивания после базового выражения
        while (currentTokenIndex < tokens.size()) {
            String nextToken = tokens.get(currentTokenIndex);
            if (nextToken.equals("=")) {
                SyntaxTreeNode operatorNode = new SyntaxTreeNode("="); // Добавляем '=' как узел
                currentTokenIndex++; // consume '='
                operatorNode.addChild(expressionNode); // левый операнд
                operatorNode.addChild(expression()); // правый операнд
                expressionNode = operatorNode; // обновляем текущее выражение
            } else if (LexicalAnalyzer.classifyToken(nextToken).equals("Логический оператор")) {
                SyntaxTreeNode operatorNode = new SyntaxTreeNode(nextToken); // Add operator as node
                currentTokenIndex++; // consume operator
                operatorNode.addChild(expressionNode); // левый операнд
                operatorNode.addChild(expression()); // правый операнд
                expressionNode = operatorNode; // обновляем текущее выражение
            } else {
                break; // выходим из цикла, если нет логического оператора или оператора '='
            }
        }
    
        return expressionNode;
    }
    
    

}