package org.magistraturasgi.pharmacystorage;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;

public class ProductController {



    private void showAddProductDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Product");
        dialog.setHeaderText("Enter the name of the new product:");
        dialog.setContentText("Product Name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(productName -> {
            // Use the entered product name to perform database interaction
            // You can replace the following line with your actual logic
            System.out.println("Adding product: " + productName);

            // Display a confirmation dialog
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Product Added");
            alert.setHeaderText(null);
            alert.setContentText("Product '" + productName + "' added successfully.");

            alert.showAndWait();
        });
    }
}
