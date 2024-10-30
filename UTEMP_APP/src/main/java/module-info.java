module org.una.programmingIII.utemp_app {
    // Requiere módulos de JavaFX
    // Para controles de UI básicos
    // Para manejar FXML

    requires com.fasterxml.jackson.databind;
    requires org.apache.httpcomponents.client5.httpclient5;
    requires static lombok;
    requires validation.api;
    requires jakarta.validation;
    requires org.slf4j; // Framework para el desarrollo de juegos en JavaFX
    requires MaterialFX;

    // Abre el paquete principal para FXML
    opens org.una.programmingIII.utemp_app to javafx.fxml; // Exporta para su uso en otros módulos

    opens org.una.programmingIII.utemp_app.view_controllers to javafx.fxml; // Permite que FXML acceda a los controladores
    // Abre otros paquetes que necesiten ser accesibles para la reflexión
    opens org.una.programmingIII.utemp_app.dtos to com.fasterxml.jackson.databind; // Modelos para JSON
    opens org.una.programmingIII.utemp_app.services.responses to com.fasterxml.jackson.databind; // Respuestas para JSON
    opens org.una.programmingIII.utemp_app.services.request to com.fasterxml.jackson.databind; // Solicitudes para JSON

    // Exporta el paquete principal
    exports org.una.programmingIII.utemp_app;
    exports org.una.programmingIII.utemp_app.exceptions;
    opens org.una.programmingIII.utemp_app.exceptions to javafx.fxml;
    exports org.una.programmingIII.utemp_app.exceptions.http;
    opens org.una.programmingIII.utemp_app.exceptions.http to javafx.fxml;

    exports org.una.programmingIII.utemp_app.test;
    opens org.una.programmingIII.utemp_app.test to javafx.fxml; // Cambia esto si el paquete es diferente
}