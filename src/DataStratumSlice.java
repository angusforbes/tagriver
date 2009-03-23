
import java.util.HashMap;
import java.util.Map;

/* DataStratumSlice.java ~ Jan 18, 2009 */

/**
 *
 * @author angus
 */
public class DataStratumSlice 
{
  int totalCount = 0;
  Map<String, Integer> tagToCount = new HashMap<String, Integer>();

  public void updateTagCount(String tag, int cnt)
  {
    /*Integer i = tagToCount.get(tag);

    if (i == null)
    {
      tagToCount.put(tag, 1);
    }
    else
    {
      tagToCount.put(tag, i + 1);
    }*/
    
    tagToCount.put(tag, cnt);
    totalCount+= cnt;
  }
}
