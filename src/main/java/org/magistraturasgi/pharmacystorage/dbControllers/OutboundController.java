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

import static org.magistraturasgi.pharmacystorage.DBUtil.getConnection;

public class OutboundController {

    @FXML
    private TextField outboundIdField;

    @FXML
    private TextField clientIdField;

    @FXML
    private TextField shipperIdField;

    @FXML
    private TextField productIdField;

    @FXML
    private TextField requestedDateField;

    @FXML
    private TextField sentDateField;

    @FXML
    private Stage outboundStage;

    private PreparedStatement addOutboundPrepared = null;
    private static final String INSERT_OUTBOUND_QUERY =
            "INSERT INTO outbound_shipments (outbound_id, outbound_client, outbound_shipper, outbound_product, " +
                    "outbound_requested_date, outbound_sent_date) VALUES (?, ?, ?, ?, ?, ?)";

    public void setOutboundStage(Stage outboundStage) {
        this.outboundStage = outboundStage;
    }

    public void showAddOutboundDialog() {
        try {
            URL url = getClass().getResource("outboundDialog.fxml");
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            // Create a new stage for the OutboundDetails window
            Stage outboundStage = new Stage();
            outboundStage.initModality(Modality.APPLICATION_MODAL);
            outboundStage.setTitle("Outbound Shipment Details");
            outboundStage.setScene(new Scene(root));

            // Set the OutboundController for the loaded FXML
            OutboundController outboundController = loader.getController();
            outboundController.setOutboundStage(outboundStage);

            // Show the OutboundDetails window
            outboundStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }
    }

    public void showDelOutboundDialog(){

    }

    @FXML
    private void insertOutbound() {
        String outboundIdStr = outboundIdField.getText();
        String clientIdStr = clientIdField.getText();
        String shipperIdStr = shipperIdField.getText();
        String productIdStr = productIdField.getText();
        String requestedDateStr = requestedDateField.getText();
        String sentDateStr = sentDateField.getText();

        try {
            // Convert IDs and dates to appropriate types
            int outboundId = Integer.parseInt(outboundIdStr);
            int clientId = Integer.parseInt(clientIdStr);
            int shipperId = Integer.parseInt(shipperIdStr);
            int productId = Integer.parseInt(productIdStr);
            Date requestedDate = Date.valueOf(requestedDateStr);
            Date sentDate = Date.valueOf(sentDateStr);

            // Insert the outbound shipment into the database
            insertOutboundIntoDatabase(outboundId, clientId, shipperId, productId, requestedDate, sentDate);

            // Display a success message (you can add this part)
        } catch (NumberFormatException e) {
            // Handle parsing errors
            e.printStackTrace(); // You might want to show an error message to the user
        } catch (SQLException e) {
            // Handle database errors
            e.printStackTrace(); // You might want to show an error message to the user
        }
    }

    private void insertOutboundIntoDatabase(int outboundId, int clientId, int shipperId, int productId,
                                            Date requestedDate, Date sentDate) throws SQLException {
        try (PreparedStatement preparedStatement = getOutboundStatement()) {
            preparedStatement.setInt(1, outboundId);
            preparedStatement.setInt(2, clientId);
            preparedStatement.setInt(3, shipperId);
            preparedStatement.setInt(4, productId);
            preparedStatement.setDate(5, requestedDate);
            preparedStatement.setDate(6, sentDate);

            preparedStatement.executeUpdate();
        }
    }

    private PreparedStatement getOutboundStatement() {
        if (addOutboundPrepared == null) {
            try {
                addOutboundPrepared = getConnection().prepareStatement(INSERT_OUTBOUND_QUERY);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return addOutboundPrepared;
    }
}
