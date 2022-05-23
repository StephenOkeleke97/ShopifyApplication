package com.shopify.repository;

import com.shopify.model.Warehouse;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface that represents a repository for the Warehouse entity.
 * 
 * @author stephen
 *
 */
@Repository
public interface WarehouseRepository extends CrudRepository<Warehouse, Long> {
	/**
	 * Get warehouse by warehouse name.
	 * 
	 * @param name name of warehouse to search for
	 * @return warehouse or null if it does not exist
	 */
	Warehouse findByWarehouseName(String name);
}
