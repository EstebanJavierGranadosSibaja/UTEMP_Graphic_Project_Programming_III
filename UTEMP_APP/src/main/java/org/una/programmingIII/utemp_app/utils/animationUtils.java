//package cr.ac.una.preguntadospackage.util;
//
//import javafx.animation.FadeTransition;
//import javafx.animation.PauseTransition;
//import javafx.animation.TranslateTransition;
//import javafx.scene.image.ImageView;
//import javafx.util.Duration;
//
//public class animationUtils {
//
//    // singleton instance of the class that handles the animation
//    private static animationUtils INSTANCE = null;
//
//    // private constructor to prevent instantiation
//    private animationUtils() {
//    }
//
//    // method to create an instance of the class
//    private static void createInstance() {
//        if (INSTANCE == null) {
//            synchronized (animationUtils.class) {
//                if (INSTANCE == null) {
//                    INSTANCE = new animationUtils();
//                }
//            }
//        }
//    }
//
//    // method to get the instance of the class
//    public static animationUtils getInstance() {
//        if (INSTANCE == null) {
//            createInstance();
//        }
//        return INSTANCE;
//    }
//
//    // method to prevent cloning of the class
//    @Override
//    public Object clone() throws CloneNotSupportedException {
//        throw new CloneNotSupportedException();
//    }
//
//    // method to play the animation
//    public void playAnimation(String effect, ImageView imageView, int oldX, int oldY, int newX, int newY) {
//        // depending on the effect, play the corresponding animation
//        switch (effect) {
//            case "fade": {
//                // play the blink animation
//                imageView.setVisible(true);
//                FadeTransition ft = new FadeTransition(Duration.millis(1000), imageView);
//                ft.setFromValue(0.8);
//                ft.setToValue(0.0);
//                ft.setCycleCount(3);
//                ft.setAutoReverse(true);
//                ft.setOnFinished(event -> {
//                    imageView.setVisible(false);
//                    ft.stop();
//                });
//                ft.play();
//                break;
//            }
//            case "nonHidingFade": {
//                // play the fade animation without hiding the image
//                imageView.setVisible(true);
//                FadeTransition ft = new FadeTransition(Duration.millis(900), imageView);
//                ft.setFromValue(0.0);
//                ft.setToValue(1.0);
//                ft.setCycleCount(3);
//                ft.setAutoReverse(true);
//                ft.setOnFinished(event -> {
//                    ft.stop();
//                });
//                ft.play();
//                break;
//            }
//
//            case "translate": {
//                // translate the image from the old position to the new position
//                System.out.println("moving image");
//                imageView.setLayoutX(oldX); // just to make shure it is in the right position
//                imageView.setLayoutY(oldY); // just to make shure it is in the right position
//                imageView.setVisible(true); // just to make shure it is visible
//                TranslateTransition tt = new TranslateTransition(Duration.millis(1000), imageView);
//                tt.setFromX(oldX);
//                tt.setFromY(oldY);
//                tt.setToX(newX);
//                tt.setToY(newY);
//                tt.setOnFinished(event -> {
//                    imageView.setLayoutX(newX);
//                    imageView.setLayoutY(newY);
//                    tt.stop();
//                });
//                break;
//            }
//            case "slowPopUp": {
//                // play a pause animation to show the slow pop up
//                imageView.setVisible(true);
//                FadeTransition ft = new FadeTransition(Duration.millis(3000), imageView);
//                ft.setFromValue(0.999999);
//                ft.setToValue(1.0);
//                ft.setCycleCount(1);
//                ft.setAutoReverse(false);
//                ft.setOnFinished(event -> {
//                    imageView.setVisible(false);
//                    ft.stop();
//                });
//
//                /*
//                PauseTransition pt = new PauseTransition(Duration.millis(4000));
//
//                pt.setOnFinished(event -> {
//                    imageView.setVisible(false);
//                    pt.stop();
//                });
//                 */
//                ft.play();
//                //pt.play();
//
//                break;
//            }
//            case "incorrect_correct":{
//                // play the longer fade animation
//                imageView.setVisible(true);
//                FadeTransition ft = new FadeTransition(Duration.millis(1000), imageView);
//                ft.setFromValue(0.0);
//                ft.setToValue(1.0);
//                ft.setCycleCount(1);
//                ft.setAutoReverse(true);
//                ft.setOnFinished(event -> {
//                    imageView.setVisible(false);
//                    ft.stop();
//                });
//                ft.play();
//                break;
//            }
//            case "pause":
//                // play the pause animation
//                PauseTransition pt = new PauseTransition(Duration.millis(3000));
//                pt.setOnFinished(event -> {
//                    pt.stop();
//                });
//                pt.play();
//                break;
//        }
//    }
//
//    public void stopAnimation(String effect, ImageView imageView) {
//        // depending on the effect, stop the corresponding animation
//
//    }
//
//}
