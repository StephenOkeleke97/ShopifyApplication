package com.shopify.services;

import com.shopify.dto.InventoryDTO;
import com.shopify.model.Warehouse;
import java.util.List;

/**
 * Interface that provides a contract for handling business logic related to
 * warehouses.
 * 
 * @author stephen
 *
 */
public interface WarehouseService {
	/**
	 * Create a new warehouse and save to database.
	 * 
	 * @param name name of new warehouse
	 */
	public void createWarehouse(String name);

	/**
	 * Edit existing warehouse in database.
	 * 
	 * @param id   id of warehouse to be updated
	 * @param name name of warehouse to be updated
	 */
	public void editWarehouse(long id, String name);

	/**
	 * Delete warehouse from database.
	 * 
	 * @param id id of warehouse to be deleted
	 */
	public void deleteWarehouse(long id);

	/**
	 * Get all warehouses.
	 * 
	 * @return list of warehouses
	 */
	public Iterable<Warehouse> getWarehouses();

	/**
	 * Add new inventory to warehouse. This creates a new entry in the
	 * InventoryWarehouse table containing added inventory and selected warehouse.
	 * 
	 * @param id       id of warehouse to receive inventory
	 * @param quantity quantity of inventory to be added
	 * @param invId    id of inventory to be added
	 */
	public void addNewInvToWarehouse(long id, int quantity, long invId);

	/**
	 * Add existing inventory to warehouse. This creates a new entry in the
	 * InventoryWarehouse table containing already existing inventory and selected
	 * warehouse.
	 * 
	 * @param id       id of warehouse to receive inventory
	 * @param quantity quantity of inventory to be added
	 * @param invId    id of existing inventory to be added
	 */
	public void addExistingInvToWarehouse(long id, int quantity, long invId);

	/**
	 * Delete inventory from warehouse. This deletes a row in InventoryWarehouse
	 * table matching specified inventory and warehouse.
	 * 
	 * @param id    id of warehouse to delete inventory from
	 * @param invId id of inventory to be deleted from warehouse
	 */
	public void deleteInvFromWarehouse(long id, long invId);

	/**
	 * Increase quantity of inventory in warehouse.
	 * 
	 * @param id       id of warehouse in focus
	 * @param invId    id of inventory to be increased
	 * @param quantity quantity of increase
	 */
	public void increaseInvInWarehouse(long id, long invId, int quantity);

	/**
	 * Decrease quantity of inventory in warehouse.
	 * 
	 * @param id       id of warehouse in focus
	 * @param invId    id of inventory to be decreased
	 * @param quantity quantity of decrease
	 */
	public void decreaseInvInWarehouse(long id, long invId, int quantity);

	/**
	 * Get all inventory. This calculates and returns an aggregate of all inventory
	 * in all warehouses.
	 * 
	 * @return list of all inventory
	 */
	public List<InventoryDTO> getAllInv();

	/**
	 * Get inventory in specified warehouse.
	 * 
	 * @param id id of warehouse to retrieve inventory from
	 * @return list of inventory in specified warehouse
	 */
	public List<InventoryDTO> getAllInvByWarehouse(long id);
}
