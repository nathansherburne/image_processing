package ch.makery.address.model;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.pearsoneduc.ip.op.FFTException;
import com.pearsoneduc.ip.op.ImageFFT;

import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class SelectableImageViewList {
	
	private CVImageView selectedImageView = null;
	private List<CVImageView> imageViews = new ArrayList<>();
	
	public SelectableImageViewList() {
		
	}
	
	public List<CVImageView> getList() {
		return imageViews;
	}
	
	/**
	 * Create a new ImageView and add it to the list.
	 * @param image
	 */
	public void add(BufferedImage image) {
		imageViews.add(new CVImageView(image));
	}
	
	/**
	 * Removes the in-focus ImageView. After this call, the selected image view will be null.
	 */
	public void remove() {
		imageViews.remove(selectedImageView);
		selectedImageView = null;
	}
	
	/**
	 * 
	 * @return the in-focus ImageView's BufferedImage
	 */
	public BufferedImage getSelectedImage() {
		if(selectedImageView == null) {
			return null;
		}
		return selectedImageView.getCurrentBufferedImage();
	}
	
	/**
	 * 
	 * @return the in-focus ImageView's BufferedImage
	 */
	public ImageFFT getSelectedImageFFT() {
		if(selectedImageView == null) {
			return null;
		}
		return selectedImageView.getCurrentImageFFT();
	}
	
	public boolean isSelectedSpectral() {
		return getSelectedImageFFT().isSpectral();
	}
	
	/**
	 * Resets the in-focus ImageView's image to it's original source image.
	 */
	public void reset() {
		selectedImageView.reset();
	}
	
	/**
	 * Undoes the most recent operation to the in-focus ImageView.
	 * 
	 * Undo only stores the previous image, so only one undo is possible.
	 */
	public void undo() {
		selectedImageView.undo();
	}
	
	/**
	 * For the ImageView that is in focus, set its image to a new image.
	 * @param image
	 */
	public void setSelectedImage(BufferedImage image) {
		selectedImageView.setPrevious();
		selectedImageView.setImage(image);
	}
	
	/**
	 * For the ImageView that is in focus, set its image to a new image.
	 * @param image
	 */
	public void setSelectedImageFFT(ImageFFT imageFFT) {
		selectedImageView.setPrevious();
		selectedImageView.setImageFFT(imageFFT);
	}
	
	public void setIndexImage(BufferedImage image, int index) {
		imageViews.get(index).setImage(image);
	}
	
	public void setIndexImageFFT(ImageFFT imageFFT, int index) {
		imageViews.get(index).setImageFFT(imageFFT);
	}
	
	public CVImageView getSelectedImageView() {
		return selectedImageView;
	}
	
	public CVImageView getLast() {
		return imageViews.get(imageViews.size() - 1);
	}
	
	public int getSelectedImageViewIndex() {
		return imageViews.indexOf(selectedImageView);
	}
	
	public BufferedImage getIndexImage(int index) {
		return imageViews.get(index).getCurrentBufferedImage();
	}
	
	public ImageFFT getIndexImageFFT(int index) {
		return imageViews.get(index).getCurrentImageFFT();
	}
	
	private void changeSelectedImageView(CVImageView imageView) {
		DropShadow ds = new DropShadow(20, Color.BLACK);
		if(selectedImageView != null) {
			selectedImageView.setEffect(null);
		}
		selectedImageView = imageView;
		selectedImageView.setEffect(ds);
	}
	
	private class CVImageView extends ImageView {
		private BufferedImage sourceImage;
		private BufferedImage currentImage;
		private BufferedImage previousImage;
		private ImageFFT imageFFT = null;
		private ImageFFT previousImageFFT = null;
		
		public CVImageView(BufferedImage sourceImage) {
			super(SwingFXUtils.toFXImage(sourceImage, null));
			this.sourceImage = sourceImage;
			this.currentImage = sourceImage;
			this.previousImage = sourceImage;
			init();
		}
		
		public BufferedImage getSourceBufferedImage() {
			return sourceImage;
		}
		
		public BufferedImage getCurrentBufferedImage() {
			return currentImage;
		}
		
		/**
		 * Return a copy so that whoever is operating on it can do so without indirectly
		 * affecting the image view.
		 * @return
		 */
		public ImageFFT getCurrentImageFFT() {
			ImageFFT copy = null;
			imageFFT.transform();
			try {
				copy = new ImageFFT(imageFFT.toImage(null));
				copy.transform();  // get it back into freq dom
			} catch (FFTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			imageFFT.transform();
			return copy;
		}
		
		public boolean isSpectral() {
			if(imageFFT == null) {
				return false;
			} else {
				return imageFFT.isSpectral();
			}
		}
		
		public void setImage(BufferedImage image) {
			currentImage = image;
			setImage(SwingFXUtils.toFXImage(image, null));
		}
		
		public void setImageFFT(ImageFFT imageFFT) {
			this.imageFFT = imageFFT;
			try {
				currentImage = imageFFT.getSpectrum();
				setImage(SwingFXUtils.toFXImage(currentImage, null));
			} catch (FFTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void reset() {
			currentImage = sourceImage;
			setImage(SwingFXUtils.toFXImage(sourceImage, null));
			imageFFT = null;
		}
		
		public void undo() {
			currentImage = previousImage;
			imageFFT = previousImageFFT;
			setImage(SwingFXUtils.toFXImage(previousImage, null));
		}
		
		public void setPrevious() {
			previousImage = currentImage;
			if(isSpectral()) {
				// Create a copy of the ImageFFT object
				try {
					imageFFT.transform();
					previousImageFFT = new ImageFFT(imageFFT.toImage(null));
					previousImageFFT.transform(); // get this into freq dom
					imageFFT.transform();
				} catch (FFTException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	
		}
		
		private void init() {
			setOnMouseClicked((MouseEvent e) -> imageViewMouseClick(e));
			focusedProperty()
			.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
				if (newValue) {
					changeSelectedImageView(this);
				} else {
				}
			});
		}
		public void imageViewMouseClick(MouseEvent e) {
			ImageView selectedImageView = (ImageView) e.getSource();
			selectedImageView.requestFocus();
		}
}
}
