package imgproc;

import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.awt.image.Kernel;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Utils {
	public static byte[] fill(byte[] arr, byte[] pattern) {
		int i = 0;
		int j = pattern.length;
		while(i < arr.length) {
			j = ++j < pattern.length ? j : 0;
			arr[i++] = pattern[j];
		}
		return arr;
	}
	
	public static short[] fill(short[] arr, short[] pattern) {
		int i = 0;
		int j = pattern.length;
		while(i < arr.length) {
			j = ++j < pattern.length ? j : 0;
			arr[i++] = pattern[j];
		}
		return arr;
	}
	
	public static int[] fill(int[] arr, int[] pattern) {
		int i = 0;
		int j = pattern.length;
		while(i < arr.length) {
			j = ++j < pattern.length ? j : 0;
			arr[i++] = pattern[j];
		}
		return arr;
	}
	
	public static double max(double[] a) {
		double max = 0;
		for (int i = 0; i < a.length; i++) {
			max = a[i] > max ? a[i] : max;
		}
		return max;
	}
	
	public static int min(int[] a) {
		int min = 0;
		for (int i = 0; i < a.length; i++) {
			min = a[i] < min ? a[i] : min;
		}
		return min;
	}
	
	public static int[][] sqeeze(int[][] a) {
		for(int y = 0; y < a.length; y++) {
			for(int x = 0; x < a[y].length; x++) {
				int val = Math.abs(a[y][x]);
				val = val > 255 ? 255 : val;
				a[y][x] = val;
			}
		}
		return a;
	}
	
	/**
	 * 
	 * @param arr a rectangular 2D array
	 * @return
	 */
	public static int[] flatten(int[][] arr) {
		int[] flat = new int[arr[0].length * arr.length];
		for(int y = 0; y < arr.length; y++) {
			for(int x = 0; x < arr[y].length; x++) {
				int index = y * arr[y].length + x;
				flat[index] = arr[y][x];
			}
		}
		return flat;
	}
	
	public static double calculateRealObjectSize(int objectPixelLength, int imagePixelLength, double imagePlaneLength, double focalLength, double distanceToObject) {
		return (imagePlaneLength * objectPixelLength * distanceToObject) / (imagePlaneLength * focalLength);
	}
	
	public static double calculateDistanceToObject(int objectPixelLength, int imagePixelLength, double imagePlaneLength, double focalLength, double objectLength) {
		return (focalLength * objectLength * imagePixelLength) / (objectPixelLength * imagePlaneLength);
	}
	
	public static double mmPerMsToMph(int mm, int ms) {
		double miles = (double) mm / (25.4 * 12* 5280);
		double hours = (double) ms / (1000 * 60 * 60);
		return miles / hours;
	}
	
	public static void printKernel(Kernel k) {
		float[] data = k.getKernelData(null);
		for(int i = 0; i < k.getHeight(); i++) {
			for(int j = 0; j < k.getWidth(); j++) {
				System.out.print(data[i * k.getWidth() + j] * 159 + " ");
			}
			System.out.println();
		}
	}
	
	public static void printGrayImageValues(BufferedImage img) {
		Raster raster = img.getRaster();
		for(int i = 0; i < img.getHeight(); i++) {
			for(int j = 0; j < img.getWidth(); j++) {
				System.out.print(raster.getSample(j, i, 0) + " ");
			}
			System.out.println();
		}
	}
	
	public static void printArray(int[][] a) {
		for(int i = 0; i < a.length; i++) {
			for(int j = 0; j < a[i].length; j++) {
				System.out.print(a[i][j] + " ");
			}
			System.out.println();
		}
	}
	
	public static void displayImage(BufferedImage img, String title) {
		JFrame myFrame = new JFrame(title);
		myFrame.setSize(300, 400);

		ImageIcon icon = new ImageIcon(img);
		JLabel imLabel = new JLabel(icon);
		myFrame.getContentPane().add(imLabel);

		myFrame.pack();
		myFrame.setVisible(true);
	}
	
	public static void saveImage(int cols, int rows, String title, BufferedImage... images) { 
		saveImage(cols, rows, title, "", images);
	}

	public static void saveImage(int cols, int rows, String title, String directory, BufferedImage... images) {
		JFrame frame = new JFrame(title);
		JPanel panel = new JPanel(new GridLayout(rows, cols));
		frame.setContentPane(panel);

		for (int i = 0; i < images.length; i++) {
			JLabel label = new JLabel();
			panel.add("test", label);
			label.setIcon(new ImageIcon(images[i]));
		}

		frame.pack();
		frame.setMinimumSize(frame.getPreferredSize());
		frame.setVisible(true);
		try {
			BufferedImage output = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics2D = output.createGraphics();
			frame.paint(graphics2D);
			ImageIO.write(output, "jpeg", new File(directory + title + ".jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		frame.setVisible(false);
	}
}
