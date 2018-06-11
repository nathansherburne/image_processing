package ch.makery.address.view;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import elements.Camera;
import imgproc.Imgproc;
import imgproc.Utils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class CalculateTabController implements Initializable {
	
	MainTabController imageDisplayController;
	@FXML TextField focalLengthField;
	@FXML TextField sensorWidthField;
	@FXML TextField sensorHeightField;
	@FXML TextField distanceField;
	@FXML TextField timeField;
	@FXML TextField image1Field;
	@FXML TextField image2Field;
	@FXML Label speedLabel;
	@FXML Label displacementLabel;
	@FXML ComboBox<String> camerasDropdown;
	Map<String, Camera> supportedCameras = new HashMap<>();
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initSupportedCameras();
		camerasDropdown.getItems().addAll(getSupportedCameras());
	}
	
	public void setRoot(MainTabController imageDisplayController) {
		this.imageDisplayController = imageDisplayController;
	}
	
	public void onCameraItemSelect() {
		Camera selectedCamera = supportedCameras.get(camerasDropdown.getValue());
		focalLengthField.setText(String.valueOf(selectedCamera.getFocalLength()));
		sensorWidthField.setText(String.valueOf(selectedCamera.getSensorWidth()));
		sensorHeightField.setText(String.valueOf(selectedCamera.getSensorHeight()));
	}
	
	public void onCalculateButtonClick() {
		int index1 = Integer.valueOf(image1Field.getText());
		int index2 = Integer.valueOf(image2Field.getText());

		BufferedImage image1 = imageDisplayController.getIndexImage(index1);
		BufferedImage image2 = imageDisplayController.getIndexImage(index2);
		double focalLength = Double.valueOf(focalLengthField.getText());
		double sensorWidth = Double.valueOf(sensorWidthField.getText());
		double sensorHeight = Double.valueOf(sensorHeightField.getText());
		double timeDifference = Double.valueOf(timeField.getText());
		double distanceToObject = Double.valueOf(distanceField.getText());

		Rectangle2D.Double bbox1 = Imgproc.getBoundingBox(image1);
		Rectangle2D.Double bbox2 = Imgproc.getBoundingBox(image2);

		BufferedImage img1Outlined = Imgproc.drawRectangleBorder(image1, bbox1, Color.WHITE);
		BufferedImage img2Outlined = Imgproc.drawRectangleBorder(image2, bbox2, Color.WHITE);
		imageDisplayController.setIndexImage(img1Outlined, index1);
		imageDisplayController.setIndexImage(img2Outlined, index2);

		Camera camera = new Camera(focalLength, sensorWidth, sensorHeight);
		double imageDisplacement = (bbox1.getCenterX() - bbox2.getCenterX());
		double physicalDisplacement = camera.getObjectWidth((int) distanceToObject, (int) imageDisplacement, image2.getWidth());
		double velocityMph = Utils.mmPerMsToMph((int) physicalDisplacement, (int) timeDifference);
		speedLabel.setText(String.valueOf(velocityMph) + "mph");
		displacementLabel.setText(String.valueOf((physicalDisplacement / (10 * 2.54 * 12))) + "ft");
	}
	
	public Set<String> getSupportedCameras() {
		return supportedCameras.keySet();
	}
	
	public void initSupportedCameras() {
		supportedCameras.put("GoPro Hero 3+ Silver", new Camera(2.239, 5.37, 4.04));
	}
}
