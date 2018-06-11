package ch.makery.address;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainApp extends Application {

	private static Stage primaryStage;
    private BorderPane rootLayout;

	@Override
	public void start(Stage primaryStage) {
		MainApp.primaryStage = primaryStage;
		initRootLayout();
	}
	
	/**
     * Initializes the root layout.
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	public static void main(String[] args) {
		launch(args);
	}
	
	public static Stage getPrimaryStage() {
		return primaryStage;
	}
	

}
