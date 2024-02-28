package org.magistraturasgi.pharmacystorage.dbControllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.sql.*;
import java.util.Optional;

public class ProductController {

    private PreparedStatement insertProductsPrepared = null;
    private static final String INSERT_PRODUCT_QUERY = "INSERT INTO products VALUES(?, ?, ?, ?, ?)";

    @FXML
    private TextField productNameField;
    @FXML
    private TextField quantityField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField accessField;
    @FXML
    private Button insertProductButton;

    private Stage dialogStage;
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @FXML
    public static void showAddProductDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Product");
        dialog.setHeaderText("Enter the name of the new product:");
        dialog.setContentText("Product Name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(productName -> {
            // Use the entered product name to perform database interaction
            // You can replace the following line with your actual logic
            System.out.println("Adding product: " + productName);

            // Display a confirmation dialog
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Product Added");
            alert.setHeaderText(null);
            alert.setContentText("Product '" + productName + "' added successfully.");

            alert.showAndWait();
        });
    }

    public static void showDelProductDialog() {
    }

    @FXML
    private void insertProduct() {
        String productName = productNameField.getText();
        String quantityStr = quantityField.getText();
        String priceStr = priceField.getText();
        String accessStr = priceField.getText();

        try {
            // Convert quantity and price to appropriate types
            int quantity = Integer.parseInt(quantityStr);
            double price = Double.parseDouble(priceStr);
            int acc = Integer.parseInt(accessStr);
            // Insert the product into the database
            insertProductIntoDatabase(productName, quantity, price, acc);

            // Display a success message
            showAlert(Alert.AlertType.INFORMATION, "Success","Product inserted successfully.");

            // Close the dialog
            dialogStage.close();
        } catch (NumberFormatException e) {
            // Handle parsing errors
            showAlert(Alert.AlertType.ERROR, "Error","Invalid quantity or price. Please enter valid numeric values.");
        }
    }

    private void insertProductIntoDatabase(String productName, int quantity, double price,int access) {
        Connection connection = DBUtil.getInstance().getConnection();
        try {
            PreparedStatement stmt = insertProductsStatement();
            // Set the values for the placeholders
            stmt.setNull(1, Types.NUMERIC);
            stmt.setString(2, productName);
            stmt.setInt(3, quantity);
            stmt.setDouble(4, price);
            stmt.setInt(5, access);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private PreparedStatement insertProductsStatement(){
        if (insertProductsPrepared == null){
            try {
                insertProductsPrepared = DBUtil.getConnection().prepareStatement(INSERT_PRODUCT_QUERY);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return insertProductsPrepared;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



}
