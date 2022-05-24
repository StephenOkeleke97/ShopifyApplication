# Shopify Backend Application

This is a simple inventory management software that allows users to create, update, view and delete inventory and warehouses. Users can also add, remove, increase and decrease quantity of inventory in a particular warehouse.

## Usage

- Visit [replit deployment link](https://replit.com/@stephenokeleke/ShopifyApplication#.replit)
- Click on "open website", and you should be navigated to main application page
- On the application homepage, click on link to visit front end UI that will allow you to interact with main backend application

Edit: You may have to click on the "run" button (in the replit link provided, before clicking on "open website") to start up the application because Replit automatically stops it after some period of inactivity. The application can also be started on the command line interface with the command "mvn spring-boot:run"

## User Interface Demo
There are two navigable links in the application, namely: Inventory and Warehouse. On either page, click green plus at the bottom right to create a new inventory/ warehouse.

- On inventory page, click red x to delete inventory. Please note that the replit application may need to wake up, hence, there may be a delay. Also note that deleting an inventory from the inventory page, automatically deletes the inventory in all warehouses as well.
- Click on edit icon to edit inventory.

- On warehouse page, click on red x to delete warehouse. Note, only empty warehouses can be deleted. 
- Click on edit icon to edit warehouse
- Click on down arrow to see inventory in that warehouse, as well as options to add new ones, increase, remove or decrease existing ones.
