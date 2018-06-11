package elements;

public class Camera {
	private double focalLength;
	private double sensorWidth;
	private double sensorHeight;
	
	public Camera(double focal_length, double sensor_width, double sensor_height) {
		this.focalLength = focal_length;
		this.sensorHeight = sensor_height;
		this.sensorWidth = sensor_width;
	}
	
	public int getObjectWidth(int distanceToObject, int objectWidthInPixels, int imageWidthInPixels) {
		return (int) ((sensorWidth * objectWidthInPixels * distanceToObject) / (imageWidthInPixels * focalLength));
	}
	
	public int getObjectHeight(int distanceToObject, int objectHeightInPixels, int imageHeightInPixels) {
		return (int) ((sensorHeight * objectHeightInPixels * distanceToObject) / (imageHeightInPixels * focalLength));
	}
	
	public double getFocalLength() {
		return focalLength;
	}
	
	public double getSensorWidth() {
		return sensorWidth;
	}
	
	public double getSensorHeight() {
		return sensorHeight;
	}
	
	@Override
	public String toString() {
		return "Focal Length: " + focalLength + "mm\n" +
	"Sensor size: " + sensorWidth + "mm x" + sensorHeight + "mm";
	}
}
