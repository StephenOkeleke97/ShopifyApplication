package com.shopify.model;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Class that represents an inventory table in application's relational
 * database.
 * 
 * @author stephen
 *
 */
@Entity
public class Inventory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long inventoryId;
	@Column(unique = true)
	private String inventoryName;
	private double price;

	/**
	 * Constructs an instance of this class without any parameters.
	 */
	public Inventory() {
		super();
	}

	/**
	 * Constructs an instance of this class with specified parameters.
	 * 
	 * @param inventoryName name of inventory item
	 * @param price         price of inventory item
	 */
	public Inventory(String inventoryName, double price) {
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

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
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
