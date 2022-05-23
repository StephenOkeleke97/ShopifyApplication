package com.shopify.services;

import com.shopify.model.Inventory;
import com.shopify.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Class that represents an implementation of the InventoryService interface.
 * 
 * @author stephen
 *
 */
@Service
public class InventoryServiceImpl implements InventoryService {

	@Autowired
	private InventoryRepository inventoryRepository;

	@Override
	public Inventory createInventory(String name, double price) {
		Inventory inventory = new Inventory(name, price);
		inventoryRepository.save(inventory);
		return inventory;
	}

	@Override
	public void updateInventory(long id, Double price, String name) {
		Inventory inv = inventoryRepository.findById(id).orElse(null);
		if (name != null) {
			inv.setInventoryName(name);
		}
		if (price != null) {
			inv.setPrice(price);
		}
		inventoryRepository.save(inv);

	}

	@Override
	public void deleteInventory(long id) {
		Inventory inventory = inventoryRepository.findById(id).orElse(null);
		inventoryRepository.delete(inventory);
	}
}
