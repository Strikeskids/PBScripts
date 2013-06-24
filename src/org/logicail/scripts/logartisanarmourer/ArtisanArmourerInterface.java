package org.logicail.scripts.logartisanarmourer;

import javafx.application.Application;
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
	public void start(final Stage stage) throws Exception {
		try {
			final Parent root = FXMLLoader.load(getClass().getResource("testlayout.fxml"));
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
