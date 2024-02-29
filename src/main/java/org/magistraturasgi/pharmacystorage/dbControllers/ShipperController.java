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
    private static final String INSERT_SHIPPER_QUERY =
            "INSERT INTO shippers (shipper_name, speed_amount, speed_scale, shipper_price) VALUES (?, ?, ?, ?)";

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

    public void showDelShipperDialog(){

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
