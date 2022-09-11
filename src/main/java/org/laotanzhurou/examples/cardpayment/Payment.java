package org.laotanzhurou.examples.cardpayment;

public class Payment {

    Card card;
    int destinationAccount;
    int amount;

    public Payment(Card card, int destinationAccount, int amount) {
        this.card = card;
        this.destinationAccount = destinationAccount;
        this.amount = amount;
    }

    public void processPayment() {
        if(!this.card.transactionApproved(amount)) {
            throw new RuntimeException("Credit limit is less than transaction amount, rejected");
        }
    }
}
