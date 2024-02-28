package org.magistraturasgi.pharmacystorage.dbControllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.magistraturasgi.pharmacystorage.dbControllers.DBUtil.getConnection;

public class ProductController {

    @FXML
    private TextField productNameField;

    @FXML
    private TextField quantityField;

    @FXML
    private TextField priceField;

    @FXML
    private TextField accessField;
    @FXML
    private Stage productStage;


    public void setProductStage(Stage productStage) {
        this.productStage = productStage;
    }

    public void showAddProductDialog() {
        try {

            URL url = getClass().getResource("productDialog.fxml");
            System.out.println(url);
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            // Create a new stage for the ProductDetails window
            Stage productStage = new Stage();
            productStage.initModality(Modality.APPLICATION_MODAL);
            productStage.setTitle("Product Details");
            productStage.setScene(new Scene(root));

            // Set the ProductController for the loaded FXML
            ProductController productController = loader.getController();
            productController.setProductStage(productStage);

            // Show the ProductDetails window
            productStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately

        }
    }


    public void showDelProductDialog() {

    }

    @FXML
    private void insertProduct() {
        String productName = productNameField.getText();
        String quantityStr = quantityField.getText();
        String priceStr = priceField.getText();
        String accessLevelStr = accessField.getText();

        try {
            // Convert quantity, price, and access level to appropriate types
            int quantity = Integer.parseInt(quantityStr);
            double price = Double.parseDouble(priceStr);
            int accessLevel = Integer.parseInt(accessLevelStr);

            // Insert the product into the database
            insertProductIntoDatabase(productName, quantity, price, accessLevel);

            // Display a success message (you can add this part)
        } catch (NumberFormatException e) {
            // Handle parsing errors
            e.printStackTrace(); // You might want to show an error message to the user
        } catch (SQLException e) {
            // Handle database errors
            e.printStackTrace(); // You might want to show an error message to the user
        }
    }

    private PreparedStatement addProductPrepared = null;
    private static final String INSERT_PRODUCTS_QUERY = "INSERT INTO products (product_name, product_quantity, product_price, product_accesslevel) VALUES (?, ?, ?, ?)";
    private void insertProductIntoDatabase(String productName, int quantity, double price, int accessLevel) throws SQLException {
        try (PreparedStatement preparedStatement = getProductsStatement()) {
            preparedStatement.setString(1, productName);
            preparedStatement.setInt(2, quantity);
            preparedStatement.setDouble(3, price);
            preparedStatement.setInt(4, accessLevel);

            preparedStatement.executeUpdate();
        }
    }
    private PreparedStatement getProductsStatement(){
        if (addProductPrepared == null){
            try {
                addProductPrepared = getConnection().prepareStatement(INSERT_PRODUCTS_QUERY);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return addProductPrepared;
    }

}


