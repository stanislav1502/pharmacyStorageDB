package org.magistraturasgi.pharmacystorage.dbControllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

import static org.magistraturasgi.pharmacystorage.DBUtil.getConnection;

public class SupplierController {

    @FXML
    private TextField supplierNameField;

    @FXML
    private Stage supplierStage;

    private PreparedStatement addSupplierPrepared = null;
    private static final String INSERT_SUPPLIER_QUERY = "INSERT INTO suppliers VALUES (supplier_t(supplier_seq.NEXTVAL, ?))";

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

    public void showDelSupplierDialog() {
        // Create a TextInputDialog
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Delete by ID");
        dialog.setHeaderText("Enter Supplier ID:");
        dialog.setContentText("Supplier ID:");

        // Show the dialog and wait for the user's response
        Optional<String> result = dialog.showAndWait();
        final String[] enteredSupplierId = new String[1];

        // Process the user's input
        result.ifPresent(supplierId -> {
            // Save the entered Supplier ID to the class variable
            enteredSupplierId[0] = supplierId;
        });

        // Handle the case where the user cancels the input
        if (!result.isPresent()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "No Supplier ID entered", ButtonType.OK);
            alert.showAndWait();
        }

        try {
            // Use the correct column name and adjust the SQL query
            PreparedStatement stmt = getConnection().prepareStatement("DELETE FROM suppliers WHERE supplier_id = ?");
            stmt.setString(1, enteredSupplierId[0]);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @FXML
    private void insertSupplier() {
        String supplierName = supplierNameField.getText();

        try {
            // Insert the supplier into the database
            insertSupplierIntoDatabase(supplierName);

            // Display a success message (you can add this part)

            supplierStage.close();


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
