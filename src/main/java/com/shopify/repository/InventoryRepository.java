package com.shopify.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.shopify.model.Inventory;

@Repository
public interface InventoryRepository extends CrudRepository<Inventory, Long> {
	Inventory findByInventoryName(String name);
}
