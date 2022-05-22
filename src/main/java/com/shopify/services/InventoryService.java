package com.shopify.services;

import com.shopify.model.Inventory;
import com.shopify.model.Warehouse;
import java.util.List;

public interface InventoryService {
	public void createWarehouse(String name);

	public void editWarehouse(long id, String name);

	public void deleteWarehouse(long id);

	public Iterable<Warehouse> getWarehouses();

	public void createInventory(String name, double price, long id, int quantity);

	public void addInventoryToWarehouse(long warehouseId, long inventoryId, int quantity);

	public void deleteInventoryFromWarehouse(long warehouseId, long inventoryId);

	public void updateInventory(long id, Double price, String name);

	public void deleteInventory(long id);

	public void increaseInventoryInWarehouse(long warehouseId, long inventoryId, int quantity);

	public void decreaseInventoryInWarehouse(long warehouseId, long inventoryId, int quantity);

	public List<Inventory> getAllInventory();

	public List<Inventory> getAllInventoryByWarehouse(long warehouseId);
}
