
import behaviors.geom.continuous.BehaviorRGBA;
import behaviors.geom.continuous.BehaviorScale;
import behaviors.geom.continuous.BehaviorTranslate;
import behaviors.geom.discrete.BehaviorIsActive;
import geometry.media.GeomImage;
import java.io.File;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Point3f;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author angus
 */
public class GeomPhoto2 extends GeomImage {
   BehaviorTranslate behaviorTranslate = null;
  BehaviorScale behaviorScale = null;
  BehaviorRGBA behaviorRGBA = null;
  BehaviorIsActive behaviorIsActive = null;
  float initialHeight;
  float initialWidth;
  public boolean highlight = true;

   public GeomPhoto2(File file,
            Point3f p3f,
            float w, float h) {
     super(file, p3f, w, h);
    isSelectable = false;
    setColor(1f,0f,0f,1f);
  }

    public void draw(GL gl, GLU glu, float offset) {
       gl.glDisable(gl.GL_BLEND);

       super.draw(gl, glu, offset);
  }


}
