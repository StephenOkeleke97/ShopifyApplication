package com.shopify;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.shopify.dto.InventoryDTO;
import com.shopify.model.Inventory;
import com.shopify.model.InventoryWarehouse;
import com.shopify.model.InventoryWarehouseId;
import com.shopify.model.Warehouse;
import com.shopify.repository.InventoryRepository;
import com.shopify.repository.InventoryWarehouseRepository;
import com.shopify.repository.WarehouseRepository;
import java.util.List;
import javax.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Transactional
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

	@Test
	public void testInventorySuccessfullyCreatedAndAddedToDefaultWarehouse() throws Exception {
		mockMvc.perform(post("/api/v1/inventory").param("name", "Iron").param("price", "10.2").param("quantity", "5"))
				.andDo(print()).andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Inventory successfully created"));
		Warehouse warehouse = warehouseRepository.findByWarehouseName("None");
		List<InventoryDTO> inventory = inventoryWarehouseRepository
				.findIdPriceQuantityByWarehouseId(warehouse.getWarehouseId());
		Assertions.assertThat(inventory.size()).isGreaterThan(0);
	}

	@Test
	public void testCreateDuplicateNameReturnsBadRequestError() throws Exception {
		Inventory inventory = new Inventory("Iron", 10.2);
		inventoryRepository.save(inventory);

		mockMvc.perform(post("/api/v1/inventory").param("name", "Iron").param("price", "10.2").param("quantity", "5"))
				.andDo(print()).andExpect(status().isBadRequest()).andExpect(
						MockMvcResultMatchers.jsonPath("$.message").value("Inventory with this name already exists"));
	}

	@Test
	public void testCreateInventoryWithInvalidName() throws Exception {
		mockMvc.perform(post("/api/v1/inventory").param("name", "    ").param("price", "10.2").param("quantity", "5"))
				.andDo(print()).andExpect(status().isBadRequest())
				.andExpect(
						MockMvcResultMatchers.jsonPath("$.message").value("Invalid inventory name, quantity or price"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.error").value("true"));
	}

	@Test
	public void testCreateInventoryWithInvalidQuantity() throws Exception {
		mockMvc.perform(post("/api/v1/inventory").param("name", "Iron").param("price", "10.2").param("quantity", "-5"))
				.andDo(print()).andExpect(status().isBadRequest())
				.andExpect(
						MockMvcResultMatchers.jsonPath("$.message").value("Invalid inventory name, quantity or price"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.error").value("true"));
	}

	@Test
	public void testCreateInventoryWithInvalidPrice() throws Exception {
		mockMvc.perform(post("/api/v1/inventory").param("name", "Iron").param("price", "-10.2").param("quantity", "5"))
				.andDo(print()).andExpect(status().isBadRequest())
				.andExpect(
						MockMvcResultMatchers.jsonPath("$.message").value("Invalid inventory name, quantity or price"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.error").value("true"));
	}

	@Test
	public void testUpdateInventoryWhenInventoryNotExists() throws Exception {
		mockMvc.perform(put("/api/v1/inventory/10").param("name", "Iron").param("price", "10.2")).andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Inventory item does not exist"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.error").value("true"));
	}

	@Test
	public void testUpdateInventoryInvalidName() throws Exception {
		Inventory inventory = new Inventory("Iron", 10.2);
		inventoryRepository.save(inventory);

		mockMvc.perform(put("/api/v1/inventory/" + inventory.getInventoryId()).param("name", "").param("price", "10.2"))
				.andDo(print()).andExpect(status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Invalid name"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.error").value("true"));
	}

	@Test
	public void testUpdateInventoryInvalidPrice() throws Exception {
		Inventory inventory = new Inventory("Iron", 10.2);
		inventoryRepository.save(inventory);

		mockMvc.perform(
				put("/api/v1/inventory/" + inventory.getInventoryId()).param("name", "Iron").param("price", "-10.2"))
				.andDo(print()).andExpect(status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Invalid price"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.error").value("true"));
	}

	@Test
	public void testUpdateInventoryWithOnlyNameParameterSpecified() throws Exception {
		Inventory inventory = new Inventory("Iron", 10.2);
		inventoryRepository.save(inventory);

		mockMvc.perform(put("/api/v1/inventory/" + inventory.getInventoryId()).param("name", "Steel")).andDo(print())
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Inventory successfully updated"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.error").value("false"));

		inventory = inventoryRepository.findById(inventory.getInventoryId()).orElse(null);
		Assertions.assertThat(inventory.getInventoryName()).isEqualTo("Steel");
	}

	@Test
	public void testUpdateInventoryWithOnlyPriceParameterSpecified() throws Exception {
		Inventory inventory = new Inventory("Iron", 10.2);
		inventoryRepository.save(inventory);

		mockMvc.perform(put("/api/v1/inventory/" + inventory.getInventoryId()).param("price", "200.33")).andDo(print())
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Inventory successfully updated"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.error").value("false"));

		inventory = inventoryRepository.findById(inventory.getInventoryId()).orElse(null);
		Assertions.assertThat(inventory.getPrice()).isEqualTo(200.33);
	}

	@Test
	public void testUpdateInventoryToSameNameDoesNotReturnError() throws Exception {
		Inventory inventory = new Inventory("Iron", 10.2);
		inventoryRepository.save(inventory);

		mockMvc.perform(put("/api/v1/inventory/" + inventory.getInventoryId()).param("name", "Iron")).andDo(print())
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Inventory successfully updated"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.error").value("false"));
	}

	@Test
	public void testUpdateInventoryWithDuplicateNameReturnsBadRequestError() throws Exception {
		Inventory inventory1 = new Inventory("Iron", 10.2);
		Inventory inventory2 = new Inventory("Cotton", 123.1);
		inventoryRepository.save(inventory1);
		inventoryRepository.save(inventory2);

		mockMvc.perform(put("/api/v1/inventory/" + inventory1.getInventoryId()).param("name", "Cotton")).andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Inventory with this name already exists"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.error").value("true"));
	}

	@Test
	public void testGetInventory() throws Exception {
		for (int i = 0; i < 2; i++) {
			mockMvc.perform(
					post("/api/v1/inventory").param("name", "Iron" + i).param("price", "10.2").param("quantity", "5"))
					.andDo(print()).andExpect(status().isOk());
		}

		mockMvc.perform(get("/api/v1/inventory")).andDo(print()).andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.data", hasSize(2)));
	}

	@Test
	public void testAddInventoryToWarehouseInvalidQuantityReturnsCorrectResponse() throws Exception {
		Warehouse warehouse = new Warehouse("Berlin");
		warehouseRepository.save(warehouse);

		mockMvc.perform(post("/api/v1/inventory").param("name", "Iron").param("price", "10.2").param("quantity", "5"))
				.andDo(print()).andExpect(status().isOk());

		Inventory inventory = inventoryRepository.findByInventoryName("Iron");

		mockMvc.perform(put("/api/v1/inventory/" + inventory.getInventoryId() + "/warehouse/"
				+ warehouse.getWarehouseId() + "/" + -10)).andDo(print()).andExpect(status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Invalid quantity"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.error").value("true"));
	}

	@Test
	public void testAddInventoryToWarehouseThatDoesNotExistReturnsCorrectResponse() throws Exception {
		mockMvc.perform(post("/api/v1/inventory").param("name", "Iron").param("price", "10.2").param("quantity", "5"))
				.andDo(print()).andExpect(status().isOk());

		Inventory inventory = inventoryRepository.findByInventoryName("Iron");

		mockMvc.perform(put("/api/v1/inventory/" + inventory.getInventoryId() + "/warehouse/" + 10 + "/" + 10))
				.andDo(print()).andExpect(status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Warehouse does not exist"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.error").value("true"));
	}

	@Test
	public void testAddInventoryThatDoesNotExistToWarehouseReturnsCorrectResponse() throws Exception {
		Warehouse warehouse = new Warehouse("Berlin");
		warehouseRepository.save(warehouse);

		mockMvc.perform(put("/api/v1/inventory/" + 10 + "/warehouse/" + warehouse.getWarehouseId() + "/" + 10))
				.andDo(print()).andExpect(status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Inventory does not exist"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.error").value("true"));
	}

	@Test
	public void testAddInventoryToWarehouseWhenInventoryAlreadyThereReturnsCorrectResponse() throws Exception {
		Warehouse warehouse = new Warehouse("Berlin");
		warehouseRepository.save(warehouse);

		Inventory inventory = new Inventory("Iron", 10.2);
		inventoryRepository.save(inventory);

		InventoryWarehouseId id = new InventoryWarehouseId(warehouse, inventory);
		InventoryWarehouse inventoryWarehouse = new InventoryWarehouse(id, 22);
		inventoryWarehouseRepository.save(inventoryWarehouse);

		mockMvc.perform(put("/api/v1/inventory/"
				+ inventory.getInventoryId() + "/warehouse/" + warehouse.getWarehouseId() + "/" + 10)).andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value("This inventory already exists in this warehouse"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.error").value("true"));
	}

	@Test
	public void testAddInventoryToWarehouseSuccessful() throws Exception {
		Warehouse warehouse = new Warehouse("Berlin");
		warehouseRepository.save(warehouse);

		mockMvc.perform(post("/api/v1/inventory").param("name", "Iron").param("price", "10.2").param("quantity", "5"))
				.andDo(print()).andExpect(status().isOk());

		Inventory inventory = inventoryRepository.findByInventoryName("Iron");

		mockMvc.perform(put("/api/v1/inventory/"
				+ inventory.getInventoryId() + "/warehouse/" + warehouse.getWarehouseId() + "/" + 10)).andDo(print())
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value("Inventory successfully created in warehouse"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.error").value("false"));
	}

	@Test
	public void testGetInventoryInWarehouseWhenWarehouseDoesNotExist() throws Exception {
		mockMvc.perform(get("/api/v1/inventory/" + 10)).andDo(print()).andExpect(status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Warehouse does not exist"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.error").value("true"));
	}

	@Test
	public void testGetInventoryInSpecificWarehouse() throws Exception {
		Warehouse warehouse = new Warehouse("Berlin");
		warehouseRepository.save(warehouse);

		mockMvc.perform(post("/api/v1/inventory").param("name", "Iron").param("price", "10.2").param("quantity", "5"))
				.andDo(print()).andExpect(status().isOk());

		Inventory inventory = inventoryRepository.findByInventoryName("Iron");

		mockMvc.perform(put("/api/v1/inventory/" + inventory.getInventoryId() + "/warehouse/"
				+ warehouse.getWarehouseId() + "/" + 10)).andDo(print()).andExpect(status().isOk());

		mockMvc.perform(get("/api/v1/inventory/" + warehouse.getWarehouseId())).andDo(print())
				.andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.data", hasSize(1)))
				.andExpect(MockMvcResultMatchers.jsonPath("$.data[0].name").value("Iron"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.data[0].quantity").value("10"));
	}

	@Test
	public void testRemoveInventoryFromWarehouseThatDoesNotExistReturnsCorrectResponse() throws Exception {
		Inventory inventory = new Inventory("Iron", 10.2);
		inventoryRepository.save(inventory);

		mockMvc.perform(delete("/api/v1/inventory/" + inventory.getInventoryId() + "/warehouse/" + 10)).andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Warehouse does not exist"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.error").value("true"));
	}

	@Test
	public void testRemoveInventoryThatDoesNotExistInWarehouseFromWarehouseReturnsCorrectResponse() throws Exception {
		Warehouse warehouse = new Warehouse("Berlin");
		warehouseRepository.save(warehouse);

		Inventory inventory = new Inventory("Iron", 10.2);
		inventoryRepository.save(inventory);

		mockMvc.perform(
				delete("/api/v1/inventory/" + inventory.getInventoryId() + "/warehouse/" + warehouse.getWarehouseId()))
				.andDo(print()).andExpect(status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value("This inventory does not exist in this warehouse"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.error").value("true"));
	}

	@Test
	public void testRemoveInventoryThatDoesNotExistFromWarehouseReturnsCorrectResponse() throws Exception {
		Warehouse warehouse = new Warehouse("Berlin");
		warehouseRepository.save(warehouse);

		mockMvc.perform(delete("/api/v1/inventory/" + 10 + "/warehouse/" + warehouse.getWarehouseId())).andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Inventory does not exist"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.error").value("true"));
	}

	@Test
	public void testRemoveInventoryFromWarehouseSuccessful() throws Exception {
		Warehouse warehouse = new Warehouse("Berlin");
		warehouseRepository.save(warehouse);

		Inventory inventory = new Inventory("Iron", 10.2);
		inventoryRepository.save(inventory);

		InventoryWarehouseId id = new InventoryWarehouseId(warehouse, inventory);
		InventoryWarehouse inventoryWarehouse = new InventoryWarehouse(id, 22);
		inventoryWarehouseRepository.save(inventoryWarehouse);

		mockMvc.perform(
				delete("/api/v1/inventory/" + inventory.getInventoryId() + "/warehouse/" + warehouse.getWarehouseId()))
				.andDo(print()).andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value("Inventory successfully deleted from warehouse"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.error").value("false"));

		List<InventoryDTO> inventoryInWarehouse = inventoryWarehouseRepository
				.findIdPriceQuantityByWarehouseId(warehouse.getWarehouseId());
		Assertions.assertThat(inventoryInWarehouse.size()).isEqualTo(0);
	}

	@Test
	public void testDeleteInventoryThatDoesNotExist() throws Exception {
		mockMvc.perform(delete("/api/v1/inventory/" + 10)).andDo(print()).andExpect(status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Inventory item does not exist"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.error").value("true"));
	}

	@Test
	public void testDeleteInventorySuccessful() throws Exception {
		Inventory inventory = new Inventory("Iron", 10.2);
		inventoryRepository.save(inventory);

		mockMvc.perform(delete("/api/v1/inventory/" + inventory.getInventoryId())).andDo(print())
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Inventory successfully deleted"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.error").value("false"));

		inventory = inventoryRepository.findById(inventory.getInventoryId()).orElse(null);
		Assertions.assertThat(inventory).isNull();
	}

	@Test
	public void testDeleteInventoryDeletesInventoryFromAllWarehouses() throws Exception {
		Warehouse warehouse = new Warehouse("Berlin");
		warehouseRepository.save(warehouse);

		Inventory inventory = new Inventory("Iron", 10.2);
		inventoryRepository.save(inventory);

		InventoryWarehouseId id = new InventoryWarehouseId(warehouse, inventory);
		InventoryWarehouse inventoryWarehouse = new InventoryWarehouse(id, 22);
		inventoryWarehouseRepository.save(inventoryWarehouse);

		mockMvc.perform(delete("/api/v1/inventory/" + inventory.getInventoryId())).andDo(print())
				.andExpect(status().isOk());

		inventoryWarehouse = inventoryWarehouseRepository.findByInventoryAndWarehouse(inventory, warehouse);
		inventory = inventoryRepository.findById(inventory.getInventoryId()).orElse(null);
		Assertions.assertThat(inventory).isNull();
		Assertions.assertThat(inventoryWarehouse).isNull();

	}

	@Test
	public void testIncreaseInventoryInWarehouseInvalidQuantity() throws Exception {
		Warehouse warehouse = new Warehouse("Berlin");
		warehouseRepository.save(warehouse);

		Inventory inventory = new Inventory("Iron", 10.2);
		inventoryRepository.save(inventory);

		mockMvc.perform(put("/api/v1/inventory/" + inventory.getInventoryId() + "/warehouse/"
				+ warehouse.getWarehouseId() + "/add/" + -10)).andDo(print()).andExpect(status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Quantity must be positive"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.error").value("true"));
	}

	@Test
	public void testIncreaseInventoryInWarehouseThatDoesNotExist() throws Exception {
		Inventory inventory = new Inventory("Iron", 10.2);
		inventoryRepository.save(inventory);

		mockMvc.perform(put("/api/v1/inventory/" + inventory.getInventoryId() + "/warehouse/" + 10 + "/add/" + 10))
				.andDo(print()).andExpect(status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Warehouse does not exist"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.error").value("true"));
	}

	@Test
	public void testIncreaseInventoryThatDoesNotExistInWarehouse() throws Exception {
		Warehouse warehouse = new Warehouse("Berlin");
		warehouseRepository.save(warehouse);

		mockMvc.perform(put("/api/v1/inventory/" + 10 + "/warehouse/" + warehouse.getWarehouseId() + "/add/" + 10))
				.andDo(print()).andExpect(status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Inventory does not exist"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.error").value("true"));
	}

	@Test
	public void testIncreaseInventoryThatDoesNotExistInWarehouseInWarehouse() throws Exception {
		Warehouse warehouse = new Warehouse("Berlin");
		warehouseRepository.save(warehouse);

		Inventory inventory = new Inventory("Iron", 10.2);
		inventoryRepository.save(inventory);

		mockMvc.perform(put("/api/v1/inventory/"
				+ inventory.getInventoryId() + "/warehouse/" + warehouse.getWarehouseId() + "/add/" + 10))
				.andDo(print()).andExpect(status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value("This inventory does not exist in this warehouse"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.error").value("true"));
	}

	@Test
	public void testIncreaseInventoryInWarehouseSuccessful() throws Exception {
		Warehouse warehouse = new Warehouse("Berlin");
		warehouseRepository.save(warehouse);

		Inventory inventory = new Inventory("Iron", 10.2);
		inventoryRepository.save(inventory);

		InventoryWarehouseId id = new InventoryWarehouseId(warehouse, inventory);
		InventoryWarehouse inventoryWarehouse = new InventoryWarehouse(id, 22);
		inventoryWarehouseRepository.save(inventoryWarehouse);

		mockMvc.perform(put("/api/v1/inventory/" + inventory.getInventoryId() + "/warehouse/"
				+ warehouse.getWarehouseId() + "/add/" + 15)) // expected new value = 22 + 15 = 37
				.andDo(print()).andExpect(status().isOk())
				.andExpect(
						MockMvcResultMatchers.jsonPath("$.message").value("Inventory quantity successfully increased"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.error").value("false"));

		inventoryWarehouse = inventoryWarehouseRepository.findByInventoryAndWarehouse(inventory, warehouse);
		Assertions.assertThat(inventoryWarehouse.getTotalQuantity()).isEqualTo(37);
	}

	@Test
	public void testDecreaseInventoryInWarehouseInvalidQuantity() throws Exception {
		Warehouse warehouse = new Warehouse("Berlin");
		warehouseRepository.save(warehouse);

		Inventory inventory = new Inventory("Iron", 10.2);
		inventoryRepository.save(inventory);

		mockMvc.perform(put("/api/v1/inventory/" + inventory.getInventoryId() + "/warehouse/"
				+ warehouse.getWarehouseId() + "/remove/" + -10)).andDo(print()).andExpect(status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Quantity must be positive"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.error").value("true"));
	}

	@Test
	public void testDecreaseInventoryInWarehouseThatDoesNotExist() throws Exception {
		Inventory inventory = new Inventory("Iron", 10.2);
		inventoryRepository.save(inventory);

		mockMvc.perform(put("/api/v1/inventory/" + inventory.getInventoryId() + "/warehouse/" + 10 + "/remove/" + 10))
				.andDo(print()).andExpect(status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Warehouse does not exist"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.error").value("true"));
	}

	@Test
	public void testDecreaseInventoryThatDoesNotExistInWarehouse() throws Exception {
		Warehouse warehouse = new Warehouse("Berlin");
		warehouseRepository.save(warehouse);

		mockMvc.perform(put("/api/v1/inventory/" + 10 + "/warehouse/" + warehouse.getWarehouseId() + "/remove/" + 10))
				.andDo(print()).andExpect(status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Inventory does not exist"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.error").value("true"));
	}

	@Test
	public void testDecreaseInventoryThatDoesNotExistInWarehouseInWarehouse() throws Exception {
		Warehouse warehouse = new Warehouse("Berlin");
		warehouseRepository.save(warehouse);

		Inventory inventory = new Inventory("Iron", 10.2);
		inventoryRepository.save(inventory);

		mockMvc.perform(put("/api/v1/inventory/"
				+ inventory.getInventoryId() + "/warehouse/" + warehouse.getWarehouseId() + "/remove/" + 10))
				.andDo(print()).andExpect(status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value("This inventory does not exist in this warehouse"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.error").value("true"));
	}

	@Test
	public void testDecreaseInventoryInWarehouseSuccessful() throws Exception {
		Warehouse warehouse = new Warehouse("Berlin");
		warehouseRepository.save(warehouse);

		Inventory inventory = new Inventory("Iron", 10.2);
		inventoryRepository.save(inventory);

		InventoryWarehouseId id = new InventoryWarehouseId(warehouse, inventory);
		InventoryWarehouse inventoryWarehouse = new InventoryWarehouse(id, 22);
		inventoryWarehouseRepository.save(inventoryWarehouse);

		mockMvc.perform(put("/api/v1/inventory/" + inventory.getInventoryId() + "/warehouse/"
				+ warehouse.getWarehouseId() + "/remove/" + 15)) // expected new value = 22 - 15 = 7
				.andDo(print()).andExpect(status().isOk())
				.andExpect(
						MockMvcResultMatchers.jsonPath("$.message").value("Inventory quantity successfully decreased"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.error").value("false"));

		inventoryWarehouse = inventoryWarehouseRepository.findByInventoryAndWarehouse(inventory, warehouse);
		Assertions.assertThat(inventoryWarehouse.getTotalQuantity()).isEqualTo(7);
	}

	@Test
	public void testCreateWarehouseInvalidNameReturnsCorrectErrorResponse() throws Exception {
		mockMvc.perform(post("/api/v1/warehouse").param("name", "  ")).andDo(print()).andExpect(status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Invalid name"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.error").value("true"));
	}

	@Test
	public void testCreateWarehouseDuplicateNameReturnsCorrectErrorResponse() throws Exception {
		Warehouse warehouse = new Warehouse("Tokyo");
		warehouseRepository.save(warehouse);

		mockMvc.perform(post("/api/v1/warehouse").param("name", "Tokyo")).andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value("There is already a warehouse with this name"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.error").value("true"));
	}

	@Test
	public void testCreateWarehouseSuccessful() throws Exception {
		mockMvc.perform(post("/api/v1/warehouse").param("name", "Tokyo")).andDo(print()).andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Warehouse successfully added"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.error").value("false"));

		Warehouse warehouse = warehouseRepository.findByWarehouseName("Tokyo");
		Assertions.assertThat(warehouse).isNotNull();
	}

	@Test
	public void testUpdateWarehouseInvalidNameReturnsCorrectErrorResponse() throws Exception {
		Warehouse warehouse = new Warehouse("Tokyo");
		warehouseRepository.save(warehouse);

		mockMvc.perform(put("/api/v1/warehouse/" + warehouse.getWarehouseId()).param("name", "  ")).andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Invalid name"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.error").value("true"));
	}

	@Test
	public void testUpdateWarehouseDuplicateNameReturnsCorrectErrorResponse() throws Exception {
		Warehouse warehouse1 = new Warehouse("Tokyo");
		Warehouse warehouse2 = new Warehouse("Nairobi");
		warehouseRepository.save(warehouse1);
		warehouseRepository.save(warehouse2);

		mockMvc.perform(put("/api/v1/warehouse/" + warehouse1.getWarehouseId()).param("name", "Nairobi")).andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value("There is already a warehouse with this name"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.error").value("true"));
	}

	@Test
	public void testUpdateWarehouseToSameNameIsSuccessful() throws Exception {
		Warehouse warehouse = new Warehouse("Tokyo");
		warehouseRepository.save(warehouse);

		mockMvc.perform(put("/api/v1/warehouse/" + warehouse.getWarehouseId()).param("name", "Tokyo")).andDo(print())
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Warehouse successfully updated"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.error").value("false"));
	}

	@Test
	public void testUpdateWarehouseSuccessful() throws Exception {
		Warehouse warehouse = new Warehouse("Tokyo");
		warehouseRepository.save(warehouse);

		mockMvc.perform(put("/api/v1/warehouse/" + warehouse.getWarehouseId()).param("name", "Denver")).andDo(print())
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Warehouse successfully updated"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.error").value("false"));

		warehouse = warehouseRepository.findByWarehouseName("Denver");
		Assertions.assertThat(warehouse).isNotNull();
	}

	@Test
	public void testCannotDeleteWarehouseWithInventoryPresent() throws Exception {
		Warehouse warehouse = new Warehouse("Berlin");
		warehouseRepository.save(warehouse);

		Inventory inventory = new Inventory("Iron", 10.2);
		inventoryRepository.save(inventory);

		InventoryWarehouseId id = new InventoryWarehouseId(warehouse, inventory);
		InventoryWarehouse inventoryWarehouse = new InventoryWarehouse(id, 22);
		inventoryWarehouseRepository.save(inventoryWarehouse);

		mockMvc.perform(delete("/api/v1/warehouse/" + warehouse.getWarehouseId())).andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
						.value("A warehouse with live inventory cannot be deleted. "
								+ "Please delete or transfer all inventory associated with this warehouse"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.error").value("true"));
	}

	@Test
	public void testDeleteWarehouse() throws Exception {
		Warehouse warehouse = new Warehouse("Berlin");
		warehouseRepository.save(warehouse);

		mockMvc.perform(delete("/api/v1/warehouse/" + warehouse.getWarehouseId())).andDo(print())
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Warehouse successfully deleted"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.error").value("false"));

		warehouse = warehouseRepository.findByWarehouseName("Berlin");
		Assertions.assertThat(warehouse).isNull();
	}

	@Test
	public void testGetAllWarehouses() throws Exception {
		Warehouse warehouse = new Warehouse("Berlin");
		warehouseRepository.save(warehouse);
		Warehouse warehouse2 = new Warehouse("Tokyo");
		warehouseRepository.save(warehouse2);

		mockMvc.perform(get("/api/v1/warehouse")).andDo(print()).andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Success"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.error").value("false"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.data", hasSize(3))); // 2 warehouses newly created +
																					// default warehouse = 3
	}
}
