package com.shopify.services;

import com.shopify.dto.InventoryDTO;
import com.shopify.model.Warehouse;
import java.util.List;

public interface WarehouseService {
	public void createWarehouse(String name);

	public void editWarehouse(long id, String name);

	public void deleteWarehouse(long id);

	public Iterable<Warehouse> getWarehouses();

	public void addNewInvToWarehouse(long id, int quantity, long invId);

	public void addExistingInvToWarehouse(long id, int quantity, long invId);

	public void deleteInvFromWarehouse(long id, long invId);

	public void increaseInvInWarehouse(long id, long invId, int quantity);

	public void decreaseInvInWarehouse(long id, long invId, int quantity);

	public List<InventoryDTO> getAllInv();

	public List<InventoryDTO> getAllInvByWarehouse(long id);
}
