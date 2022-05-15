package com.shopify.util;

import org.springframework.stereotype.Component;

import com.shopify.dto.ResponseDTO;
import com.shopify.model.Inventory;
import com.shopify.model.Warehouse;
import com.shopify.repository.InventoryRepository;
import com.shopify.repository.WarehouseRepository;

@Component
public class Utility {

	private Utility() {

	}

	/**
	 * Since warehouse entities must have unique name, this validates that a
	 * warehouse with the provided name does not exist in database.
	 * 
	 * @param name                name to validate
	 * @param warehouseRepository repository for location entity
	 * @return true if no warehouse with name exists or false otherwise
	 */
	public static boolean validateWareHouseNameIsUnique(String name, WarehouseRepository warehouseRepository) {
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
	public static boolean validateInventoryNameIsUnique(String name, InventoryRepository inventoryRepository) {
		Inventory inventory = inventoryRepository.findByInventoryName(name);
		return inventory == null;
	}

	/**
	 * Validates that string arguments are not empty.
	 * 
	 * @param args strings to be validated
	 * @return true of string not empty or false otherwise
	 */
	public static boolean validateStringArgs(String... args) {
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
	public static boolean validateDoubleMustBePositive(double number) {
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
	public static boolean validateIntMustBePositive(int number) {
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
	public static ResponseDTO generateResponse(String message, boolean isError, ResponseDTO response) {
		response.setMessage(message);
		response.setError(isError);
		return response;
	}
}
