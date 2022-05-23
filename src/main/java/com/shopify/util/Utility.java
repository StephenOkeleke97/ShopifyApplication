package com.shopify.util;

import com.shopify.dto.ResponseDTO;
import com.shopify.model.Inventory;
import com.shopify.model.InventoryWarehouse;
import com.shopify.model.Warehouse;
import com.shopify.repository.InventoryRepository;
import com.shopify.repository.InventoryWarehouseRepository;
import com.shopify.repository.WarehouseRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Class that represents a utility with methods that perform tasks such as
 * validation of end point parameters.
 * 
 * @author stephen
 *
 */
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

	/**
	 * Validates that a warehouse exists.
	 * 
	 * @param id id of warehouse to be validated
	 * @return true if warehouse exists or false otherwise
	 */
	public boolean validateWarehouseExists(long id) {
		Warehouse warehouse = warehouseRepository.findById(id).orElse(null);
		return warehouse != null;
	}

	/**
	 * Validates if warehouse name can be updated. Since warehouse names are unique,
	 * no duplicates are allowed. However a warehouse can update its name to the
	 * same value.
	 * 
	 * @param id   id of warehouse to be validated
	 * @param name name of warehouse to be validated
	 * @return true if warehouse name can be updated or false otherwise
	 */
	public boolean validateWarehouseNameCanBeUpdated(long id, String name) {
		Warehouse warehouse = warehouseRepository.findById(id).orElse(null);
		return validateWareHouseNameIsUnique(name) || name.equals(warehouse.getWarehouseName());
	}

	/**
	 * Validates if inventory name can be updated. Since inventory names are unique,
	 * no duplicates are allowed. However an inventory item can update its name to
	 * the same value.
	 * 
	 * @param id   id of inventory to be validated
	 * @param name name of inventory to be validated
	 * @return true if inventory name can be updated or false otherwise
	 */
	public boolean validateInventoryNameCanBeUpdated(long id, String name) {
		Inventory inventory = inventoryRepository.findById(id).orElse(null);
		return validateInventoryNameIsUnique(name) || name.equals(inventory.getInventoryName());
	}

	/**
	 * Validates if warehouse can be deleted. Warehouses that contain inventory
	 * cannot be deleted and must be cleared first.
	 * 
	 * @param id id of warehouse to be validated
	 * @return true if warehouse can be deleted or false otherwise
	 */
	public boolean validateWarehouseInventoryCanBeDeleted(long id) {
		List<InventoryWarehouse> warehouseInventory = inventoryWarehouseRepository.findOneByWarehouse(id);
		if (warehouseInventory.size() > 0)
			return false;
		return true;
	}

	/**
	 * Validates if inventory exists.
	 * 
	 * @param id id of inventory to be validated
	 * @return true if inventory exists or false otherwise
	 */
	public boolean validateInventoryExists(long id) {
		Inventory inventory = inventoryRepository.findById(id).orElse(null);
		return inventory != null;
	}

	/**
	 * Validates that inventory does not exist in warehouse. Inventory already
	 * contained in warehouse cannot be added. It can however be increased, reduced
	 * or removed.
	 * 
	 * @param invId       id of inventory to be validated
	 * @param warehouseId id of warehouse to validate inventory based upon
	 * @return true if inventory does not exist in warehouse or false otherwise
	 */
	public boolean validateInventoryDoesNotExistInWarehouse(long invId, long warehouseId) {
		Inventory inventory = inventoryRepository.findById(invId).orElse(null);
		Warehouse warehouse = warehouseRepository.findById(warehouseId).orElse(null);
		InventoryWarehouse inventoryWarehouse = inventoryWarehouseRepository.findByInventoryAndWarehouse(inventory,
				warehouse);
		return inventoryWarehouse == null;
	}
}
