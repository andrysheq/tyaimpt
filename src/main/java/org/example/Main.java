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
            //List<TokenWithLine> tokens = lexicalAnalyzer.tokenizeWithLines(code);

//            SyntaxAnalyzer analyzer = new SyntaxAnalyzer(tokens);
//            TreeNode parseTree = analyzer.analyze();
//            System.out.println(parseTree);

            List<TokenWithLine> tokens = lexicalAnalyzer.tokenizeWithLines(code);

            Interpreter interpreter = new Interpreter(tokens);
            interpreter.interpret();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}