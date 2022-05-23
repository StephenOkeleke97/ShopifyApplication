package com.shopify;

import com.shopify.model.Warehouse;
import com.shopify.repository.WarehouseRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * A class that represents the entry point of this application.
 * 
 * @author stephen
 *
 */
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	/**
	 * Create default warehouse if it does not exist. The default warehouse has
	 * name, "None".
	 * 
	 * @param warehouseRepository warehouseRepository instance
	 * @return anonymous function executing operation
	 */
	@Bean
	CommandLineRunner createDefaultWarehouse(WarehouseRepository warehouseRepository) {
		return (args) -> {
			Warehouse warehouse = warehouseRepository.findByWarehouseName("None");
			if (warehouse == null) {
				warehouse = new Warehouse("None");
				warehouseRepository.save(warehouse);
			}
		};
	}

}
