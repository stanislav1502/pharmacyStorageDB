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

public class ClientController {

    @FXML
    private ChoiceBox clientTypeField;

    @FXML
    private TextField clientNameField;

    @FXML
    private TextField addressField;

    @FXML
    private TextField accessLevelField;

    @FXML
    private Stage clientStage;

    private PreparedStatement addClientPrepared = null;
    private static final String INSERT_CLIENT_QUERY = "INSERT INTO clients VALUES (?(client_seq.NEXTVAL,?, ?, ?))";

    public void setClientStage(Stage clientStage) {
        this.clientStage = clientStage;
        clientTypeField.getItems().addAll("Hospital","Pharmacy","Drugstore");
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
        // Create a TextInputDialog
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Delete by ID");
        dialog.setHeaderText("Enter Client ID:");
        dialog.setContentText("ID:");

        // Show the dialog and wait for the user's response
        Optional<String> result = dialog.showAndWait();
        final String[] enteredClientId = new String[1];

        // Process the user's input
        result.ifPresent(clientId -> {
            // Save the entered Client ID to the class variable
            enteredClientId[0] = clientId;
        });

        // Handle the case where the user cancels the input
        if (!result.isPresent()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "No Client ID entered", ButtonType.OK);
            alert.showAndWait();
        }

        try {
            // Use the correct column name and adjust the SQL query
            PreparedStatement stmt = getConnection().prepareStatement("DELETE FROM clients WHERE client_id = ?");
            stmt.setString(1, enteredClientId[0]);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void insertClient() {
        String clientType = clientTypeField.getValue().toString();
        String clientName = clientNameField.getText();
        String address = addressField.getText();
        String accessLevelStr = accessLevelField.getText();

        try {
            // Convert access level to the appropriate type
            int accessLevel = Integer.parseInt(accessLevelStr);

            // Insert the client into the database
            insertClientIntoDatabase(clientType, clientName, address, accessLevel);

            // Display a success message (you can add this part)
            clientStage.close();
        } catch (NumberFormatException e) {
            // Handle parsing errors
            e.printStackTrace(); // You might want to show an error message to the user
        } catch (SQLException e) {
            // Handle database errors
            e.printStackTrace(); // You might want to show an error message to the user
        }
    }

    private void insertClientIntoDatabase(String clientType, String clientName, String address, int accessLevel) throws SQLException {

        try (PreparedStatement preparedStatement = getClientStatement()) {
            switch (clientType){
                case "Hospital": preparedStatement.setString(1, "hospital_t"); break;
                case "Pharmacy": preparedStatement.setString(1, "pharmacy_t"); break;
                case "Drugstore": preparedStatement.setString(1, "drugstore_t"); break;
                default: preparedStatement.setString(1, "client_t");
            }
            preparedStatement.setString(2, clientName);
            preparedStatement.setString(3, address);
            preparedStatement.setInt(4, accessLevel);

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
