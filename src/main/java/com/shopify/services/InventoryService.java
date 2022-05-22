package com.shopify.services;

import com.shopify.model.Inventory;

public interface InventoryService {
	public Inventory createInventory(String name, double price);
	
	public void updateInventory(long id, Double price, String name);

	public void deleteInventory(long id);
}
