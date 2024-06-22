package com.emma.Ecommerce.api.controller.product;

import com.emma.Ecommerce.api.model.ProductBody;
import com.emma.Ecommerce.model.Product;
import com.emma.Ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    /*This fetches all the products available in our database. no need to login to use this api,
    This api is whitelisted, so anyone can access it when he or she visit our website.
    https://localhost/8080/product
    */
    @GetMapping
    public List<Product> getProducts() {
        return productService.getProducts();
    }

    @PostMapping("/add")
    public ResponseEntity<Product> addProduct(@RequestBody ProductBody productBody) {

        Product product = productService.addProduct(productBody);
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }
}
