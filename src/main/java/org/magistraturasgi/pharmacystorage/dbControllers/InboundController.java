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
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

import static org.magistraturasgi.pharmacystorage.DBUtil.getConnection;

public class InboundController {


    @FXML
    private TextField supplierIdField;

    @FXML
    private TextField productIdField;

    @FXML
    private TextField arriveDateField;

    @FXML
    private TextField statusField;

    @FXML
    private TextField quantityField;

    @FXML
    private TextField priceField;

    @FXML
    private Stage inboundStage;

    private PreparedStatement addInboundPrepared = null;
    private static final String INSERT_INBOUND_QUERY =
            "INSERT INTO inbound_shipments VALUES (inbound_shipment_t(?, ?, ?, inbound_seq.NEXTVAL, " +
                    "(SELECT REF(s) FROM suppliers s WHERE s.supplier_id = ?)," +
                    "(SELECT REF(p) FROM products p WHERE p.product_id = ?), " +
                    "?))";

    public void setInboundStage(Stage inboundStage) {
        this.inboundStage = inboundStage;
    }

    public void showAddInboundDialog() {
        try {
            URL url = getClass().getResource("inboundDialog.fxml");
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            // Create a new stage for the InboundDetails window
            Stage inboundStage = new Stage();
            inboundStage.initModality(Modality.APPLICATION_MODAL);
            inboundStage.setTitle("Inbound Shipment Details");
            inboundStage.setScene(new Scene(root));

            // Set the InboundController for the loaded FXML
            InboundController inboundController = loader.getController();
            inboundController.setInboundStage(inboundStage);

            // Show the InboundDetails window
            inboundStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }
    }

    public void showDelInboundDialog() {
        // Create a TextInputDialog
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Delete by ID");
        dialog.setHeaderText("Enter Inbound ID:");
        dialog.setContentText("ID:");

        // Show the dialog and wait for the user's response
        Optional<String> result = dialog.showAndWait();
        final String[] enteredInboundId = new String[1];

        // Process the user's input
        result.ifPresent(inboundId -> {
            // Save the entered Supplier ID to the class variable
            enteredInboundId[0] = inboundId;
        });

        // Handle the case where the user cancels the input
        if (!result.isPresent()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "No Inbound Shipment ID entered", ButtonType.OK);
            alert.showAndWait();
        }

        try {
            // Use the correct column name and adjust the SQL query
            PreparedStatement stmt = getConnection().prepareStatement("DELETE FROM inbound_shipments WHERE inbound_id = ?");
            stmt.setString(1, enteredInboundId[0]);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @FXML
    private void insertInbound() {
        String supplierIdStr = supplierIdField.getText();
        String productIdStr = productIdField.getText();
        String arriveDateStr = arriveDateField.getText();
        String statusStr = statusField.getText();
        String quantityStr = quantityField.getText();
        String priceStr = priceField.getText();

        try {
            // Convert IDs and arrival date to appropriate types
            int supplierId = Integer.parseInt(supplierIdStr);
            int productId = Integer.parseInt(productIdStr);
            Date arriveDate = Date.valueOf(arriveDateStr);
            int quantity = Integer.parseInt(quantityStr);
            double price = Double.parseDouble(priceStr);

            // Insert the inbound shipment into the database
            insertInboundIntoDatabase(statusStr,quantity,price,supplierId, productId, arriveDate);

            // Display a success message (you can add this part)
            inboundStage.close();
        } catch (NumberFormatException e) {
            // Handle parsing errors
            e.printStackTrace(); // You might want to show an error message to the user
        } catch (SQLException e) {
            // Handle database errors
            e.printStackTrace(); // You might want to show an error message to the user
        }
    }

    private void insertInboundIntoDatabase(String status,int quantity, double price, int supplierId, int productId, Date arriveDate) throws SQLException {
        try (PreparedStatement preparedStatement = getInboundStatement()) {
            preparedStatement.setString(1, status);
            preparedStatement.setInt(2, quantity);
            preparedStatement.setDouble(3, price);
            preparedStatement.setInt(4, supplierId);
            preparedStatement.setInt(5, productId);
            preparedStatement.setDate(6, arriveDate);
            preparedStatement.executeUpdate();
        }
    }

    private PreparedStatement getInboundStatement() {
        if (addInboundPrepared == null) {
            try {
                addInboundPrepared = getConnection().prepareStatement(INSERT_INBOUND_QUERY);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return addInboundPrepared;
    }
}
