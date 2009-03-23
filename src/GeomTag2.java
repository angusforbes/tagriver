
import behaviors.geom.continuous.BehaviorRGBA;
import behaviors.geom.continuous.BehaviorScale;
import behaviors.geom.continuous.BehaviorTranslate;
import behaviors.geom.discrete.BehaviorIsActive;
import geometry.text.GeomTextOutset;
import javax.vecmath.Point3f;

/* GeomTag2.java ~ Jan 25, 2009 */

/**
 *
 * @author angus
 */
public class GeomTag2 extends GeomTextOutset
{
  BehaviorTranslate behaviorTranslate = null;
  BehaviorScale behaviorScale = null;
  BehaviorRGBA behaviorRGBA = null;
  BehaviorIsActive behaviorIsActive = null;

  float initialHeight;
  float initialWidth;
  
  public boolean highlight = true;
  
  public static GeomTag2 newGeomTextConstrainedByHeight(String text,
																												 Point3f p3f,
																												 float h)
	{
    GeomTextBuilder gtb = new GeomTextBuilder(text).constrainByHeight(h).
      //backgroundColor(new Colorf(1f,0f,0f,.25f)).
      backgroundColor(null).
      exactBounds(false)
      //justify(-1, 0)

      //exactPadding(.015f, .015f)
      //exactPadding(0f, 0f)
      ;
		GeomTag2 gt2 = new GeomTag2(gtb);


    //GeomTag2 gt2 = new GeomTag2(p3f, 0f, h, text);
    //gt2.exactPixelBounds = true;
    //gt2.insetX = 0f;
    //gt2.insetY = 0f;
		gt2.setWidthByHeight(h);
   gt2.initialHeight = gt2.h;
   gt2.initialWidth = gt2.w;

    //System.out.println("insetX/Y = " + gt2.insetX + "/" + gt2.insetY);
    return gt2;
	}

	public static GeomTag2 newGeomTextConstrainedByHeight(String text,
																												 Point3f p3f,
																												 float h,
                                                         boolean exactPixelBounds)
	{
//		//GeomTag2 gt2 = new GeomTag2(p3f, h, 0f, text);
//		GeomTag2 gt2 = new GeomTag2(p3f, 0f, h, text);
//    //gt2.insetX = .1f;
//    //gt2.insetY = .1f;
//
//    gt2.exactPixelBounds = exactPixelBounds;
//		gt2.setWidthByHeight(h);
//		return gt2;
    return null;
	}

	//public GeomTag2(Point3f p3f, float w, float h, String text)
	public GeomTag2(GeomTextBuilder gtb)
  {
    //super(p3f, w, h, text);
    super(gtb);
    isSelectable = false;
  }
}
