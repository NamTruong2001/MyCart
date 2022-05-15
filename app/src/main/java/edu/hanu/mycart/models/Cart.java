package edu.hanu.mycart.models;

import java.util.ArrayList;
import java.util.List;

//nếu như đã có 1 productDetail chứa product này -> +1 quanitity cho product đó
public class Cart {
    private static Cart cart;
    private List<ProductDetail> productDetailList;

    private Cart() {
        productDetailList = new ArrayList<>();
    }

    public static Cart getInstance() {
        if (cart == null) {
            cart = new Cart();
        }

        return cart;
    }

    public List<ProductDetail> getProductDetailList() {
        return productDetailList;
    }

    public void setProductDetailList(List<ProductDetail> productDetailList) {
        this.productDetailList = productDetailList;
    }

    public ProductDetail hasProduct(Product product) {
        for (int i = 0; i < productDetailList.size(); i++) {
            if (productDetailList.get(i).hasProduct(product)) {
                return productDetailList.get(i);
            }
        }
        return null;
    }

    public int TinhTien() {
        int total = 0;
        for (int i = 0; i < productDetailList.size(); i++) {
            total += productDetailList.get(i).getQuantity() * productDetailList.get(i).getProduct().getUnitPrice();
        }
        return total;
    }

}
