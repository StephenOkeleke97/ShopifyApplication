package com.shopify.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Embeddable;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class InventoryWarehouseId implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ManyToOne
	@JoinColumn(referencedColumnName = "warehouseId", 
	foreignKey = @ForeignKey(name = "WAREHOUSE_ID_FK"))
	private Warehouse warehouse;
	
	@ManyToOne
	@JoinColumn(referencedColumnName = "inventoryId", 
	foreignKey = @ForeignKey(name = "INVENTORY_ID_FK", 
	foreignKeyDefinition = "FOREIGN KEY (inventory_inventory_id) REFERENCES "
			+ "inventory(inventory_id) ON DELETE CASCADE"))
	private Inventory inventory;
	

	public InventoryWarehouseId() {
		super();
	}

	/**
	 * @param warehouse
	 * @param inventory
	 */
	public InventoryWarehouseId(Warehouse warehouse, Inventory inventory) {
		super();
		this.warehouse = warehouse;
		this.inventory = inventory;
	}

	public Warehouse getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

	@Override
	public int hashCode() {
		return Objects.hash(inventory, warehouse);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InventoryWarehouseId other = (InventoryWarehouseId) obj;
		return Objects.equals(inventory, other.inventory) && Objects.equals(warehouse, other.warehouse);
	}	
}
