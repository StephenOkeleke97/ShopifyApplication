package com.shopify.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.shopify.dto.ResponseDTO;
import com.shopify.model.InventoryWarehouse;
import com.shopify.model.Warehouse;
import com.shopify.repository.InventoryWarehouseRepository;
import com.shopify.repository.WarehouseRepository;
import com.shopify.util.Utility;

@RestController("api")
public class Controller {
	@Autowired
	private WarehouseRepository warehouseRepository;

	@Autowired
	private InventoryWarehouseRepository inventoryWarehouseRepository;

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
			result.setError(true);
			result.setMessage("Invalid Name");
			return result;
		}

		if (!Utility.validateWareHouseNameIsUnique(name, warehouseRepository)) {
			result.setMessage("There is already a warehouse with this name.");
			return result;
		}

		Warehouse warehouse = new Warehouse(name);
		warehouseRepository.save(warehouse);

		result.setMessage("Warehouse successfully added");
		return result;
	}

	/**
	 * Update warehouse name.
	 * 
	 * @param id       id of warehouse to be updated
	 * @param name     new name of warehouse
	 * @param response {@link HttpServletResponse}
	 * @return result of action
	 */
	@PutMapping("/warehouse")
	public @ResponseBody ResponseDTO editWarehouse(@RequestParam long id, @RequestParam String name,
			HttpServletResponse response) {
		ResponseDTO result = new ResponseDTO();

		if (!Utility.validateStringArgs(name)) {
			response.setStatus(400);
			result.setError(true);
			result.setMessage("Invalid Name");
			return result;
		}

		if (!Utility.validateWareHouseNameIsUnique(name, warehouseRepository)) {
			result.setMessage("There is already a warehouse with this name.");
			return result;
		}

		Warehouse warehouse = warehouseRepository.findById(id).orElse(null);

		if (warehouse == null) {
			response.setStatus(400);
			result.setError(true);
			result.setMessage("Warehouse does not exist");
			return result;
		}

		warehouse.setWarehouseName(name);
		warehouseRepository.save(warehouse);

		result.setMessage("Warehouse successfully updated");
		return result;
	}

	/**
	 * Delete warehouse.
	 * 
	 * @param id       id of warehouse to be deleted
	 * @param response {@link HttpServletResponse}
	 * @return result of action
	 */
	@DeleteMapping("/warehouse")
	public @ResponseBody ResponseDTO deleteWarehouse(@RequestParam long id, HttpServletResponse response) {
		ResponseDTO result = new ResponseDTO();

		Warehouse warehouse = warehouseRepository.findById(id).orElse(null);

		if (warehouse != null) {
			List<InventoryWarehouse> warehouseInventory = inventoryWarehouseRepository.findOneByWarehouse(id);
			if (warehouseInventory.size() > 0) {
				response.setStatus(400);
				result.setError(true);
				result.setMessage("A warehouse with live inventory cannot be deleted. "
						+ "Please delete or transfer all inventory associated with this warehouse.");
				return result;
			}

			warehouseRepository.delete(warehouse);
		}

		result.setMessage("Warehouse successfully deleted");
		return result;
	}

	/**
	 * Get all warehouses.
	 * 
	 * @return list of warehouses
	 */
	@GetMapping("/warehouse")
	public @ResponseBody Iterable<Warehouse> getWarehouse() {
		return warehouseRepository.findAll();
	}

}
