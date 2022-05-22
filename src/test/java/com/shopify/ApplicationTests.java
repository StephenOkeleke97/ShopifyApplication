package com.shopify;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import com.shopify.dto.InventoryDTO;
import com.shopify.model.Inventory;
import com.shopify.model.Warehouse;
import com.shopify.repository.InventoryRepository;
import com.shopify.repository.InventoryWarehouseRepository;
import com.shopify.repository.WarehouseRepository;

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
	public void testAddInventoryToWarehouse() throws Exception {
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

	public void testGetInventoryInSpecificWarehouse() {

	}

	public void testDeleteInventory() {

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
