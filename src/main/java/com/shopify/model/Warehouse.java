package com.shopify.model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Warehouse {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long warehouseId;
	@Column(unique = true)
	private String warehouseName;

	public Warehouse() {
		super();
	}

	/**
	 * @param warehouseName
	 */
	public Warehouse(String warehouseName) {
		super();
		this.warehouseName = warehouseName;
	}

	public long getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(long warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	@Override
	public int hashCode() {
		return Objects.hash(warehouseId, warehouseName);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Warehouse other = (Warehouse) obj;
		return warehouseId == other.warehouseId && Objects.equals(warehouseName, other.warehouseName);
	}
}
