/* PackingAlgorithm3.java ~ Jan 21, 2009 */

package algorithms2;

import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import utils.GeomUtils;
import utils.Utils;

/*
TO DO:
 1)  be able to specfiy ordered sort criteria THESE ARE DONE
    rect closest to point ASC/DESC
    rect with best fitting aspect ration
    rect with largest area
 2)  specify a maximum scaling for the rectangle
 3)  when we find a rect that fits/ where to place?
    random
    center
    closest to specfied point / line / rect
*/
/**
 *
 * @author angus
 */
public class PackingAlgorithm3 
{
  //variables
  public double MIN_RECTANGLE_WIDTH = .01;
  public double MIN_RECTANGLE_HEIGHT = .01;
  public double MAX_RECTANGLE_DISTANCE = 20;

  public float FLUB = .01f; //for floating point errors, prob should make super small

  //globals
  public final List<Rectangle2D> placedRectangles = Collections.synchronizedList(new ArrayList<Rectangle2D>());
  public final List<Rectangle2D> availableRectangles = Collections.synchronizedList(new ArrayList<Rectangle2D>());

  //public Path2D boundingPath = null;
  public Rectangle2D boundingRect = null;
  private List<Line2D> boundingLines = null;

  public PackingAlgorithm3(Rectangle2D boundingRect)
  {
    initialize(boundingRect);
  }

  public PackingAlgorithm3(Path2D boundingPath)
  {
    initialize(boundingPath);
  }

  public void initialize(Path2D boundingPath)
  {
    synchronized(availableRectangles)
    {
      this.availableRectangles.addAll(createAvailableRectanglesForPath(boundingPath, 5, 3));
    }
  }

  public void initialize(Rectangle2D boundingRect)
  {
    synchronized(availableRectangles)
    {
      this.availableRectangles.add(boundingRect);
    }
  }

  /**
   * Creates a series of rectangles that fit inside the specifed Path2D. This is a specialized
   * implementation that assumes that the Path2D is a quadrilateral
   * whose left and right sides are parallel to the Y-axis.
   * @param path The bounds of the TagRiver StratumSlice quadrilateral
   * @param NUM_STEPS Number of positions to create rectangles at.
   * @param NUM_SUB_STEPS Number of rectangles to create at each position.
   * @return A List of Rectangle2Ds that fit within the specifed Path2D.
   */
  public List<Rectangle2D> createAvailableRectanglesForPath(Path2D path, int NUM_STEPS, int NUM_SUB_STEPS)
  {
    List<Rectangle2D> pathRects = new ArrayList<Rectangle2D>();

    List<Line2D> pathLines = GeomUtils.getLinesFromPath2D(path);
    List<Point2D> pathPoints = GeomUtils.getPointsFromPath2D(path);

    Line2D bottomLine = pathLines.get(0);
    Line2D rightLine = pathLines.get(1);
    Line2D topLine = pathLines.get(2);
    Line2D leftLine = pathLines.get(3);

    double minX = Double.POSITIVE_INFINITY;
    double minY = Double.POSITIVE_INFINITY;
    double maxX = Double.NEGATIVE_INFINITY;
    double maxY = Double.NEGATIVE_INFINITY;

    for (Point2D pathPoint : pathPoints)
    {
      if (pathPoint.getX() < minX)
      {
        minX = pathPoint.getX();
      }
      if (pathPoint.getX() > maxX)
      {
        maxX = pathPoint.getX();
      }
      if (pathPoint.getY() < minY)
      {
        minY = pathPoint.getY();
      }
      if (pathPoint.getY() > maxY)
      {
        maxY = pathPoint.getY();
      }
    }

//    System.out.println("min x = " + minX);
//    System.out.println("max x = " + maxX);
//    System.out.println("min y = " + minY);
//    System.out.println("max y = " + maxY);

    double y0 = pathPoints.get(0).getY();
    double y1 = pathPoints.get(1).getY();
    double y2 = pathPoints.get(2).getY();
    double y3 = pathPoints.get(3).getY();

//    System.out.println("0y/1y/2y/3y = " +
//      y0 + "/" + y1 + "/" +
//      y2 + "/" + y3 );


    //1. the path is an axis-aligned rectangle
    if ( y0 == y1 && y2 == y3 )
    {
   //   System.out.println("in createAvailableRectanglesForPath() : CASE 1");
      pathRects.add(path.getBounds2D());
      return pathRects;
    }

    //2. the left min Y < right min Y and the left max X > right max Y
    if ( y0 <= y1 && y3 >= y2 )
    {
     // System.out.println("in createAvailableRectanglesForPath() : CASE 2");
      double inc = 1.0 / (double)NUM_STEPS ;

      for (int i = 1; i <= NUM_STEPS; i++)
      {
        Point2D pB = GeomUtils.getPointAlongLineSegment(bottomLine, i * inc);
        Point2D pT = GeomUtils.getPointAlongLineSegment(topLine, (NUM_STEPS - i) * inc);
        pathRects.add(new Rectangle2D.Double(minX, pB.getY(),
          pB.getX() - minX, pT.getY() - pB.getY()));
      }

      return pathRects;
    }

    //3. the right min Y < left min Y and the right max X > left max Y
    if ( y1 <= y0 && y2 >= y3 )
    {
     // System.out.println("in createAvailableRectanglesForPath() : CASE 3");
      double inc = 1.0 / (double) NUM_STEPS;

      for (int i = 0; i < NUM_STEPS; i++)
      {
        Point2D pB = GeomUtils.getPointAlongLineSegment(bottomLine, i * inc);
        Point2D pT = GeomUtils.getPointAlongLineSegment(topLine, (NUM_STEPS - i) * inc);

        pathRects.add(new Rectangle2D.Double(pB.getX(), pB.getY(),
          maxX - pB.getX(), pT.getY() - pB.getY()));
      }

      return pathRects;
    }

    //4. if left min Y < right min Y && left max Y < right max Y
    if ( y0 <= y1 && y3 <= y2 )
    {
     // System.out.println("in createAvailableRectanglesForPath() : CASE 4");
      double inc = 1.0 / (double)NUM_STEPS;

      for (int i = 1; i <= NUM_STEPS; i++)
      {
        Point2D pB = GeomUtils.getPointAlongLineSegment(bottomLine, i * inc);
        Point2D pT = GeomUtils.getPointAlongLineSegment(topLine, (NUM_STEPS - i) * inc);

        if (pB.getY() >= pathPoints.get(3).getY())
        {
          
          Point2D insectPt = GeomUtils.getIntersectionBetweenLines(
            topLine, new Line2D.Double(pB, new Point2D.Double(minX, pB.getY())));

          double inc2 = 1.0 / (double) NUM_SUB_STEPS;
          Line2D bottomLine2 = new Line2D.Double(insectPt, pB);
          Line2D topLine2 = new Line2D.Double(insectPt, pT);

          for (int ii = 1; ii <= NUM_SUB_STEPS; ii++)
          {
            Point2D pB2 = GeomUtils.getPointAlongLineSegment(bottomLine2, ii * inc2);
            Point2D pT2 = GeomUtils.getPointAlongLineSegment(topLine2, ii * inc2);

            pathRects.add(new Rectangle2D.Double(
              pB2.getX(), pB2.getY(), pB.getX() - pB2.getX(), pT2.getY() - pB2.getY()
              ));
          }
        }
        else
        {
          double inc2 = 1.0 / (double) (NUM_SUB_STEPS);
          Line2D bottomLine2 = new Line2D.Double(new Point2D.Double(minX, pB.getY()), pB);
          Line2D topLine2 = new Line2D.Double(pathPoints.get(3), pT);

          for (int ii = 0; ii < NUM_SUB_STEPS ; ii++)
          {
            Point2D pB2 = GeomUtils.getPointAlongLineSegment(bottomLine2, ii * inc2);
            Point2D pT2 = GeomUtils.getPointAlongLineSegment(topLine2, ii * inc2);

            pathRects.add(new Rectangle2D.Double(
              pB2.getX(), pB2.getY(), pB.getX() - pB2.getX(), pT2.getY() - pB2.getY()
              ));
          }
        }
      }
      return pathRects;
    }

    //5. if right min Y < less min Y && right max Y < left max Y
    if ( y1 <= y0 && y2 <= y3 )
    {
     // System.out.println("in createAvailableRectanglesForPath() : CASE 5");
      double inc = 1.0 / (double)NUM_STEPS;

      for (int i = 0; i < NUM_STEPS; i++)
      {
        Point2D pB = GeomUtils.getPointAlongLineSegment(bottomLine, i * inc);
        Point2D pT = GeomUtils.getPointAlongLineSegment(topLine, (NUM_STEPS - i) * inc);

        if (pB.getY() >= pathPoints.get(2).getY())
        {
          Point2D insectPt = GeomUtils.getIntersectionBetweenLines(
            topLine, new Line2D.Double(pB, new Point2D.Double(maxX, pB.getY())));

          double inc2 = 1.0 / (double)NUM_SUB_STEPS;
          Line2D bottomLine2 = new Line2D.Double(pB, insectPt);
          Line2D topLine2 = new Line2D.Double(pT, insectPt);

          for (int ii = 1; ii < NUM_SUB_STEPS; ii++)
          {
            Point2D pB2 = GeomUtils.getPointAlongLineSegment(bottomLine2, ii * inc2);
            Point2D pT2 = GeomUtils.getPointAlongLineSegment(topLine2, ii * inc2);

            pathRects.add(new Rectangle2D.Double(
              pB.getX(), pB.getY(),
              pB2.getX() - pB.getX(), pT2.getY() - pB2.getY()
              ));
          }
        }
        else
        {
          double inc2 = 1.0 / (double)NUM_SUB_STEPS;
          Line2D bottomLine2 = new Line2D.Double(new Point2D.Double(maxX, pB.getY()), pB);
          Line2D topLine2 = new Line2D.Double(pathPoints.get(2), pT);

          for (int ii = 0; ii < NUM_SUB_STEPS; ii++)
          {
            Point2D pB2 = GeomUtils.getPointAlongLineSegment(bottomLine2, ii * inc2);
            Point2D pT2 = GeomUtils.getPointAlongLineSegment(topLine2, ii * inc2);

            pathRects.add(new Rectangle2D.Double(
              pB.getX(), pB.getY(), pB2.getX() - pB.getX(), pT2.getY() - pB2.getY()
              ));
          }
        }
      }
      return pathRects;
    }
    else
    {
      System.err.println("in createAvailableRectanglesForPath() : ERROR, no case?");
    }
    return null;
  }

  public boolean placeRectInClosestAvailableArea(Rectangle2D rectangle)
  {
    return placeRectInClosestAvailableAreaToPoint(rectangle,
      new Point2D.Double(rectangle.getCenterX(), rectangle.getCenterY()));
  }

  public boolean placeRectInClosestAvailableAreaToPoint(Rectangle2D rectangle, Point2D centerPt)
  {
    //System.out.println("\nrectangle trying to be at : " + rectangle);

    //System.out.println("it's centerPt is : " + centerPt);

    synchronized(this.availableRectangles)
    {
      GeomUtils.sortRectanglesByDistanceToPoint(this.availableRectangles, centerPt, 1);
      //GeomUtils.sortRectanglesByDistanceOfCenterToPoint(this.availableRectangles, centerPt, 1);
    }

    Rectangle2D ur = null;
    for (Rectangle2D ar : this.availableRectangles)
    {
      if (ar.getWidth() >= rectangle.getWidth() && ar.getHeight() >= rectangle.getHeight())
      {
        ur = ar;
        break;
      }
    }

    if (ur == null)
    {
      //System.out.println("in placeRectInLargestAvailableArea() : we could not place the rectangle! (need to scale...)");
      return false;
    }

    //System.out.println("\nclosest available rectangle is : " + ur);

    //trying this...
    //placeAgainstNearestRect(ur, rectangle);
    double nx, ny;
    if (centerPt.getX() + (rectangle.getWidth() / 2f) > ur.getX() + ur.getWidth())
    {
      //System.out.println("x 1");
      nx = ur.getX() + ur.getWidth() - rectangle.getWidth();
    }
    else if (centerPt.getX() - (rectangle.getWidth() / 2f) < ur.getX())
    {
      //System.out.println("x 2");
      nx = ur.getX();
    }
    else
    {
      //System.out.println("x 3");
      nx = centerPt.getX() - (rectangle.getWidth() / 2f);
    }

    if (centerPt.getY() + (rectangle.getHeight() / 2f) > ur.getY() + ur.getHeight())
    {
      //System.out.println("y 1");
      ny = ur.getY() + ur.getHeight() - rectangle.getHeight();
    }
    else if (centerPt.getY() - (rectangle.getHeight() / 2f) < ur.getY())
    {
      //System.out.println("y 2");
      ny = ur.getY();
    }
    else
    {
      //System.out.println("y 3");
      ny = centerPt.getY() - (rectangle.getHeight() / 2f);
    }

    //System.out.println("nx/ny = " + nx + "/" + ny);
    rectangle.setRect(nx, ny, rectangle.getWidth(), rectangle.getHeight());
    //System.out.println("we place a rectangle at : " + rectangle);
    

      adjustAvailableRectangles(rectangle); //update availble rectangles


      synchronized(this.placedRectangles)
      {
        placedRectangles.add(rectangle);
      }

      return true;
  }


  public boolean placeRectInBestFittingArea(Rectangle2D rectangle)
  {
    double aspectRatio = rectangle.getWidth() / rectangle.getHeight();
    synchronized(this.availableRectangles)
    {
      GeomUtils.sortRectanglesByAspectRatio(this.availableRectangles, aspectRatio, 1);
    }

    Rectangle2D ur = null;
    for (Rectangle2D ar : this.availableRectangles)
    {
      if (ar.getWidth() > rectangle.getWidth() && ar.getHeight() > rectangle.getHeight())
      {
        ur = ar;
        break;
      }
    }

    if (ur == null)
    {
      //System.out.println("in placeRectInLargestAvailableArea() : we could not place the rectangle! (need to scale...)");
      return false;
    }


    rectangle.setRect(
      Utils.random(ur.getX(), ur.getX() + ur.getWidth() - rectangle.getWidth()),
      Utils.random(ur.getY(), ur.getY() + ur.getHeight() - rectangle.getHeight()),
        rectangle.getWidth(),
        rectangle.getHeight()
        );

      adjustAvailableRectangles(rectangle); //update availble rectangles
      synchronized(this.placedRectangles)
      {
        placedRectangles.add(rectangle);
      }

      return true;
  }


  public boolean placeRectInCenterOfBestFittingArea(Rectangle2D rectangle)
  {
    double aspectRatio = rectangle.getWidth() / rectangle.getHeight();
    synchronized(this.availableRectangles)
    {
      GeomUtils.sortRectanglesByAspectRatio(this.availableRectangles, aspectRatio, 1);
    }

    Rectangle2D ur = null;
    for (Rectangle2D ar : this.availableRectangles)
    {
      if (ar.getWidth() > rectangle.getWidth() && ar.getHeight() > rectangle.getHeight())
      {
        ur = ar;
        break;
      }
    }

    if (ur == null)
    {
      //System.out.println("in placeRectInLargestAvailableArea() : we could not place the rectangle! (need to scale...)");
      return false;
    }



    rectangle.setRect(
        (ur.getX() + (ur.getWidth() * .5) - (rectangle.getWidth() * .5) ),
        (ur.getY() + (ur.getHeight() * .5) - (rectangle.getHeight() * .5) ),
        rectangle.getWidth(),
        rectangle.getHeight()
        );

      adjustAvailableRectangles(rectangle); //update availble rectangles
      synchronized(this.placedRectangles)
      {
        placedRectangles.add(rectangle);
      }

      return true;
  }

  public boolean placeRectInLargestAvailableArea(Rectangle2D rectangle)
  {
    synchronized(this.availableRectangles)
    {
      GeomUtils.sortRectanglesByArea(this.availableRectangles, -1);
    }
    Rectangle2D ur = null;
    for (Rectangle2D ar : this.availableRectangles)
    {
      if (ar.getWidth() > rectangle.getWidth() && ar.getHeight() > rectangle.getHeight())
      {
        ur = ar;
        break;
      }
    }

    if (ur == null)
    {
      //System.out.println("in placeRectInLargestAvailableArea() : we could not place the rectangle! (need to scale...)");
      return false;
    }


    rectangle.setRect(
      Utils.random(ur.getX(), ur.getX() + ur.getWidth() - rectangle.getWidth()),
      Utils.random(ur.getY(), ur.getY() + ur.getHeight() - rectangle.getHeight()),
        rectangle.getWidth(),
        rectangle.getHeight()
        );


      adjustAvailableRectangles(rectangle); //update availble rectangles


      synchronized(this.placedRectangles)
      {
        placedRectangles.add(rectangle);
      }

      return true;
  }

  public boolean placeRectInCenterOfLargestAvailableArea(Rectangle2D rectangle)
  {
    synchronized(this.availableRectangles)
    {
      GeomUtils.sortRectanglesByArea(this.availableRectangles, -1);
    }
    Rectangle2D ur = null;
    for (Rectangle2D ar : this.availableRectangles)
    {
      if (ar.getWidth() > rectangle.getWidth() && ar.getHeight() > rectangle.getHeight())
      {
        ur = ar;
        break;
      }
    }

    if (ur == null)
    {
      //System.out.println("in placeRectInLargestAvailableArea() : we could not place the rectangle! (need to scale...)");
      return false;
    }


    rectangle.setRect(
        (ur.getX() + (ur.getWidth() * .5) - (rectangle.getWidth() * .5) ),
        (ur.getY() + (ur.getHeight() * .5) - (rectangle.getHeight() * .5) ),
        rectangle.getWidth(),
        rectangle.getHeight()
        );


      adjustAvailableRectangles(rectangle); //update availble rectangles

    synchronized(this.placedRectangles)
      {

    placedRectangles.add(rectangle);
    }

      return true;
  }

   /**
   * Places the specified Rectangle2D inside the boundingPath associated with this PackingAlgorithm.
   * @param rectangle The rectangle that we are trying to place.
   * @param firstTime A boolean value indicating if this is the first rectangle being placed.
   * @return true if the rectangle was placed successfully, false if it could not be placed.
   */
  public boolean placeRect(Rectangle2D rectangle, boolean firstTime)
  {
    //FOR NOW WE ARE assuming that the rectangle we are trying to place fits within the bounds...

    //does this rectangle intersect an existing rectangle?
    boolean doesRectangleIntersect = checkIfRectangleIntersects(rectangle);

    if (firstTime != true && doesRectangleIntersect == true) //then we need to reposition it.
    {
      /*
      //find closest corner (within range and with a legitimate possible rectangle
      Corner closestCorner = findClosestCorner(rectangle, MAX_RECTANGLE_DISTANCE, MIN_RECTANGLE_WIDTH, MIN_RECTANGLE_HEIGHT); //max distance, min width&height
      //Corner closestCorner = findClosestCorner(rectangle, 100f, .0001f); //.005f //max distance, min width&height

      if (closestCorner == null && firstTime == false)
      {
        //we failed to find a legitimate corner
        rectangle.setRect(0f, 0f, 0f, 0f);
        if(debug) System.out.println("Nowhere to place this Rectangle!");
        return false;
      }


      //we know that our rectangle will fit one of the corners
      //rectangles, so position our rectangle against the corner
      //in proper direction

      positionRectangle(rectangle, closestCorner);
       */
    }

    //we have now got a rectangle placed somewhere legitamely

    adjustAvailableRectangles(rectangle); //update availble rectangles


    placedRectangles.add(rectangle);


    return true;
  }


  private void removeRectanglesContainedBy(List<Rectangle2D> rects, Rectangle2D rect)
  {
    for (int i = rects.size() - 1; i >= 0; i--)
    {
      Rectangle2D er = rects.get(i);
      if (rect.contains(er))
      {
        rects.remove(i);
      }
    }
  }

  private boolean rectanglesContainRectangle(List<Rectangle2D> rects, Rectangle2D rect)
  {
    for (Rectangle2D er : rects)
    {
      if (er.contains(rect))
      {
        return true;
      }
    }
    return false;
  }

  public void adjustAvailableRectangles(Rectangle2D rectangle)
  {
    synchronized(this.availableRectangles)
    {
    List<Rectangle2D> interesectingAvailableRects = getIntersectingAvailableRects(rectangle);
    List<Rectangle2D> newRectangles = new ArrayList<Rectangle2D>();

    for (int i = interesectingAvailableRects.size() - 1; i >= 0; i--)
    {
      List<Rectangle2D> possibleNewRectangles = new ArrayList<Rectangle2D>();
      Rectangle2D existingRect = interesectingAvailableRects.get(i);

      Point2D eLL = new Point2D.Double(existingRect.getX(), existingRect.getY());
      Point2D eLR = new Point2D.Double(existingRect.getX() + existingRect.getWidth(), existingRect.getY());
      Point2D eUR = new Point2D.Double(existingRect.getX() + existingRect.getWidth(), existingRect.getY() + existingRect.getHeight());
      Point2D eUL = new Point2D.Double(existingRect.getX(), existingRect.getY() + existingRect.getHeight());


      Rectangle2D tryingRect = rectangle.createIntersection(existingRect);

      Point2D nLL = new Point2D.Double(tryingRect.getX(), tryingRect.getY());
      Point2D nLR = new Point2D.Double(tryingRect.getX() + tryingRect.getWidth(), tryingRect.getY());
      Point2D nUR = new Point2D.Double(tryingRect.getX() + tryingRect.getWidth(), tryingRect.getY() + tryingRect.getHeight());
      Point2D nUL = new Point2D.Double(tryingRect.getX(), tryingRect.getY() + tryingRect.getHeight());


      //make new bottom rect
      Rectangle2D bR = new Rectangle2D.Double(
          eLL.getX(),
          eLL.getY(),
          eLR.getX() - eLL.getX(),
          nLL.getY() - eLL.getY()
          );

      if (!rectanglesContainRectangle(newRectangles, bR) && rectangleBigEnough(bR))
      {
        possibleNewRectangles.add(bR);
        removeRectanglesContainedBy(newRectangles, bR);
      }

      //make new right rect
      Rectangle2D rR = new Rectangle2D.Double(
          nLR.getX(),
          eLL.getY(),
          eLR.getX() - nLR.getX(),
          eUR.getY() - eLR.getY()
          );

      if (!rectanglesContainRectangle(newRectangles, rR) && rectangleBigEnough(rR))
      {
        possibleNewRectangles.add(rR);
        removeRectanglesContainedBy(newRectangles, rR);
      }


      //make new top rect
      Rectangle2D tR = new Rectangle2D.Double(
          eUL.getX(),
          nUL.getY(),
          eUR.getX() - eUL.getX(),
          eUR.getY() - nUR.getY()
          );


      if (!rectanglesContainRectangle(newRectangles, tR) && rectangleBigEnough(tR))
      {
        possibleNewRectangles.add(tR);
        removeRectanglesContainedBy(newRectangles, tR);
      }

      //make new left rect
      Rectangle2D lR = new Rectangle2D.Double(
          eLL.getX(),
          eLL.getY(),
          nLL.getX() - eLL.getX(),
          eUL.getY() - eLL.getY()
          );


      if (!rectanglesContainRectangle(newRectangles, lR) && rectangleBigEnough(lR))
      {
        possibleNewRectangles.add(lR);
        removeRectanglesContainedBy(newRectangles, lR);
      }

      newRectangles.addAll(possibleNewRectangles);

      availableRectangles.remove(existingRect);

    }

    //we now have a List of new Rectangles
    //check to see if any completely contain the others. and remove those ones.
    //then add them all to availableRectangles.
    availableRectangles.addAll(newRectangles);
  
    }
  }

  /**
   * Place the specified rectangle inside the bounds of available rectangle we have selected
   * by pushing it up against the nearest already placed rectangle. 
   * @param availableRectangle
   * @param rectangle
   */
  private void placeAgainstNearestRect(Rectangle2D ur, Rectangle2D rectangle)
  {
    int i = 0;
    for (Rectangle2D placedRect : placedRectangles)
    {
      /*
       System.out.println("" + i + ": testing av rect \n" + ur + " \nagainst pl rect \n" + placedRect );
       System.out.println("\tav LL/UR = " + (new Point2D.Double(ur.getX(), ur.getY())) + 
         "/" + (new Point2D.Double(ur.getX() + ur.getWidth(), ur.getY() + ur.getHeight())) +
         "\n\tpr LL/UR) " + (new Point2D.Double(placedRect.getX(), placedRect.getY())) + 
         "/" + (new Point2D.Double(placedRect.getX() + placedRect.getWidth(), placedRect.getY() + placedRect.getHeight())));
       */

       if (ur.intersects(placedRect))
      {
      //System.out.println("yes!");
      }
       i++;
    }
      rectangle.setRect(
      Utils.random(ur.getX(), ur.getX() + ur.getWidth() - rectangle.getWidth()),
      Utils.random(ur.getY(), ur.getY() + ur.getHeight() - rectangle.getHeight()),
        rectangle.getWidth(),
        rectangle.getHeight()
        );
  }

  private boolean rectangleBigEnough(Rectangle2D r)
  {
    if (r.getWidth() >= MIN_RECTANGLE_WIDTH && r.getHeight() >= MIN_RECTANGLE_HEIGHT)
    {
      return true;
    }
    return false;
  }

  private List<Rectangle2D> getIntersectingAvailableRects(Rectangle2D r)
  {
    List<Rectangle2D> interesectingAvailableRects = new ArrayList<Rectangle2D>();

    for (Rectangle2D ar : this.availableRectangles)
    {
      if(r.intersects(ar))
      {
        interesectingAvailableRects.add(ar);
      }
    }

    return interesectingAvailableRects;
  }
   /**
   * Checks to see if the specified rectangle intersects with any of the already place rectangles
   * or with the boundaries of the boundingPath associated with this PackingAlgorithm.
   * @param r The rectangle we are checking.
   * @return true if the specified rectangle intersects, false if it does not.
   */
  private boolean checkIfRectangleIntersects(Rectangle2D r)
  {
    for (Rectangle2D pr : this.placedRectangles)
    {
      if(r.intersects(pr))
      {
        return true;
      }
    }

    //does the bounding box completely contain the photo?
    if (!boundingRect.contains(r))
    {
      return true;
    }

    return false;
  }

}
