package org.example;

import org.example.util.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            String code = new String(Files.readAllBytes(Paths.get("src/main/java/org/example/util/code.txt")));

            LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer();
            List<TokenWithLine> tokens = lexicalAnalyzer.tokenizeWithLines(code);

//            SyntaxAnalyzer analyzer = new SyntaxAnalyzer(tokens);
//            TreeNode parseTree = analyzer.analyze();
//            System.out.println(parseTree);

            List<TokenWithLine> tokens2 = lexicalAnalyzer.tokenizeWithLines(code);

            Interpreter interpreter = new Interpreter(tokens2);
            interpreter.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printSyntaxTree(SyntaxTreeNodeDeprecated node, int depth) {
        for (int i = 0; i < depth; i++) {
            System.out.print("  "); // Indentation
        }
        System.out.println(node.getValue());
        for (SyntaxTreeNodeDeprecated child : node.getChildren()) {
            printSyntaxTree(child, depth + 1);
        }
    }
}