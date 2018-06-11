package elements;

import java.awt.image.BufferedImage;
import java.awt.image.ByteLookupTable;
import java.awt.image.LookupOp;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class Histogram {
	private BufferedImage image;
	private final int NUM_PIXEL_INTENSITIES = 256;
	private int[][] histogram;
	private int[][][] RGBHistogram;
	private int numBands;
	private int binsPerBand = 4;
	
	public Histogram(BufferedImage image) {
		this.image = image;
		this.numBands = image.getRaster().getNumBands();
		histogram = getColorSplitHistograms();

	}
	
	public Histogram clone() {
		return new Histogram(image);
	}

	public BufferedImage getImage() {
		return image;
	}

	public int getNumBands() {
		return numBands;
	}

	public int getNumIntensities() {
		return NUM_PIXEL_INTENSITIES;
	}
	
	public int getNumPixels() {
		return image.getHeight() * image.getWidth();
	}

	public int[][] getHistogram() {
		return histogram;
	}
	
	public int[][][] getColorHistogram() {
		return RGBHistogram;
	}
	
	public int[] getHistogram(int band) {
		return histogram[band];
	}
	
	public int minValue() {
		int minIndex = 0;
		while(getHistogram(0)[minIndex++] != 0);
		return minIndex;
	}
	
	public int maxValue() {
		for(int maxIndex = getHistogram(0).length; maxIndex >= 0; maxIndex--) {
			if(maxIndex != 0) {
				return maxIndex;
			}
		}
		return 0;
	}
	
	public double[] getPMF(int band) {
		int[] grays = getHistogram(band);
		double[] pdf = new double[grays.length];
		for(int i = 0; i < grays.length; i++) {
			pdf[i] = (double) grays[i] / getNumPixels();
		}
		return pdf;
	}
	
	public double[] getCDF(int band) {
		double[] pdf = getPMF(band);
		double[] cdf = new double[pdf.length];
		double total = 0;
		for(int i = 0; i < pdf.length; i++) {
			total += pdf[i];
			cdf[i] = total;
		}
		return cdf;
	}
	
	public BufferedImage getPMFImage(int band) {
		return getPMFImage(getImage().getWidth(), getImage().getHeight(), 0.05f, band);
	}
	
	public BufferedImage getCDFImage(int band) {
		return getCDFImage(getImage().getWidth(), getImage().getHeight(), 0.05f, band);
	}
	
	public BufferedImage getPMFImage(int width, int height, float marginScale, int band) {
		return getHistogramImage(width, height, marginScale, band, getPMF(band));
	}
	
	public BufferedImage getCDFImage(int width, int height, float marginScale, int band) {
		return getHistogramImage(width, height, marginScale, band, getCDF(band));
	}

	/**
	 * 
	 * @param width
	 * @param height
	 * @param marginScale
	 * @param band
	 * @return
	 */
	private BufferedImage getHistogramImage(int width, int height, float marginScale, int band, double[] distribution) {
		BufferedImage hist = null;
		if(getNumBands() == 1) {
			hist = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);

		} else if(getNumBands() == 3) {
			hist = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		}
		
		WritableRaster raster = hist.getRaster();

		// Plotting parameters //
		int marginWidth = (int) (width * marginScale);
		int marginHeight = (int) (height * marginScale);
		float barScale = 0.9f;
		int xAxisWidth = (width - (2 * marginWidth));
		int xAxisHeight = 2;
		int yAxisWidth = 2;
		int yAxisHeight = (height - (2 * marginHeight));
		int widthPerEntry = (xAxisWidth / getNumIntensities());
		int barWidth = Math.max((int) (widthPerEntry * barScale), 1);
		byte[] axisColor = new byte[] {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
		byte[] barColor = new byte[3];
		barColor[band] = (byte) 0xFF;

		// Plot x axis
		byte[] xAxisBytes = new byte[xAxisWidth * xAxisHeight * getNumBands()];
		fill(xAxisBytes, axisColor);
		raster.setDataElements(marginWidth, height - marginHeight - xAxisHeight, xAxisWidth, xAxisHeight, xAxisBytes);

		// Plot x axis
		byte[] yAxisBytes = new byte[yAxisWidth * yAxisHeight * getNumBands()];
		fill(yAxisBytes, axisColor);
		raster.setDataElements(marginWidth, marginHeight, yAxisWidth, yAxisHeight, yAxisBytes);

		// Plot data bars.
		OrderedPair<Double> imageYRange = new OrderedPair<Double>((double) marginHeight, (double) (height - marginHeight));
		OrderedPair<Double> histYRange = new OrderedPair<Double>(0.0, imgproc.Utils.max(distribution));
		LinearMapping mapping = new LinearMapping(histYRange, imageYRange);

		for (int val = 0; val < getNumIntensities(); val++) {
			int x0 = marginWidth + (int) (val * widthPerEntry);
			int y0 = marginHeight;
			int x1 = x0 + barWidth;
			int y1 = (int) mapping.map(distribution[val]);
			int w = x1 - x0;
			int h = y1 - y0;
			byte[] rectData = new byte[w * h * getNumBands()];
			fill(rectData, barColor);
			raster.setDataElements(x0, height - y1, w, h, rectData);
		}
		return hist;

	}
	
	private byte[] fill(byte[] arr, byte[] pattern) {
		int i = 0;
		int j = pattern.length;
		while(i < arr.length) {
			j = ++j < pattern.length ? j : 0;
			arr[i++] = pattern[j];
		}
		return arr;
	}

	private int[][] getColorSplitHistograms() {
		int[][] histogram = new int[getNumBands()][getNumIntensities()];
		Raster raster = getImage().getRaster();
		for (int y = 0; y < raster.getHeight(); y++) {
			for (int x = 0; x < raster.getWidth(); x++) {
				for (int b = 0; b < getNumBands(); b++) {
					histogram[b][raster.getSample(x, y, b)]++;
				}
			}
		}
		return histogram;
	}
	
	private int[][][] getCombinedColorHistogram() {
		int binSize = getNumIntensities() / binsPerBand;
		int[][][] histogram = new int[binsPerBand][binsPerBand][binsPerBand];
		Raster raster = getImage().getRaster();
		byte[] pixel = null;
		for (int y = 0; y < raster.getHeight(); y++) {
			for (int x = 0; x < raster.getWidth(); x++) {
				pixel = (byte[]) raster.getDataElements(x, y, pixel);
				histogram[getBin(pixel[2] & 0xFF, binSize)][getBin(pixel[1] & 0xFF, binSize)][getBin(pixel[0] & 0xFF, binSize)]++;
			}
		}
		return histogram;
	}
	
	private int getBin(int bandVal, int binSize) {
		return bandVal / binSize;

	}
	
	public float getColorSimilarity(Histogram other) {
		float intersectingPixels = 0;
		int[][][] colorHist1 = getColorHistogram();
		int[][][] colorHist2 = other.getColorHistogram();
		for(int b = 0; b < colorHist1.length; b++) {
			for(int g = 0; g < colorHist1.length; g++) {
				for(int r = 0; r < colorHist1.length; r++) {
					intersectingPixels += Math.min(colorHist1[b][g][r], colorHist2[b][g][r]);
				}
			}
		}
		return intersectingPixels / getNumPixels();
	}
	
	public float getSimilarity(Histogram other) {
		float similarity = 0;
		for(int band = 0; band < other.getNumBands(); band++) {
			similarity += getSimilarity(other, band);
		}
		return similarity / other.getNumBands();
	}
	
	public float getSimilarity(Histogram other, int band) {
		return getIntersections(other.getHistogram(band), band) / other.getNumPixels();
	}

	
	private int getIntersections(int[] hist, int band) {
		int totalIntersections = 0;
		for(int i = 0; i < hist.length; i++) {
			totalIntersections += Math.min(hist[i], getHistogram()[band][i]);
		}
		return totalIntersections;
	}
	
	private double getNormalizationFactor() {
		return getNumIntensities() / (image.getWidth() * image.getHeight());
	}
	
	public BufferedImage getEqualizedGray() {
		if(getNumBands() != 1) {
			return null;
		}
		double[] cdf = getCDF(0);
		byte[] mapping = new byte[cdf.length];
		for(int i = 0; i < cdf.length; i++) {
			mapping[i] = (byte) (Math.floor(cdf[i] * getNumIntensities()));
		}
		LookupOp equalizeOp = new LookupOp(new ByteLookupTable(0, mapping), null);
		BufferedImage equalized = equalizeOp.filter(getImage(), null);
		return equalized;
	}

}
