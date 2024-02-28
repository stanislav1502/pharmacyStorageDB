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

public class ClientController {

    @FXML
    private TextField clientNameField;

    @FXML
    private TextField addressField;

    @FXML
    private TextField accessLevelField;

    @FXML
    private Stage clientStage;

    private PreparedStatement addClientPrepared = null;
    private static final String INSERT_CLIENT_QUERY = "INSERT INTO clients (client_name, client_address, client_accessLevel) VALUES (?, ?, ?)";

    public void setClientStage(Stage clientStage) {
        this.clientStage = clientStage;
    }

    public void showAddClientDialog() {
        try {
            URL url = getClass().getResource("clientDialog.fxml");
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            // Create a new stage for the ClientDetails window
            Stage clientStage = new Stage();
            clientStage.initModality(Modality.APPLICATION_MODAL);
            clientStage.setTitle("Client Details");
            clientStage.setScene(new Scene(root));

            // Set the ClientController for the loaded FXML
            ClientController clientController = loader.getController();
            clientController.setClientStage(clientStage);

            // Show the ClientDetails window
            clientStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }
    }

    public void showDelClientDialog() {

    }

    @FXML
    private void insertClient() {
        String clientName = clientNameField.getText();
        String address = addressField.getText();
        String accessLevelStr = accessLevelField.getText();

        try {
            // Convert access level to the appropriate type
            int accessLevel = Integer.parseInt(accessLevelStr);

            // Insert the client into the database
            insertClientIntoDatabase(clientName, address, accessLevel);

            // Display a success message (you can add this part)
        } catch (NumberFormatException e) {
            // Handle parsing errors
            e.printStackTrace(); // You might want to show an error message to the user
        } catch (SQLException e) {
            // Handle database errors
            e.printStackTrace(); // You might want to show an error message to the user
        }
    }

    private void insertClientIntoDatabase(String clientName, String address, int accessLevel) throws SQLException {
        try (PreparedStatement preparedStatement = getClientStatement()) {
            preparedStatement.setString(1, clientName);
            preparedStatement.setString(2, address);
            preparedStatement.setInt(3, accessLevel);

            preparedStatement.executeUpdate();
        }
    }

    private PreparedStatement getClientStatement() {
        if (addClientPrepared == null) {
            try {
                addClientPrepared = getConnection().prepareStatement(INSERT_CLIENT_QUERY);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return addClientPrepared;
    }


}
