package org.magistraturasgi.pharmacystorage.dbControllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

import static org.magistraturasgi.pharmacystorage.DBUtil.getConnection;

public class ProductController {

    @FXML
    private TextField infoField;
    @FXML
    private ChoiceBox productTypeField;
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
        productTypeField.getItems().addAll("Medicine","Supplement","Other");
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
        // Create a TextInputDialog
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Delete by ID");
        dialog.setHeaderText("Enter Product ID:");
        dialog.setContentText("ID:");

        // Show the dialog and wait for the user's response
        Optional<String> result = dialog.showAndWait();
        final String[] enteredProductId = new String[1];

        // Process the user's input
        result.ifPresent(productId -> {
            // Save the entered Supplier ID to the class variable
            enteredProductId[0] = productId;
        });

        // Handle the case where the user cancels the input
        if (!result.isPresent()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "No Product ID entered", ButtonType.OK);
            alert.showAndWait();
        }

        try {
            // Use the correct column name and adjust the SQL query
            PreparedStatement stmt = getConnection().prepareStatement("DELETE FROM products WHERE product_id = ?");
            stmt.setString(1, enteredProductId[0]);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @FXML
    private void insertProduct() {
        String productType = productTypeField.getValue().toString();
        String productName = productNameField.getText();
        String quantityStr = quantityField.getText();
        String priceStr = priceField.getText();
        String accessLevelStr = accessField.getText();
        String infoStr = infoField.getText();

        try {
            // Convert quantity, price, and access level to appropriate types
            int quantity = Integer.parseInt(quantityStr);
            double price = Double.parseDouble(priceStr);
            int accessLevel = Integer.parseInt(accessLevelStr);

            // Insert the product into the database
            insertProductIntoDatabase(productType, productName, quantity, price, accessLevel, infoStr);

            // Display a success message (you can add this part)
            productStage.close();
        } catch (NumberFormatException e) {
            // Handle parsing errors
            e.printStackTrace(); // You might want to show an error message to the user
        } catch (SQLException e) {
            // Handle database errors
            e.printStackTrace(); // You might want to show an error message to the user
        }
    }

    private PreparedStatement addProductPrepared = null;
    private static final String INSERT_PRODUCTS_QUERY = "INSERT INTO products VALUES (?(product_seq.NEXTVAL, ?, ?, ?, ?, ?))";
    private void insertProductIntoDatabase(String clientType,String productName, int quantity, double price, int accessLevel, String info) throws SQLException {
        try (PreparedStatement preparedStatement = getProductsStatement()) {
            switch (clientType) {
                case "Medicine": preparedStatement.setString(1, "medicine_t"); break;
                case "Supplement": preparedStatement.setString(1, "supplement_t"); break;
                case "Other": preparedStatement.setString(1, "other_t"); break;
                default: preparedStatement.setString(1, "product_t");
            }
            preparedStatement.setString(2, productName);
            preparedStatement.setInt(3, quantity);
            preparedStatement.setDouble(4, price);
            preparedStatement.setInt(5, accessLevel);
            preparedStatement.setString(6, info);
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


