package org.laotanzhurou.examples.cardpayment;

public class Address {
    String address = "101 Jason Street";
    int postalNumber = 1001;

    public Address(String address, int postalNumber) {
        this.address = address;
        this.postalNumber = postalNumber;
    }

    @Override
    public String toString() {
        return this.address + ", " + postalNumber;
    }

}
