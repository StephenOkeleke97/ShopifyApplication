package com.shopify;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.shopify.model.Warehouse;
import com.shopify.repository.InventoryRepository;
import com.shopify.repository.InventoryWarehouseRepository;
import com.shopify.repository.WarehouseRepository;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class ApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	InventoryRepository inventoryRepository;

	@Autowired
	WarehouseRepository warehouseRepository;

	@Autowired
	InventoryWarehouseRepository inventoryWarehouseRepository;

	@Test
	void contextLoads() {
	}

	@Test
	public void testDefaultWarehouseCreatedWhenApplicationStarts() {
		Warehouse warehouse = warehouseRepository.findByWarehouseName("None");
		Assertions.assertThat(warehouse).isNotNull();
	}

	public void testInventorySuccessfullyCreated() {

	}

	public void testInventoryAddedToDefaultWarehouseWhenCreated() {

	}

	public void testCreateDuplicateNameReturnsBadRequestError() {

	}

	public void testEditInventory() {

	}

	public void testEditInventoryWithDuplicateNameReturnsBadRequestError() {

	}

	public void testGetInventory() {

	}

	public void testGetInventoryInSpecificWarehouse() {

	}

	public void testDeleteInventory() {

	}

	public void testAddInventoryToWarehouse() {

	}

	public void testAddInventoryToWarehouseReturnsBadRequestIfInventoryAlreadyPresent() {

	}

	public void testDeleteInventoryFromWarehouse() {

	}

	public void testDeleteInventoryDeletesInventoryFromAllWarehouses() {

	}

	public void testIncreaseInventoryInWarehouse() {

	}

	public void testDecreaseInventoryInWarehouse() {

	}

	public void testCreateWarehouse() {

	}

	public void testUpdateWarehouse() {

	}

	public void testUpdateWarehouseWithDuplicateNameReturnsError() {

	}

	public void testCannotDeleteWarehouseWithInventoryPresent() {

	}

	public void testDeleteWarehouse() {

	}

	public void testGetAllWarehouses() {

	}

	public void testInvalidParameterReturnsAppropriateError() {

	}
}
