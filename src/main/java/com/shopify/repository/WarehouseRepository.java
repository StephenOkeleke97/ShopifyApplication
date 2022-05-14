package com.shopify.repository;

import org.springframework.data.repository.CrudRepository;

import com.shopify.model.Warehouse;

public interface WarehouseRepository extends CrudRepository<Warehouse, Long> {
	Warehouse findByName(String name);
}
