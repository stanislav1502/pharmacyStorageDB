package org.magistraturasgi.pharmacystorage;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import org.magistraturasgi.pharmacystorage.dbControllers.*;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MenuController {
    @FXML
    private Label tableNameText;
    @FXML
    private TableView resultsTable;
@FXML
private Button addBtn;
    @FXML
    private Button delBtn;

    private String activeTable = "";

    @FXML
    protected void onProductsButtonClick() {
        activeTable = "products";
        tableNameText.setText("Products:");
        addBtn.setText("Add product");
        addBtn.setDisable(false);
        delBtn.setText("Delete product");
        delBtn.setDisable(false);
        ResultSet products = DBUtil.getInstance().getProducts();
        try {
            setTableResultset(products);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onClientsButtonClick() {
        activeTable = "clients";
        tableNameText.setText("Clients:");
        addBtn.setText("Add client");
        addBtn.setDisable(false);
        delBtn.setText("Delete client");
        delBtn.setDisable(false);
        ResultSet clients = DBUtil.getInstance().getClients();
        try {
            setTableResultset(clients);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onSuppliersButtonClick() {
        activeTable = "suppliers";
        tableNameText.setText("Suppliers:");
        addBtn.setText("Add supplier");
        addBtn.setDisable(false);
        delBtn.setText("Delete supplier");
        delBtn.setDisable(false);
        ResultSet suppliers = DBUtil.getInstance().getSuppliers();
        try {
            setTableResultset(suppliers);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onShippersButtonClick() {
        activeTable = "shippers";
        tableNameText.setText("Shippers:");
        addBtn.setText("Add shipper");
        addBtn.setDisable(false);
        delBtn.setText("Delete shipper");
        delBtn.setDisable(false);
        ResultSet shippers = DBUtil.getInstance().getShippers();
        try {
            setTableResultset(shippers);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onInboundsButtonClick() {
        activeTable = "inbound";
        tableNameText.setText("Inbound deliveries:");
        addBtn.setText("Add delivery");
        addBtn.setDisable(false);
        delBtn.setText("Delete delivery");
        delBtn.setDisable(false);
        ResultSet inbounds = DBUtil.getInstance().getInbounds();
        try {
            setTableResultset(inbounds);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onOutboundsButtonClick() {
        activeTable = "outbound";
        tableNameText.setText("Outbound deliveries:");
        addBtn.setText("Add delivery");
        addBtn.setDisable(false);
        delBtn.setText("Delete delivery");
        delBtn.setDisable(false);
        ResultSet outbounds = DBUtil.getInstance().getOutbounds();
        try {
            setTableResultset(outbounds);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onAddBtnClick() {
        switch (activeTable) {
            case "products": ProductController.showAddProductDialog(); break;
            case "clients": ClientController.showAddClientDialog(); break;
            case "suppliers": SupplierController.showAddSupplierDialog(); break;
            case "shippers": ShipperController.showAddShipperDialog(); break;
            case "inbound": InboundController.showAddInboundDialog(); break;
            case "outbound": OutboundController.showAddOutboundDialog(); break;
        }
    }

    public void onDelBtnClick(ActionEvent actionEvent) {
        switch (activeTable) {
            case "products": ProductController.showDelProductDialog(); break;
            case "clients": ClientController.showDelClientDialog(); break;
            case "suppliers": SupplierController.showDelSupplierDialog(); break;
            case "shippers": ShipperController.showDelShipperDialog(); break;
            case "inbound": InboundController.showDelInboundDialog(); break;
            case "outbound": OutboundController.showDelOutboundDialog(); break;
        }
    }

    public void setTableResultset(ResultSet resultSet) throws SQLException {
        ObservableList<ObservableList> data = FXCollections.observableArrayList();

        resultsTable.getItems().clear();
        resultsTable.getColumns().clear();
        resultsTable.refresh();
        int tblColCount=resultSet.getMetaData().getColumnCount();
        for (int i = 0; i < tblColCount; i++) {
            final int j = i;
            TableColumn col = new TableColumn(resultSet.getMetaData().getColumnName(i + 1));
            col.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param ->
                    new SimpleStringProperty(param.getValue().get(j).toString()));
            resultsTable.getColumns().add(col);
        }
        while (resultSet.next()) {
            //Iterate Row
            ObservableList<String> row = FXCollections.observableArrayList();
            for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                //Iterate Column
                row.add(resultSet.getString(i));
            }
            data.add(row);
        }
        //FINALLY ADDED TO TableView
        resultsTable.setItems(data);
    }


}
