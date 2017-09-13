package com.example.asus.myapplication;


public class Data {
    private int count;
    private double price;
    private String barcode, article, code, name;


    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Data(int count, String name, double price) {
        this.count = count;
        this.name = name;
        this.price = price;
    }

    public Data() {
    }

    public Data(String name,  String  article, String barcode,
                String code, int count,
                 double price) {
        this.count = count;
        this.article = article;
        this.barcode = barcode;
        this.code = code;
        this.name = name;
        this.price = price;
    }

    public Data(String name) {
        this.name = name;
    }
}
