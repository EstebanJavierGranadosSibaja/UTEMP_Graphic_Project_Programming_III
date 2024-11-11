//package cr.ac.una.preguntadospackage.util;
//
//import javafx.scene.image.Image;
//import javafx.scene.media.Media;
//import javafx.scene.media.MediaPlayer;
//
//import java.net.URL;
//
//public class soundUtils {
//
//    // singleton instance of the class that handles the sound
//    private static soundUtils INSTANCE = null;
//
//    // private constructor to prevent instantiation
//    private soundUtils() {
//    }
//
//    // method to create an instance of the class
//    private static void createInstance() {
//        if (INSTANCE == null) {
//            synchronized (soundUtils.class) {
//                if (INSTANCE == null) {
//                    INSTANCE = new soundUtils();
//                }
//            }
//        }
//    }
//
//    // method to get the instance of the class
//    public static soundUtils getInstance() {
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
//    // method to play the sound
//    public void playSound(String effect) {
//        // depending on the effect, play the corresponding sound
//        switch (effect) {
//            case "correct":
//                // play the correct sound
//                URL url5 = getClass().getResource("/cr/ac/una/preguntadospackage/resources/correct.mp3");
//                assert url5 != null;
//                Media media5 = new Media(url5.toString());
//                MediaPlayer mediaPlayer5 = new MediaPlayer(media5);
//                mediaPlayer5.play();
//                break;
//            case "incorrect":
//                // play the incorrect sound
//                URL url = getClass().getResource("/cr/ac/una/preguntadospackage/resources/incorrect.mp3");
//                assert url != null;
//                Media media = new Media(url.toString());
//                MediaPlayer mediaPlayer = new MediaPlayer(media);
//                mediaPlayer.play();
//                break;
//            case "click":
//            //case "green": return new Image(getClass().getResource("/cr/ac/una/preguntadospackage/resources/PawnGreen.png").toString());
//                URL url2 = getClass().getResource("/cr/ac/una/preguntadospackage/resources/mini_click.wav");
//                Media media2 = new Media(url2.toString());
//                MediaPlayer mediaPlayer2 = new MediaPlayer(media2);
//                mediaPlayer2.play();
//                break;
//            case "roulleteClick":
//                // play the roullete click sound
//                URL url1 = getClass().getResource("/cr/ac/una/preguntadospackage/resources/start_spin.mp3");
//                Media media1 = new Media(url1.toString());
//                MediaPlayer mediaPlayer1 = new MediaPlayer(media1);
//                mediaPlayer1.play();
//
//                break;
//            case "lose":
//                // play the lose sound
//                break;
//            case "menu":
//                // play the menu sound
//                break;
//            case "question":
//                // play the question sound
//
//                URL url3 = getClass().getResource("/cr/ac/una/preguntadospackage/resources/question_pop.wav");
//                Media media3 = new Media(url3.toString());
//                MediaPlayer mediaPlayer3 = new MediaPlayer(media3);
//                mediaPlayer3.play();
//
//
//                break;
//            case "select":
//                // play the help sound
//
//                URL url4 = getClass().getResource("/cr/ac/una/preguntadospackage/resources/select.mp3");
//                assert url4 != null;
//                Media media4 = new Media(url4.toString());
//                MediaPlayer mediaPlayer4 = new MediaPlayer(media4);
//                mediaPlayer4.play();
//
//                break;
//            case "exit":
//                // play the exit sound
//                break;
//
//
//            default:
//                // play the default sound
//                break;
//        }
//    }
//}
