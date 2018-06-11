package ch.makery.address.view;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ResourceBundle;

import com.pearsoneduc.ip.op.ImageFFT;

import ch.makery.address.model.SelectableImageViewList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class MainTabController implements Initializable {

	private RootController rootController;
	
	@FXML private Label selectedImageViewNumber;
	@FXML private Label selectedImageViewType;
	@FXML private ImageView imageView;
	@FXML private FlowPane imageFlowPane;
	@FXML private Button recordButton;
	@FXML private TextField customOperationNameField;
	private boolean isRecording;
	private SelectableImageViewList imageViews = new SelectableImageViewList();
	
	@FXML private OperateTabController operateTabController;
	@FXML private CalculateTabController calculateTabController;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		operateTabController.setRoot(this);
		calculateTabController.setRoot(this);
		recordButton.setGraphic(new Circle(7, Color.RED));
		isRecording = false;
	}
	
	public void setRoot(RootController rootController) {
		this.rootController = rootController;
	}

	public void onFlowPaneClick() {
		int selectedImageViewIndex = imageViews.getSelectedImageViewIndex();
		selectedImageViewNumber.setText(String.valueOf(selectedImageViewIndex));
		BufferedImage selectedImage = imageViews.getSelectedImage();
		int type = selectedImage.getType();
		selectedImageViewType.setText(String.valueOf(type));
	}
	
	public void addImageView(BufferedImage img) {
		imageViews.add(img);
		imageFlowPane.getChildren().add(imageViews.getLast());
	}

	public void addImageViewButton() {
		BufferedImage selectedImage = imageViews.getSelectedImage();
		if(selectedImage != null) {
			imageViews.add(selectedImage);
			imageFlowPane.getChildren().add(imageViews.getLast());
		}
	}

	public void removeImageViewButton() {
		imageFlowPane.getChildren().remove(imageViews.getSelectedImageView());
		imageViews.remove();
	}
	
	public void onRecordButtonClick() {
		if(!isRecording) {
			recordButton.setGraphic(new Rectangle(recordButton.getHeight() * 0.5, recordButton.getHeight() * 0.5, Color.BLACK));
			isRecording = true;
			operateTabController.setRecording(true);
		} else {
			isRecording = false;
			recordButton.setGraphic(new Circle(recordButton.getHeight() * 0.5 * 0.5, Color.RED));
			operateTabController.setRecording(false);
		}
	}
	
	public void onSaveCustomOperationClick() {
		String operationName = customOperationNameField.getText();
		customOperationNameField.setText(null);
		operateTabController.saveRecorded(operationName);
	}

	public void addImage(BufferedImage image) {
		imageViews.add(image);
	}
	
	public void resetImage() {
		imageViews.reset();
	}

	public void undoImageOperation() {
		imageViews.undo();
	}
	
	public BufferedImage getSelectedImage() {
		return imageViews.getSelectedImage();
	}
	
	public ImageFFT getSelectedImageFFT() {
		return imageViews.getSelectedImageFFT();
	}
	
	public BufferedImage getIndexImage(int index) {
		return imageViews.getIndexImage(index);
	}
	
	public ImageFFT getIndexImageFFT(int index) {
		return imageViews.getIndexImageFFT(index);
	}
	
	public void setSelectedImage(BufferedImage image) {
		imageViews.setSelectedImage(image);
	}
	
	public void setSelectedImageFFT(ImageFFT imageFFT) {
		imageViews.setSelectedImageFFT(imageFFT);
	}
	
	public void setIndexImage(BufferedImage image, int index) {
		imageViews.setIndexImage(image, index);
	}
	
	public void setIndexImageFFT(ImageFFT imageFFT, int index) {
		imageViews.setIndexImageFFT(imageFFT, index);
	}
}
