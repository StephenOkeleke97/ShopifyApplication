package com.shopify.repository;

import org.springframework.data.repository.CrudRepository;

import com.shopify.model.Inventory;

public interface InventoryRepository extends CrudRepository<Inventory, Long> {
	
}
