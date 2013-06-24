package org.logicail.scripts.logartisanarmourer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 24/06/13
 * Time: 18:42
 */
public class ArtisanArmourerInterface extends Application {
	@Override
	public void start(Stage stage) throws Exception {
		Platform.runLater(new Runnable() {
			public void run() {
				try {
					final Parent root = FXMLLoader.load(getClass().getResource("testlayout.fxml"));
					final Stage stage = new Stage() {{
						setScene(new Scene(root, 250, 315));
						show();
					}};
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
