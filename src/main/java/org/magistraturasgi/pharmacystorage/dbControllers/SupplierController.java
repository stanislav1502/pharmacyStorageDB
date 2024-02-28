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

public class SupplierController {

    @FXML
    private TextField supplierNameField;

    @FXML
    private Stage supplierStage;

    private PreparedStatement addSupplierPrepared = null;
    private static final String INSERT_SUPPLIER_QUERY = "INSERT INTO suppliers (supplier_name) VALUES (?)";

    public void setSupplierStage(Stage supplierStage) {
        this.supplierStage = supplierStage;
    }

    public void showAddSupplierDialog() {
        try {
            URL url = getClass().getResource("supplierDialog.fxml");
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            // Create a new stage for the SupplierDetails window
            Stage supplierStage = new Stage();
            supplierStage.initModality(Modality.APPLICATION_MODAL);
            supplierStage.setTitle("Supplier Details");
            supplierStage.setScene(new Scene(root));

            // Set the SupplierController for the loaded FXML
            SupplierController supplierController = loader.getController();
            supplierController.setSupplierStage(supplierStage);

            // Show the SupplierDetails window
            supplierStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }
    }

    public void showDelSupplierDialog(){

    }

    @FXML
    private void insertSupplier() {
        String supplierName = supplierNameField.getText();

        try {
            // Insert the supplier into the database
            insertSupplierIntoDatabase(supplierName);

            // Display a success message (you can add this part)
        } catch (SQLException e) {
            // Handle database errors
            e.printStackTrace(); // You might want to show an error message to the user
        }
    }

    private void insertSupplierIntoDatabase(String supplierName) throws SQLException {
        try (PreparedStatement preparedStatement = getSupplierStatement()) {
            preparedStatement.setString(1, supplierName);

            preparedStatement.executeUpdate();
        }
    }

    private PreparedStatement getSupplierStatement() {
        if (addSupplierPrepared == null) {
            try {
                addSupplierPrepared = getConnection().prepareStatement(INSERT_SUPPLIER_QUERY);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return addSupplierPrepared;
    }

}
