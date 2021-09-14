import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class JavaFXTemplate extends Application {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}
	
	public void loadGame(Stage primaryStage) throws Exception {
		Parent game = FXMLLoader.load(getClass().getResource("/fxml/Game.fxml"));
		Scene s2 = new Scene(game, 900, 900);
		primaryStage.setScene(s2);
		primaryStage.show();
	}

	//feel free to remove the starter code from this method
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		primaryStage.setTitle("Welcome to Project 4");
		primaryStage.setResizable(false);
		Parent root = FXMLLoader.load(getClass().getResource("/fxml/cover.fxml"));
		Scene s1 = new Scene(root, 900, 900);
		primaryStage.setScene(s1);
		primaryStage.show();

		//  Fade transition from welcome screen to game screen.
		PauseTransition pause = new PauseTransition(Duration.seconds(3));
		pause.setOnFinished(event -> {
			FadeTransition ft = new FadeTransition(Duration.millis(1000));
			ft.setNode(root);
			ft.setFromValue(1.0);
	        ft.setToValue(0.0);
	        ft.setOnFinished(event3 -> {
		        try {
					loadGame(primaryStage);
				} catch (Exception e) {
					e.printStackTrace();
				}
	        });
        	ft.play();
        });
		pause.play();
	}

}
