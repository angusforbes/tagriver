
import behaviors.Behavior;
import geometry.GeomPoly;

/* GeomStratum.java ~ Jan 16, 2009 */

/**
 *
 * @author angus
 */
public class GeomStratum extends GeomPoly
{
  Behavior behaviorX_left = null;
  Behavior behaviorX_right = null;
  
  Behavior behaviorY_lowerLeft = null;
  Behavior behaviorY_upperLeft = null;
  Behavior behaviorY_lowerRight = null;
  Behavior behaviorY_upperRight = null;
//
  float lowerLeft = 0f;
  float upperLeft = 0f;
  float lowerRight = 0f;
  float upperRight = 0f;
  public GeomStratum(GeomStrataHolder holder)
  {
    registerClickableObject(holder);
    isSelectable = true;
    registerDraggableObject(null);
    //setColor(0f,0f,0f,0f);
  }

  public void createVerticesFromPercentages(float lowerLeft, float lowerRight, float upperRight, float upperLeft)
  {
    this.lowerLeft = lowerLeft;
    this.lowerRight = lowerRight;
    this.upperRight = upperRight;
    this.upperLeft = upperLeft;
  }

}
