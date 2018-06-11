package imgproc;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ByteLookupTable;
import java.awt.image.ColorModel;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.awt.image.Raster;
import java.awt.image.ShortLookupTable;
import java.awt.image.WritableRaster;
import java.util.Arrays;

import elements.Histogram;

public class Imgproc {

	public static void fourier(double[] data) {
		for (int u = 0; u < data.length; u++) {
			double sumReal = 0;
			double sumImag = 0;
			for (int x = 0; x < data.length; x++) {
				double angle = 2 * Math.PI * u * x / data.length;
				double fxy = data[x];
				sumReal += fxy * Math.cos(angle);
				sumImag += -fxy * Math.sin(angle);
			}
			System.out.println(sumReal + " + i" + sumImag);
		}
		// sumReal = sumReal / n;
		// sumImag = sumImag / n;
		// double ampSpectrum = Math.sqrt(Math.pow(sumReal, 2) + Math.pow(sumImag, 2));
		// fRast.setSample(u, v, b, ampSpectrum);
	}

//	public static BufferedImage fourierTransform(BufferedImage img) {
//		try {
//			ImageFFT imageFFT = new ImageFFT(img);
//			imageFFT.transform();
//			BufferedImage spectrum = imageFFT.getSpectrum();
//			//imageFFT.
//		} catch (FFTException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

	public static BufferedImage drawRectangleBorder(BufferedImage img, Rectangle2D rectangle, Color color) {
		BufferedImage newImg = Imgproc.deepCopy(img);
		Graphics2D g2d = newImg.createGraphics();
		float thickness = 2;
		Stroke oldStroke = g2d.getStroke();
		g2d.setColor(color);
		g2d.setStroke(new BasicStroke(thickness));
		g2d.drawRect((int) rectangle.getMinX(), (int) rectangle.getMinY(), (int) rectangle.getWidth(),
				(int) rectangle.getHeight());
		g2d.setStroke(oldStroke);
		return newImg;
	}

	public static BufferedImage invert(BufferedImage img) {
		BufferedImage inverted = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
		WritableRaster rastInv = inverted.getRaster();
		Raster rastOrig = img.getRaster();
		int bandsToInvert = rastOrig.getNumBands() >= 4 ? 3 : rastOrig.getNumBands(); // So that alpha is not inverted
		for (int y = 0; y < rastInv.getHeight(); y++) {
			for (int x = 0; x < rastInv.getWidth(); x++) {
				for (int b = 0; b < bandsToInvert; b++) {
					rastInv.setSample(x, y, b, 255 - rastOrig.getSample(x, y, b));
				}
			}
		}
		return inverted;
	}

	public static BufferedImage threshold(BufferedImage gray, int threshold) {
		BufferedImage binary = new BufferedImage(gray.getWidth(), gray.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
		WritableRaster rast = binary.getRaster();
		Raster raster = gray.getRaster();
		int b = 0;
		int min = 0;
		int max = 255;
		for (int y = 0; y < raster.getHeight(); y++) {
			for (int x = 0; x < raster.getWidth(); x++) {
				if (raster.getSample(x, y, b) > threshold) {
					rast.setSample(x, y, b, max);
				} else {
					rast.setSample(x, y, b, min);
				}
			}
		}
		return binary;
	}

	/**
	 * 
	 * @param gray
	 * @param ksize
	 * @param sigma
	 * @return
	 */
	public static BufferedImage adaptiveThreshold(BufferedImage gray, int blockSize) {
		double sigma = 1.4;
		BufferedImage gaussianAvg = gaussianBlur(gray, blockSize, sigma);
		BufferedImage binary = new BufferedImage(gray.getWidth(), gray.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
		Raster gaussRaster = gaussianAvg.getRaster();
		Raster grayRaster = gray.getRaster();
		WritableRaster binRaster = binary.getRaster();
		byte[] gaussPixel = null;
		byte[] grayPixel = null;
		for (int y = 0; y < gaussRaster.getHeight(); y++) {
			for (int x = 0; x < gaussRaster.getWidth(); x++) {
				gaussPixel = (byte[]) gaussRaster.getDataElements(x, y, gaussPixel);
				grayPixel = (byte[]) grayRaster.getDataElements(x, y, grayPixel);
				byte[] binaryPixel = new byte[1];
				if (grayPixel[0] > gaussPixel[0]) {
					binaryPixel[0] = (byte) 1;
				} else {
					binaryPixel[0] = (byte) 0;
				}
				binRaster.setDataElements(x, y, binaryPixel);
			}
		}
		return binary;
	}

	public static BufferedImage saltAndPepper(BufferedImage gray, double amount) {
		Raster data = gray.getData();
		BufferedImage sap = new BufferedImage(gray.getWidth(), gray.getHeight(), gray.getType());
		WritableRaster raster = sap.getRaster();
		double tmp;
		double low = amount / 2;
		double high = 1 - low;
		for (int y = 0; y < gray.getHeight(); y++) {
			for (int x = 0; x < gray.getWidth(); x++) {
				if ((tmp = Math.random()) <= low) {
					raster.setSample(x, y, 0, 0);
				} else if (tmp >= high) {
					raster.setSample(x, y, 0, 255);
				} else {
					raster.setSample(x, y, 0, data.getSample(x, y, 0));
				}
			}
		}
		return sap;
	}

	public static BufferedImage salt(BufferedImage gray, double amount) {
		Raster data = gray.getData();
		BufferedImage sap = new BufferedImage(gray.getWidth(), gray.getHeight(), gray.getType());
		WritableRaster raster = sap.getRaster();
		double tmp;
		for (int y = 0; y < gray.getHeight(); y++) {
			for (int x = 0; x < gray.getWidth(); x++) {
				if ((tmp = Math.random()) <= amount) {
					raster.setSample(x, y, 0, 255);
				} else {
					raster.setSample(x, y, 0, data.getSample(x, y, 0));
				}
			}
		}
		return sap;
	}

	public static BufferedImage pepper(BufferedImage gray, double amount) {
		Raster data = gray.getData();
		BufferedImage sap = new BufferedImage(gray.getWidth(), gray.getHeight(), gray.getType());
		WritableRaster raster = sap.getRaster();
		double tmp;
		for (int y = 0; y < gray.getHeight(); y++) {
			for (int x = 0; x < gray.getWidth(); x++) {
				if ((tmp = Math.random()) <= amount) {
					raster.setSample(x, y, 0, 0);
				} else {
					raster.setSample(x, y, 0, data.getSample(x, y, 0));
				}
			}
		}
		return sap;
	}
	
	public static BufferedImage meanFilter(BufferedImage img, int kSize) {
		return alphaTrimmedMeanFilter(img, kSize, 0);
	}

	public static BufferedImage medianFilter(BufferedImage img, int kSize) {
		int[] which = { (int) (0.5 * ((kSize * kSize) - 1)) };
		return filter(img, kSize, which);
	}

	public static BufferedImage maxFilter(BufferedImage img, int kSize) {
		int[] which = { (int) (1 * ((kSize * kSize) - 1)) };
		return filter(img, kSize, which);
	}

	public static BufferedImage minFilter(BufferedImage img, int kSize) {
		int[] which = { (int) (0 * ((kSize * kSize) - 1)) };
		return filter(img, kSize, which);
	}

	public static BufferedImage alphaTrimmedMeanFilter(BufferedImage img, int kSize, int alpha) {
		int[] which = new int[(kSize * kSize) - (2 * alpha)];
		for (int i = 0; i < which.length; i++) {
			which[i] = i + alpha;
		}
		return filter(img, kSize, which);
	}

	public static BufferedImage geometricMeanFilter(BufferedImage img, int kSize) {
		Raster rast = img.getRaster();
		BufferedImage filtered = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
		WritableRaster wrast = filtered.getRaster();
		for (int y = kSize / 2; y < img.getHeight() - kSize / 2; y++) {
			for (int x = kSize / 2; x < img.getWidth() - kSize / 2; x++) {
				BufferedImage sub = getNeighborhood(x, y, kSize, kSize, img);
				Raster rSub = sub.getRaster();
				int[] data = new int[sub.getWidth() * sub.getHeight()];
				data = rSub.getSamples(0, 0, sub.getWidth(), sub.getHeight(), 0, data);
				Arrays.sort(data);
				wrast.setSample(x, y, 0, geometricMean(data));
			}
		}
		return filtered;
	}

	public static BufferedImage harmonicMeanFilter(BufferedImage img, int kSize) {
		return contraHarmonicMeanFilter(img, kSize, -1);
	}

	public static BufferedImage contraHarmonicMeanFilter(BufferedImage img, int kSize, int r) {
		Raster rast = img.getRaster();
		BufferedImage filtered = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
		WritableRaster wrast = filtered.getRaster();
		for (int y = kSize / 2; y < img.getHeight() - kSize / 2; y++) {
			for (int x = kSize / 2; x < img.getWidth() - kSize / 2; x++) {
				BufferedImage sub = getNeighborhood(x, y, kSize, kSize, img);
				Raster rSub = sub.getRaster();
				int[] data = new int[sub.getWidth() * sub.getHeight()];
				data = rSub.getSamples(0, 0, sub.getWidth(), sub.getHeight(), 0, data);
				Arrays.sort(data);
				wrast.setSample(x, y, 0, contraHarmonicMean(data, r));
			}
		}
		return filtered;
	}

	public static BufferedImage minimalMeanSquareErrorFilter(BufferedImage img, int kSize) {
		Raster rast = img.getRaster();
		BufferedImage filtered = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
		WritableRaster wrast = filtered.getRaster();
		int[] allValues = new int[rast.getWidth() * rast.getHeight()];
		allValues = rast.getSamples(0, 0, rast.getWidth(), rast.getHeight(), 0, allValues);
		double totalVariance = variance(allValues);
		System.out.println(totalVariance);
		for (int y = kSize / 2; y < img.getHeight() - kSize / 2; y++) {
			for (int x = kSize / 2; x < img.getWidth() - kSize / 2; x++) {
				BufferedImage sub = getNeighborhood(x, y, kSize, kSize, img);
				Raster rSub = sub.getRaster();
				int[] data = new int[sub.getWidth() * sub.getHeight()];
				data = rSub.getSamples(0, 0, sub.getWidth(), sub.getHeight(), 0, data);
				Arrays.sort(data);
				wrast.setSample(x, y, 0, minimalMeanSquareError(data, totalVariance));
			}
		}
		return filtered;
	}

	public static BufferedImage kRootMapping(BufferedImage img, float k) {
		byte[] lookupTable = new byte[256];
		for (int i = 0; i < lookupTable.length; i++) {
			lookupTable[i] = (byte) (Math.pow(255 * Math.pow(i, k - 1), 1 / k));
		}
		LookupOp lookup = new LookupOp(new ByteLookupTable(0, lookupTable), null);
		BufferedImage mapped = lookup.filter(img, null);
		return mapped;
	}

	/**
	 * Performs a linear mapping to increase the spread of values in an image.
	 * 
	 * The default spreads the values to a min: 0, max: 255 distribution.
	 * 
	 * @param img
	 * @return
	 */
	public static BufferedImage stretchContrast(BufferedImage img) {
		Histogram h = new Histogram(img);
		return stretchContrast(img, h.minValue(), h.maxValue());
	}

	/**
	 * 
	 * @param img
	 * @param min
	 *            the minimum value in the input image's codomain.
	 * @param max
	 *            the maximum value in the input image's codomain.
	 * @return
	 */
	public static BufferedImage stretchContrast(BufferedImage img, int min, int max) {
		return linearMapping(img, new Point2D.Float(min, 0), new Point2D.Float(max, 255));
	}

	public static BufferedImage linearMapping(BufferedImage img, Point2D.Float p1, Point2D.Float p2) {
		return linearMapping(img, (float) ((p2.getY() - p1.getY()) / (p2.getX() - p1.getX())), 0.0f);
	}

	public static BufferedImage linearMapping(BufferedImage img, float slope, float bias) {
		byte[] lookupTable = new byte[256];
		for (int i = 0; i < lookupTable.length; i++) {
			lookupTable[i] = (byte) (clamp(i * slope + bias));
		}
		LookupOp lookup = new LookupOp(new ByteLookupTable(0, lookupTable), null);
		BufferedImage mapped = lookup.filter(img, null);
		return mapped;
	}

	public static BufferedImage edgeDetect(BufferedImage gray) {
		float edgeArr[] = { 0, -1, 0, -1, 4, -1, 0, -1, 0 };
		Kernel edgeKern = new Kernel(3, 3, edgeArr);
		ConvolveOp edgeOp = new ConvolveOp(edgeKern, ConvolveOp.EDGE_NO_OP, null);
		BufferedImage edges = null;
		edges = edgeOp.filter(gray, null);
		return edges;
	}

	public static BufferedImage sobelEdgeX(BufferedImage img) {
		int[][] sobX = sobelXMags(img);
		sobX = Utils.sqeeze(sobX);
		return toBufferedImage(sobX);
	}

	public static BufferedImage sobelEdgeY(BufferedImage img) {
		int[][] sobY = sobelYMags(img);
		sobY = Utils.sqeeze(sobY);
		return toBufferedImage(sobY);
	}

	public static BufferedImage sobelBW(BufferedImage img) {
		int[][][] sobelElements = sobel(img);
		int[][] magnitudes = sobelElements[0];
		int[][] angles = sobelElements[1];

		int[][] supSob = nonMaxSuppression(magnitudes, angles);
		return toBufferedImage(Utils.sqeeze(supSob));
	}

	/**
	 * 
	 * @param gray
	 *            The source image
	 * @param threshold1
	 *            the lower threshold for the hysteresis procedure.
	 * @param threshold2
	 *            the upper threshold for the hysteresis procedure.
	 * @return
	 */
	public static BufferedImage cannyEdges(BufferedImage gray, int threshold1, int threshold2) {
		BufferedImage blur = Imgproc.gaussianBlur(gray, 5, 1.4);
		int[][][] sobelInfo = Imgproc.sobel(blur);
		int[][] magnitudes = sobelInfo[0];
		int[][] angles = sobelInfo[1];
		BufferedImage sobel = Imgproc.threshold(Imgproc.toBufferedImage(magnitudes), 1);
		int[][] suppressedSobMags = Imgproc.nonMaxSuppression(magnitudes, angles);
		BufferedImage suppressedSob = Imgproc.threshold(Imgproc.toBufferedImage(suppressedSobMags), 1);
		int[][] thresh = Imgproc.hysteresisThreshold(suppressedSobMags, threshold1, threshold2);
		BufferedImage cannyColor = Imgproc.getCannyColored(thresh, angles);

		return cannyColor;
	}

	public static BufferedImage robertEdge(BufferedImage img) {
		float[] robPos = { 1, 0, 0, -1 };
		Kernel robKernPos = new Kernel(2, 2, robPos);
		ConvolveOp robOpPos = new ConvolveOp(robKernPos, ConvolveOp.EDGE_NO_OP, null);
		BufferedImage posEdges = null;
		posEdges = robOpPos.filter(img, null);

		float[] robNeg = { 0, 1, -1, 0 };
		Kernel robKernNeg = new Kernel(2, 2, robNeg);
		ConvolveOp robOpNeg = new ConvolveOp(robKernNeg, ConvolveOp.EDGE_NO_OP, null);
		BufferedImage negEdges = null;
		negEdges = robOpNeg.filter(img, null);

		float[] robPos1 = { -1, 0, 0, 1 };
		Kernel robKernPos1 = new Kernel(2, 2, robPos1);
		ConvolveOp robOpPos1 = new ConvolveOp(robKernPos1, ConvolveOp.EDGE_NO_OP, null);
		BufferedImage posEdges1 = null;
		posEdges1 = robOpPos1.filter(img, null);

		float[] robNeg1 = { 0, -1, 1, 0 };
		Kernel robKernNeg1 = new Kernel(2, 2, robNeg1);
		ConvolveOp robOpNeg1 = new ConvolveOp(robKernNeg1, ConvolveOp.EDGE_NO_OP, null);
		BufferedImage negEdges1 = null;

		return bitwise_or(posEdges1, negEdges1, negEdges1, posEdges1);
	}

	public static BufferedImage sharpen(BufferedImage img) {
		float[] data = { -1, -1, -1, -1, 8, -1, -1, -1, -1 };
		Kernel sharpKern = new Kernel(3, 3, data);
		ConvolveOp sharpenOp = new ConvolveOp(sharpKern, ConvolveOp.EDGE_NO_OP, null);
		BufferedImage sharpImg = null;
		sharpImg = sharpenOp.filter(img, null);
		return sharpImg;
	}

	public static BufferedImage gaussianBlur(BufferedImage gray, int ksize, double sigma) {
		BufferedImage gaussianAvg = null;
		Kernel gKern = getGaussianKernel(ksize, sigma);
		float[] values = gKern.getKernelData(null);
		float total = 0;
		for (int i = 0; i < values.length; i++) {
			total += values[i];
		}
		ConvolveOp gaussOp = new ConvolveOp(gKern, ConvolveOp.EDGE_NO_OP, null);
		gaussianAvg = gaussOp.filter(gray, null);
		return gaussianAvg;
	}

	public static BufferedImage toGrayscaleLum(BufferedImage img) {
		double RED_LUMINOSITY = 0.21;
		double GREEN_LUMINOSITY = 0.72;
		double BLUE_LUMINOSITY = 0.07;

		Raster raster = img.getRaster();
		BufferedImage gray = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		WritableRaster outRaster = gray.getRaster();
		for (int y = 0; y < raster.getHeight(); y++) {
			for (int x = 0; x < raster.getWidth(); x++) {
				int[] rgb = new int[4];
				for (int i = 0; i < raster.getNumBands(); i++) {
					rgb[i] = raster.getSample(x, y, i);
				}
				int grayPixel = (int) (rgb[2] * BLUE_LUMINOSITY + rgb[1] * GREEN_LUMINOSITY + rgb[0] * RED_LUMINOSITY);
				outRaster.setSample(x, y, 0, grayPixel);
			}
		}
		return gray;
	}

	public static BufferedImage toGrayscaleAvg(BufferedImage img) {
		Raster raster = img.getRaster();
		BufferedImage gray = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		WritableRaster outRaster = gray.getRaster();
		byte[] pixel = null;
		for (int y = 0; y < raster.getHeight(); y++) {
			for (int x = 0; x < raster.getWidth(); x++) {
				int[] rgb = new int[4];
				for (int i = 0; i < raster.getNumBands(); i++) {
					rgb[i] = raster.getSample(x, y, i);
				}
				int grayPixel = (int) ((rgb[2] + rgb[1] + rgb[0]) / 3);
				outRaster.setSample(x, y, 0, grayPixel);
			}
		}
		return gray;
	}

	public static BufferedImage subtract(BufferedImage fg, BufferedImage bg) {
		BufferedImage subtracted = new BufferedImage(bg.getWidth(), bg.getHeight(), bg.getType());

		Raster rasterBG = bg.getRaster();
		Raster rasterFG = fg.getRaster();
		WritableRaster rasterSub = subtracted.getRaster();
		byte[] pixelBG = null;
		byte[] pixelFG = null;

		for (int i = 0; i < rasterBG.getHeight(); i++) {
			for (int j = 0; j < rasterBG.getWidth(); j++) {
				pixelBG = (byte[]) rasterBG.getDataElements(j, i, pixelBG);
				pixelFG = (byte[]) rasterFG.getDataElements(j, i, pixelFG);
				for (int k = 0; k < pixelBG.length; k++) {
					pixelBG[k] = (byte) (Math.abs((pixelBG[k] & 0xFF) - (pixelFG[k] & 0xFF)));
					//int s = ((pixelBG[k] & 0xFF) - (pixelFG[k] & 0xFF));
					//s = s < 0 ? 0 : s;
					//pixelBG[k] = (byte) s;
				}
				rasterSub.setDataElements(j, i, pixelBG);
				// rasterFG.setDataElements(i, j, inData);
			}
		}
		return subtracted;
	}

	public static BufferedImage erode(BufferedImage img, int crossSize) {
		return morph(img, crossSize, MORPH.ERODE);
	}

	public static BufferedImage dilate(BufferedImage img, int crossSize) {
		return morph(img, crossSize, MORPH.DILATE);
	}

	public static BufferedImage open(BufferedImage img, int crossSize) {
		return dilate(erode(img, crossSize), crossSize);
	}

	public static BufferedImage bitwise_or(BufferedImage... images) {
		BufferedImage or = new BufferedImage(images[0].getWidth(), images[0].getHeight(), images[0].getType());
		WritableRaster rastOr = or.getRaster();
		for (int i = 0; i < images.length; i++) {
			Raster rast = images[i].getRaster();
			for (int y = 0; y < or.getHeight(); y++) {
				for (int x = 0; x < or.getWidth(); x++) {
					for (int b = 0; b < rastOr.getNumBands(); b++) {
						int current = rastOr.getSample(x, y, b);
						int next = rast.getSample(x, y, b);
						int val = current + next;
						val = val > 255 ? 255 : val;
						rastOr.setSample(x, y, b, val);
					}
				}
			}
		}
		return or;
	}

	public static BufferedImage bitwise_and(BufferedImage... images) {
		BufferedImage and = deepCopy(images[0]);
		WritableRaster rastAnd = and.getRaster();
		for (int i = 1; i < images.length; i++) {
			Raster rast = images[i].getRaster();
			for (int y = 0; y < and.getHeight(); y++) {
				for (int x = 0; x < and.getWidth(); x++) {
					int result = rastAnd.getSample(x, y, 0) & rast.getSample(x, y, 0);
					rastAnd.setSample(x, y, 0, result);
				}
			}
		}
		return and;
	}

	public static BufferedImage getChannel(BufferedImage img, int channel) {
		short[][] mapping = new short[3][256];
		for (int i = 0; i < mapping[0].length; i++) {
			mapping[channel][i] = (short) i;
		}
		BufferedImage singleChannelImage = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
		LookupTable lookup = new ShortLookupTable(0, mapping);
		LookupOp chanOp = new LookupOp(lookup, null);
		singleChannelImage = chanOp.filter(img, singleChannelImage);
		return singleChannelImage;
	}

	public static BufferedImage resize(BufferedImage img, double scale) {
		int width = (int) (scale * img.getWidth());
		int height = (int) (scale * img.getHeight());
		BufferedImage scaledImage = new BufferedImage(width, height, img.getType());
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				scaledImage.setRGB(x, y, img.getRGB((int) (x / scale), (int) (y / scale)));
			}
		}
		return scaledImage;

	}

	public static Rectangle2D.Double getBoundingBox(BufferedImage binary) {
		double x = leftbound(binary);
		double y = upperbound(binary);
		double w = rightbound(binary) - x;
		double h = lowerbound(binary) - y;
		return new Rectangle2D.Double(x, y, w, h);
	}

	public static BufferedImage getHistogramCDF(BufferedImage image) {
		return getHistogramCDF(image, 0);
	}

	public static BufferedImage getHistogramPDF(BufferedImage image) {
		return getHistogramPDF(image, 0);
	}

	public static BufferedImage getHistogramCDF(BufferedImage image, int band) {
		Histogram h = new Histogram(image);
		return h.getCDFImage(image.getWidth(), image.getHeight(), 0.05f, 0);
	}

	public static BufferedImage getHistogramPDF(BufferedImage image, int band) {
		Histogram h = new Histogram(image);
		return h.getPMFImage(image.getWidth(), image.getHeight(), 0.05f, 0);
	}

	private static BufferedImage filter(BufferedImage img, int kSize, int[] which) {
		Raster rast = img.getRaster();
		BufferedImage filtered = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
		WritableRaster wrast = filtered.getRaster();
		for (int y = kSize / 2; y < img.getHeight() - kSize / 2; y++) {
			for (int x = kSize / 2; x < img.getWidth() - kSize / 2; x++) {
				BufferedImage sub = getNeighborhood(x, y, kSize, kSize, img);
				Raster rSub = sub.getRaster();
				int[] data = new int[sub.getWidth() * sub.getHeight()];
				data = rSub.getSamples(0, 0, sub.getWidth(), sub.getHeight(), 0, data);
				Arrays.sort(data);
				wrast.setSample(x, y, 0, mean(data, which));
			}
		}
		return filtered;
	}

	private static double minimalMeanSquareError(int[] values, double totalVariance) {
		double center = values[values.length / 2]; // f(x,y)
		double neighVariance = variance(values);
		double neighMean = mean(values);
		if (neighVariance == 0) {
			return center;
		}
		return center - (totalVariance / neighVariance) * (center - neighMean);
	}

	private static int geometricMean(int[] values) {
		double product = 1;
		for (int i = 0; i < values.length; i++) {
			double pow = Math.pow(values[i], (1.0 / values.length));
			product = product * pow;
		}
		return (int) product;
	}

	private static int contraHarmonicMean(int[] values, int r) {
		double numerator = 0;
		double denominator = 0;
		for (int i = 0; i < values.length; i++) {
			denominator += Math.pow(values[i], r);
			numerator += Math.pow(values[i], r + 1);
		}
		return (int) (numerator / denominator);
	}

	private static double mean(int[] values) {
		int[] which = new int[values.length];
		for (int i = 0; i < which.length; i++) {
			which[i] = i;
		}
		return mean(values, which);
	}

	private static double mean(int[] values, int[] which) {
		int mean = 0;
		for (int i = 0; i < which.length; i++) {
			mean += values[which[i]];
		}
		return mean / which.length;
	}

	private static int convolve(int[] values, Kernel kernel) {
		float[] data = kernel.getKernelData(null);
		int sum = 0;
		for (int y = 0; y < kernel.getHeight(); y++) {
			for (int x = 0; x < kernel.getWidth(); x++) {
				int currentIndex = y * kernel.getWidth() + x;
				sum += values[currentIndex] * data[currentIndex];
			}
		}
		return sum;
	}

	private static double variance(int[] values) {
		double mean = mean(values);
		double variance = 0;
		for (int i = 0; i < values.length; i++) {
			variance += Math.pow((values[i] - mean), 2) / values.length;
		}
		return variance;
	}

	private static BufferedImage getNeighborhood(int x, int y, int kHeight, int kWidth, BufferedImage b) {
		// Assumes anchor is center.
		int startX = x - kWidth / 2;
		int startY = y - kHeight / 2;
		BufferedImage sub = b.getSubimage(startX, startY, kWidth, kHeight);
		return sub;
	}

	private static int[][] convolve(BufferedImage img, Kernel kernel) {
		Raster rast = img.getRaster();
		BufferedImage convolved = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_BGR);
		int[][] conv = new int[img.getHeight()][img.getWidth()];
		WritableRaster cRast = convolved.getRaster();
		for (int y = kernel.getHeight() / 2; y < img.getHeight() - kernel.getHeight() / 2; y++) {
			for (int x = kernel.getWidth() / 2; x < img.getWidth() - kernel.getWidth() / 2; x++) {
				BufferedImage sub = getNeighborhood(x, y, kernel.getHeight(), kernel.getWidth(), img);
				Raster rSub = sub.getRaster();
				int[] data = new int[sub.getWidth() * sub.getHeight()];
				data = rSub.getSamples(0, 0, sub.getWidth(), sub.getHeight(), 0, data);
				conv[y][x] = convolve(data, kernel);
			}
		}
		return conv;
	}

	private static int clamp(float value) {
		// return 0 if value <0, or 255 if value >255
		return Math.min(Math.max(Math.round(value), 0), 255);
	}

	private static int[][] sobelYMags(BufferedImage img) {
		float[] sobelX = { -1, 0, 1, -2, 0, 2, -1, 0, 1 };
		Kernel sobKernX = new Kernel(3, 3, sobelX);
		return convolve(img, sobKernX);
	}

	private static int[][] sobelXMags(BufferedImage img) {
		float[] sobelY = { -1, -2, -1, 0, 0, 0, 1, 2, 1 };
		Kernel sobKernY = new Kernel(3, 3, sobelY);
		return convolve(img, sobKernY);
	}

	private static int[][][] sobel(BufferedImage img) {
		int[][] sobX = sobelXMags(img);
		int[][] sobY = sobelYMags(img);

		int[][] magnitudes = getGradientMagnitude(sobX, sobY);
		int[][] angles = getGradientAngle(sobX, sobY);

		return new int[][][] { magnitudes, angles };
	}

	private static BufferedImage getCannyColored(int[][] mag, int[][] ang) {
		BufferedImage coloredAngles = new BufferedImage(mag[0].length, mag.length, BufferedImage.TYPE_INT_RGB);
		Color color = Color.BLACK;
		for (int y = 0; y < mag.length; y++) {
			for (int x = 0; x < mag[y].length; x++) {
				if (mag[y][x] == 0) {
					color = Color.BLACK;
				} else if (ang[y][x] == 0) {
					color = Color.YELLOW;
				} else if (ang[y][x] == 45) {
					color = Color.GREEN;
				} else if (ang[y][x] == 90) {
					color = Color.BLUE;
				} else if (ang[y][x] == 135) {
					color = Color.RED;
				}
				coloredAngles.setRGB(x, y, color.getRGB());
			}
		}
		return coloredAngles;
	}

	private static int[][] hysteresisThreshold(int[][] mag, int tLow, int tHigh) {
		int[][] thresh = new int[mag.length][mag[0].length];

		for (int y = 2; y < mag.length - 2; y++) {
			for (int x = 2; x < mag[y].length - 2; x++) {
				int out = 0;
				if (mag[y][x] < tLow) {
					out = 0;
				} else if (mag[y][x] > tHigh) {
					out = 255;
				} else if (mag[y][x] >= tLow && mag[y][x] <= tHigh) {
					boolean set = false;
					boolean check5x5 = false;
					for (int y3 = y - 1; y3 < y + 2; y3++) {
						for (int x3 = x - 1; x3 < x + 2; x3++) {
							if (mag[y3][x3] > tHigh) {
								set = true;
								break;
							} else if (mag[y3][x3] >= tLow && mag[y3][x3] <= tHigh) {
								check5x5 = true;
							}
						}
					}
					if (set) {
						out = 255;
					} else if (check5x5) {
						for (int y5 = y - 1; y5 < y + 2; y5++) {
							for (int x5 = x - 1; x5 < x + 2; x5++) {
								if (mag[y5][x5] > tHigh) {
									out = 255;
									break;
								}
							}
						}
					}
				} else {
					out = 0;
				}
				thresh[y][x] = out;
			}
		}
		return thresh;
	}

	private static int[][] nonMaxSuppression(int[][] mag, int[][] ang) {
		int[][] suppressedMags = new int[mag.length][mag[0].length];
		int p1 = 0;
		int p2 = 0;
		int p3 = 0;
		for (int y = 1; y < mag.length - 1; y++) {
			for (int x = 1; x < mag[y].length - 1; x++) {
				if (ang[y][x] == 0) {
					p1 = mag[y][x + 1];
					p2 = mag[y][x];
					p3 = mag[y][x - 1];
				} else if (ang[y][x] == 90) {
					p1 = mag[y + 1][x];
					p2 = mag[y][x];
					p3 = mag[y - 1][x];
				} else if (ang[y][x] == 45) {
					p1 = mag[y + 1][x + 1];
					p2 = mag[y][x];
					p3 = mag[y - 1][x - 1];
				} else if (ang[y][x] == 135) {
					p1 = mag[y - 1][x + 1];
					p2 = mag[y][x];
					p3 = mag[y + 1][x - 1];
				}
				if (p2 > p1 && p2 > p3) {
					suppressedMags[y][x] = p2;
				} else {
					suppressedMags[y][x] = 0;
				}
			}
		}
		return suppressedMags;
	}

	private static int[][] getGradientMagnitude(int[][] xGrad, int[][] yGrad) {
		int[][] magnitude = new int[xGrad.length][xGrad[0].length];
		for (int y = 0; y < magnitude.length; y++) {
			for (int x = 0; x < magnitude[y].length; x++) {
				double xSq = Math.pow(xGrad[y][x], 2);
				double ySq = Math.pow(yGrad[y][x], 2);
				magnitude[y][x] = (int) Math.pow(xSq + ySq, 0.5);
			}
		}
		return magnitude;
	}

	private static int[][] getGradientAngle(int[][] xGrad, int[][] yGrad) {
		int[][] angles = new int[xGrad.length][xGrad[0].length];
		for (int y = 0; y < angles.length; y++) {
			for (int x = 0; x < angles[y].length; x++) {
				double angle;
				if (yGrad[y][x] == 0 && xGrad[y][x] == 0) {
					// no edge?
					angle = 0;
				} else if (yGrad[y][x] == 0) {
					// horizontal
					angle = 0;
				} else if (xGrad[y][x] == 0) {
					// vertical
					angle = 90;
				} else {
					angle = Math.toDegrees(Math.atan(yGrad[y][x] / xGrad[y][x]));
					if (angle >= -22.5 && angle < 22.5) {
						angle = 0;
					} else if (angle >= 22.5 && angle < 67.5) {
						angle = 45;
					} else if ((angle >= 67.5 && angle <= 90) || (angle < -67.5 && angle >= -90)) {
						angle = 90;
					} else if ((angle < -22.5 && angle >= -67.5)) {
						angle = 135;
					}
				}
				angles[y][x] = (int) angle;
			}
		}
		return angles;
	}

	private static Kernel getGaussianKernel(int size, double sigma) {
		float[] data = new float[size * size];
		int center = size / 2;
		double factor = 1 / getGuassianValue(center, center, sigma);
		float total = 0.0f;
		for (int y = 0; y < size; y++) {
			for (int x = 0; x < size; x++) {
				int currX = center - x;
				int currY = center - y;
				data[y * size + x] = (float) (factor * getGuassianValue(currX, currY, sigma));
				total += data[y * size + x];
			}
		}
		for (int i = 0; i < data.length; i++) {
			data[i] = data[i] / total;
		}
		return new Kernel(size, size, data);

	}

	private static double getGuassianValue(int x, int y, double sigma) {
		return (1 / (2 * Math.PI * sigma * sigma)) * Math.pow(Math.E, -(x * x + y * y) / (2 * sigma * sigma));
	}

	private enum MORPH {
		DILATE, ERODE
	}

	private static BufferedImage deepCopy(BufferedImage bi) {
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

	public static BufferedImage makeTester(int w, int h, int value) {
		BufferedImage tester = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
		WritableRaster raster = tester.getRaster();
		int[] data = new int[h * w];
		Utils.fill(data, new int[] { value });
		raster.setSamples(0, 0, w, h, 0, data);
		return tester;
	}

	/**
	 * Used to Dilate or Erode, depending on "bitOfInterest"
	 * 
	 * @param img
	 * @param crossSize
	 *            the width and height of the cross that is used to perform the
	 *            morphological operation.
	 * @param m
	 *            the desired morphological operation (dilate or erode).
	 * @return
	 */
	private static BufferedImage morph(BufferedImage img, int crossSize, MORPH m) {
		int bitOfInterest, oppositeBit;
		switch (m) {
		case DILATE:
			bitOfInterest = 1;
			oppositeBit = 0;
			break;
		case ERODE:
			bitOfInterest = 0;
			oppositeBit = 1;
			break;
		default:
			bitOfInterest = 1;
			oppositeBit = 0;
		}
		int b = 0; // band

		int[][] distances = new int[img.getWidth()][img.getHeight()];
		WritableRaster raster = img.getRaster();
		int maxDistance = img.getHeight() * img.getWidth();
		for (int y = 0; y < raster.getHeight(); y++) {
			for (int x = 0; x < raster.getWidth(); x++) {
				if (raster.getSample(x, y, b) == bitOfInterest) {
					distances[x][y] = 0;
				} else {
					distances[x][y] = maxDistance;
					if (y > 0) {
						distances[x][y] = Math.min(distances[x][y], distances[x][y - 1] + 1);
					}
					if (x > 0) {
						distances[x][y] = Math.min(distances[x][y], distances[x - 1][y] + 1);
					}
				}
			}
		}

		for (int y = raster.getHeight() - 1; y >= 0; y--) {
			for (int x = raster.getWidth() - 1; x >= 0; x--) {
				if (y + 1 < raster.getHeight()) {
					distances[x][y] = Math.min(distances[x][y], distances[x][y + 1] + 1);
				}
				if (x + 1 < raster.getWidth()) {
					distances[x][y] = Math.min(distances[x][y], distances[x + 1][y] + 1);
				}
			}
		}

		BufferedImage morphed = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
		WritableRaster morphedRaster = morphed.getRaster();
		for (int y = 0; y < raster.getHeight(); y++) {
			for (int x = 0; x < raster.getWidth(); x++) {
				int bitToSet = distances[x][y] < crossSize ? bitOfInterest : oppositeBit;
				morphedRaster.setSample(x, y, b, bitToSet);
			}
		}
		return morphed;
	}

	private static BufferedImage toBufferedImage(int[][] arr) {
		int w = arr[0].length;
		int h = arr.length;
		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
		WritableRaster r = img.getRaster();
		int[] flat = Utils.flatten(arr);
		r.setSamples(0, 0, w, h, 0, flat);
		return img;
	}

	private static double leftbound(BufferedImage binary) {
		Raster raster = binary.getRaster();
		for (int x = 0; x < binary.getWidth(); x++) {
			for (int y = 0; y < binary.getHeight(); y++) {
				if (raster.getSample(x, y, 0) != 0) {
					return x;
				}
			}
		}
		return 0;
	}

	private static double upperbound(BufferedImage binary) {
		Raster raster = binary.getRaster();
		for (int y = 0; y < binary.getHeight(); y++) {
			for (int x = 0; x < binary.getWidth(); x++) {
				if (raster.getSample(x, y, 0) != 0) {
					return y;
				}
			}
		}
		return 0;
	}

	private static double lowerbound(BufferedImage binary) {
		Raster raster = binary.getRaster();
		for (int y = binary.getHeight() - 1; y >= 0; y--) {
			for (int x = binary.getWidth() - 1; x >= 0; x--) {
				if (raster.getSample(x, y, 0) != 0) {
					return y;
				}
			}
		}
		return 0;
	}

	private static double rightbound(BufferedImage binary) {
		Raster raster = binary.getRaster();
		for (int x = binary.getWidth() - 1; x >= 0; x--) {
			for (int y = binary.getHeight() - 1; y >= 0; y--) {
				if (raster.getSample(x, y, 0) != 0) {
					return x;
				}
			}
		}
		return 0;
	}
}
