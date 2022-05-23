package com.shopify.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

/**
 * A class that represents an "is contained in" relationship between an
 * inventory entity and a warehouse entity. Inventories can be contained in
 * warehouses.
 * 
 * @author stephen
 *
 */
@Entity
public class InventoryWarehouse {

	@EmbeddedId
	private InventoryWarehouseId id;

	private int totalQuantity;

	@ManyToOne
	@MapsId("inventoryId")
	private Inventory inventory;

	@ManyToOne
	@MapsId("locationId")
	private Warehouse warehouse;

	/**
	 * Constructs an instance of this class without any parameters.
	 */
	public InventoryWarehouse() {
		super();
	}

	/**
	 * Constructs an instance of this class with specified parameters.
	 * 
	 * @param id       composite primary key consisting of an inventory and a
	 *                 warehouse entity
	 * @param quantity quantity of inventory to be added to warehouse
	 */
	public InventoryWarehouse(InventoryWarehouseId id, int quantity) {
		super();
		this.id = id;
		this.totalQuantity = quantity;
	}

	public InventoryWarehouseId getId() {
		return id;
	}

	public void setId(InventoryWarehouseId id) {
		this.id = id;
	}

	public int getTotalQuantity() {
		return totalQuantity;
	}

	public void setTotalQuantity(int quantity) {
		this.totalQuantity = quantity;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

	public Warehouse getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}
}
