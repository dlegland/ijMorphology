/**
 * 
 */
package ijt.filter.morphology.strel;

import java.util.ArrayList;
import java.util.Collection;

/**
 * An Octagonal structuring element, obtained by decomposition into horizontal,
 * vertical, and diagonal linear structuring elements.
 * 
 * @see SquareStrel
 * @author David Legland
 *
 */
public class OctagonStrel extends AbstractSeparableStrel {

	// ==================================================
	// Static methods 
	
	public final static OctagonStrel fromDiameter(int diam) {
		return new OctagonStrel(diam);
	}
	
	public final static OctagonStrel fromRadius(int radius) {
		return new OctagonStrel(2 * radius + 1, radius);
	}
	
	// ==================================================
	// Class variables
	
	/**
	 * The orthogonal diameter of the octagon. Computed from the square and
	 * diagonal sizes.
	 */
	int size;

	/**
	 * The offset of the octagon. Computed from the square and diagonal offsets.
	 */
	int offset;

	/**
	 * Size of the square sides, also the length of orthogonal linear
	 * structuring elements.
	 */
	int squareSize;

	/**
	 * Size of the diagonal sides, also the length of diagonal linear
	 * structuring elements.
	 */
	int diagSize;

	int squareOffset;
	int diagOffset;
	
	
	// ==================================================
	// Constructors 
	
	/**
	 * Creates a new octagonal structuring element of a given orthogonal
	 * diameter.
	 * 
	 * @param size
	 *            the orthogonal diameter of the octagon
	 */
	public OctagonStrel(int size) {
		this(size, (int) Math.floor((size - 1) / 2));
	}

	/**
	 * Creates a new octagonal structuring element of a given orthogonal
	 * diameter and with a given offset.
	 * 
	 * @param size
	 *            the orthogonal diameter of the octagon
	 * @param offset
	 *            the position of the reference pixel in each direction
	 * @throws IllegalArgumentException
	 *             if size is negative or zero
	 * @throws IllegalArgumentException
	 *             if offset is negative or greater than size
	 */
	public OctagonStrel(int size, int offset) {
		if (size < 1) {
			throw new IllegalArgumentException("Requires a positive size");
		}
		this.size = size;
		
		if (offset < 0) {
			throw new IllegalArgumentException("Requires a non-negative offset");
		}
		if (offset >= size) {
			throw new IllegalArgumentException("Offset can not be greater than size");
		}
		this.offset = offset;
		
		// init side lengths
		this.diagSize = (int) Math.round((this.size + 2) / (2 + Math.sqrt(2)));
		this.squareSize = this.size - 2 * (this.diagSize - 1);

		// Init offsets
		this.squareOffset = (int) Math.floor((this.squareSize - 1) / 2);
		this.diagOffset = (int) Math.floor((this.diagSize - 1) / 2);
	}

	/**
	 * Creates the octagonal structuring element by specifying both square and
	 * diagonal sizes and offsets.
	 * 
	 * @param squareSize
	 *            the length of the sides in orthogonal directions
	 * @param diagSize
	 *            the length of the sides in diagonal directions
	 * @param squareOffset
	 *            the offset in orthogonal directions
	 * @param diagOffset
	 *            the offset in diagonal directions
	 * @throws IllegalArgumentException
	 *             if a size is negative or zero
	 * @throws IllegalArgumentException
	 *             if an offset is negative or greater than size
	 */
	protected OctagonStrel(int squareSize, int diagSize, int squareOffset,
			int diagOffset) {
		if (squareSize < 1) {
			throw new IllegalArgumentException("Requires a positive square size");
		}
		this.squareSize = squareSize;
		
		if (diagSize < 1) {
			throw new IllegalArgumentException("Requires a positive diagonal size");
		}
		this.diagSize = diagSize;
		
		if (squareOffset < 0) {
			throw new IllegalArgumentException("Requires a non-negative square offset");
		}
		this.squareOffset = squareOffset;
		
		if (diagOffset < 0) {
			throw new RuntimeException("Requires a non-negative diagonal offset");
		}
		this.diagOffset = diagOffset;
		
		this.size = this.squareSize + 2 * (this.diagSize - 1);
		this.offset = this.squareOffset + this.diagSize - 1;
	}

	
	// ==================================================
	// General methods 
	
	/**
	 * Returns a decomposition into four structuring elements, corresponding to
	 * horizontal, vertical, and diagonal linear structuring elements.
	 * 
	 * @see SeparableStrel#decompose()
	 * @see LinearHorizontalStrel
	 * @see LinearVerticalStrel
	 * @see LinearDiagUpStrel
	 * @see LinearDiagDownStrel
	 */
	@Override
	public Collection<InPlaceStrel> decompose() {
		// We need to use a different offset for horizontal lines, because 
		// the sum of offsets for diagonals shifts the reference point by one
		// pixel to the right
		int horizOffset = this.squareOffset;
		if (this.diagSize % 2 == 0)
			horizOffset = this.squareSize - 1 - this.squareOffset;
		
		// Allocate memory for linear strels
		ArrayList<InPlaceStrel> strels = new ArrayList<InPlaceStrel>(4);
		
		// create elementary strels in each of the four directions
		strels.add(new LinearHorizontalStrel(this.squareSize, horizOffset));
		strels.add(new LinearVerticalStrel(this.squareSize, this.squareOffset));
		strels.add(new LinearDiagUpStrel(this.diagSize, this.diagOffset));
		strels.add(new LinearDiagDownStrel(this.diagSize, this.diagOffset));
		return strels;
	}

	/* (non-Javadoc)
	 * @see ijt.morphology.Strel#getMask()
	 */
	@Override
	public int[][] getMask() {
		// Create array
		int[][] mask = new int[this.size][this.size];
		
		// Process the center part: the whole line is set to 255
		for (int y = this.diagSize; y < this.size - this.diagSize; y++) {
			for (int x = 0; x < this.size; x++) {
				mask[y][x] = 255;
			}
		}
		
		// Process the vertical center part: the whole column is set to 255
		for (int x = this.diagSize; x < this.size - this.diagSize; x++) {
			for (int y = 0; y < this.size; y++) {
				mask[y][x] = 255;
			}
		}
		
		// Process the corners
		for (int i = 0; i < this.diagSize; i++) {
			
		}
		
		return mask;
	}

	/* (non-Javadoc)
	 * @see ijt.morphology.Strel#getShifts()
	 */
	@Override
	public int[][] getShifts() {
		int n = this.size * this.size;
		int[][] shifts = new int[n][2];
		int i = 0;
		
		for (int y = 0; y < this.size; y++) {
			for (int x = 0; x < this.size; x++) {
				shifts[i][0] = x - this.offset;
				shifts[i][1] = x - this.offset;
				i++;
			}
		}
		return shifts;
	}

	/* (non-Javadoc)
	 * @see ijt.morphology.Strel#getSize()
	 */
	@Override
	public int[] getSize() {
		return new int[]{this.size, this.size};
	}

	/* (non-Javadoc)
	 * @see ijt.morphology.Strel#getOffset()
	 */
	@Override
	public int[] getOffset() {
		return new int[]{this.offset, this.offset};
	}

	/* (non-Javadoc)
	 * @see ijt.morphology.Strel#reverse()
	 */
	@Override
	public OctagonStrel reverse() {
		return new OctagonStrel(this.squareSize, this.diagSize, 
				this.squareSize - this.squareOffset - 1, 
				this.diagSize - this.diagOffset - 1);
	}

}
