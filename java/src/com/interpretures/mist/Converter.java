package com.interpretures.mist;

import javax.swing.*;
import java.util.HashMap;
import java.util.Stack;

public class Converter {
    public static HashMap<String, Integer> OPERATORS = new HashMap<String, Integer>();

    static {
        OPERATORS.put("+", 1);
        OPERATORS.put("-", 1);
        OPERATORS.put("*", 2);
        OPERATORS.put("/", 2);
        OPERATORS.put("^", 3);
        OPERATORS.put("(", 4);
    }

    public static String InfixToPostfix(String expression) {
        var RPN = new Stack<String>();
        var operators = new Stack<String>();
        for (var token : expression.split(" ")) {
            if (OPERATORS.get(token) != null) {
                if (operators.empty() || operators.peek().equals("(") || OPERATORS.get(operators.peek()) < OPERATORS.get(token)) {
                    operators.push(token);
                } else {
                    while (!operators.empty() && OPERATORS.get(operators.peek()) >= OPERATORS.get(token)) {
                        RPN.push(operators.pop());
                    }
                    operators.push(token);
                }
            } else if (token.equals(")")) {
                while (!operators.empty() && !operators.peek().equals("(")) {
                    RPN.push(operators.pop());
                }
                operators.pop();
            } else {
                RPN.push(token);
            }
        }
        while (!operators.empty()) {
            RPN.push(operators.pop());
        }
        System.out.println(RPN);
        System.out.println(operators);
        return null;
    }
}
