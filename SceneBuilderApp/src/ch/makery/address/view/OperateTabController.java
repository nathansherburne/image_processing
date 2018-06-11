package ch.makery.address.view;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeSet;

import com.pearsoneduc.ip.op.FFTException;
import com.pearsoneduc.ip.op.ImageFFT;

import ch.makery.address.model.Parameter;
import elements.Histogram;
import imgproc.Imgproc;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

public class OperateTabController implements Initializable {

	private MainTabController imageDisplayController;
	@FXML private ListView<String> methodList;
	@FXML private Button runMethodButton;
	@FXML private VBox paramRegion;
	@FXML Label errorLabel;
	private List<Parameter> currentParams = new ArrayList<>();
	private Map<String, Parameter[]> methodParams = new HashMap<>();
	private LinkedList<Pair<String, List<Parameter>>> recordedOperation = new LinkedList<>();
	private Map<String, LinkedList<Pair<String, List<Parameter>>>> recordedOperations = new HashMap<>();
	private boolean recording = false;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initializeMethodParameterMap();

		methodList.getItems().addAll(getSupportedMethods());
		methodList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);		
	}
	
	public void setRoot(MainTabController imageDisplayController) {
		this.imageDisplayController = imageDisplayController;
	}
	
	public void methodItemSelected() {
		String selectedMethod = methodList.getSelectionModel().getSelectedItem();
		paramRegion.getChildren().removeAll(currentParams);
		currentParams = new ArrayList<>();

		Parameter[] parameters = methodParams.get(selectedMethod);

		for (int i = 0; i < parameters.length; i++) {
			Parameter parameter = parameters[i];
			String promptText = parameter.getName();
			if (parameter.isOptional()) {
				promptText = promptText + " (Optional)";
			}
			parameter.setPromptText(promptText);
			currentParams.add(parameter);
		}
		paramRegion.getChildren().addAll(currentParams);
	}
	
	public void executeOperationQueue(LinkedList<Pair<String, List<Parameter>>> recordedOperation) {
		for(Pair<String, List<Parameter>> methodParamPair : recordedOperation) {
			currentParams = methodParamPair.getValue();
			methodList.getSelectionModel().select(methodParamPair.getKey());
			methodButtonClicked();
		}
	}

	public void methodButtonClicked() {
		String selectedMethod = methodList.getSelectionModel().getSelectedItem();
		for(String recordedMethodName : recordedOperations.keySet()) {
			if(recordedMethodName.equals(selectedMethod)) {
				executeOperationQueue(recordedOperations.get(recordedMethodName));
				return;
			}
		}
		if (imageDisplayController.getSelectedImage() == null) {
			// No image to operate on.
			return;
		}
		if (!checkParamFieldsFilled()) {
			errorLabel.setText("Please fill in required parameters");
			return;
		} else {
			errorLabel.setText(null);
		}
		if(recording) {
			recordedOperation.addLast(new Pair<String, List<Parameter>>(selectedMethod, currentParams));
		}
		BufferedImage inputImage = imageDisplayController.getSelectedImage();
		BufferedImage img = null;
		ImageFFT imageFFT = null;
		boolean FFTout = false;

		if (selectedMethod == "Gaussian Blur") {
			int ksize = (int) currentParams.get(0).getTextFormatter().getValue();
			double sigma = (double) currentParams.get(1).getTextFormatter().getValue();
			img = Imgproc.gaussianBlur(inputImage, ksize, sigma);
		} else if (selectedMethod == "Grayscale Luminence") {
			img = Imgproc.toGrayscaleLum(inputImage);
		} else if (selectedMethod == "Grayscale Average") {
			img = Imgproc.toGrayscaleAvg(inputImage);
		} else if (selectedMethod == "Threshold") {
			int threshold = (int) currentParams.get(0).getTextFormatter().getValue();
			img = Imgproc.threshold(inputImage, threshold);
		} else if (selectedMethod == "Histogram") {
			Histogram histogram = new Histogram(inputImage);
			int band;
			if (!currentParams.get(0).hasValue()) {
				band = (int) currentParams.get(0).getDefualtValue();
			} else {
				band = (int) currentParams.get(0).getTextFormatter().getValue();
			}
			String histType = currentParams.get(1).getText();
			if(histType.equals("CDF") || histType.equals("cdf") || histType.equals("c") || histType.equals("C")) {
				img = histogram.getCDFImage(band);
			} else if(histType.equals("PMF") || histType.equals("pmf") || histType.equals("p") || histType.equals("P")) {
				img = histogram.getPMFImage(band);
			} else {
				errorLabel.setText("Histogram type: CDF or PMF");
				return;
			}
		} else if (selectedMethod == "Erode") {
			int crossSize = (int) currentParams.get(0).getTextFormatter().getValue();
			img = Imgproc.erode(inputImage, crossSize);
		} else if (selectedMethod == "Dilate") {
			int crossSize = (int) currentParams.get(0).getTextFormatter().getValue();
			img = Imgproc.dilate(inputImage, crossSize);
		} else if (selectedMethod == "Salt and Pepper") {
			double amount = (double) currentParams.get(0).getTextFormatter().getValue();
			img = Imgproc.saltAndPepper(inputImage, amount);
		} else if (selectedMethod == "Salt") {
			double amount = (double) currentParams.get(0).getTextFormatter().getValue();
			img = Imgproc.salt(inputImage, amount);
		} else if (selectedMethod == "Pepper") {
			double amount = (double) currentParams.get(0).getTextFormatter().getValue();
			img = Imgproc.pepper(inputImage, amount);
		} else if (selectedMethod == "Invert") {
			img = Imgproc.invert(inputImage);
		} else if (selectedMethod == "Adaptive Threshold") {
			int ksize = (int) currentParams.get(0).getTextFormatter().getValue();
			img = Imgproc.adaptiveThreshold(inputImage, ksize);
		} else if (selectedMethod == "Canny Edge Detector") {
			int threshold1 = (int) currentParams.get(0).getTextFormatter().getValue();
			int threshold2 = (int) currentParams.get(1).getTextFormatter().getValue();
			img = Imgproc.cannyEdges(inputImage, threshold1, threshold2);
		} else if (selectedMethod == "Subtract") {
			int img1Index = (int) currentParams.get(0).getTextFormatter().getValue();
			int img2Index = (int) currentParams.get(1).getTextFormatter().getValue();
			BufferedImage img1 = imageDisplayController.getIndexImage(img1Index);
			BufferedImage img2 = imageDisplayController.getIndexImage(img2Index);
			img = Imgproc.subtract(img1, img2);
		} else if (selectedMethod.equals("Resize")) {
			double scale = (double) currentParams.get(0).getTextFormatter().getValue();
			img = Imgproc.resize(inputImage, scale);
		} else if (selectedMethod.equals("Stretch Contrast")) {
			img = Imgproc.stretchContrast(inputImage);
		} else if (selectedMethod.equals("Equalize")){
			Histogram h = new Histogram(inputImage);
			img = h.getEqualizedGray();
		} else if (selectedMethod.equals("Fourier Transform")){
			try {
				FFTout = true;
				if(inputImage.getType() != BufferedImage.TYPE_BYTE_GRAY) {
					errorLabel.setText("FFT input must be grayscale");
					return;
				}
				imageFFT = new ImageFFT(inputImage);
				imageFFT.transform();
			} catch (FFTException e) {
				e.printStackTrace();
			}
		} else if (selectedMethod.equals("Inverse Fourier Transform")){
			try {
				imageFFT = imageDisplayController.getSelectedImageFFT();
				imageFFT.transform();
				img = imageFFT.toImage(null);
				FFTout = false;
			} catch (FFTException e) {
				e.printStackTrace();
			}
		} else if (selectedMethod.equals("Butterworth Low Pass Filter")){
			try {
				imageFFT = imageDisplayController.getSelectedImageFFT();
				if(imageFFT == null) {
					errorLabel.setText("Cannot apply frequency domain filter to spatial domain image.");
					return;
				}
				int order = (int) currentParams.get(0).getTextFormatter().getValue();
				double radius = (double) currentParams.get(1).getTextFormatter().getValue();
				imageFFT.butterworthLowPassFilter(order, radius);
				FFTout = true;
			} catch (FFTException e) {
				e.printStackTrace();
			}
		} else if (selectedMethod.equals("Butterworth High Pass Filter")){
			try {
				imageFFT = imageDisplayController.getSelectedImageFFT();
				if(imageFFT == null) {
					errorLabel.setText("Cannot apply frequency domain filter to spatial domain image.");
					return;
				}
				int order = (int) currentParams.get(0).getTextFormatter().getValue();
				double radius = (double) currentParams.get(1).getTextFormatter().getValue();
				imageFFT.butterworthHighPassFilter(order, radius);
				FFTout = true;
			} catch (FFTException e) {
				e.printStackTrace();
			}
		} else if (selectedMethod.equals("Butterworth Band Pass Filter")){
			try {
				imageFFT = imageDisplayController.getSelectedImageFFT();
				if(imageFFT == null) {
					errorLabel.setText("Cannot apply frequency domain filter to spatial domain image.");
					return;
				}
				int order = (int) currentParams.get(0).getTextFormatter().getValue();
				double radius = (double) currentParams.get(1).getTextFormatter().getValue();
				double bandwidth = (double) currentParams.get(2).getTextFormatter().getValue();
				imageFFT.butterworthBandPassFilter(order, radius, bandwidth);
				FFTout = true;
			} catch (FFTException e) {
				e.printStackTrace();
			}
		} else if (selectedMethod.equals("Butterworth Band Stop Filter")){
			try {
				imageFFT = imageDisplayController.getSelectedImageFFT();
				if(imageFFT == null) {
					errorLabel.setText("Cannot apply frequency domain filter to spatial domain image.");
					return;
				}
				int order = (int) currentParams.get(0).getTextFormatter().getValue();
				double radius = (double) currentParams.get(1).getTextFormatter().getValue();
				double bandwidth = (double) currentParams.get(2).getTextFormatter().getValue();
				imageFFT.butterworthBandStopFilter(order, radius, bandwidth);
				FFTout = true;
			} catch (FFTException e) {
				e.printStackTrace();
			}
		} else if (selectedMethod.equals("Ideal Low Pass Filter")){
			try {
				imageFFT = imageDisplayController.getSelectedImageFFT();
				if(imageFFT == null) {
					errorLabel.setText("Cannot apply frequency domain filter to spatial domain image.");
					return;
				}
				double radius = (double) currentParams.get(0).getTextFormatter().getValue();
				imageFFT.idealLowPassFilter(radius);
				FFTout = true;
			} catch (FFTException e) {
				e.printStackTrace();
			}
		} else if (selectedMethod.equals("Ideal High Pass Filter")){
			try {
				imageFFT = imageDisplayController.getSelectedImageFFT();
				if(imageFFT == null) {
					errorLabel.setText("Cannot apply frequency domain filter to spatial domain image.");
					return;
				}
				double radius = (double) currentParams.get(0).getTextFormatter().getValue();
				imageFFT.idealHighPassFilter(radius);				
				FFTout = true;
			} catch (FFTException e) {
				e.printStackTrace();
			}
		} else if (selectedMethod.equals("Ideal Band Pass Filter")){
			try {
				imageFFT = imageDisplayController.getSelectedImageFFT();
				if(imageFFT == null) {
					errorLabel.setText("Cannot apply frequency domain filter to spatial domain image.");
					return;
				}
				double radius = (double) currentParams.get(0).getTextFormatter().getValue();
				double bandwidth = (double) currentParams.get(1).getTextFormatter().getValue();
				imageFFT.idealBandPassFilter(radius, bandwidth);
				FFTout = true;
			} catch (FFTException e) {
				e.printStackTrace();
			}
		} else if (selectedMethod.equals("Ideal Band Stop Filter")){
			try {
				imageFFT = imageDisplayController.getSelectedImageFFT();
				if(imageFFT == null) {
					errorLabel.setText("Cannot apply frequency domain filter to spatial domain image.");
					return;
				}
				double radius = (double) currentParams.get(0).getTextFormatter().getValue();
				double bandwidth = (double) currentParams.get(1).getTextFormatter().getValue();
				imageFFT.idealBandStopFilter(radius, bandwidth);
				FFTout = true;
			} catch (FFTException e) {
				e.printStackTrace();
			}
		}
		
		if(FFTout) {
			imageDisplayController.setSelectedImageFFT(imageFFT);
		} else {
			imageDisplayController.setSelectedImage(img);
		}
	}

	private boolean checkParamFieldsFilled() {
		for (Parameter param : currentParams) {
			if (!param.isOptional() && !param.hasValue()) {
				return false;
			}
		}
		return true;
	}

	public void resetImageButton() {
		imageDisplayController.resetImage();
	}

	public void onUndoButtonClick() {
		imageDisplayController.undoImageOperation();
		if(recording && !recordedOperation.isEmpty()) {
			recordedOperation.removeLast();
		}
	}
	
	public TreeSet<String> getSupportedMethods() {
		return new TreeSet<String>(methodParams.keySet());
	}

	public void initializeMethodParameterMap() {
		// 0 parameter operations
		methodParams.put("Grayscale Luminence", new Parameter[] {});
		methodParams.put("Grayscale Average", new Parameter[] {});
		methodParams.put("Invert", new Parameter[] {});
		methodParams.put("Stretch Contrast", new Parameter[] {});
		methodParams.put("Equalize", new Parameter[] {});
		methodParams.put("Fourier Transform", new Parameter[] {});
		methodParams.put("Inverse Fourier Transform", new Parameter[] {});
		
		// 1 parameter operations
		methodParams.put("Histogram", new Parameter[] { new Parameter<Integer>("band", Integer.class, true, 0), new Parameter<String>("CDF or PMF", String.class) });
		methodParams.put("Threshold", new Parameter[] { new Parameter<Integer>("threshold", Integer.class) });
		methodParams.put("Adaptive Threshold",
				new Parameter[] { new Parameter<Integer>("kernel size", Integer.class) });
		methodParams.put("Erode", new Parameter[] { new Parameter<Integer>("cross size", Integer.class) });
		methodParams.put("Dilate", new Parameter[] { new Parameter<Integer>("cross size", Integer.class) });
		methodParams.put("Salt and Pepper", new Parameter[] { new Parameter<Double>("amount", Double.class) });
		methodParams.put("Salt", new Parameter[] { new Parameter<Double>("amount", Double.class) });
		methodParams.put("Pepper", new Parameter[] { new Parameter<Double>("amount", Double.class) });
		methodParams.put("Resize", new Parameter[] { new Parameter<Double>("scale", Double.class) });
		methodParams.put("Ideal Low Pass Filter", new Parameter[] { new Parameter<Double>("radius", Double.class) });
		methodParams.put("Ideal High Pass Filter", new Parameter[] { new Parameter<Double>("radius", Double.class) });

		// 2 parameter operations
		methodParams.put("Subtract", new Parameter[] { new Parameter<Integer>("image 1 number", Integer.class),
				new Parameter<Integer>("image 2 number", Integer.class) });
		methodParams.put("Gaussian Blur", new Parameter[] { new Parameter<Integer>("kernel size", Integer.class),
				new Parameter<Double>("sigma", Double.class) });
		methodParams.put("Canny Edge Detector", new Parameter[] { new Parameter<Integer>("threshold 1", Integer.class),
				new Parameter<Integer>("threshold 2", Integer.class) });
		methodParams.put("Butterworth Low Pass Filter", new Parameter[] { new Parameter<Integer>("order", Integer.class),
				new Parameter<Double>("radius", Double.class) });
		methodParams.put("Butterworth High Pass Filter", new Parameter[] { new Parameter<Integer>("order", Integer.class),
				new Parameter<Double>("radius", Double.class) });
	}
	
	public void setRecording(boolean recording) {
		this.recording = recording;
	}
	
	public void saveRecorded(String operationName) {
		methodParams.put(operationName,  new Parameter[] {});
		recordedOperations.put(operationName, recordedOperation);
		recordedOperation = new LinkedList<>();
		methodList.getItems().add(operationName);
	}
}
