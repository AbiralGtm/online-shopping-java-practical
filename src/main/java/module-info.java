module com.abiralgautam.onlineshopping {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.abiralgautam.onlineshopping to javafx.fxml;
    exports com.abiralgautam.onlineshopping;
    exports com.abiralgautam.onlineshopping.Controllers;
    opens com.abiralgautam.onlineshopping.Controllers to javafx.fxml;
}
