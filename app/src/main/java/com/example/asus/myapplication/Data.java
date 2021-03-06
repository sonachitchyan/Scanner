package com.example.asus.myapplication;


public class Data {
    private double count;
    private double price, count_db;
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

    public double getCount() {
        return count;
    }

    public void setCount(double count) {
        this.count = count;
    }

    public double getCount_db() {
        return count_db;
    }

    public void setCount_db(double count_db) {
        this.count_db = count_db;
    }

    public Data(int count, String name, double price) {
        this.count = count;
        this.name = name;
        this.price = price;
    }

    public Data() {
    }

    public Data(double count, double price,  String barcode, String article, String code, String name,double count_db) {
        this.count = count;
        this.price = price;
        this.count_db = count_db;
        this.barcode = barcode;
        this.article = article;
        this.code = code;
        this.name = name;
    }

    public Data(String name, String  article, String barcode,
                String code, double count,
                double price, double count_db) {
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
