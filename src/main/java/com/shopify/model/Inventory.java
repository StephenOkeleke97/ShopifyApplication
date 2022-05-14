package com.shopify.model;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Inventory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long inventoryId;
	private String inventoryName;
	private long price;
	
	public Inventory() {
		super();
	}

	/**
	 * @param inventoryName
	 * @param price
	 */
	public Inventory(String inventoryName, long price) {
		super();
		this.inventoryName = inventoryName;
		this.price = price;
	}

	public long getInventoryId() {
		return inventoryId;
	}

	public void setInventoryId(long inventoryId) {
		this.inventoryId = inventoryId;
	}

	public String getInventoryName() {
		return inventoryName;
	}

	public void setInventoryName(String inventoryName) {
		this.inventoryName = inventoryName;
	}

	public long getPrice() {
		return price;
	}

	public void setPrice(long price) {
		this.price = price;
	}

	@Override
	public int hashCode() {
		return Objects.hash(inventoryId, inventoryName, price);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Inventory other = (Inventory) obj;
		return inventoryId == other.inventoryId && Objects.equals(inventoryName, other.inventoryName)
				&& price == other.price;
	}
}
