package ru.rsreu.Babaian.Tokens;

import java.util.ArrayList;
import java.util.List;

public class ThreeAddressInstructions {
    private final Instructions instruction;

    private List<Token> token = new ArrayList<>();



    public Instructions getInstruction() {
        return instruction;
    }

    public List<Token> getToken() {
        return token;
    }

    public ThreeAddressInstructions(Instructions instruction, List<Token> token) {
        this.instruction = instruction;
        this.token = token;
    }

    @Override
    public String toString(){
        String res = instruction + " ";
        for (Token tk: token){
            res += tk.getToken();
            res+=" ";
        }
        return res;
    }
}
