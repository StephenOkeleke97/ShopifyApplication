package com.shopify.controller;

import java.util.List;
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
import com.shopify.model.InventoryWarehouse;
import com.shopify.model.InventoryWarehouseId;
import com.shopify.model.Warehouse;
import com.shopify.repository.InventoryRepository;
import com.shopify.repository.InventoryWarehouseRepository;
import com.shopify.repository.WarehouseRepository;
import com.shopify.util.Utility;

@RestController
@RequestMapping("api/v1/")
public class Controller {
	@Autowired
	private WarehouseRepository warehouseRepository;

	@Autowired
	private InventoryWarehouseRepository inventoryWarehouseRepository;

	@Autowired
	private InventoryRepository inventoryRepository;

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

		if (!Utility.validateStringArgs(name)) {
			response.setStatus(400);
			return Utility.generateResponse("Invalid name", true, result);
		}

		if (!Utility.validateWareHouseNameIsUnique(name, warehouseRepository)) {
			response.setStatus(400);
			return Utility.generateResponse("There is already a warehouse with this name.", true, result);
		}

		Warehouse warehouse = new Warehouse(name);
		warehouseRepository.save(warehouse);

		return Utility.generateResponse("Warehouse successfully added.", false, result);
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

		if (!Utility.validateStringArgs(name)) {
			response.setStatus(400);
			return Utility.generateResponse("Invalid name.", true, result);
		}

		if (!Utility.validateWareHouseNameIsUnique(name, warehouseRepository)) {
			response.setStatus(400);
			return Utility.generateResponse("There is already a warehouse with this name.", true, result);
		}

		Warehouse warehouse = warehouseRepository.findById(id).orElse(null);

		if (warehouse == null) {
			response.setStatus(400);
			return Utility.generateResponse("Warehouse does not exist", true, result);
		}

		warehouse.setWarehouseName(name);
		warehouseRepository.save(warehouse);

		return Utility.generateResponse("Warehouse successfully updated", false, result);
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

		Warehouse warehouse = warehouseRepository.findById(id).orElse(null);

		if (warehouse != null) {
			List<InventoryWarehouse> warehouseInventory = inventoryWarehouseRepository.findOneByWarehouse(id);
			if (warehouseInventory.size() > 0) {
				response.setStatus(400);
				String message = "A warehouse with live inventory cannot be deleted. "
						+ "Please delete or transfer all inventory associated with this warehouse.";
				return Utility.generateResponse(message, true, result);
			}

			warehouseRepository.delete(warehouse);
		}

		return Utility.generateResponse("Warehouse successfully deleted", false, result);
	}

	/**
	 * Get all warehouses.
	 * 
	 * @return list of warehouses
	 */
	@GetMapping("/warehouse")
	public @ResponseBody ResponseDTO getWarehouse() {
		ResponseDTO result = new ResponseDTO("Success", false);
		result.setData(warehouseRepository.findAll());
		return result;
	}

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
			@RequestParam long warehouseId, @RequestParam int quantity, HttpServletResponse response) {
		ResponseDTO result = new ResponseDTO();

		if (!Utility.validateStringArgs(name) || !Utility.validateDoubleMustBePositive(price)
				|| !Utility.validateIntMustBePositive(quantity)) {
			response.setStatus(400);
			return Utility.generateResponse("Invalid inventory name, quantity or price", true, result);
		}

		if (!Utility.validateInventoryNameIsUnique(name, inventoryRepository)) {
			response.setStatus(400);
			return Utility.generateResponse("Inventory with this name already exists", true, result);
		}

		Warehouse warehouse = warehouseRepository.findById(warehouseId).orElse(null);
		if (warehouse == null) {
			response.setStatus(400);
			return Utility.generateResponse("Warehouse does not exist", true, result);
		}

		Inventory inventory = new Inventory(name, price);
		inventoryRepository.save(inventory);
		InventoryWarehouseId compositeKey = new InventoryWarehouseId(warehouse, inventory);
		InventoryWarehouse inventoryWarehouse = new InventoryWarehouse(compositeKey, quantity);
		inventoryWarehouseRepository.save(inventoryWarehouse);

		return Utility.generateResponse("Inventory successfully created", false, result);
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

		if (!Utility.validateIntMustBePositive(quantity)) {
			response.setStatus(400);
			return Utility.generateResponse("Invalid quantity", true, result);
		}

		Warehouse warehouse = warehouseRepository.findById(warehouseId).orElse(null);
		if (warehouse == null) {
			response.setStatus(400);
			return Utility.generateResponse("Warehouse does not exist", true, result);
		}

		Inventory inventory = inventoryRepository.findById(inventoryId).orElse(null);

		if (inventory == null) {
			response.setStatus(400);
			return Utility.generateResponse("Inventory does not exist", true, result);
		}

		InventoryWarehouse inventoryWarehouse = inventoryWarehouseRepository.findByInventoryAndWarehouse(inventory,
				warehouse);

		if (inventoryWarehouse != null) {
			response.setStatus(400);
			return Utility.generateResponse("This inventory already exists in this warehouse", true, result);
		}

		InventoryWarehouseId compositeKey = new InventoryWarehouseId(warehouse, inventory);
		inventoryWarehouse = new InventoryWarehouse(compositeKey, quantity);
		inventoryWarehouseRepository.save(inventoryWarehouse);

		return Utility.generateResponse("Inventory successfully created in warehouse", false, result);
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
		Inventory inventory = inventoryRepository.findById(id).orElse(null);
		if (inventory == null) {
			response.setStatus(400);
			return Utility.generateResponse("Inventory item does not exist", true, result);
		}

		if (name != null) {
			if (!Utility.validateStringArgs(name)) {
				response.setStatus(400);
				return Utility.generateResponse("Invalid name", true, result);
			}
			inventory.setInventoryName(name);
		}

		if (price != null) {
			if (!Utility.validateDoubleMustBePositive(price)) {
				response.setStatus(400);
				return Utility.generateResponse("Invalid Price", true, result);
			}
			inventory.setPrice(price);
		}

		if (!Utility.validateInventoryNameIsUnique(name, inventoryRepository)) {
			response.setStatus(400);
			return Utility.generateResponse("Inventory with this name already exists", true, result);
		}

		inventoryRepository.save(inventory);
		return Utility.generateResponse("Inventory successfully created", false, result);
	}

	/**
	 * Moves inventory from one warehouse to another.
	 * 
	 * @param inventoryId    id of inventory to be moved
	 * @param newWarehouseId id of destination warehouse
	 * @param oldWarehouseId id of source warehouse
	 * @param quantity       quantity of inventory to be removed
	 * @param response       {@link HttpServletResponse}
	 * @return result of action
	 */
	@PutMapping("/inventory/{inventoryId}/warehouse/{oldWarehouseId}/{newWarehouseId}/{quantity}")
	public @ResponseBody ResponseDTO moveInventoryToWarehouseFromAnother(@PathVariable long inventoryId,
			@PathVariable long newWarehouseId, @PathVariable long oldWarehouseId, @PathVariable int quantity,
			HttpServletResponse response) {
		ResponseDTO result = new ResponseDTO();

		if (!Utility.validateIntMustBePositive(quantity)) {
			response.setStatus(400);
			return Utility.generateResponse("Quantity must be positive", true, result);
		}

		Inventory inventory = inventoryRepository.findById(inventoryId).orElse(null);
		Warehouse newWarehouse = warehouseRepository.findById(newWarehouseId).orElse(null);
		Warehouse oldWarehouse = warehouseRepository.findById(oldWarehouseId).orElse(null);

		// Validate that inventory exists
		if (inventory == null) {
			response.setStatus(400);
			return Utility.generateResponse("Inventory does not exist", true, result);
		}

		// Validate that source or destination exists
		if (newWarehouse == null || oldWarehouse == null) {
			response.setStatus(400);
			return Utility.generateResponse("Invalid source or destination warehouse", true, result);
		}

		InventoryWarehouse inventoryWarehouse = inventoryWarehouseRepository.findByInventoryAndWarehouse(inventory,
				oldWarehouse);

		// Validate that inventory is currently being stored in source warehouse
		if (inventoryWarehouse == null) {
			response.setStatus(400);
			return Utility.generateResponse("Inventory does not exist in selected warehouse", true, result);
		}

		// Validate that inventory in source is up to the requested quantity
		if (inventoryWarehouse.getTotalQuantity() < quantity) {
			response.setStatus(400);
			return Utility.generateResponse("Insufficient amount of inventory", true, result);
		}

		InventoryWarehouse newInventoryWarehouse = inventoryWarehouseRepository.findByInventoryAndWarehouse(inventory,
				newWarehouse);

		// If inventory is not stored in destination warehouse, create new record for it
		if (newInventoryWarehouse == null) {
			InventoryWarehouseId id = new InventoryWarehouseId(newWarehouse, inventory);
			newInventoryWarehouse = new InventoryWarehouse(id, 0);
			inventoryWarehouseRepository.save(newInventoryWarehouse);
		}

		int qtyNewWarehouse = newInventoryWarehouse.getTotalQuantity() + quantity;
		int qtyOldWarehouse = inventoryWarehouse.getTotalQuantity() - quantity;

		newInventoryWarehouse.setTotalQuantity(qtyNewWarehouse);
		inventoryWarehouse.setTotalQuantity(qtyOldWarehouse);

		inventoryWarehouseRepository.save(newInventoryWarehouse);
		inventoryWarehouseRepository.save(inventoryWarehouse);

		return Utility.generateResponse("Inventory successfully moved", false, result);
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
		Inventory inventory = inventoryRepository.findById(id).orElse(null);

		if (inventory == null) {
			result.setError(true);
			result.setMessage("Inventory item does not exist");
			response.setStatus(400);
			return result;
		}

		inventoryRepository.delete(inventory);
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

		if (!Utility.validateIntMustBePositive(quantity)) {
			response.setStatus(400);
			return Utility.generateResponse("Quantity must be positive", true, result);
		}

		Inventory inventory = inventoryRepository.findById(inventoryId).orElse(null);
		Warehouse warehouse = warehouseRepository.findById(warehouseId).orElse(null);

		if (inventory == null) {
			response.setStatus(400);
			return Utility.generateResponse("Inventory does not exist", true, result);
		}

		if (warehouse == null) {
			response.setStatus(400);
			return Utility.generateResponse("Warehouse does not exist", true, result);
		}

		InventoryWarehouse inventoryWarehouse = inventoryWarehouseRepository.findByInventoryAndWarehouse(inventory,
				warehouse);

		if (inventoryWarehouse == null) {
			response.setStatus(400);
			return Utility.generateResponse("This inventory does not exist in this warehouse", true, result);
		}

		int newQuantity = inventoryWarehouse.getTotalQuantity() + quantity;
		inventoryWarehouse.setTotalQuantity(newQuantity);
		inventoryWarehouseRepository.save(inventoryWarehouse);

		return Utility.generateResponse("Inventory quantity successfully increased", false, result);
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

		if (!Utility.validateIntMustBePositive(quantity)) {
			response.setStatus(400);
			return Utility.generateResponse("Quantity must be positive", true, result);
		}

		Inventory inventory = inventoryRepository.findById(inventoryId).orElse(null);
		Warehouse warehouse = warehouseRepository.findById(warehouseId).orElse(null);

		if (inventory == null) {
			response.setStatus(400);
			return Utility.generateResponse("Inventory does not exist", true, result);
		}

		if (warehouse == null) {
			response.setStatus(400);
			return Utility.generateResponse("Warehouse does not exist", true, result);
		}

		InventoryWarehouse inventoryWarehouse = inventoryWarehouseRepository.findByInventoryAndWarehouse(inventory,
				warehouse);

		if (inventoryWarehouse == null) {
			response.setStatus(400);
			return Utility.generateResponse("This inventory does not exist in this warehouse", true, result);
		}

		int newQuantity = Math.max(0, inventoryWarehouse.getTotalQuantity() - quantity);
		inventoryWarehouse.setTotalQuantity(newQuantity);
		inventoryWarehouseRepository.save(inventoryWarehouse);

		return Utility.generateResponse("Inventory quantity successfully decreased", false, result);
	}

	// get inv
	// get inv in warehouse
	// add or remove from inventory

}
