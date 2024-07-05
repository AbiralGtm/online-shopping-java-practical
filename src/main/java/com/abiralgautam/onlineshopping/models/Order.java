package com.abiralgautam.onlineshopping.models;

import java.util.Date;
import java.util.List;

public class Order {
    private int orderId;
    private String customerName;
    private String deliveryAddress;
    private String contactNumber;
    private String  orderDate;
    private List<OrderProduct> orderedProducts;

    // Constructor, getters, and setters
    public Order(int orderId, String customerName, String deliveryAddress, String contactNumber, String orderDate, List<OrderProduct> orderedProducts) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.deliveryAddress = deliveryAddress;
        this.contactNumber = contactNumber;
        this.orderDate = orderDate;
        this.orderedProducts = orderedProducts;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public List<OrderProduct> getOrderedProducts() {
        return orderedProducts;
    }

    public void setOrderedProducts(List<OrderProduct> orderedProducts) {
        this.orderedProducts = orderedProducts;
    }
}
