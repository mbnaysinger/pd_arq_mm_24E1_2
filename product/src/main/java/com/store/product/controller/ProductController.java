package com.store.product.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.product.domain.Product;
import com.store.product.service.ProductService;

@RestController
@RequestMapping("api/product")
public class ProductController extends GenericController<Product> {
	public ProductController(ProductService service) {
		super(service);
	}
}
