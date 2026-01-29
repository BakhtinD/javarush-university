package com.javarush.example;

public class BankAccount {
    private double balance = 1000;

    public class Transaction {
        private double amount;

        public Transaction(double amount) {
            this.amount = amount;
        }

        public void execute() {
            // BankAccount.this.balance -= this.amount;
            balance -= this.amount;
            System.out.println("Списано: " + this.amount + ", Остаток: " + BankAccount.this.balance);
        }
    }

    public static void main(String[] args) {
        BankAccount account = new BankAccount();
        BankAccount.Transaction transaction1 = account.new Transaction(100);
        BankAccount.Transaction transaction2 = account.new Transaction(200);

        transaction1.execute();
        transaction2.execute();
    }

}
