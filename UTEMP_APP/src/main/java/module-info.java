module org.una.programmingIII.utemp_app {

    // Abre el paquete principal para FXML
    opens org.una.programmingIII.utemp_app to javafx.fxml;

    // Abre paquetes para FXML y JSON
    opens org.una.programmingIII.utemp_app.utils to com.fasterxml.jackson.databind, javafx.fxml;
    opens org.una.programmingIII.utemp_app.services.models to com.fasterxml.jackson.databind;
    opens org.una.programmingIII.utemp_app.responses to com.fasterxml.jackson.databind;
    opens org.una.programmingIII.utemp_app.services to com.fasterxml.jackson.databind;
    opens org.una.programmingIII.utemp_app.requests to com.fasterxml.jackson.databind;
    opens org.una.programmingIII.utemp_app.dtos to com.fasterxml.jackson.databind;
    opens org.una.programmingIII.utemp_app.exceptions.http to javafx.fxml;
    opens org.una.programmingIII.utemp_app.controllers to javafx.fxml;
    opens org.una.programmingIII.utemp_app.exceptions to javafx.fxml;
    opens org.una.programmingIII.utemp_app.utils.view to com.fasterxml.jackson.databind, javafx.fxml;
    opens org.una.programmingIII.utemp_app.utils.services to com.fasterxml.jackson.databind, javafx.fxml;

    // Exporta paquetes
    exports org.una.programmingIII.utemp_app.exceptions.http;
    exports org.una.programmingIII.utemp_app.exceptions;
    exports org.una.programmingIII.utemp_app.dtos.enums;
    exports org.una.programmingIII.utemp_app;

    exports org.una.programmingIII.utemp_app.utils.services;
    exports org.una.programmingIII.utemp_app.utils.view;
    exports org.una.programmingIII.utemp_app.utils;
    exports org.una.programmingIII.utemp_app.responses;
    exports org.una.programmingIII.utemp_app.dtos;
    exports org.una.programmingIII.utemp_app.services;
    exports org.una.programmingIII.utemp_app.services.models;


    // Requiere módulos de JavaFX
    requires org.apache.httpcomponents.client5.httpclient5;
    requires org.apache.httpcomponents.core5.httpcore5;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires com.fasterxml.jackson.databind;
    requires spring.data.commons;
    requires jakarta.validation;
    requires validation.api;
    requires static lombok;
    requires MaterialFX;
    requires org.slf4j;
    requires java.sql;


}
