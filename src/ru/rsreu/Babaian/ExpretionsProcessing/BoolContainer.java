package ru.rsreu.Babaian.ExpretionsProcessing;

public class BoolContainer {
    public Boolean allDouble = true;
    public Boolean converted = false;

    public boolean getRes(){
        return allDouble || converted;
    }
}
