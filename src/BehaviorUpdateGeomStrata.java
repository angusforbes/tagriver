
import behaviors.Behavior.LoopEnum;
import behaviors.BehaviorContinuous.ContinuousBehaviorBuilder;
import behaviors.geom.continuous.BehaviorGeomContinuous;
import geometry.Geom;
import java.util.List;

/* BehaviorUpdateGeomStrata.java ~ Jan 18, 2009 */

/**
 *
 * @author angus
 */
public class BehaviorUpdateGeomStrata extends BehaviorGeomContinuous
{
  GeomStrata geomStrata = null;
  List<Float> newLeftFloats;
  List<Float> newRightFloats;

 public static BehaviorUpdateGeomStrata updateStrata(
    GeomStrata geomStrata,
    List<Float> newLeftFloats,
    List<Float> newRightFloats,
    long startTime,
    long lengthMS
    )
  {
    return new BehaviorUpdateGeomStrata(geomStrata, newLeftFloats, newRightFloats,
      new ContinuousBehaviorBuilder(startTime, lengthMS)
      .loop(LoopEnum.ONCE));
  }

  public BehaviorUpdateGeomStrata(GeomStrata geomStrata, List<Float> newLeftFloats, List<Float> newRightFloats,
      ContinuousBehaviorBuilder builder)
  {
    super(builder);
    this.geomStrata = geomStrata;

  }

  public void updateGeom(Geom g)
  {
//    for (GeomStratum gs : geomStrata.stratums)
//    {
//      gs.vertices.add(new GeomPoint(0f, gs.lowerLeft * h, 0f));
//      gs.vertices.add(new GeomPoint(w, gs.lowerRight * h, 0f));
//      gs.vertices.add(new GeomPoint(w, gs.upperRight * h, 0f));
//      gs.vertices.add(new GeomPoint(0f, gs.upperLeft * h, 0f));
//
//      gs.setColor(new Colorf());
//      addGeom(gs);
//    }


//    gridLine.rectColor.a += offsets[0];
  }

}
