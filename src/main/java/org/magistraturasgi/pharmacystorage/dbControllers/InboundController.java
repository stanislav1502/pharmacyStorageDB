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
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.magistraturasgi.pharmacystorage.dbControllers.DBUtil.getConnection;

public class InboundController {

    @FXML
    private TextField inboundIdField;

    @FXML
    private TextField supplierIdField;

    @FXML
    private TextField productIdField;

    @FXML
    private TextField arriveDateField;

    @FXML
    private Stage inboundStage;

    private PreparedStatement addInboundPrepared = null;
    private static final String INSERT_INBOUND_QUERY =
            "INSERT INTO inbound_shipments (inbound_id, inbound_supplier, inbound_product, inbound_arrive_date) VALUES (?, ?, ?, ?)";

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

    }

    @FXML
    private void insertInbound() {
        String inboundIdStr = inboundIdField.getText();
        String supplierIdStr = supplierIdField.getText();
        String productIdStr = productIdField.getText();
        String arriveDateStr = arriveDateField.getText();

        try {
            // Convert IDs and arrival date to appropriate types
            int inboundId = Integer.parseInt(inboundIdStr);
            int supplierId = Integer.parseInt(supplierIdStr);
            int productId = Integer.parseInt(productIdStr);
            Date arriveDate = Date.valueOf(arriveDateStr);

            // Insert the inbound shipment into the database
            insertInboundIntoDatabase(inboundId, supplierId, productId, arriveDate);

            // Display a success message (you can add this part)
        } catch (NumberFormatException e) {
            // Handle parsing errors
            e.printStackTrace(); // You might want to show an error message to the user
        } catch (SQLException e) {
            // Handle database errors
            e.printStackTrace(); // You might want to show an error message to the user
        }
    }

    private void insertInboundIntoDatabase(int inboundId, int supplierId, int productId, Date arriveDate) throws SQLException {
        try (PreparedStatement preparedStatement = getInboundStatement()) {
            preparedStatement.setInt(1, inboundId);
            preparedStatement.setInt(2, supplierId);
            preparedStatement.setInt(3, productId);
            preparedStatement.setDate(4, arriveDate);

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
