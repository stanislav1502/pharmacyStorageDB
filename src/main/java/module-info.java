module org.magistraturasgi.pharmacystorage {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires ojdbc7;


    opens org.magistraturasgi.pharmacystorage to javafx.fxml;
    exports org.magistraturasgi.pharmacystorage;
    exports org.magistraturasgi.pharmacystorage.dbControllers;
    opens org.magistraturasgi.pharmacystorage.dbControllers to javafx.fxml;
}