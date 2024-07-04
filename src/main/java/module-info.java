module com.abiralgautam.onlineshopping {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires jakarta.servlet;
    requires java.sql;
    requires org.json;
    opens com.abiralgautam.onlineshopping to javafx.fxml;
    exports com.abiralgautam.onlineshopping;
    opens com.abiralgautam.onlineshopping.models to javafx.base;
    exports com.abiralgautam.onlineshopping.models;
}
