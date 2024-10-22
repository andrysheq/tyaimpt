package org.example;

import org.example.util.LexicalAnalyzer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            String code = new String(Files.readAllBytes(Paths.get("F:\\javaprojects\\tyaimpt\\src\\main\\java\\org\\example\\util\\code.txt")));
            List<String> tokens = LexicalAnalyzer.tokenize(code);

            // Классификация и вывод лексем
            for (String token : tokens) {
                System.out.println(token + " - " + LexicalAnalyzer.classifyToken(token));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}