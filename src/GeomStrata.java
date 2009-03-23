
import behaviors.geom.continuous.BehaviorTranslate;
import geometry.GeomPoint;
import geometry.GeomRect;
import geometry.text.GeomTextOutset;
import geometry.text.GeomTextOutset.GeomTextBuilder;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import utils.GeomUtils;

/* GeomStrata.java ~ Jan 16, 2009 */

/**
 *
 * @author angus
 */
public class GeomStrata extends GeomRect
{
  public DataSlice leftDataSlice = null;
  public DataSlice dataSlice = null;
  public DataSlice rightDataSlice = null;

  public boolean isOpen = false;
  public AtomicBoolean isZoomedOver = new AtomicBoolean(false);
  public List<GeomStratum> stratums;
  GeomStrataHolder holder;
  BehaviorTranslateXAndUpdateWidth behaviorXandW = null;
  
  public GeomTag2 weekLbl = GeomTag2.newGeomTextConstrainedByHeight("weekLabl", new Point3f(), 0.1f);
  
  public GeomStrata(GeomStrataHolder holder, Point3f p3f, float w, float h)
  {
    super(p3f, w, h);
    this.holder = holder;
    setColor(1f,0f,0f, .2f);
    weekLbl.setColor(0.8f, 0.8f, 0.8f, 1f);

    registerClickableObject(null);
    registerDraggableObject(null);
    
    
    weekLbl.setRotateAnchor(new GeomPoint(0, 0, 0));
    weekLbl.rotate.z = 60.0;
    weekLbl.setPos(0f, 4.2f, 0.1f);
    this.addGeom(weekLbl, true);

    //registerMouseoverableObject(this.holder);
  }

  /*
  public GeomStrata(Point3f p3f, float w, float h, List<GeomStratum> stratums)
  {
    super(p3f, w, h);
    initializeStrata(stratumse;
  }
  */

  public void initializeStrata(List<GeomStratum> stratums)
  {
    this.stratums = stratums;

    //for (GeomStratum gs : stratums)
    for (int i = 0; i < stratums.size(); i++)
    {
      GeomStratum gs = stratums.get(i);

      gs.vertices.add(new GeomPoint(0f, gs.lowerLeft, 0f));
      gs.vertices.add(new GeomPoint(w, gs.lowerRight, 0f));
      gs.vertices.add(new GeomPoint(w, gs.upperRight, 0f));
      gs.vertices.add(new GeomPoint(0f, gs.upperLeft, 0f));

      gs.geoms.add(gs.vertices.get(0));
      gs.geoms.add(gs.vertices.get(1));
      gs.geoms.add(gs.vertices.get(2));
      gs.geoms.add(gs.vertices.get(3));

      if (i == 0)
      {
        gs.setColor(TagRiver2.COLOR0);
      }
      else if (i == 1)
      {
        gs.setColor(TagRiver2.COLOR1);
      }
      else if (i == 2)
      {
        gs.setColor(TagRiver2.COLOR2);
      }
      else if (i == 3)
      {
        gs.setColor(TagRiver2.COLOR3);
      }

      addGeom(gs);
      gs.registerMouseoverableObject(this);
    }
  }

  @Override
  public void mouseOverAction(MouseEvent e)
  {
    holder.updateBehaviors(this);
  }

  @Override
  public void clickAction(MouseEvent e)
  {
    //holder.updateBehaviors(this);
  }


  public void updateStrata(long baseNano, long SPEED, float nw, float ow)
  {
    for (int i = 0; i < stratums.size(); i++)
    {
      GeomStratum gs = stratums.get(i);

      if (gs.behaviorX_left != null)
      {
        gs.behaviorX_left.interruptImmediately();
      }
      gs.behaviorX_left = BehaviorTranslate.translate(gs.vertices.get(1), baseNano, SPEED,
       GeomUtils.subtractPoint3f( new Point3f(nw, 0f, 0f), new Point3f(gs.vertices.get(1).anchor.x, 0f, 0f)));
       // GeomUtils.subtractPoint3f( new Point3f(nw, 0f, 0f), new Point3f(ow,0f,0f)));
      if (gs.behaviorX_right != null)
      {
        gs.behaviorX_right.interruptImmediately();
      }
      gs.behaviorX_right = BehaviorTranslate.translate(gs.vertices.get(2), baseNano, SPEED,
        GeomUtils.subtractPoint3f( new Point3f(nw, 0f, 0f), new Point3f(gs.vertices.get(2).anchor.x, 0f, 0f)));
        //GeomUtils.subtractPoint3f( new Point3f(nw, 0f, 0f), new Point3f(ow, 0f, 0f)));
    }
  }

  public boolean drawLines = true;
  @Override
  public void draw(GL gl, GLU glu, float offset)
  {
    //super.draw(gl, glu, offset);

    if (drawLines)
    {
    gl.glLineWidth(.5f);
    gl.glBegin(GL.GL_LINES);
    gl.glColor4f(1f,1f,1f,1f);

    gl.glVertex3f(0f, 0f, 0f);
    gl.glVertex3f(0f, h, 0f);
    gl.glVertex3f(w, 0f, 0f);
    gl.glVertex3f(w, h, 0f);
    gl.glEnd();
    }


  }


}
