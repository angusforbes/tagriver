
import geometry.Colorf;
import java.util.HashMap;
import java.util.Map;
import javax.vecmath.Point3f;
import utils.Utils;

/* DataStratum.java ~ Jan 18, 2009 */

/**
 *
 * @author angus
 */
public class DataStratum 
{
  static float MIN_INIT_HEIGHT_OF_TAGS = .1f; //.1f
  static float MAX_INIT_HEIGHT_OF_TAGS = .25f;
  Map<String, GeomTag2> geomTags = new HashMap<String, GeomTag2>();
  Map<Integer, DataStratumSlice> timeToDataStratumSlice = new HashMap<Integer, DataStratumSlice>();
  
  public Colorf nrmColor = new Colorf();
  public Colorf highColor = new Colorf();

  
  
  public void updateTags(String tag, Colorf cl, Colorf hgCl, GeomStrataHolder holder)
  {
    GeomTag2 gt2 = geomTags.get(tag);

    if (gt2 == null)
    {
      gt2 = GeomTag2.newGeomTextConstrainedByHeight(tag, new Point3f(), 
        Utils.randomFloat(MIN_INIT_HEIGHT_OF_TAGS, MAX_INIT_HEIGHT_OF_TAGS));
      geomTags.put(tag, gt2);

      gt2.setColor(cl.r, cl.g, cl.b, cl.a);
      nrmColor = cl;
      highColor = hgCl;
      //gt2.backgroundColor(new Colorf(1f, 0f,0f,.25f));
      holder.addGeom(gt2, false);
    }
  }

}
