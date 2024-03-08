package com.store.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.store.product.domain.Product;

public interface ProductRepository extends JpaRepository<Product, Long>{

}
