package com.shopify.util;

import org.springframework.stereotype.Component;
import com.shopify.model.Warehouse;
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
		Warehouse warehouse = warehouseRepository.findByName(name);
		return warehouse == null;
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
}
