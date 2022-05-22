package com.shopify.controller;

import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.shopify.dto.ResponseDTO;
import com.shopify.model.Inventory;
import com.shopify.services.InventoryService;
import com.shopify.services.WarehouseService;
import com.shopify.util.Utility;

@RestController
@RequestMapping("api/v1/")
public class Controller {

	@Autowired
	private InventoryService inventoryService;

	@Autowired
	private WarehouseService warehouseService;

	@Autowired
	Utility utility;

	/**
	 * Add new warehouse to database.
	 * 
	 * @param name     name of warehouse to be added
	 * @param response {@link HttpServletResponse}
	 * @return result of action
	 */
	@PostMapping("/warehouse")
	public @ResponseBody ResponseDTO createWarehouse(@RequestParam String name, HttpServletResponse response) {
		ResponseDTO result = new ResponseDTO();

		if (!utility.validateStringArgs(name)) {
			response.setStatus(400);
			return utility.generateResponse("Invalid name", true, result);
		}

		if (!utility.validateWareHouseNameIsUnique(name)) {
			response.setStatus(400);
			return utility.generateResponse("There is already a warehouse with this name.", true, result);
		}

		warehouseService.createWarehouse(name);

		return utility.generateResponse("Warehouse successfully added.", false, result);
	}

	/**
	 * Update warehouse name.
	 * 
	 * @param id       id of warehouse to be updated
	 * @param name     new name of warehouse
	 * @param response {@link HttpServletResponse}
	 * @return result of action
	 */
	@PutMapping("/warehouse/{id}")
	public @ResponseBody ResponseDTO editWarehouse(@PathVariable long id, @RequestParam String name,
			HttpServletResponse response) {
		ResponseDTO result = new ResponseDTO();

		if (!utility.validateStringArgs(name)) {
			response.setStatus(400);
			return utility.generateResponse("Invalid name.", true, result);
		}

		if (!utility.validateWarehouseExists(id)) {
			response.setStatus(400);
			return utility.generateResponse("Warehouse does not exist", true, result);
		}

		if (!utility.validateWarehouseNameCanBeUpdated(id, name)) {
			response.setStatus(400);
			return utility.generateResponse("There is already a warehouse with this name.", true, result);
		}

		warehouseService.editWarehouse(id, name);

		return utility.generateResponse("Warehouse successfully updated", false, result);
	}

	/**
	 * Delete warehouse.
	 * 
	 * @param id       id of warehouse to be deleted
	 * @param response {@link HttpServletResponse}
	 * @return result of action
	 */
	@DeleteMapping("/warehouse/{id}")
	public @ResponseBody ResponseDTO deleteWarehouse(@PathVariable long id, HttpServletResponse response) {
		ResponseDTO result = new ResponseDTO();

		if (utility.validateWarehouseExists(id)) {
			if (!utility.validateWarehouseInventoryCanBeDeleted(id)) {
				response.setStatus(400);
				String message = "A warehouse with live inventory cannot be deleted. "
						+ "Please delete or transfer all inventory associated with this warehouse.";
				return utility.generateResponse(message, true, result);
			}
			warehouseService.deleteWarehouse(id);
		}
		return utility.generateResponse("Warehouse successfully deleted", false, result);
	}

	/**
	 * Get all warehouses.
	 * 
	 * @return list of warehouses
	 */
	@GetMapping("/warehouse")
	public @ResponseBody ResponseDTO getWarehouse() {
		ResponseDTO result = new ResponseDTO("Success", false);
		result.setData(warehouseService.getWarehouses());
		return result;
	}

//
	/**
	 * Creates new inventory and adds to "None" warehouse. Which means that
	 * inventory is unassigned. If "None" warehouse does not exist, it is created.
	 * 
	 * @param name        name of new inventory
	 * @param price       price of new inventory
	 * @param warehouseId id of warehouse to store inventory
	 * @param quantity    quantity to be added
	 * @param response    {@link HttpServletResponse}
	 * @return result of action
	 */
	@PostMapping("/inventory")
	public @ResponseBody ResponseDTO createInventory(@RequestParam String name, @RequestParam double price,
			@RequestParam(required = false, defaultValue = "1") long warehouseId, @RequestParam int quantity,
			HttpServletResponse response) {
		ResponseDTO result = new ResponseDTO();

		if (!utility.validateStringArgs(name) || !utility.validateDoubleMustBePositive(price)
				|| !utility.validateIntMustBePositive(quantity)) {
			response.setStatus(400);
			return utility.generateResponse("Invalid inventory name, quantity or price", true, result);
		}

		if (!utility.validateInventoryNameIsUnique(name)) {
			response.setStatus(400);
			return utility.generateResponse("Inventory with this name already exists", true, result);
		}

		if (!utility.validateWarehouseExists(warehouseId)) {
			response.setStatus(400);
			return utility.generateResponse("Warehouse does not exist", true, result);
		}

		Inventory inventory = inventoryService.createInventory(name, price);
		warehouseService.addNewInvToWarehouse(warehouseId, quantity, inventory.getInventoryId());

		return utility.generateResponse("Inventory successfully created", false, result);
	}

	/**
	 * Adds inventory to warehouse if it does not exist already.
	 * 
	 * @param warehouseId id of warehouse receiving inventory
	 * @param inventoryId id of inventory to be added
	 * @param quantity    quantity of inventory to be added
	 * @param response    {@link HttpServletResponse}
	 * @return result of action
	 */
	@PutMapping("/inventory/{inventoryId}/warehouse/{warehouseId}/{quantity}")
	public @ResponseBody ResponseDTO addInventoryToWarehouse(@PathVariable long warehouseId,
			@PathVariable long inventoryId, @PathVariable int quantity, HttpServletResponse response) {
		ResponseDTO result = new ResponseDTO();

		if (!utility.validateIntMustBePositive(quantity)) {
			response.setStatus(400);
			return utility.generateResponse("Invalid quantity", true, result);
		}

		if (!utility.validateWarehouseExists(warehouseId)) {
			response.setStatus(400);
			return utility.generateResponse("Warehouse does not exist", true, result);
		}

		if (!utility.validateInventoryExists(inventoryId)) {
			response.setStatus(400);
			return utility.generateResponse("Inventory does not exist", true, result);
		}

		if (!utility.validateInventoryDoesNotExistInWarehouse(inventoryId, warehouseId)) {
			response.setStatus(400);
			return utility.generateResponse("This inventory already exists in this warehouse", true, result);
		}

		warehouseService.addExistingInvToWarehouse(warehouseId, quantity, inventoryId);
		return utility.generateResponse("Inventory successfully created in warehouse", false, result);
	}

	/**
	 * Delete inventory from warehouse.
	 * 
	 * @param warehouseId id of warehouse to delete inventory from
	 * @param inventoryId id of inventory to be deleted
	 * @param response    {@link HttpServletResponse}
	 * @return result of action
	 */
	@DeleteMapping("/inventory/{inventoryId}/warehouse/{warehouseId}")
	public @ResponseBody ResponseDTO removeInventoryFromWarehouse(@PathVariable long warehouseId,
			@PathVariable long inventoryId, HttpServletResponse response) {
		ResponseDTO result = new ResponseDTO();

		if (!utility.validateWarehouseExists(warehouseId)) {
			response.setStatus(400);
			return utility.generateResponse("Warehouse does not exist", true, result);
		}

		if (!utility.validateInventoryExists(inventoryId)) {
			response.setStatus(400);
			return utility.generateResponse("Inventory does not exist", true, result);
		}

		if (utility.validateInventoryDoesNotExistInWarehouse(inventoryId, warehouseId)) {
			response.setStatus(400);
			return utility.generateResponse("This inventory does not exist in this warehouse", true, result);
		}

		warehouseService.deleteInvFromWarehouse(warehouseId, inventoryId);

		return utility.generateResponse("Inventory successfully deleted from warehouse", false, result);
	}

	/**
	 * Update inventory.
	 * 
	 * @param id       id of inventory to be updated
	 * @param price    new price of inventory
	 * @param name     new name of inventory
	 * @param response {@link HttpServletResponse}
	 * @return result of action
	 */
	@PutMapping("/inventory/{id}")
	public @ResponseBody ResponseDTO updateInventory(@PathVariable long id, Double price, String name,
			HttpServletResponse response) {
		ResponseDTO result = new ResponseDTO();
		if (!utility.validateInventoryExists(id)) {
			response.setStatus(400);
			return utility.generateResponse("Inventory item does not exist", true, result);
		}

		if (name != null) {
			if (!utility.validateStringArgs(name)) {
				response.setStatus(400);
				return utility.generateResponse("Invalid name", true, result);
			}
		}

		if (price != null) {
			if (!utility.validateDoubleMustBePositive(price)) {
				response.setStatus(400);
				return utility.generateResponse("Invalid Price", true, result);
			}
		}

		if (!utility.validateInventoryNameCanBeUpdated(id, name)) {
			response.setStatus(400);
			return utility.generateResponse("Inventory with this name already exists", true, result);
		}

		inventoryService.updateInventory(id, price, name);
		return utility.generateResponse("Inventory successfully created", false, result);
	}

	/**
	 * Deletes inventory with id specified.
	 * 
	 * @param id       id of inventory to be deleted
	 * @param response {@link HttpServletResponse}
	 * @return result of action
	 */
	@DeleteMapping("/inventory/{id}")
	public ResponseDTO deleteInventory(@PathVariable long id, HttpServletResponse response) {
		ResponseDTO result = new ResponseDTO();
		if (!utility.validateInventoryExists(id)) {
			result.setError(true);
			result.setMessage("Inventory item does not exist");
			response.setStatus(400);
			return result;
		}
		inventoryService.deleteInventory(id);
		result.setMessage("Inventory successfully deleted");
		return result;
	}

	/**
	 * Increase size of inventory in warehouse.
	 * 
	 * @param warehouseId target warehouse
	 * @param inventoryId target inventory
	 * @param quantity    quantity to increase by
	 * @param response    {@link HttpServletResponse}
	 * @return result of action
	 */
	@PutMapping("/inventory/{inventoryId}/warehouse/{warehouseId}/add/{quantity}")
	public @ResponseBody ResponseDTO increaseInventoryQuantity(@PathVariable long warehouseId,
			@PathVariable long inventoryId, @PathVariable int quantity, HttpServletResponse response) {
		ResponseDTO result = new ResponseDTO();

		if (!utility.validateIntMustBePositive(quantity)) {
			response.setStatus(400);
			return utility.generateResponse("Quantity must be positive", true, result);
		}

		if (!utility.validateInventoryExists(inventoryId)) {
			response.setStatus(400);
			return utility.generateResponse("Inventory does not exist", true, result);
		}

		if (!utility.validateWarehouseExists(warehouseId)) {
			response.setStatus(400);
			return utility.generateResponse("Warehouse does not exist", true, result);
		}

		if (utility.validateInventoryDoesNotExistInWarehouse(inventoryId, warehouseId)) {
			response.setStatus(400);
			return utility.generateResponse("This inventory does not exist in this warehouse", true, result);
		}

		warehouseService.increaseInvInWarehouse(warehouseId, inventoryId, quantity);
		return utility.generateResponse("Inventory quantity successfully increased", false, result);
	}

	/**
	 * Decrease size of inventory in warehouse.
	 * 
	 * @param warehouseId target warehouse
	 * @param inventoryId target inventory
	 * @param quantity    quantity to decrease by
	 * @param response    {@link HttpServletResponse}
	 * @return result of action
	 */
	@PutMapping("/inventory/{inventoryId}/warehouse/{warehouseId}/remove/{quantity}")
	public @ResponseBody ResponseDTO decreaseInventoryQuantity(@PathVariable long warehouseId,
			@PathVariable long inventoryId, @PathVariable int quantity, HttpServletResponse response) {
		ResponseDTO result = new ResponseDTO();

		if (!utility.validateIntMustBePositive(quantity)) {
			response.setStatus(400);
			return utility.generateResponse("Quantity must be positive", true, result);
		}

		if (!utility.validateInventoryExists(inventoryId)) {
			response.setStatus(400);
			return utility.generateResponse("Inventory does not exist", true, result);
		}

		if (!utility.validateWarehouseExists(warehouseId)) {
			response.setStatus(400);
			return utility.generateResponse("Warehouse does not exist", true, result);
		}

		if (utility.validateInventoryDoesNotExistInWarehouse(inventoryId, warehouseId)) {
			response.setStatus(400);
			return utility.generateResponse("This inventory does not exist in this warehouse", true, result);
		}

		warehouseService.decreaseInvInWarehouse(warehouseId, inventoryId, quantity);
		return utility.generateResponse("Inventory quantity successfully decreased", false, result);
	}

	/**
	 * Get total inventories in all warehouses.
	 * 
	 * @return inventory as data in response dto
	 */
	@GetMapping("/inventory")
	public @ResponseBody ResponseDTO getAllInventory() {
		ResponseDTO result = new ResponseDTO("Success", false);

		result.setData(warehouseService.getAllInv());
		return result;
	}

	/**
	 * Get inventory by warehouse.
	 * 
	 * @param warehouseId target warehouse
	 * @param response    {@link HttpServletResponse}
	 * @return result of action
	 */
	@GetMapping("/inventory/{warehouseId}")
	public @ResponseBody ResponseDTO getInventoryByWarehouse(@PathVariable long warehouseId,
			HttpServletResponse response) {
		ResponseDTO result = new ResponseDTO("Success", false);

		if (warehouseId < 0) {
			response.setStatus(400);
			return utility.generateResponse("Warehouse ID must be positive", true, result);
		}

		if (!utility.validateWarehouseExists(warehouseId)) {
			response.setStatus(400);
			return utility.generateResponse("Warehouse does not exist", true, result);
		}

		result.setData(warehouseService.getAllInvByWarehouse(warehouseId));
		return result;
	}
}
