/**
 * 
 */
package ijt.filter.morphology.geodrec;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.LinkedList;

import ij.IJ;
import ij.ImageStack;


/**
 * Geodesic reconstruction by dilation for 3D stacks of byte processors.
 * 
 * This version uses iterations of forward and backward passes until no more
 * modifications are made.
 * 
 * @author David Legland
 * 
 */
public class GeodesicReconstructionByDilation3DGray8Scanning implements
		GeodesicReconstruction3DAlgo {
	ImageStack marker;
	ImageStack mask;
	
	ImageStack result;
	
	/** image width */
	int sizeX = 0;
	/** image height */
	int sizeY = 0;
	/** image depth */
	int sizeZ = 0;

	int connectivity = 6;
	
	LinkedList<int[]> queue;
	
	/**
	 * The flag indicating whether the result image has been modified during
	 * last image scan
	 */
	boolean modif;

	/**
	 * 
	 * boolean flag for toggling the display of debugging infos.
	 */
	public boolean verbose = false;
	
	public boolean showStatus = true; 
	public boolean showProgress = false; 

	/**
	 * Creates a new instance of geodesic reconstruction by dilation algorithm,
	 * using the default connectivity 6.
	 */
	public GeodesicReconstructionByDilation3DGray8Scanning() {
	}
	
	/**
	 * Creates a new instance of geodesic reconstruction by dilation algorithm,
	 * that specifies the connectivity to use.
	 */
	public GeodesicReconstructionByDilation3DGray8Scanning(int connectivity) {
		this.connectivity = connectivity;
	}

	public int getConnectivity() {
		return this.connectivity;
	}
	
	public void setConnectivity(int conn) {
		this.connectivity = conn;
	}


	/**
	 * Run the reconstruction by dilation algorithm using the images specified
	 * as argument.
	 */
	public ImageStack applyTo(ImageStack marker, ImageStack mask) {
		// Keep references to input images
		this.marker = marker;
		this.mask = mask;
		
		// Check sizes are consistent
		this.sizeX 	= marker.getWidth();
		this.sizeY 	= marker.getHeight();
		this.sizeZ 	= marker.getSize();
		if (sizeX != mask.getWidth() || sizeY != mask.getHeight() || sizeZ != mask.getSize()) {
			throw new IllegalArgumentException("Marker and Mask images must have the same size");
		}
		
		// Check connectivity has a correct value
		if (connectivity != 6 && connectivity != 26) {
			throw new RuntimeException(
					"Connectivity for stacks must be either 6 or 26, not "
							+ connectivity);
		}

		initializeResult();
		
		// Count the number of iterations for eventually displaying progress
		int iter = 1;
		
		// Iterate forward and backward propagations until no more pixel have been modified
		do {
			modif = false;

			// Display current status
			if (verbose) {
				System.out.println("Forward iteration " + iter);
			}
			if (showStatus) {
				IJ.showStatus("Geod. Rec. by Dil. Fwd " + iter);
			}
			
			// forward iteration
			switch (connectivity) {
			case 6:
				forwardDilationC6(); 
				break;
			case 26:
				forwardDilationC26(); 
				break;
			}

			// Display current status
			if (verbose) {
				System.out.println("Backward iteration " + iter);
			}
			if (showStatus) {
				IJ.showStatus("Geod. Rec. by Dil. Bwd " + iter);
			}
			
			// backward iteration
			switch (connectivity) {
			case 6:
				backwardDilationC6();
				break;
			case 26:	
				backwardDilationC26(); 
				break;
			}

			iter++;
		} while (modif);
	
		return this.result;
	}

	/** 
	 * Initialize the result image with the minimum value of marker and mask
	 * images.
	 */
	private void initializeResult() {
		// Create result image the same size as marker image
		this.result = ImageStack.create(sizeX, sizeY, sizeZ, marker.getBitDepth());

		Object[] stack = result.getImageArray();
		Object[] markerStack = marker.getImageArray();
		Object[] maskStack = mask.getImageArray();
		byte[] slice;
		byte[] markerSlice;
		byte[] maskSlice;

		// Initialize the result image with the minimum value of marker and mask
		// images
		for (int z = 0; z < sizeZ; z++) {
			slice = (byte[]) stack[z];
			maskSlice = (byte[]) maskStack[z];
			markerSlice = (byte[]) markerStack[z];
			
			for (int y = 0; y < sizeY; y++) {
				for (int x = 0; x < sizeX; x++) {
					int index = y * sizeX + x;
					int value = min(markerSlice[index] & 0x00FF, maskSlice[index] & 0x00FF);
					slice[index] = (byte) value;
				}
			}
		}
	}
	

	/**
	 * Update result image using pixels in the upper left neighborhood, using
	 * the 6-adjacency, assuming pixels are stored in bytes.
	 */
	private void forwardDilationC6() {
		// the maximal value around current pixel
		int maxValue;

		Object[] stack = result.getImageArray();
		byte[] slice;
		byte[] slice2;
		
		if (showProgress) {
			IJ.showProgress(0, sizeZ);
		}

		// Iterate over pixels
		for (int z = 0; z < sizeZ; z++) {
			if (showProgress) {
				IJ.showProgress(z + 1, sizeZ);
				System.out.println("z = " + z);
			}
			
			slice = (byte[]) stack[z];
			for (int y = 0; y < sizeY; y++) {
				for (int x = 0; x < sizeX; x++) {
					int currentValue = slice[y * sizeX + x] & 0x00FF;
					maxValue = currentValue;
					
					// Iterate over the 3 'upper' neighbors of current pixel
					if (x > 0) 
						maxValue = max(maxValue, slice[y * sizeX + x - 1] & 0x00FF);
					if (y > 0) 
						maxValue = max(maxValue, slice[(y - 1) * sizeX + x] & 0x00FF);
					if (z > 0) {
						slice2 = (byte[]) stack[z - 1];
						maxValue = max(maxValue, slice2[y * sizeX + x] & 0x00FF);
					}
					
					// update value of current voxel
					maxValue = Math.min(maxValue, (int) mask.getVoxel(x, y, z));
					if (maxValue > currentValue) {
						slice[y * sizeX + x] = (byte) (maxValue & 0x00FF);
						modif = true;
					}
				}
			}
		} // end of pixel iteration
	}

	/**
	 * Update result image using pixels in the upper left neighborhood, using
	 * the 26-adjacency, assuming pixels are stored in bytes.
	 */
	private void forwardDilationC26() {
		// the maximal value around current pixel
		int maxValue;

		Object[] stack = result.getImageArray();
		byte[] slice;
		byte[] slice2;
		
		if (showProgress) {
			IJ.showProgress(0, sizeZ);
		}

		// Iterate over pixels
		for (int z = 0; z < sizeZ; z++) {
			if (showProgress) {
				IJ.showProgress(z + 1, sizeZ);
				System.out.println("z = " + z);
			}

			slice = (byte[]) stack[z];
			for (int y = 0; y < sizeY; y++) {
				for (int x = 0; x < sizeX; x++) {
					int currentValue = slice[y * sizeX + x] & 0x00FF;
					maxValue = currentValue;
					
					// Iterate over neighbors of current pixel
					int zmax = min(z + 1, sizeZ);
					for (int z2 = max(z - 1, 0); z2 < zmax; z2++) {
						slice2 = (byte[]) stack[z2];
						
						int ymax = z2 == z ? y : min(y + 1, sizeY - 1); 
						for (int y2 = max(y - 1, 0); y2 <= ymax; y2++) {
							int xmax = (z2 == z && y2 == y) ? x - 1 : min(x + 1, sizeX - 1); 
							for (int x2 = max(x - 1, 0); x2 <= xmax; x2++) {
								int neighborValue = slice2[y2 * sizeX + x2] & 0x00FF;
								if (neighborValue > maxValue)
									maxValue = neighborValue;
							}
						}
					}

					// update value of current voxel
					maxValue = Math.min(maxValue, (int) mask.getVoxel(x, y, z));
					if (maxValue > currentValue) {
						slice[y * sizeX + x] = (byte) (maxValue & 0x00FF);
						modif = true;
					}

				}
			}
		}
	}

	/**
	 * Update result image using pixels in the lower right neighborhood, using
	 * the 6-adjacency.
	 */
	private void backwardDilationC6() {
		// the maximal value around current pixel
		int maxValue;

		Object[] stack = result.getImageArray();
		byte[] slice;
		byte[] slice2;

		if (showProgress) {
			IJ.showProgress(0, sizeZ);
		}

		// Iterate over voxels
		for (int z = sizeZ - 1; z >= 0; z--) {
			if (showProgress) {
				IJ.showProgress(sizeZ - z, sizeZ);
				System.out.println("z = " + z);
			}

			slice = (byte[]) stack[z];
			for (int y = sizeY - 1; y >= 0; y--) {
				for (int x = sizeX - 1; x >= 0; x--) {
					int currentValue = slice[y * sizeX + x] & 0x00FF;
					maxValue = currentValue;
					
					// Iterate over the 3 'lower' neighbors of current voxel
					if (x < sizeX - 2) 
						maxValue = max(maxValue, slice[y * sizeX + x + 1] & 0x00FF);
					if (y < sizeY - 2) 
						maxValue = max(maxValue, slice[(y + 1) * sizeX + x] & 0x00FF);
					if (z < sizeZ - 2) {
						slice2 = (byte[]) stack[z + 1];
						maxValue = max(maxValue, slice2[y * sizeX + x] & 0x00FF);
					}

					// update value of current voxel
					maxValue = Math.min(maxValue, (int) mask.getVoxel(x, y, z));
					if (maxValue > currentValue) {
						slice[y * sizeX + x] = (byte) (maxValue & 0x00FF);
						modif = true;
					}
				}
			}
		}	
	}
	
	/**
	 * Update result image using pixels in the upper left neighborhood, using
	 * the 26-adjacency.
	 */
	private void backwardDilationC26() {
		// the maximal value around current pixel
		int maxValue;

		Object[] stack = result.getImageArray();
		byte[] slice;
		byte[] slice2;

		if (showProgress) {
			IJ.showProgress(0, sizeZ);
		}

		// Iterate over voxels
		for (int z = sizeZ - 1; z >= 0; z--) {
			if (showProgress) {
				IJ.showProgress(sizeZ - z, sizeZ);
				System.out.println("z = " + z);
			}

			slice = (byte[]) stack[z];
			for (int y = sizeY - 1; y >= 0; y--) {
				for (int x = sizeX - 1; x >= 0; x--) {
					int currentValue = slice[y * sizeX + x] & 0x00FF;
					maxValue = currentValue;
					
					// Iterate over neighbors of current voxel
					int zmin = max(z - 1, 0);
					for (int z2 = min(z + 1, sizeZ - 1); z2 >= zmin; z2--) {
						slice2 = (byte[]) stack[z2];
						
						int ymin = z2 == z ? y : max(y - 1, 0); 
						for (int y2 = min(y + 1, sizeY - 1); y2 >= ymin; y2--) {
							int xmin = (z2 == z && y2 == y) ? x : max(x - 1, 0); 
							for (int x2 = min(x + 1, sizeX - 1); x2 >= xmin; x2--) {
								int index = y2 * sizeX + x2;
								int neighborValue = slice2[index] & 0x00FF;
								if (neighborValue > maxValue)
									maxValue = neighborValue;
							}
						}
					}

					// update value of current voxel
					maxValue = Math.min(maxValue, (int) mask.getVoxel(x, y, z));
					if (maxValue > currentValue) {
						slice[y * sizeX + x] = (byte) (maxValue & 0x00FF);
						modif = true;
					}
				}
			}
		}	
	}
}
