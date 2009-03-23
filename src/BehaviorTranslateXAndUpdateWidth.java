
import behaviors.Behavior.LoopEnum;
import behaviors.BehaviorContinuous.ContinuousBehaviorBuilder;
import behaviors.geom.continuous.BehaviorGeomContinuous;
import geometry.Geom;

/* BehaviorTranslateXAndUpdateWidth.java ~ Jan 22, 2009 */

/**
 *
 * @author angus
 */
public class BehaviorTranslateXAndUpdateWidth  extends BehaviorGeomContinuous
{
  float destX = 0f;
  float destW = 0f;
  public static BehaviorTranslateXAndUpdateWidth translate(
    Geom g,
    long startTime,
    long lengthMS,
    float[] ranges)
  {
    BehaviorTranslateXAndUpdateWidth bt = new BehaviorTranslateXAndUpdateWidth(
      new ContinuousBehaviorBuilder(startTime, lengthMS).ranges(ranges).loop(
        LoopEnum.ONCE )
       );

    g.attachBehavior(bt);

    return bt;
  }

  public static BehaviorTranslateXAndUpdateWidth translate(
    Geom g,
    long startTime,
    long lengthMS,
    float[] ranges,
    float destX,
    float destW)
  {
    BehaviorTranslateXAndUpdateWidth bt = new BehaviorTranslateXAndUpdateWidth(
      new ContinuousBehaviorBuilder(startTime, lengthMS).ranges(ranges).loop(
        LoopEnum.ONCE )
       );

    bt.destX = destX;
    bt.destW = destW;
    g.attachBehavior(bt);

    return bt;
  }

  public BehaviorTranslateXAndUpdateWidth(ContinuousBehaviorBuilder builder)
  {
    super(builder);
  }

  @Override
  public void updateGeom(Geom g)
  {
    if (isActive == true)
    {
      g.anchor.x += offsets[0];
      g.w += offsets[1];
    }
  }

  public void dispose()
  {
    //System.out.println("in BehaviorTranslateXAndUpdateWidth : dispose()");
    percentage = 1f;
  }
}
