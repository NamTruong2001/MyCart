package edu.hanu.mycart.models;

import java.util.Objects;


public class ProductDetail {
    private int id;
    private Product product;
    private int quantity;
    private int total;

    public ProductDetail(int id, Product product, int quantity, int total) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.total = total;
    }
    public ProductDetail() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductDetail that = (ProductDetail) o;
        return id == that.id && quantity == that.quantity && total == that.total && Objects.equals(product, that.product);
    }

    public boolean hasProduct(Product product) {
        if (this.product.equals(product)) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "ProductDetail{" +
                "id=" + id +
                ", product=" + product +
                ", quantity=" + quantity +
                ", total=" + total +
                '}';
    }
}
