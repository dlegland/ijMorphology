package ijt.filter.morphology;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// generic classes
	StrelTest.class,
	MorphologyTest.class,
	FloodFillTest.class,
	LabelingPluginTest.class, 
	GeodesicReconstructionTest.class,
	MinimaAndMaximaTest.class,
	MinimaAndMaxima3DTest.class
	})
public class AllTests {
  //nothing
}
