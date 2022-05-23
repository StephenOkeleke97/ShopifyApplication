package com.shopify.services;

import com.shopify.model.Inventory;

/**
 * Interface that provides a contract for handling business logic related to
 * inventory.
 * 
 * @author stephen
 *
 */
public interface InventoryService {
	/**
	 * Create new inventory and persist in database.
	 * 
	 * @param name  name of inventory to be created
	 * @param price price of inventory to be created
	 * @return the newly created inventory
	 */
	public Inventory createInventory(String name, double price);

	/**
	 * Update existing inventory item in database.
	 * 
	 * @param id    id of inventory to be updated
	 * @param price new price of inventory
	 * @param name  new name of inventory
	 */
	public void updateInventory(long id, Double price, String name);

	/**
	 * Delete inventory from database. This also deletes all entries with this
	 * inventory in the InventoryWarehouse table.
	 * 
	 * @param id id of inventory to be deleted
	 */
	public void deleteInventory(long id);
}
