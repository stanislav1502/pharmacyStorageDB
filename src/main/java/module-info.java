module org.magistraturasgi.pharmacystoragedb {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.magistraturasgi.pharmacystoragedb to javafx.fxml;
    exports org.magistraturasgi.pharmacystoragedb;
}