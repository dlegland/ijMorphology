package ijt.filter.morphology.strel;

import static org.junit.Assert.*;

import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;
import ijt.filter.morphology.Strel;


public class DiamondStrelTest {

	@Test
	public void testGetSize() {
		Strel se = new DiamondStrel(5);
		int[] size = se.getSize();
		assertEquals(5, size[0]);
		assertEquals(5, size[1]);
	}

	@Test
	public void testGetMask() {
		Strel se = new DiamondStrel(5);
		int[][] mask = se.getMask();
		
		// test mask size
		assertEquals(5, mask.length);
		assertEquals(5, mask[0].length);
		
		// test each value!
		assertEquals(  0, mask[0][0]);
		assertEquals(  0, mask[0][1]);
		assertEquals(255, mask[0][2]);
		assertEquals(  0, mask[0][3]);
		assertEquals(  0, mask[0][4]);
		
		assertEquals(  0, mask[1][0]);
		assertEquals(255, mask[1][1]);
		assertEquals(255, mask[1][2]);
		assertEquals(255, mask[1][3]);
		assertEquals(  0, mask[1][4]);
		
		assertEquals(255, mask[2][0]);
		assertEquals(255, mask[2][1]);
		assertEquals(255, mask[2][2]);
		assertEquals(255, mask[2][3]);
		assertEquals(255, mask[2][4]);
		
		assertEquals(  0, mask[3][0]);
		assertEquals(255, mask[3][1]);
		assertEquals(255, mask[3][2]);
		assertEquals(255, mask[3][3]);
		assertEquals(  0, mask[3][4]);
		
		assertEquals(  0, mask[4][0]);
		assertEquals(  0, mask[4][1]);
		assertEquals(255, mask[4][2]);
		assertEquals(  0, mask[4][3]);
		assertEquals(  0, mask[4][4]);
	}

	@Test
	public void testReverse() {
		Strel se = new DiamondStrel(5);
		
		Strel se2 = se.reverse();
		assertTrue(se2 instanceof DiamondStrel);
		
		int[] dim = se2.getSize();
		assertEquals(5, dim[0]);
		assertEquals(5, dim[1]);
	}

	@Test
	public void testDecompose() {
		DiamondStrel se = new DiamondStrel(5);
		Collection<InPlaceStrel> strels = se.decompose();
		assertEquals(3, strels.size());
		
		Iterator<InPlaceStrel> iter = strels.iterator();
		InPlaceStrel strel1 = (InPlaceStrel) iter.next();
		assertTrue(strel1 == ShiftedCross3x3Strel.RIGHT);
		
		InPlaceStrel strel2 = (InPlaceStrel) iter.next();
		assertTrue(strel2 instanceof LinearDiagUpStrel);
		assertEquals(2, ((LinearDiagUpStrel) strel2).size);
		
		InPlaceStrel strel3 = (InPlaceStrel) iter.next();
		assertTrue(strel3 instanceof LinearDiagDownStrel);
		assertEquals(2, ((LinearDiagDownStrel) strel3).size);
	}

	@Test
	public void testMaskAndShifts() {
		Strel strel = new DiamondStrel(5);

		int[][] shifts = strel.getShifts();
		int[][] mask = strel.getMask();
		int[] offset = strel.getOffset();
		
		for (int s = 0; s < shifts.length; s++) {
			int[] shift = shifts[s];
			
			int indX = shift[0] + offset[0];
			int indY = shift[1] + offset[1];
			assertEquals(255, mask[indY][indX]);
		}
	}


//	@Test
//	public void testDecomposeDilation_Square4x4() {
//		ImageProcessor image = createImage_Square4x4();
//		DiamondStrel strel = new DiamondStrel(5);
//		
//		ImageProcessor result = image.duplicate();
//		
//		Collection<InPlaceStrel> strels = strel.decompose();
//		for (InPlaceStrel ips : strels) {
//			System.out.println("------------");
//			System.out.println(ips.toString());
//			ips.inPlaceDilation(result);
//			
//			for (int y = 0; y < 10; y++) {
//				for (int x = 0; x < 10; x++) {
//					System.out.print(" " + result.get(x, y));
//				}
//				System.out.println();
//			}
//		}
		
//		ImageProcessor expected = image.createProcessor(10, 10);
//		for (int x = 3; x < 7; x++) {
//			expected.set(x, 1, 255);
//			expected.set(x, 8, 255);
//		}
//		for (int x = 2; x < 8; x++) {
//			expected.set(x, 2, 255);
//			expected.set(x, 7, 255);
//		}
//		for (int y = 3; y < 7; y++) {
//			for (int x = 1; x < 9; x++) {
//				expected.set(x, y, 255);
//			}
//		}
//
//		ImageProcessor result = strel.dilation(image);
//		
//		for (int y = 0; y < image.getHeight(); y++) {
//			for (int x = 0; x < image.getWidth(); x++) {
//				int exp = expected.get(x, y);
//				int res = result.get(x, y);
//				if(expected.get(x, y) != result.get(x, y)) {
//					System.out.println("At x=" + x + " and y=" + y
//							+ ", exp=" + exp + " and res = " + res);
//				}
//				assertEquals(exp, res);
//			}			
//		}
//	}


	@Test
	public void testDilation_Square4x4() {
		ImageProcessor image = createImage_Square4x4();
		Strel strel = new DiamondStrel(5);
		
		ImageProcessor expected = image.createProcessor(10, 10);
		for (int x = 3; x < 7; x++) {
			expected.set(x, 1, 255);
			expected.set(x, 8, 255);
		}
		for (int x = 2; x < 8; x++) {
			expected.set(x, 2, 255);
			expected.set(x, 7, 255);
		}
		for (int y = 3; y < 7; y++) {
			for (int x = 1; x < 9; x++) {
				expected.set(x, y, 255);
			}
		}

		ImageProcessor result = strel.dilation(image);
		
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				int exp = expected.get(x, y);
				int res = result.get(x, y);
				if(expected.get(x, y) != result.get(x, y)) {
					System.out.println("At x=" + x + " and y=" + y
							+ ", exp=" + exp + " and res = " + res);
				}
				assertEquals(exp, res);
			}			
		}
	}

	@Test
	public void testErosion_Square10x10() {
		ImageProcessor image = createImage_Square10x10();
		Strel strel = new DiamondStrel(5);
		
		ImageProcessor expected = image.createProcessor(30, 30);
		for (int y = 12; y < 18; y++) {
			for (int x = 12; x < 18; x++) {
				expected.set(x, y, 255);
			}
		}

		ImageProcessor result = strel.erosion(image);
		
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				int exp = expected.get(x, y);
				int res = result.get(x, y);
				if(expected.get(x, y) != result.get(x, y)) {
					System.out.println("At x=" + x + " and y=" + y
							+ ", exp=" + exp + " and res = " + res);
				}
				assertEquals(exp, res);
			}			
		}
	}


	@Test
	public void testClosing_5x5_Square4x4() {
		ImageProcessor image = createImage_Square4x4();
		Strel strel = new DiamondStrel(5);
		
		ImageProcessor result = strel.closing(image);
		
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				assertEquals(image.get(x, y), result.get(x, y));
			}			
		}
	}
	
	private ImageProcessor createImage_Square4x4 () {
		ImageProcessor image = new ByteProcessor(10, 10);
		image.setValue(0);
		image.fill();
		
		for (int y = 3; y < 7; y++) {
			for (int x = 3; x < 7; x++) {
				image.set(x, y, 255);
			}			
		}
		
		return image;
	}

	private ImageProcessor createImage_Square10x10 () {
		ImageProcessor image = new ByteProcessor(30, 30);
		image.setValue(0);
		image.fill();
		
		for (int y = 10; y < 20; y++) {
			for (int x = 10; x < 20; x++) {
				image.set(x, y, 255);
			}			
		}
		
		return image;
	}
}
