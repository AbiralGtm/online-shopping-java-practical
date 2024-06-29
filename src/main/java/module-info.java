module com.abiralgautam.onlineshopping {
    requires javafx.controls;
    requires javafx.fxml;
    requires jakarta.servlet;
    requires java.sql;
    opens com.abiralgautam.onlineshopping to javafx.fxml;
    exports com.abiralgautam.onlineshopping;
}
