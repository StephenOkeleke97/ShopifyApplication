package com.shopify.model;

import java.util.Date;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class InventoryWarehouse {

	@EmbeddedId
	private InventoryWarehouseId id; 
	
	private int totalQuantity;
	private int quantityOfLastSupply;
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateOfLastSupply;
	
	@ManyToOne
	@MapsId("inventoryId")
	private Inventory inventory;
	
	@ManyToOne
	@MapsId("locationId")
	private Warehouse warehouse;
	
	public InventoryWarehouse() {
		super();
	}

	/**
	 * @param id
	 * @param quantity
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

	public int getQuantityOfLastSupply() {
		return quantityOfLastSupply;
	}

	public void setQuantityLastSupply(int quantityLastSupplied) {
		this.quantityOfLastSupply = quantityLastSupplied;
	}

	public Date getDateOfLastSupply() {
		return dateOfLastSupply;
	}

	public void setDateOfLastSupply(Date dateOfLastSupply) {
		this.dateOfLastSupply = dateOfLastSupply;
	}
}
