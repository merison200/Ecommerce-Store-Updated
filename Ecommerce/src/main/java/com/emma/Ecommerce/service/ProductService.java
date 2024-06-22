package com.emma.Ecommerce.service;

import com.emma.Ecommerce.api.model.ProductBody;
import com.emma.Ecommerce.model.Product;
import com.emma.Ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    public Product addProduct(ProductBody productBody) {
        Product product = new Product();

        product.setName(productBody.getName());
        product.setShortDescription(productBody.getShortDescription());
        product.setLongDescription(productBody.getLongDescription());
        product.setPrice(productBody.getPrice());
        return productRepository.save(product);
    }
}
