package com.interpretures.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

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

        writer.println("package com.interpretures.mist;\n");
        writer.println("abstract class " + baseName + " {\n");

        defineVisitor(writer, baseName, types);

        // The AST classes
        for (String type : types) {
            String className = type.split(":")[0].trim();
            String fields = type.split(":")[1].trim();
            defineType(writer, baseName, className, fields);
        }

        // The base accept method for Visitors
        writer.println();
        writer.println("    abstract <R> R accept(Visitor<R> visitor);");

        writer.println("}\n");
        writer.close();


    }

    private static void defineType(
            PrintWriter writer, String baseName, String className, String fields) throws IOException {

        writer.println("    static class " + className + " extends " + baseName + " {\n");

        // Constructor
        writer.println("        " + className + "(" + fields + ") {");

        String[] fieldsList = fields.split(",");
        for (String field : fieldsList) {
            String name = field.trim().split(" ")[1].trim();
            writer.println("            this." + name.trim() + " = " + name + ";");
        }
        writer.println("        }\n");

        writer.println("        @Override");
        writer.println("        <R> R accept(Visitor<R> visitor){");
        writer.println("            return visitor.visit"  + className + baseName +"(this);");
        writer.println("        }\n");

        // Fields
        for (String field : fieldsList) {
            writer.println("        final " + field.trim() + ";");
        }
        writer.println("    }\n");
    }

    private static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
        writer.println("    interface Visitor<R> {");

        for (String type: types){
            String typeName = type.split(":")[0].trim();
            writer.println("        R visit" + typeName + baseName +
                    "(" + typeName + " " + baseName.toLowerCase() + ");");
        }

        writer.println("    }\n");
    }
}
