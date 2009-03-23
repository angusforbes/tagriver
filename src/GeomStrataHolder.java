
import geometry.GeomPoly;
import geometry.GeomRect;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Point3f;
import utils.Utils;

/* GeomStrataHolder.java ~ Jan 22, 2009 */
/**
 *
 * @author angus
 */
public class GeomStrataHolder extends GeomRect
{

  TagRiver2 tagRiver;
  long START = 0L; //10L;
  long SPEED = 150L;
  GeomStrata openStrata;
  List<GeomStrata> geomStratas;
  int numStrata;

  public GeomStrataHolder(Point3f p3f, float w, float h, TagRiver2 tagRiver)
  {
    super(p3f, w, h);

    this.tagRiver = tagRiver;
    this.numStrata = TagRiver2.NUM_TIMES;
    initStrata(numStrata / 2);
  }

  public void initStrata(int openStrataIdx)
  {
    System.out.println("in initStrata()...");
    this.geomStratas = new ArrayList();

    float closedInc = (this.w / 2f) / (numStrata - 1);
    float openInc = (this.w / 2f);

    float ux = 0f;
    float uw;
    float uh = this.h;
    for (int i = 0; i < numStrata; i++)
    {
      if (i == openStrataIdx)
      {
        uw = openInc;
      }
      else
      {
        uw = closedInc;
      }

      GeomStrata gs = new GeomStrata(this, new Point3f(ux, 0f, 0f), uw, uh);

      if (i == openStrataIdx)
      {
        gs.isZoomedOver.set(true);
        openStrata = gs;
      }

      this.geomStratas.add(gs);
      addGeom(gs);

      ux += uw;
    }
  }

  public void printInfo()
  {
    for (int i = 0; i < numStrata; i++)
    {
      GeomStrata gs = geomStratas.get(i);

      System.out.println("gs " + i + " : width = " + gs.w + " ");
    }
  }
  boolean runRiver = false;

  @Override
  public void clickAction(MouseEvent me)
  {
    toggleRiver();

  }

  public void toggleRiver()
  {
    runRiver = !runRiver;

    System.out.println("you clicked the holder... runRiver = " + runRiver);

    if (runRiver == true)
    {
      tagRiver.updateAllStrata();
    }
  }

  public void updateBehaviors(GeomStrata strata)
  {
    if (strata == openStrata  ) // || runRiver == true)
    {
      return;
    }
    else
    {
      long baseNano = Utils.nowPlusMillis(START);

      openStrata = strata;

      float closedInc = (w / 2f) / (numStrata - 1);
      float openInc = (w / 2f);

      float ux = 0f;//this.anchor.x;
      float uw;
      float uh = h;

      float newStrataX = 0f;

      for (int i = 0; i < numStrata; i++)
      {
        GeomStrata gs = geomStratas.get(i);

        if (gs.behaviorXandW != null)
        {
          gs.behaviorXandW.interruptImmediately();
        }

        if (gs == openStrata)
        {
          //openStrataIdx = i;
          gs.behaviorXandW = BehaviorTranslateXAndUpdateWidth.translate(gs, baseNano,
            SPEED,
            new float[]
            {
              ux - gs.anchor.x, openInc - gs.w
            }, ux, openInc);

          newStrataX = ux;

          gs.updateStrata(baseNano, SPEED, openInc, gs.w);
          gs.isOpen = false;

          ux += openInc;
        }
        else
        {
          gs.behaviorXandW = BehaviorTranslateXAndUpdateWidth.translate(gs, baseNano,
            SPEED,
            new float[]
            {
              ux - gs.anchor.x, closedInc - gs.w
            }, ux, closedInc);
          gs.updateStrata(baseNano, SPEED, closedInc, gs.w);
          gs.isOpen = true;
          ux += closedInc;

        }
      }

      //DataSlice prevDataSlice = tagRiver.timeToDataSlice.get(openStrataIdx); //timeIdx);
      //DataSlice currDataSlice = tagRiver.timeToDataSlice.get(openStrataIdx + 1); //timeIdx);
      DataSlice dataSlice = strata.dataSlice;
      DataSlice rightDataSlice = strata.rightDataSlice;

      List<Float> leftSide = dataSlice.layerPositions;
      List<Float> rightSide = rightDataSlice.layerPositions;

      List<GeomPoly> stratumPolys = new ArrayList<GeomPoly>();
      for (int ii = 0; ii < 4; ii++)
      {
        GeomPoly poly = new GeomPoly(
          new Point3f(0f, leftSide.get(ii), 0f),
          new Point3f(w * .5f, rightSide.get(ii), 0f),
          new Point3f(w * .5f, rightSide.get(ii + 1), 0f),
          new Point3f(0f, leftSide.get(ii + 1), 0f));
        stratumPolys.add(poly);
      }

      tagRiver.placeTagsWhenBrowsing(Utils.nowPlusMillis(500), strata, dataSlice, stratumPolys, newStrataX);

    // tagRiver.placeTags(Utils.nanoPlusMillis(baseNano, SPEED * 2), strata, prevDataSlice, currDataSlice);

    }
  }

  public void draw(GL gl, GLU glu, float offset)
  {
//    if (isReadyToShift.get() == false)
//    {
//      return;
//    }

    //updateStrata();
    //super.draw(gl, glu, offset);
  }
}
