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

public class OutboundController {


    @FXML
    private TextField clientIdField;
    @FXML
    private TextField shipperIdField;
    @FXML
    private TextField productIdField;
    @FXML
    private TextField statusField;
    @FXML
    private TextField quantityField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField requestedDateField;
    @FXML
    private TextField sentDateField;

    @FXML
    private Stage outboundStage;

    private PreparedStatement addOutboundPrepared = null;
    private static final String INSERT_OUTBOUND_QUERY =
            "INSERT INTO outbound_shipments VALUES (outbound_shipment_t(?, ?, ?, outbound_seq.NEXTVAL, " +
                    "(SELECT REF(c) FROM clients c WHERE c.client_id = ?), " +
                    "(SELECT REF(s) FROM shippers s WHERE s.shipper_id = ?), " +
                    "(SELECT REF(p) FROM products p WHERE p.product_id = ?), " +
                    "?, ?)";

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

    public void showDelOutboundDialog() {
        // Create a TextInputDialog
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Delete by ID");
        dialog.setHeaderText("Enter ");
        dialog.setContentText("ID:");

        // Show the dialog and wait for the user's response
        Optional<String> result = dialog.showAndWait();
        final String[] enteredId = new String[1];
        // Process the user's input
        result.ifPresent(id -> {
            // Save the entered ID to the class variable
            enteredId[0] = id;
        });

        // Handle the case where the user cancels the input
        if (!result.isPresent()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "No Outbound Shipment ID entered", ButtonType.OK);
            alert.showAndWait();
        }

        try {
            PreparedStatement stmt = getConnection().prepareStatement("DELETE FROM outbound_shipments WHERE outbound_id = ?");
            stmt.setString(1, enteredId[0]);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void insertOutbound() {
        String statusStr = statusField.getText();
        String quantityStr = quantityField.getText();
        String priceStr = priceField.getText();
        String clientIdStr = clientIdField.getText();
        String shipperIdStr = shipperIdField.getText();
        String productIdStr = productIdField.getText();
        String requestedDateStr = requestedDateField.getText();
        String sentDateStr = sentDateField.getText();

        try {
            // Convert IDs and dates to appropriate types

            int quantity = Integer.parseInt(quantityStr);
            double price = Double.parseDouble(priceStr);
            int clientId = Integer.parseInt(clientIdStr);
            int shipperId = Integer.parseInt(shipperIdStr);
            int productId = Integer.parseInt(productIdStr);
            Date requestedDate = Date.valueOf(requestedDateStr);
            Date sentDate = Date.valueOf(sentDateStr);

            // Insert the outbound shipment into the database
            insertOutboundIntoDatabase(statusStr, quantity, price, clientId, shipperId, productId, requestedDate, sentDate);

            // Display a success message (you can add this part)
            outboundStage.close();
        } catch (NumberFormatException e) {
            // Handle parsing errors
            e.printStackTrace(); // You might want to show an error message to the user
        } catch (SQLException e) {
            // Handle database errors
            e.printStackTrace(); // You might want to show an error message to the user
        }
    }

    private void insertOutboundIntoDatabase(String status, int quantity, double price, int clientId, int shipperId, int productId,
                                            Date requestedDate, Date sentDate) throws SQLException {
        try (PreparedStatement preparedStatement = getOutboundStatement()) {
            preparedStatement.setString(1, status);
            preparedStatement.setInt(2, quantity);
            preparedStatement.setDouble(3, price);
            preparedStatement.setInt(4, clientId);
            preparedStatement.setInt(5, shipperId);
            preparedStatement.setInt(6, productId);
            preparedStatement.setDate(7, requestedDate);
            preparedStatement.setDate(8, sentDate);

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
