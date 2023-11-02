package ru.rsreu.Babaian;

import ru.rsreu.Babaian.TokensProcessing.ExpressionTreeBuilder;
import ru.rsreu.Babaian.TokensProcessing.SyntaxAnalyzer;
import ru.rsreu.Babaian.TokensProcessing.TokenGenerator;

public class lab3 {
    public static void main(String[] args) {
        try {
            if(args[0].equalsIgnoreCase("lex")){
                TokenGenerator tokenGenerator = new TokenGenerator();
                tokenGenerator.writeToken(args[1], args[2], args[3]);
            } else if(args[0].equalsIgnoreCase("syn")){
                TokenGenerator tokenGenerator = new TokenGenerator();
                var tok = tokenGenerator.getTokens(args[1]);
                SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer(tok);
                syntaxAnalyzer.analyze();
                ExpressionTreeBuilder tree = new ExpressionTreeBuilder(tok);
                tree.treeToFile(args[2]);

            } else throw new Exception();
        } catch (Exception e){
            System.out.println("Wrong input!");
        }
    }
}
