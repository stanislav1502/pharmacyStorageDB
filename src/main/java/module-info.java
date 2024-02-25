module org.magistraturasgi.pharmacystorage {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.magistraturasgi.pharmacystorage to javafx.fxml;
    exports org.magistraturasgi.pharmacystorage;
}