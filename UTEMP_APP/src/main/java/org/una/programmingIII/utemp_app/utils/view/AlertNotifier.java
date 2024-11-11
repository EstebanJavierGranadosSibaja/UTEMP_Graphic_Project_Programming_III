//package org.una.programmingIII.utemp_app.utils.view;
//
//import javafx.application.Platform;
//import javafx.scene.control.Alert;
//import javafx.scene.control.ButtonType;
//import lombok.Getter;
//import lombok.Setter;
//
//import java.util.Objects;
//import java.util.Optional;
//
//@Getter
//@Setter
//public class AlertNotifier {
//
//    private final AppContext instance = AppContext.getInstance();
//
//    public void show(Alert.AlertType type, String title, String message) {
//        Platform.runLater(() -> {
//            Alert alert = createAlert(type, title, message);
//            styleAlert(alert);
//            alert.show();
//        });
//    }
//
//    public void showModal(Alert.AlertType type, String title, String message) {
//        Platform.runLater(() -> {
//            Alert alert = createAlert(type, title, message);
//            styleAlert(alert);
//            setAlertOwner(alert);
//            alert.showAndWait();
//        });
//    }
//
//    public boolean showConfirmation(String title, String message) {
//        final boolean[] result = {false};
//        Platform.runLater(() -> {
//            Alert alert = createAlert(Alert.AlertType.CONFIRMATION, title, message);
//            styleAlert(alert);
//            setAlertOwner(alert);
//            Optional<ButtonType> alertResult = alert.showAndWait();
//            result[0] = alertResult.isPresent() && alertResult.get() == ButtonType.OK;
//        });
//        return result[0];
//    }
//
//    private Alert createAlert(Alert.AlertType type, String title, String message) {
//        Alert alert = new Alert(type);
//        alert.setTitle(title);
//        alert.setHeaderText(null);
//        alert.setContentText(message);
//        return alert;
//    }
//
//    private void styleAlert(Alert alert) {
//        alert.getDialogPane().setStyle("-fx-background-color: #4b4b4b;"); // Fondo gris medio
//        alert.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/General.css")).toExternalForm()); // Asegúrate de que la ruta sea correcta
//        alert.getDialogPane().setHeaderText(null);
//    }
//
//    private void setAlertOwner(Alert alert) {
////        if (instance.getViewManager() != null &&
////                instance.getViewManager().getPrimaryStage() != null) {
////            alert.initOwner(instance.getViewManager().getPrimaryStage());
////        } else {
////            // Maneja el caso donde el primaryStage es null
////            System.out.println("El primaryStage es null. No se puede establecer el propietario de la alerta.");
////        }
//    }
//}
//
//
