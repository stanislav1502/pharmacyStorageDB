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

public class ShipperController {

    @FXML
    private TextField shipperNameField;

    @FXML
    private TextField speedAmountField;

    @FXML
    private TextField speedScaleField;

    @FXML
    private TextField priceField;

    @FXML
    private Stage shipperStage;

    private PreparedStatement addShipperPrepared = null;
    private static final String INSERT_SHIPPER_QUERY = "INSERT INTO shippers VALUES (shipper_t(shipper_seq.NEXTVAL, ?, ?, ?, ?))";

    public void setShipperStage(Stage shipperStage) {
        this.shipperStage = shipperStage;
    }

    public void showAddShipperDialog() {
        try {
            URL url = getClass().getResource("shipperDialog.fxml");
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            // Create a new stage for the ShipperDetails window
            Stage shipperStage = new Stage();
            shipperStage.initModality(Modality.APPLICATION_MODAL);
            shipperStage.setTitle("Shipper Details");
            shipperStage.setScene(new Scene(root));

            // Set the ShipperController for the loaded FXML
            ShipperController shipperController = loader.getController();
            shipperController.setShipperStage(shipperStage);

            // Show the ShipperDetails window
            shipperStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }
    }

    public void showDelShipperDialog() {
        // Create a TextInputDialog
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Delete by ID");
        dialog.setHeaderText("Enter Shipper ID:");
        dialog.setContentText("Shipper ID:");

        // Show the dialog and wait for the user's response
        Optional<String> result = dialog.showAndWait();
        final String[] enteredShipperId = new String[1];

        // Process the user's input
        result.ifPresent(shipperId -> {
            // Save the entered Supplier ID to the class variable
            enteredShipperId[0] = shipperId;
        });

        // Handle the case where the user cancels the input
        if (!result.isPresent()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "No Shipper ID entered", ButtonType.OK);
            alert.showAndWait();
        }

        try {
            // Use the correct column name and adjust the SQL query
            PreparedStatement stmt = getConnection().prepareStatement("DELETE FROM shippers WHERE shipper_id = ?");
            stmt.setString(1, enteredShipperId[0]);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @FXML
    private void insertShipper() {
        String shipperName = shipperNameField.getText();
        String speedAmountStr = speedAmountField.getText();
        String speedScale = speedScaleField.getText();
        String priceStr = priceField.getText();

        try {
            // Convert speed amount and price to appropriate types
            int speedAmount = Integer.parseInt(speedAmountStr);
            double price = Double.parseDouble(priceStr);

            // Insert the shipper into the database
            insertShipperIntoDatabase(shipperName, speedAmount, speedScale, price);

            // Display a success message (you can add this part)
            shipperStage.close();
        } catch (NumberFormatException e) {
            // Handle parsing errors
            e.printStackTrace(); // You might want to show an error message to the user
        } catch (SQLException e) {
            // Handle database errors
            e.printStackTrace(); // You might want to show an error message to the user
        }
    }

    private void insertShipperIntoDatabase(String shipperName, int speedAmount, String speedScale, double price) throws SQLException {
        try (PreparedStatement preparedStatement = getShipperStatement()) {
            preparedStatement.setString(1, shipperName);
            preparedStatement.setInt(2, speedAmount);
            preparedStatement.setString(3, speedScale);
            preparedStatement.setDouble(4, price);

            preparedStatement.executeUpdate();
        }
    }

    private PreparedStatement getShipperStatement() {
        if (addShipperPrepared == null) {
            try {
                addShipperPrepared = getConnection().prepareStatement(INSERT_SHIPPER_QUERY);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return addShipperPrepared;
    }
}
