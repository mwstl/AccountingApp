package com.example.expensestracker;

public class Purchase {
    private double amount;
    private String currency;
    private String note;
    private String type;
    private boolean isExpense;

    public Purchase(double amount, String currency, String note, boolean isExpense) {
        this.amount = amount;
        this.currency = currency;
        this.note = note;
        this.isExpense = isExpense;
    }

    public Purchase(String amount, String currency, String type, String note) {
        this.amount = Double.parseDouble(amount);
        this.currency = currency;
        this.type = type;
        this.note = note;
    }

    public Purchase(double amount, String type) {
        this.amount = amount;
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getNote() {
        return note;
    }

    public boolean isExpense() {
        return isExpense;
    }

    public String getType() { return type; }
}

