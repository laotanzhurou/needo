package org.laotanzhurou.examples.cardpayment;

public class Order {

    Payment payment;
    Address address;

    public Order(Payment payment, Address address) {
        this.payment = payment;
        this.address = address;
    }

    public void processPayment() {
        processPayment();
        System.out.println("Payment Processed!");
        System.out.println("Shipment en route to:" + address);
    }

}
