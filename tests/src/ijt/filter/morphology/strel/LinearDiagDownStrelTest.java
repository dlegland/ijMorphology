package ijt.filter.morphology.strel;

import static org.junit.Assert.*;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

import org.junit.Test;
import ijt.filter.morphology.Strel;

public class LinearDiagDownStrelTest {

	@Test
	public void testGetSize() {
		Strel strel = new LinearDiagDownStrel(5);
		int[] size = strel.getSize();
		assertEquals(size[0], 5);
		assertEquals(size[1], 5);
	}

	@Test
	public void testGetMask() {
		Strel strel = new LinearDiagDownStrel(5);
		int[][] mask = strel.getMask();
		
		assertEquals(mask.length, 5);
		assertEquals(mask[0].length, 5);
	}

	@Test
	public void testGetShifts() {
		Strel strel = new LinearDiagDownStrel(5);
		int[][] shifts = strel.getShifts();
		
		assertEquals(shifts.length, 5);
		assertEquals(shifts[0].length, 2);
	}

	@Test
	public void testReverse() {
		Strel strel = new LinearDiagDownStrel(5);
		int[] size = strel.getSize();
		Strel strel2 = strel.reverse();
		int[] size2 = strel2.getSize();
		assertEquals(size[0], size2[0]);
		assertEquals(size[1], size2[1]);
	}

//	@Test
//	public void testErosion_Square4x4() {
//		ImageProcessor image = createImage_Square4x4();
//		Strel strel = new LinearDiagDownStrel(3);
//		
//		ImageProcessor result = strel.erosion(image);
//
//		for (int x = 3; x < 7; x++) {
//			assertEquals(0, result.get(x, 3));
//			assertEquals(255, result.get(x, 4));
//			assertEquals(255, result.get(x, 5));
//			assertEquals(0, result.get(x, 6));
//		}
//	}

	@Test
	public void testDilation_Square4x4() {
		ImageProcessor image = createImage_Square4x4();
		Strel strel = new LinearDiagDownStrel(3);
		
		ImageProcessor result = strel.dilation(image);

		assertEquals(0, 	result.get(2, 1));
		assertEquals(255, 	result.get(2, 2));
		assertEquals(255, 	result.get(2, 5));
		assertEquals(0, 	result.get(2, 6));
		
		assertEquals(0, 	result.get(3, 1));
		assertEquals(255, 	result.get(3, 2));
		assertEquals(255, 	result.get(3, 6));
		assertEquals(0, 	result.get(3, 7));
		
		assertEquals(0, 	result.get(4, 1));
		assertEquals(255, 	result.get(4, 2));
		assertEquals(255, 	result.get(4, 7));
		assertEquals(0, 	result.get(4, 8));

		assertEquals(0, 	result.get(5, 1));
		assertEquals(255, 	result.get(5, 2));
		assertEquals(255, 	result.get(5, 7));
		assertEquals(0, 	result.get(5, 8));

		assertEquals(0, 	result.get(6, 2));
		assertEquals(255, 	result.get(6, 3));
		assertEquals(255, 	result.get(6, 7));
		assertEquals(0, 	result.get(6, 8));

		assertEquals(0, 	result.get(7, 3));
		assertEquals(255, 	result.get(7, 4));
		assertEquals(255, 	result.get(7, 7));
		assertEquals(0, 	result.get(7, 8));
	}

	@Test
	public void testClosing() {
		ImageProcessor image = createImage_Square10x10();
		Strel strel = new LinearDiagDownStrel(5);
		
		ImageProcessor result = strel.closing(image);
		
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				assertEquals(image.get(x, y), result.get(x, y));
			}			
		}
	}

//	@Test
//	public void testOpening() {
//		ImageProcessor image = createImage_Square10x10();
//		Strel strel = new LinearDiagDownStrel(5);
//		
//		ImageProcessor result = strel.opening(image);
//		
//		for (int y = 0; y < image.getHeight(); y++) {
//			for (int x = 0; x < image.getWidth(); x++) {
//				assertEquals(image.get(x, y), result.get(x, y));
//			}			
//		}
//	}
	
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
