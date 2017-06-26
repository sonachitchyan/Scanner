package com.example.asus.myapplication;


public class Data {
    private int count,article, count_db, price;
    private String barcode, code, name;


    public int getArticle() {
        return article;
    }

    public void setArticle(int article) {
        this.article = article;
    }

    public int getCount_db() {
        return count_db;
    }

    public void setCount_db(int count_db) {
        this.count_db = count_db;
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
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

    public Data(int count, String name, int price, int count_db) {
        this.count = count;
        this.name = name;
        this.price = price;
        this.count_db = count_db;
    }

    public Data() {
    }

    public Data(String name,  int article, String barcode,
                String code, int count,
                int count_db, int price) {
        this.count = count;
        this.article = article;
        this.count_db = count_db;
        this.barcode = barcode;
        this.code = code;
        this.name = name;
        this.price = price;
    }

    public Data(String name) {
        this.name = name;
    }
}
