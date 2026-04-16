package com.fooddelivery.ui.customer.cart.request;

import com.fooddelivery.model.Order;

public class CheckoutForm {

    private final String street;
    private final String area;
    private final String city;
    private final Order.PaymentMethod paymentMethod;
    private final String note;

    public CheckoutForm(String street,
                        String area,
                        String city,
                        Order.PaymentMethod paymentMethod,
                        String note) {
        this.street = street;
        this.area = area;
        this.city = city;
        this.paymentMethod = paymentMethod;
        this.note = note;
    }

    public String getStreet() {
        return street;
    }

    public String getArea() {
        return area;
    }

    public String getCity() {
        return city;
    }

    public Order.PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public String getNote() {
        return note;
    }
}