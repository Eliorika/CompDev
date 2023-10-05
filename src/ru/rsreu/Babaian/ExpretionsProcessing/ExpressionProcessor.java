package ru.rsreu.Babaian.ExpretionsProcessing;

public abstract class ExpressionProcessor {
    private boolean isError;
    private String message;

    public boolean isError() {
        return isError;
    }

    protected void setError(String message) {
        this.isError = true;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
