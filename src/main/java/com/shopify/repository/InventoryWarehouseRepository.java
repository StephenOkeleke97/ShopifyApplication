package com.shopify.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import com.shopify.model.InventoryWarehouse;

public interface InventoryWarehouseRepository extends CrudRepository<InventoryWarehouse, Long> {
	/**
	 * Method used to validate delete request. To ensure all inventory is accounted
	 * for, a warehouse with inventory cannot be deleted. If returned list is not
	 * empty, warehouse cannot be deleted.
	 * 
	 * @param id id of warehouse to be validated
	 * @return list of inventory associated with warehouse with only one item
	 */
	@Query(value = "select * from inventory_warehouse where warehouse_warehouse_id = :id limit 1", nativeQuery = true)
	List<InventoryWarehouse> findOneByWarehouse(@Param("id") long id);
}
