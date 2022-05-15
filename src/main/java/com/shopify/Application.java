package com.shopify;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.shopify.model.Warehouse;
import com.shopify.repository.WarehouseRepository;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

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
