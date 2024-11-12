package org.example;

import org.example.util.LexicalAnalyzer;
import org.example.util.SyntaxAnalyzer;
import org.example.util.SyntaxTreeNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            String code = new String(Files.readAllBytes(Paths.get("C:\\у(ч)ёба\\3 курс\\ТЯП\\tyaimpt\\src\\main\\java\\org\\example\\util\\code.txt")));
            List<String> tokens = LexicalAnalyzer.tokenize(code);

            // Классификация и вывод лексем
            for (String token : tokens) {
                System.out.println(token + " - " + LexicalAnalyzer.classifyToken(token));
            }

            // Создание синтаксического анализатора и построение дерева
            SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer(tokens);
            SyntaxTreeNode syntaxTree = syntaxAnalyzer.parse();

            // Вывод дерева
            printSyntaxTree(syntaxTree, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printSyntaxTree(SyntaxTreeNode node, int depth) {
        for (int i = 0; i < depth; i++) {
            System.out.print("  "); // Indentation
        }
        System.out.println(node.getValue());
        for (SyntaxTreeNode child : node.getChildren()) {
            printSyntaxTree(child, depth + 1);
        }
    }
}