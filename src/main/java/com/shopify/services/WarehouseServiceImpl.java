package com.shopify.services;

import com.shopify.dto.InventoryDTO;
import com.shopify.model.Inventory;
import com.shopify.model.InventoryWarehouse;
import com.shopify.model.InventoryWarehouseId;
import com.shopify.model.Warehouse;
import com.shopify.repository.InventoryRepository;
import com.shopify.repository.InventoryWarehouseRepository;
import com.shopify.repository.WarehouseRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Class that represents an implementation of the WarehouseService interface.
 * 
 * @author stephen
 *
 */
@Service
public class WarehouseServiceImpl implements WarehouseService {

	@Autowired
	private WarehouseRepository warehouseRepository;

	@Autowired
	private InventoryWarehouseRepository inventoryWarehouseRepository;

	@Autowired
	private InventoryRepository inventoryRepository;

	@Override
	public void createWarehouse(String name) {
		Warehouse warehouse = new Warehouse(name);
		warehouseRepository.save(warehouse);
	}

	@Override
	public void editWarehouse(long id, String name) {
		Warehouse warehouse = warehouseRepository.findById(id).orElse(null);
		warehouse.setWarehouseName(name);
		warehouseRepository.save(warehouse);
	}

	@Override
	public void deleteWarehouse(long id) {
		Warehouse warehouse = warehouseRepository.findById(id).orElse(null);
		warehouseRepository.delete(warehouse);
	}

	@Override
	public Iterable<Warehouse> getWarehouses() {
		return warehouseRepository.findAll();
	}

	@Override
	public void addNewInvToWarehouse(long id, int quantity, long invId) {
		Inventory inv = inventoryRepository.findById(invId).orElse(null);
		Warehouse warehouse = warehouseRepository.findById(id).orElse(null);
		InventoryWarehouseId compositeKey = new InventoryWarehouseId(warehouse, inv);
		InventoryWarehouse inventoryWarehouse = new InventoryWarehouse(compositeKey, quantity);
		inventoryWarehouseRepository.save(inventoryWarehouse);

	}

	@Override
	public void addExistingInvToWarehouse(long id, int quantity, long invId) {
		Inventory inv = inventoryRepository.findById(invId).orElse(null);
		Warehouse warehouse = warehouseRepository.findById(id).orElse(null);
		InventoryWarehouseId compositeKey = new InventoryWarehouseId(warehouse, inv);
		InventoryWarehouse inventoryWarehouse = new InventoryWarehouse(compositeKey, quantity);
		inventoryWarehouseRepository.save(inventoryWarehouse);

	}

	@Override
	public void deleteInvFromWarehouse(long id, long invId) {
		Inventory inv = inventoryRepository.findById(invId).orElse(null);
		Warehouse warehouse = warehouseRepository.findById(id).orElse(null);
		InventoryWarehouse inventoryWarehouse = inventoryWarehouseRepository.findByInventoryAndWarehouse(inv,
				warehouse);
		inventoryWarehouseRepository.delete(inventoryWarehouse);
	}

	@Override
	public void increaseInvInWarehouse(long id, long invId, int quantity) {
		Inventory inv = inventoryRepository.findById(invId).orElse(null);
		Warehouse warehouse = warehouseRepository.findById(id).orElse(null);
		InventoryWarehouse inventoryWarehouse = inventoryWarehouseRepository.findByInventoryAndWarehouse(inv,
				warehouse);
		int newQuantity = inventoryWarehouse.getTotalQuantity() + quantity;
		inventoryWarehouse.setTotalQuantity(newQuantity);
		inventoryWarehouseRepository.save(inventoryWarehouse);

	}

	@Override
	public void decreaseInvInWarehouse(long id, long invId, int quantity) {
		Inventory inv = inventoryRepository.findById(invId).orElse(null);
		Warehouse warehouse = warehouseRepository.findById(id).orElse(null);
		InventoryWarehouse inventoryWarehouse = inventoryWarehouseRepository.findByInventoryAndWarehouse(inv,
				warehouse);
		int newQuantity = Math.max(0, inventoryWarehouse.getTotalQuantity() - quantity);
		inventoryWarehouse.setTotalQuantity(newQuantity);
		inventoryWarehouseRepository.save(inventoryWarehouse);

	}

	@Override
	public List<InventoryDTO> getAllInv() {
		return inventoryWarehouseRepository.findIdPriceQuantityGroupById();
	}

	@Override
	public List<InventoryDTO> getAllInvByWarehouse(long id) {
		return inventoryWarehouseRepository.findIdPriceQuantityByWarehouseId(id);
	}
}
