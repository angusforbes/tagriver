
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.vecmath.Point3f;

/* DataStratumSlice.java ~ Jan 18, 2009 */

/**
 *
 * @author angus
 */
public class DataStratumSlice 
{
  int totalCount = 0;
  Map<String, Integer> tagToCount = new HashMap<String, Integer>();
  public List<GeomPhoto2> geomImages = new ArrayList<GeomPhoto2>();

  public void updatePhotos(String filename, GeomStrataHolder holder) {

    try {
      File f = new File(TagRiver2.PHOTO_PATH + "/" + filename);
      GeomPhoto2 gi = new GeomPhoto2(f, new Point3f(), .75f, .75f);
      geomImages.add(gi);
      holder.addGeom(gi, false);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

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
