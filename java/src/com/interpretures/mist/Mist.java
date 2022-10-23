package com.interpretures.mist;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Mist {
    public static boolean hadError = false;

    public static void main(String[] args) throws IOException {
        // Generating ASTs expressions
//        GenerateAst.main(new String[]{"/Users/artur/Programming/MyProjects/Reflex/java/src/com/interpretures/mist"});
        if (args.length > 1){
            System.out.println("Usage: reflex [script]");
            System.exit(64);
        } else if (args.length ==1 ){
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        System.out.println("File successfully founded...");
        run(new String(bytes, Charset.defaultCharset()));

        // indicate an error in the exit code
        if (hadError) System.exit(65);
    }

    private static void runPrompt() throws IOException{
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for (;;){
            System.out.print("> ");
            String line = reader.readLine();
            if (line.equals("exit()")) break;
            run(line);
        }

    }

    private static void run(String source){
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        for (Token token : tokens){
            System.out.println(token);
        }
    }

    static void error(int line, String message){
        report(line, "", message);
    }

    private static void report(int line, String where, String message){
        System.err.println("[line "+ line+"] Error" + where + ": " + message);
        hadError = true;
    }

}
