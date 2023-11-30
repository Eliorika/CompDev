package ru.rsreu.executor;

import ru.rsreu.Babaian.Tokens.ThreeAddressInstructions;
import ru.rsreu.Babaian.Tokens.Token;
import ru.rsreu.Babaian.Tokens.TokenType;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Expression {
    private List<ThreeAddressInstructions> codeLines;
    private Map<Integer, Token> symbolTable;

    public Expression(List<ThreeAddressInstructions> codeLines, Map<Integer, Token> symbolTable) {
        this.codeLines = codeLines;
        this.symbolTable = symbolTable;
    }

    public void process() {
        try {
            for (Map.Entry<Integer, Token> entry : symbolTable.entrySet()) {
                Scanner scanner = new Scanner(System.in);
                if (!entry.getValue().getToken().contains("#")) {
                    if (entry.getValue().getTokenType() == TokenType.TOKEN_ID_I) {
                        System.out.print("\nvar" + entry.getKey() + "[int]= ");
                        int i;
                        i = scanner.nextInt();

                        entry.getValue().setToken("<" + i + ">");
                    } else {
                        System.out.print("\nvar" + entry.getKey() + "[double]= ");
                        double d = scanner.nextDouble();
                        entry.getValue().setToken("<" + d + ">");
                    }
                }
            }


            for(ThreeAddressInstructions instruction : codeLines){
                var res = instruction.execute();
                res.performAssign();
            }

            System.out.println(codeLines.get(codeLines.size()-1).getOperands().get(0).getToken());

        } catch (Exception e) {
            System.err.println("Wrong input");
        }
    }
}
