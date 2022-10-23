package com.interpretures.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class GenerateAst {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: generate_ast <output directory>");
            System.exit(64);
        } else {
            String outputDir = args[0];
            defineAst(outputDir, "Expr", Arrays.asList(
                    "Binary   : Expr left, Token operator, Expr right",
                    "Grouping : Expr expression",
                    "Literal  : Object value",
                    "Unary    : Token operator, Expr right"
            ));
        }
    }

    private static void defineAst(String outputDir, String baseName, List<String> types) throws IOException {
        String path = outputDir + "/" + baseName + ".java";
        PrintWriter writer = new PrintWriter(path, "UTF-8");

        writer.println("package com.interpretures.reflex;\n");
        writer.println("import java.utils.List;\n");
        writer.println("abstract class " + baseName + " {\n");

        for (String type : types) {
            String className = type.split(":")[0].trim();
            String fields = type.split(":")[1].trim();
            defineType(writer, baseName, className, fields);
        }

        writer.println("}\n");
        writer.close();


    }

    private static void defineType(
            PrintWriter writer, String baseName, String className, String fields) throws IOException {

        writer.println("static class " + className + " extends " + baseName + " {\n");

        // Constructor
        writer.println("    " + className + "(" + fields + ") {");

        String[] fieldsList = fields.split(",");
        for (String field : fieldsList) {
            String name = field.trim().split(" ")[1].trim();
            writer.println("        this." + name.trim() + " = " + name + ";");
        }
        writer.println("}\n");

        for (String field : fieldsList) {
            writer.println("final " + field.trim() + ";");
        }
        writer.println("}\n");


    }
}
