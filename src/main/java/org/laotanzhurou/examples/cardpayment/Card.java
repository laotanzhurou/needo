package org.laotanzhurou.examples.cardpayment;

public class Card {

    int cardNumber;
    int verificationNumber;
    String owner;
    String expiryDate;
    int creditLimit;

    public Card(int cardNumber, int verificationNumber, String owner, String expiryDate, int creditLimit) {
        this.cardNumber = cardNumber;
        this.verificationNumber = verificationNumber;
        this.owner = owner;
        this.expiryDate = expiryDate;
        this.creditLimit = creditLimit;
    }

    public boolean transactionApproved(int transactionAmount) {
        return transactionAmount <= creditLimit;
    }

}
