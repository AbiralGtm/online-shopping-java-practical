package com.abiralgautam.onlineshopping.models;

public class Product {
    private Integer id;
    private String name;
    private Double price;
    private String description;

    public Product(Integer id, String name, Double price, String description)
    {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
    }

    public Integer getId() { return id; }
    public String getName()
    {
        return name;
    }
    public String getDescription()
    {
        return description;
    }

    public Double getPrice() { return price; }


}
