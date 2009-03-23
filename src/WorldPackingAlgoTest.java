
import algorithms2.PackingAlgorithm3;
import behaviorism.BehaviorismDriver;
import behaviors.Behavior;
import geometry.GeomPoly;
import geometry.GeomRect;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Properties;
import javax.vecmath.Point3f;
import utils.GeomUtils;
import utils.Utils;
import worlds.WorldGeom;

/* WorldPackingAlgoTest.java ~ Jan 21, 2009 */


//
/**
 *
 * @author angus
 */
public class WorldPackingAlgoTest extends WorldGeom
{
  GeomRect worldRect;
  public static void main(String[] args)
  {
    //load in application specific properties
    Properties properties = loadPropertiesFile("behaviorism.properties");

    //create the world
    WorldGeom world = new WorldPackingAlgoTest();

    //create an instance of the behaviorism framework set to the world
    new BehaviorismDriver(world, properties);
  }

  public void setUpWorld()
  {
    //testPtSegDist();
    //testSegementDist();
    testPackingAlgo();

    //testMultipleBehaviors();

  }

  public void testPtSegDist()
  {
    float dist = GeomUtils.getDistanceBetweenPointAndLineSegment(
      new Point3f(0f, 0f, 0f),
      new Point3f(-1f, -1f, 0f), new Point3f(1f, -1f, 0f)
      );

    System.out.println("distaa = " + dist);

  }
  public void testSegementDist()
  {
    float dist = GeomUtils.getDistanceBetweenLineSegments(
      new Point3f(-1f, -1f, 0f), new Point3f(1f, -1f, 0f),
      new Point3f(0f, 0f, 0f), new Point3f(1f, 1f, 0f)
      );

    System.out.println("dist = " + dist);
    //hmm
    System.out.println("huh?");
  }

  int currentMOR = -1;
  GeomRect gr1;
  Behavior b1 = null;
  Behavior b2 = null;

  public void testMultipleBehaviors()
  {
    gr1 = new GeomRect(new Point3f(-1f, 0f, 0f), .1f, .5f);
    GeomRect marker1 = new GeomRect(new Point3f(-1f, 0f, 0f), .1f, .1f);
    GeomRect marker2 = new GeomRect(new Point3f(1f, 0f, 0f), .1f, .1f);
    GeomRect marker3 = new GeomRect(new Point3f(2f, 0f, 0f), .1f, .1f);

    gr1.setColor(1f,0f,0f);
    marker1.setColor(0f,1f,0f);
    marker2.setColor(0f,1f,0f);
    marker3.setColor(0f,1f,0f);
    addGeom(gr1);
    addGeom(marker1);
    addGeom(marker2);
    addGeom(marker3);


    GeomRect mor1 = new GeomRect(new Point3f(-1f, -1.5f, 0f), 1f, 1f)
    {
       public void mouseOverAction(MouseEvent e)
       {
         if (currentMOR != 1)
         {
           currentMOR = 1;
          System.out.println("you moused over 1 " + behaviors.size() + " : " + gr1.behaviors.size());
           System.out.println("gr.x = " + gr1.anchor.x);
           doBehavior1();
         }
       }
    };

    GeomRect mor2 = new GeomRect(new Point3f(0f, -1.5f, 0f), 1f, 1f)
    {
       public void mouseOverAction(MouseEvent e)
       {
         if (currentMOR != 2)
         {
           currentMOR = 2;
          System.out.println("you moused over 2 : " + behaviors.size() + " : " + gr1.behaviors.size());
           System.out.println("gr.x = " + gr1.anchor.x);
          doBehavior2();
      
         }
       }
    };
    addGeom(mor1);
    addGeom(mor2);
  }

  public void doBehavior1()
  {
      if (b1 != null)
      {
        b1.interruptImmediately();
      }

      b1 = BehaviorTranslateXAndUpdateWidth.translate(gr1, Utils.now(), 100L,
          new float[]{1f - gr1.anchor.x , 1f - gr1.w} );
  }
  public void doBehavior2()
  {
      if (b1 != null)
      {
        b1.interruptImmediately();
      }

       b1 = BehaviorTranslateXAndUpdateWidth.translate(gr1, Utils.now(), 100L,
          new float[]{-1f - gr1.anchor.x, .1f - gr1.w});
  }

  public boolean checkKeys(boolean[] keys, boolean[] keysPressing)
  {
    if (keys[KeyEvent.VK_A])
		{
			if (keysPressing[KeyEvent.VK_A] == false)
			{
        doBehavior1();
        keysPressing[KeyEvent.VK_A] = true;
			}
			return true;
		}
    else if (keys[KeyEvent.VK_Z])
		{
			if (keysPressing[KeyEvent.VK_Z] == false)
			{
        doBehavior2();
  
				keysPressing[KeyEvent.VK_Z] = true;
			}
			return true;
		}

    return false;
  }

  public void testPackingAlgo()
  {
    Utils.sleep(1000);
    this.worldRect = getWorldRect();
    //PackingAlgorithm3 pa = new PackingAlgorithm3(worldRect.makeRectangle2DFromRect());

    GeomPoly poly = new GeomPoly(
      new Point3f(-2f, 2f, 0f), new Point3f(2f, -2f, 0f),
      new Point3f(2f, -1f, 0f), new Point3f(-2f, 3f, 0f)
      );
    poly.setColor(1f,0f,0f,.1f);
    addGeom(poly);
    PackingAlgorithm3 pa = new PackingAlgorithm3(poly.makePath2DFromPoly());
    GeomAvailableRects gar = new GeomAvailableRects(pa);
    addGeom(gar);

    Point2D centerPt = new Point2D.Float(worldRect.anchor.x + worldRect.w * .5f, worldRect.anchor.y + worldRect.h * .5f);

    /*
    for (int i = 0; i < 10000; i++)
    {
       double scale = Utils.random(.1, 10);
       if (
        //pa.placeRectInCenterOfLargestAvailableArea(new Rectangle2D.Double(-.5,-.5,.25, .25));
        //pa.placeRectInLargestAvailableArea(new Rectangle2D.Double(-.5,-.5,.25, .25));

        pa.placeRectInClosestAvailableAreaToPoint(
          //new Rectangle2D.Double( 0, 0, 1 - (i * .0001), .5 - (i * .00005)),
          new Rectangle2D.Double( 0, 0, .3 * scale, .1 * scale),
          new Point2D.Double(0, 0)
          )

        //pa.placeRectInCenterOfBestFittingArea(new Rectangle2D.Double(-.5,-.5, Utils.random(.5, 2), Utils.random(.1, .3)))
        //pa.placeRectInClosestAvailableAreaToPoint(new Rectangle2D.Double(-.5,-.5,.25, .25), centerPt)
        )
      {
        if (pa.availableRectangles.size() == 0)
        {
          break;
        }

        System.out.println("iteration " + i + ": number of availableRect = " + pa.availableRectangles.size());
        //Utils.sleep(425);

      }
    }
    */
    
    System.out.println("Done: number of availableRect = " + pa.availableRectangles.size());
  }

}
