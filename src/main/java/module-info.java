module org.aruiz {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.aruiz to javafx.fxml;
    exports org.aruiz;
}
