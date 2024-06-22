module com.abiralgautam.onlineshopping {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.abiralgautam.onlineshopping to javafx.fxml;
    exports com.abiralgautam.onlineshopping;
}
