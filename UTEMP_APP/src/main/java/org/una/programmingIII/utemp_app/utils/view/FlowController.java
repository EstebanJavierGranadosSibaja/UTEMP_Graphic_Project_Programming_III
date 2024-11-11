//package org.una.programmingIII.utemp_app.utils.view;
//
//import org.una.programmingIII.utemp_app.UTEMP_Application;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Objects;
//import java.util.ResourceBundle;
//import java.util.logging.Level;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.scene.layout.BorderPane;
//import javafx.scene.layout.Pane;
//import javafx.scene.layout.VBox;
//import javafx.stage.Modality;
//import javafx.stage.Stage;
//import javafx.stage.WindowEvent;
//import org.una.programmingIII.utemp_app.controllers.BaseController;
//import io.github.palexdev.materialfx.css.themes.MFXThemeManager;
//import io.github.palexdev.materialfx.css.themes.Themes;
//
//public class FlowController {
//
//    private static FlowController INSTANCE = null;
//    private static Stage mainStage;
//    private static ResourceBundle language;
//    private static final HashMap<String, FXMLLoader> loaders = new HashMap<>();
//
//    private FlowController() {
//    }
//
//    private static void createInstance() {
//        if (INSTANCE == null) {
//            synchronized (FlowController.class) {
//                if (INSTANCE == null) {
//                    INSTANCE = new FlowController();
//                }
//            }
//        }
//    }
//
//    public static FlowController getInstance() {
//        if (INSTANCE == null) {
//            createInstance();
//        }
//        return INSTANCE;
//    }
//
//    @Override
//    public Object clone() throws CloneNotSupportedException {
//        throw new CloneNotSupportedException();
//    }
//
//    public void InitializeFlow(Stage stage, ResourceBundle idioma) {
//        getInstance();
//        mainStage = stage;
//        FlowController.language = idioma;
//    }
//
//    private FXMLLoader getLoader(String name) {
//        FXMLLoader loader = loaders.get(name);
//        if (loader == null) {
//            synchronized (FlowController.class) {
//                try {
//                    loader = new FXMLLoader(UTEMP_Application.class.getResource("/views/" + name + ".fxml"), language);
//                    loader.load();
//                    loaders.put(name, loader);
//                } catch (Exception ex) {
//                    loader = null;
//                    java.util.logging.Logger.getLogger(FlowController.class.getName()).log(Level.SEVERE, "Creando loader [" + name + "].", ex);
//                }
//            }
//        }
//        return loader;
//    }
//
//    public void goMain() {
//        try {
//            mainStage.setScene(new Scene(FXMLLoader.load(Objects.requireNonNull(UTEMP_Application.class.getResource("/views/MainBackgroundView.fxml")), language)));
//            MFXThemeManager.addOn(mainStage.getScene(), Themes.DEFAULT, Themes.LEGACY);
//            mainStage.show();
//        } catch (IOException ex) {
//            java.util.logging.Logger.getLogger(FlowController.class.getName()).log(Level.SEVERE, "Error inicializando la vista base.", ex);
//        }
//    }
//
//    public void goView(String viewName) {
//        goView(viewName, "Center", null);
//    }
//
//    public void goView(String viewName, String accion) {
//        goView(viewName, "Center", accion);
//    }
//
//    public void goView(String viewName, String location, String accion) {
//        FXMLLoader loader = getLoader(viewName);
//        BaseController controller = loader.getController();
////        controllersetAccion(accion);
//        controller.initialize();
//        Stage stage = controller.getStage();
//        if (stage == null) {
//            stage = mainStage;
//            controller.setStage(stage);
//        }
//        switch (location) {
//            case "Center":
//                VBox vBox = ((VBox) ((BorderPane) stage.getScene().getRoot()).getCenter());
//                vBox.getChildren().clear();
//                vBox.getChildren().add(loader.getRoot());
//                break;
//            case "Top":
//                break;
//            case "Bottom":
//                break;
//            case "Right":
//                break;
//            case "Left":
//                break;
//            default:
//                break;
//        }
//    }
//
//    public void goViewInStage(String viewName, Stage stage) {
//        FXMLLoader loader = getLoader(viewName);
//        BaseController controller = loader.getController();
//        controller.setStage(stage);
//        stage.getScene().setRoot(loader.getRoot());
//        MFXThemeManager.addOn(stage.getScene(), Themes.DEFAULT, Themes.LEGACY);
//
//    }
//
//    public void goViewInWindow(String viewName) {
//        FXMLLoader loader = getLoader(viewName);
//        BaseController controller = loader.getController();
//        controller.initialize();
//        Stage stage = new Stage();
//        //stage.getIcons().add(new Image("../resources/cr/ac/una/preguntadospackage/resources/AppIcon.png"));
////        stage.setTitle(controller.getNombreVista());
//        stage.setOnHidden((WindowEvent event) -> {
//            controller.getStage().getScene().setRoot(new Pane());
//            controller.setStage(null);
//        });
//        controller.setStage(stage);
//        Parent root = loader.getRoot();
//        Scene scene = new Scene(root);
//        MFXThemeManager.addOn(scene, Themes.DEFAULT, Themes.LEGACY);
//        stage.setScene(scene);
//        stage.centerOnScreen();
//        stage.show();
//    }
//
//    public void goViewInWindowModal(String viewName, Stage parentStage, Boolean resizable) {
//        FXMLLoader loader = getLoader(viewName);
//        BaseController controller = loader.getController();
//        controller.initialize();
//        Stage stage = new Stage();
//        //stage.getIcons().add(new Image("../resources/cr/ac/una/preguntadospackage/resources/AppIcon.png"));
////        stage.setTitle(controller.getNombreVista());
//        stage.setResizable(resizable);
//        stage.setOnHidden((WindowEvent event) -> {
//            controller.getStage().getScene().setRoot(new Pane());
//            controller.setStage(null);
//        });
//        controller.setStage(stage);
//        Parent root = loader.getRoot();
//        Scene scene = new Scene(root);
//        MFXThemeManager.addOn(scene, Themes.DEFAULT, Themes.LEGACY);
//        stage.setScene(scene);
//        stage.initModality(Modality.WINDOW_MODAL);
//        stage.initOwner(parentStage);
//        stage.centerOnScreen();
//        stage.showAndWait();
//
//    }
//
//    public BaseController getController(String viewName) {
//        return getLoader(viewName).getController();
//    }
//
//    public void limpiarLoader(String view){
//        loaders.remove(view);
//    }
//
//    public static void setLanguage(ResourceBundle language) {
//        FlowController.language = language;
//    }
//
//    public void initialize() {
//        loaders.clear();
//    }
//
//    public void salir() {
//        mainStage.close();
//    }
//
//}
