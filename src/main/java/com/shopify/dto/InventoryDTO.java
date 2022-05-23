package com.shopify.dto;

/**
 * Interface that represents a data transfer object which is mapped to queries
 * from database tables.
 * 
 * @author stephen
 *
 */
public interface InventoryDTO {
	long getId();

	String getName();

	int getQuantity();

	double getPrice();
}
