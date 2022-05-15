package com.shopify.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shopify.dto.InventoryDTO;
import com.shopify.model.Inventory;
import com.shopify.model.InventoryWarehouse;
import com.shopify.model.Warehouse;

@Repository
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

	InventoryWarehouse findByInventoryAndWarehouse(Inventory inventory, Warehouse warehouse);

	@Query(value = "select inventory_id as id, inventory_name as name, "
			+ "sum(total_quantity) as quantity, price from inventory_warehouse left join inventory on inventory_id="
			+ "inventory_inventory_id  group by inventory_inventory_id", nativeQuery = true)
	List<InventoryDTO> findIdPriceQuantityGroupById();

	@Query(value = "select total_quantity as quantity, inventory_name as name, "
			+ "inventory_id as id, price from inventory_warehouse w left join inventory "
			+ "i on inventory_id = inventory_inventory_id left join warehouse"
			+ " on warehouse_id = warehouse_warehouse_id where warehouse_warehouse_id = :id", nativeQuery = true)
	List<InventoryDTO> findIdPriceQuantityByWarehouseId(@Param("id") long warehouseId);
}
