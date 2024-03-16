package com.store.shoppingcart.repository;

import com.store.shoppingcart.domain.ShoppingCartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShoppingCartRepositoryItemRepository extends JpaRepository<ShoppingCartItem, Long> {
}
