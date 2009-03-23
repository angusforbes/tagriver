
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* DataSlice.java ~ Jan 18, 2009 */

/**
 *
 * @author angus
 */
public class DataSlice 
{
  //
  int totalCount = 0;//
  Map<Integer, DataStratumSlice> layerToDataStratumSlice = new HashMap<Integer, DataStratumSlice>();
  List<Float> layerPositions;
}
