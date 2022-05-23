package com.shopify.repository;

import com.shopify.model.Inventory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface that represents a repository for the Inventory entity.
 * 
 * @author stephen
 *
 */
@Repository
public interface InventoryRepository extends CrudRepository<Inventory, Long> {
	/**
	 * Get inventory by inventory name.
	 * 
	 * @param name name of inventory to search for
	 * @return inventory or null if it does not exist
	 */
	Inventory findByInventoryName(String name);
}
