package com.shopify.util;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.shopify.dto.ResponseDTO;
import com.shopify.model.Inventory;
import com.shopify.model.InventoryWarehouse;
import com.shopify.model.Warehouse;
import com.shopify.repository.InventoryRepository;
import com.shopify.repository.InventoryWarehouseRepository;
import com.shopify.repository.WarehouseRepository;

@Component
public class Utility {

	@Autowired
	private WarehouseRepository warehouseRepository;

	@Autowired
	private InventoryRepository inventoryRepository;

	@Autowired
	private InventoryWarehouseRepository inventoryWarehouseRepository;

	/**
	 * Since warehouse entities must have unique name, this validates that a
	 * warehouse with the provided name does not exist in database.
	 * 
	 * @param name                name to validate
	 * @param warehouseRepository repository for location entity
	 * @return true if no warehouse with name exists or false otherwise
	 */
	public boolean validateWareHouseNameIsUnique(String name) {
		Warehouse warehouse = warehouseRepository.findByWarehouseName(name);
		return warehouse == null;
	}

	/**
	 * Since inventory entities must have unique name, this validates that an
	 * inventory with the provided name does not exist in database.
	 * 
	 * @param name                name to validate
	 * @param inventoryRepository repository for location entity
	 * @return true if no inventory with name exists or false otherwise
	 */
	public boolean validateInventoryNameIsUnique(String name) {
		Inventory inventory = inventoryRepository.findByInventoryName(name);
		return inventory == null;
	}

	/**
	 * Validates that string arguments are not empty.
	 * 
	 * @param args strings to be validated
	 * @return true of string not empty or false otherwise
	 */
	public boolean validateStringArgs(String... args) {
		for (String s : args) {
			if (s.trim().length() == 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Validates that a double input is non-negative.
	 * 
	 * @param number number to be validated
	 * @return true if number is valid or false otherwise
	 */
	public boolean validateDoubleMustBePositive(double number) {
		if (number < 0)
			return false;
		return true;
	}

	/**
	 * Validates that an integer input is non-negative.
	 * 
	 * @param number number to be validated
	 * @return true if number is valid or false otherwise
	 */
	public boolean validateIntMustBePositive(int number) {
		if (number < 0)
			return false;
		return true;
	}

	/**
	 * Generate appropriate response json messages and errors.
	 * 
	 * @param message  response message
	 * @param isError  response error state
	 * @param response response to be returned
	 * @return response dto object
	 */
	public ResponseDTO generateResponse(String message, boolean isError, ResponseDTO response) {
		response.setMessage(message);
		response.setError(isError);
		return response;
	}

	public boolean validateWarehouseExists(long id) {
		Warehouse warehouse = warehouseRepository.findById(id).orElse(null);
		return warehouse != null;
	}

	public boolean validateWarehouseNameCanBeUpdated(long id, String name) {
		Warehouse warehouse = warehouseRepository.findById(id).orElse(null);
		return validateWareHouseNameIsUnique(name) || name.equals(warehouse.getWarehouseName());
	}

	public boolean validateInventoryNameCanBeUpdated(long id, String name) {
		Inventory inventory = inventoryRepository.findById(id).orElse(null);
		return validateInventoryNameIsUnique(name) || name.equals(inventory.getInventoryName());
	}

	public boolean validateWarehouseInventoryCanBeDeleted(long id) {
		List<InventoryWarehouse> warehouseInventory = inventoryWarehouseRepository.findOneByWarehouse(id);
		if (warehouseInventory.size() > 0)
			return false;
		return true;
	}

	public boolean validateInventoryExists(long id) {
		Inventory inventory = inventoryRepository.findById(id).orElse(null);
		return inventory != null;
	}

	public boolean validateInventoryDoesNotExistInWarehouse(long invId, long warehouseId) {
		Inventory inventory = inventoryRepository.findById(invId).orElse(null);
		Warehouse warehouse = warehouseRepository.findById(warehouseId).orElse(null);
		InventoryWarehouse inventoryWarehouse = inventoryWarehouseRepository.findByInventoryAndWarehouse(inventory,
				warehouse);
		return inventoryWarehouse == null;
	}
}
