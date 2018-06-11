package ch.makery.address.view;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;
import ch.makery.address.MainApp;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;

public class RootController implements Initializable
{
    @FXML private MenuItem fileOpen;
    @FXML private MenuItem fileSave;
    @FXML private MenuItem fileExit;
    @FXML private MainTabController mainTabController;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		fileExit.setOnAction(e -> System.exit(0));
		fileOpen.setOnAction(e -> openImage());
		mainTabController.setRoot(this);
	}
	
	public void openImage() {
		FileChooser fileChooser = new FileChooser();
		File picturesDir = new File(System.getProperty("user.home"), "/Pictures");
//		if (! recordsDir.exists()) {
//		    recordsDir.mkdirs();
//		}
		fileChooser.setInitialDirectory(picturesDir);
		
		File file = fileChooser.showOpenDialog(MainApp.getPrimaryStage());
		if(file == null) {
			return;
		}
		BufferedImage img = null;
		try {
			img = ImageIO.read(file);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		mainTabController.addImageView(img);
	}
	
	public void saveImage() {
		FileChooser fileChooser = new FileChooser();
		File picturesDir = new File(System.getProperty("user.home"), "/Pictures");
		fileChooser.setInitialDirectory(picturesDir);
		
		File outputfile = fileChooser.showSaveDialog(MainApp.getPrimaryStage());
		BufferedImage imgToSave = mainTabController.getSelectedImage();
		try {
			ImageIO.write(imgToSave, "png", outputfile);
		} catch (IOException e) {
			System.out.println("Error: could not write image to to file: " + outputfile.getAbsolutePath());
			e.printStackTrace();
		}
		
	}
}
