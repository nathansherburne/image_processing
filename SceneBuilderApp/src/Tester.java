import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.pearsoneduc.ip.op.FFTException;
import com.pearsoneduc.ip.op.ImageFFT;

import imgproc.Imgproc;
import imgproc.Utils;

public class Tester {

	public static void main(String[] args) throws FFTException {
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File("/home/nathan/Downloads/index2.png"));
			//Utils.displayImage(img, "Test");
			ImageFFT fft = new ImageFFT(img);
			Utils.displayImage(fft.toImage(null), "FFT");
			fft.transform();
			Utils.displayImage(fft.getSpectrum(), "FFT");
			BufferedImage thresh = Imgproc.threshold(fft.getSpectrum(), 150);
			//Utils.displayImage(thresh, "thresh");
			System.out.println(fft.getWidth());
			System.out.println(fft.getHeight());
			System.out.println(img.getWidth());
			System.out.println(img.getHeight());
			fft.threshold(235);
			Utils.displayImage(fft.getSpectrum(), "FFT");
			fft.transform();
			Utils.displayImage(fft.toImage(null), "THREESH");

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
