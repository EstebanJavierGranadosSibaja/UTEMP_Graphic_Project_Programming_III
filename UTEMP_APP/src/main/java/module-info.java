module org.una.programmingiii.utemp_app {
    // Requiere módulos de JavaFX
    requires javafx.controls; // Para controles de UI básicos
    requires javafx.fxml;
    requires MaterialFX; // Para manejar FXML

    // Requiere bibliotecas para manipulación y estilo
//    requires org.controlsfx.controls; // Controles adicionales para JavaFX
//    requires com.dlsc.formsfx; // Proporciona funcionalidad para formularios avanzados
//    requires net.synedra.validatorfx; // Validación de formularios en JavaFX

    //opcionales
//    requires org.kordamp.ikonli.javafx; // Proporciona una colección de íconos
//    requires org.kordamp.bootstrapfx.core; // Estilos de Bootstrap para JavaFX
//    requires eu.hansolo.tilesfx; // Componentes gráficos adicionales para JavaFX
//    requires com.almasb.fxgl.all; // Framework para el desarrollo de juegos en JavaFX


    // Abre el paquete principal para FXML
    opens org.una.programmingiii.utemp_app to javafx.fxml; // Permite que FXML acceda al paquete principal

    // Abre otros paquetes que necesiten ser accesibles para la reflexión
    opens org.una.programmingiii.utemp_app.controllers to javafx.fxml; // Permite que FXML acceda a los controladores
    opens org.una.programmingiii.utemp_app.models to com.fasterxml.jackson.databind; // Modelos para JSON
    opens org.una.programmingiii.utemp_app.responses to com.fasterxml.jackson.databind; // Respuestas para JSON
    opens org.una.programmingiii.utemp_app.request to com.fasterxml.jackson.databind; // Solicitudes para JSON

    // Exporta el paquete principal
    exports org.una.programmingiii.utemp_app; // Exporta para su uso en otros módulos
}

//    de referencia
    /*
     Abre el paquete 'model' para que Jackson y JavaFX puedan acceder
    opens com.example.cliente.model to com.fasterxml.jackson.databind, javafx.base;

    // Abre otros paquetes para JavaFX
    opens com.example.cliente to javafx.fxml;
    opens com.example.cliente.controller to javafx.fxml;

     Exporta los paquetes para su uso
    exports com.example.cliente;
    exports com.example.cliente.controller;
    */