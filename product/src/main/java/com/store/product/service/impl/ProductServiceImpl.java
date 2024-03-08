package com.store.product.service.impl;

import org.springframework.stereotype.Service;

import com.store.product.domain.Product;
import com.store.product.repository.ProductRepository;
import com.store.product.service.ProductService;

@Service
public class ProductServiceImpl extends GenericServiceImpl<Product, Long, ProductRepository> implements ProductService {
	public ProductServiceImpl(ProductRepository repository) {
		super(repository);
	}
}
