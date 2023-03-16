package com.example.expensestracker;

public class Purchase {
    private double amount;
    private String currency;
    private String note;
    private String type;
    private int isExpense;
    private String imageURI;

    public Purchase(double amount, String currency, String note, int isExpense, String img) {
        this.amount = amount;
        this.currency = currency;
        this.note = note;
        this.isExpense = isExpense;
        this.imageURI = img;
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

    public Purchase() {}

    public double getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getNote() {
        return note;
    }

//    public int isExpense() {
//        return isExpense;
//    }

    public String getType() { return type; }

    public String getImageURI() { return imageURI; }
}

