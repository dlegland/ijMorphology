/**
 * 
 */
package ijt.filter.morphology.strel;

import ij.ImageStack;
import ijt.filter.morphology.Strel3D;

/**
 * A structuring element that can performs erosion or dilation directly in the
 * original image stack. As InPlaceStrel do not require memory allocation, 
 * they result in faster execution.
 * 
 * @see SeparableStrel3D
 * @author David Legland
 *
 */
public interface InPlaceStrel3D extends Strel3D {

	/**
	 * Performs dilation of the stack given as argument, and stores the result
	 * in the same image. 
	 * @param image the input image to dilate
	 */
	public void inPlaceDilation(ImageStack stack);

	/**
	 * Performs erosion of the image given as argument, and stores the result
	 * in the same image. 
	 * @param image the input image to erode
	 */
	public void inPlaceErosion(ImageStack stack);
	
	/**
	 * The reverse structuring element of an InPlaceStrel is also an
	 * InPlaceStrel.
	 */
	public InPlaceStrel3D reverse();
}
