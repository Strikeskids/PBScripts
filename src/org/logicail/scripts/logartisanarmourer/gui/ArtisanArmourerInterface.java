package org.logicail.scripts.logartisanarmourer.gui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.logicail.scripts.logartisanarmourer.LogArtisanArmourer;
import org.logicail.scripts.logartisanarmourer.gui.model.Person;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 24/06/13
 * Time: 18:42
 */
public class ArtisanArmourerInterface extends Application {

	private ObservableList<Person> personData = FXCollections.observableArrayList();

	public ArtisanArmourerInterface() {
		personData.add(new Person("Hans", "Muster"));
		personData.add(new Person("Ruth", "Mueller"));
		personData.add(new Person("Heinz", "Kurz"));
		personData.add(new Person("Cornelia", "Meier"));
		personData.add(new Person("Werner", "Meyer"));
		personData.add(new Person("Lydia", "Kunz"));
		personData.add(new Person("Anna", "Best"));
		personData.add(new Person("Stefan", "Meier"));
		personData.add(new Person("Martin", "Mueller"));
	}

	/**
	 * Returns the data as an observable list of Persons.
	 *
	 * @return
	 */
	public ObservableList<Person> getPersonData() {
		return personData;
	}

	private Stage primaryStage;
	private BorderPane rootLayout;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("AddressApp");

		try {
			// Load the root layout from the fxml file
			FXMLLoader loader = new FXMLLoader(LogArtisanArmourer.class.getResource("gui/view/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			// Exception gets thrown if the fxml file could not be loaded
			e.printStackTrace();
		}

		showPersonOverview();
	}

	/**
	 * Returns the main stage.
	 *
	 * @return
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	/**
	 * Shows the person overview scene.
	 */
	public void showPersonOverview() {
		try {
			// Load the fxml file and set into the center of the main layout
			FXMLLoader loader = new FXMLLoader(ArtisanArmourerInterface.class.getResource("view/PersonOverview.fxml"));
			AnchorPane overviewPage = (AnchorPane) loader.load();
			rootLayout.setCenter(overviewPage);

			// Give the controller access to the main app
			PersonOverviewController controller = loader.getController();
			controller.setMainApp(this);
		} catch (IOException e) {
			// Exception gets thrown if the fxml file could not be loaded
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
