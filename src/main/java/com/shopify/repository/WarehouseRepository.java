package com.shopify.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.shopify.model.Warehouse;

@Repository
public interface WarehouseRepository extends CrudRepository<Warehouse, Long> {
	Warehouse findByWarehouseName(String name);
}
