
import algorithms2.PackingAlgorithm3;
import geometry.Colorf;
import geometry.Geom;
import java.awt.geom.Rectangle2D;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

/* GeomAvailableRects.java ~ Jan 21, 2009 */
/**
 *
 * @author angus
 */
public class GeomAvailableRects extends Geom
{

  PackingAlgorithm3 pa3 = null;

  public GeomAvailableRects(PackingAlgorithm3 pa3)
  {
    this.pa3 = pa3;
  }

  public void draw(GL gl, GLU glu, float offset)
  {
    if (this.pa3.availableRectangles != null)
    {
      synchronized (this.pa3.availableRectangles)
      {
        gl.glColor4f(0f, 0f, 1f, .1f);

        for (Rectangle2D r2d : this.pa3.availableRectangles)
        {
          gl.glBegin(gl.GL_POLYGON);
          gl.glVertex3f((float) r2d.getX(), (float) r2d.getY(), offset);
          gl.glVertex3f((float) (r2d.getX() + r2d.getWidth()), (float) r2d.getY(), offset);
          gl.glVertex3f((float) (r2d.getX() + r2d.getWidth()), (float) (r2d.getY() + r2d.getHeight()), offset);
          gl.glVertex3f((float) r2d.getX(), (float) (r2d.getY() + r2d.getHeight()), offset);
          gl.glEnd();
        }

      }
    }
    if (this.pa3.placedRectangles != null)
    {
      synchronized (this.pa3.placedRectangles)
      {


        for (Rectangle2D r2d : this.pa3.placedRectangles)
        {
          gl.glColor4fv(Colorf.newRandomColor(.2f, .8f, .5f).array(), 0);
          gl.glBegin(gl.GL_POLYGON);
          gl.glVertex3f((float) r2d.getX(), (float) r2d.getY(), offset);
          gl.glVertex3f((float) (r2d.getX() + r2d.getWidth()), (float) r2d.getY(), offset);
          gl.glVertex3f((float) (r2d.getX() + r2d.getWidth()), (float) (r2d.getY() + r2d.getHeight()), offset);
          gl.glVertex3f((float) r2d.getX(), (float) (r2d.getY() + r2d.getHeight()), offset);
          gl.glEnd();
        }
      }

    }
  }

}
