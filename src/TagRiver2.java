
import algorithms2.PackingAlgorithm3;
import behaviorism.BehaviorismDriver;
import behaviors.geom.continuous.BehaviorScale;
import behaviors.geom.continuous.BehaviorTranslate;
import behaviors.geom.continuous.BehaviorRGBA;
import behaviors.geom.discrete.BehaviorIsActive;
import geometry.Colorf;
import geometry.Geom;
import geometry.GeomPoint;
import geometry.GeomPoly;
import geometry.GeomRect;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;
import javax.vecmath.Point3f;
import utils.DebugTimer;
import utils.FileUtils;
import utils.GeomUtils;
import utils.Utils;
import worlds.WorldGeom;

/* TagRiver2.java ~ Jan 16, 2009 */
/**
 *
 * @author angus
 */
public class TagRiver2 extends WorldGeom {
  //bbb

  GeomStrataHolder holder;
  public static int NUM_TIMES = 21; //21
  public static int NUM_LAYERS = 4; //4
  public static final String RIVER_PATH = "riverDataCT/";
  public static final String PHOTO_PATH = "riverDataCT/PHOTOS/";

  public static final String USER_1 = "Wellesley";
  public static final String USER_2 = "SOMArts";
  public static final String USER_3 = "Poznan";
  public static final String USER_4 = "user 4";

  //public static final String RIVER_PATH = "riverData2/";
  //public static final String RIVER_PATH = "river_data/";
  public static Colorf COLOR0 = new Colorf(.659f, .682f, .502f, 1f);
  public static Colorf COLOR2 = new Colorf(.573f, .639f, .365f, 1f);
  public static Colorf COLOR1 = new Colorf(.933f, .969f, .788f, 1f);
  public static Colorf COLOR3 = new Colorf(.616f, .690f, .267f, 1f);
  public static Colorf textCOLORS[] = {
    //    new Colorf(.933f, .969f, .788f, 0.7f),
    //    new Colorf(.816f, .890f, .267f, 0.7f),
    //    new Colorf(.460f, .135f, .354f, 0.7f),
    //    new Colorf(.876f, .916f, .867f, 0.7f)
    new Colorf(.933f, .969f, .788f, 1.0f),
    new Colorf(.460f, .135f, .354f, 1.0f),
    new Colorf(.816f, .890f, .267f, 1.0f),
    new Colorf(.876f, .916f, .867f, 1.0f)
  };
  public static Colorf textHighCOLORS[] = {
    new Colorf(.633f, .999f, .288f, 1.0f),
    new Colorf(.916f, .990f, .467f, 1.0f),
    new Colorf(.860f, .235f, .754f, 1.0f),
    new Colorf(.776f, .816f, .967f, 1.0f)
  };
  public GeomTag2 userLabel1;
  public GeomTag2 userLabel2;
  public GeomTag2 userLabel3;
  public GeomTag2 userLabel4;
  List<GeomStrata> geomStratas = new ArrayList<GeomStrata>();
  //GeomStrata currentStrata;
  SortedMap<Integer, DataSlice> timeToDataSlice = new TreeMap<Integer, DataSlice>();
  SortedMap<Integer, DataStratum> layerToDataStratum = new TreeMap<Integer, DataStratum>();
  GeomRect worldRect;

  public static void main(String[] args) {
    //load in application specific properties
    Properties properties = loadPropertiesFile("behaviorism.properties");

    //create the world
    WorldGeom world = new TagRiver2();


    //create an instance of the behaviorism framework set to the world
    new BehaviorismDriver(world, properties);
  }

  public void initLabels() {
    userLabel1 = GeomTag2.newGeomTextConstrainedByHeight(USER_1, new Point3f(), 0.12f);

    this.holder.addGeom(userLabel1);
    userLabel1.setPos(-0.5f, 0.2f, 0.0f);
    userLabel1.setColor(1f,1f,1f);

    userLabel2 = GeomTag2.newGeomTextConstrainedByHeight(USER_2, new Point3f(), 0.12f);

    this.holder.addGeom(userLabel2);
    userLabel2.setPos(-0.5f, 1.2f, 0.0f);
    userLabel2.setColor(1f,1f,1f);


    userLabel3 = GeomTag2.newGeomTextConstrainedByHeight(USER_3, new Point3f(), 0.12f);

    this.holder.addGeom(userLabel3);
    userLabel3.setPos(-0.5f, 2.8f, 0.0f);
    userLabel3.setColor(1f,1f,1f);

    userLabel4 = GeomTag2.newGeomTextConstrainedByHeight(USER_4, new Point3f(), 0.12f);

//    this.holder.addGeom(userLabel4);
    userLabel4.setPos(-0.3f, 3.9f, 0.0f);
    userLabel4.setColor(0.7f, 0.7f, 0.7f);
  }

  public void setUpWorld() {
    Utils.sleep(1000);
    this.worldRect = getWorldRect();

    System.out.println("\n\n *** IN WORLD TEST setUpWorld() ***");

    this.cam.moveZ(-1.0f);
    this.cam.moveY(-0.25f);

    this.holder = new GeomStrataHolder(this.worldRect.anchor, this.worldRect.w, this.worldRect.h, this);


    initLabels();

    loadAllData();
    createAllStrata();
    initializeStrataData(); //assoicate initial data slices

    DataSlice currDataSlice = timeToDataSlice.get(this.holder.numStrata / 2);
    DataSlice nextDataSlice = timeToDataSlice.get((this.holder.numStrata / 2) + 1);
    List<Float> leftSide = currDataSlice.layerPositions;
    List<Float> rightSide = nextDataSlice.layerPositions;

    List<GeomPoly> stratumPolys = new ArrayList<GeomPoly>();
    for (int ii = 0; ii < NUM_LAYERS; ii++) {
      GeomPoly poly = new GeomPoly(
	      new Point3f(0f, leftSide.get(ii), 0f),
	      new Point3f(holder.w * .5f, rightSide.get(ii), 0f),
	      new Point3f(holder.w * .5f, rightSide.get(ii + 1), 0f),
	      new Point3f(0f, leftSide.get(ii + 1), 0f));
      stratumPolys.add(poly);
    }

    placeTagsWhenBrowsing(Utils.now(), this.holder.openStrata, currDataSlice, stratumPolys, this.holder.openStrata.x);


    updateAllStrata();
  }

  private void initializeStrataData() {
    int timeIdx = 0;
    for (int s = 0; s < holder.geomStratas.size(); s++) {
      //System.out.println("s = " + s);
      GeomStrata strata = holder.geomStratas.get(s);

      //System.out.println("\n** iteration # " + i + "_" + s);
      //System.out.println("before ... timeIdx = " + timeIdx + " timeToDataSlice.size() = " + timeToDataSlice.size());

      DataSlice prevDataSlice = timeToDataSlice.get(timeIdx);
      timeIdx = (s) % (timeToDataSlice.size());
      DataSlice currDataSlice = timeToDataSlice.get(timeIdx);

      strata.leftDataSlice = prevDataSlice;
      strata.dataSlice = currDataSlice;
      strata.rightDataSlice = timeToDataSlice.get((timeIdx + 1) % (timeToDataSlice.size()));

      strata.weekLbl.text = "week " + (timeIdx + 1);


    }
  }

  public void userLabelsUpdate(float y2, float y3) {

    // System.out.println("upLefts = "+leftSide.get(1)+" "+leftSide.get(2)+" "+leftSide.get(3)+" "+leftSide.get(3));
    userLabel2.setPos(userLabel2.x, y2, userLabel2.z);
    userLabel3.setPos(userLabel3.x, y3, userLabel3.z);

  }

  public void loadAllData() {
    Set allTags = new HashSet<String>();

    for (int layer = 0; layer < NUM_LAYERS; layer++) {
      DataStratum dataStratum = new DataStratum();
      layerToDataStratum.put(layer, dataStratum);
    }

    for (int time = 0; time < NUM_TIMES; time++) {
      DataSlice dataSlice = new DataSlice();
      timeToDataSlice.put(time, dataSlice);
    }

    File file;
    Scanner s;

    for (int layer = 0; layer < NUM_LAYERS; layer++) {
      DataStratum dataStratum = layerToDataStratum.get(layer);
      Colorf currentColor = textCOLORS[layer];
      Colorf currentHghColor = textHighCOLORS[layer];
      for (int time = 0; time < NUM_TIMES; time++) {
	DataSlice dataSlice = timeToDataSlice.get(time);

	DataStratumSlice dataStratumSlice = new DataStratumSlice();
	dataSlice.layerToDataStratumSlice.put(layer, dataStratumSlice);
	dataStratum.timeToDataStratumSlice.put(time, dataStratumSlice);

	//String filename = FileUtils.toCrossPlatformFilename(RIVER_PATH + "test" + time + "_" + layer + ".txt");
	String filename = FileUtils.toCrossPlatformFilename(RIVER_PATH + "week" + time + "_group" + layer + ".txt");

	System.out.println(filename);
	try {
	  file = new File(filename);
	  s = new Scanner(file);

	  while (s.hasNextLine()) {
	    String str = s.nextLine();

	    if (str.startsWith("p")) {
	      String split[] = str.split(":");
	      //System.out.print(split[0] + ":");

	      str = split[1];
	      System.out.println("photo at :" + str);
	      dataStratumSlice.updatePhotos(str, this.holder);



	    } else {
	      String split[] = str.split(" ");
	      System.out.print(split[0] + ": ");
	      str = str.substring(split[0].length(), str.length());
	      System.out.println(str);
	      dataStratum.updateTags(str, currentColor, currentHghColor, this.holder);
	      int cnt = Integer.parseInt(split[0]);
	      dataStratumSlice.updateTagCount(str, cnt);
	      //dataSlice.totalCount++;
	      dataSlice.totalCount += cnt;
	    }
	  }
	  s.close();
	} catch (FileNotFoundException e) {
	  e.printStackTrace();
	}
      }
    }

    //now we have associated tags and tag counts with all slices and stratums, so we can calculate volumes
    for (int time = 0; time < NUM_TIMES; time++) {
      DataSlice dataSlice = timeToDataSlice.get(time);
      int totalSliceCount = dataSlice.totalCount;

      List<Float> stratumPositions = new ArrayList<Float>();
      float curPos = 0f;
      stratumPositions.add(curPos);
      for (Map.Entry<Integer, DataStratumSlice> entry : dataSlice.layerToDataStratumSlice.entrySet()) {
	double perc = (float) entry.getValue().totalCount / (float) totalSliceCount;

	curPos += (worldRect.h * perc);
	stratumPositions.add(curPos);
      }

      dataSlice.layerPositions = stratumPositions;
    }
  }

  public boolean checkKeys(boolean[] keys, boolean[] keysPressing) {
    if (keys[KeyEvent.VK_A]) {
      if (keysPressing[KeyEvent.VK_A] == false) {
	this.holder.toggleRiver();
	keysPressing[KeyEvent.VK_A] = true;
      }
      return true;
    }
    return false;
  }

  public void updateAllStrata() {
    Thread t = new Thread() {

      public void run() {
	System.out.println("in updateAllStrata()");
	int timeIdx = 0;

	long PAUSE_TIME = 4000L; //1990L; //2000L;
	long UPDATE_SPEED = 500L; //1990L;

	for (int i = 1; i < 10000; i++) {

	  System.out.println("looping... runRiver = " + holder.runRiver);
	  if (holder.runRiver == false) {
	    System.out.println("breaking out of loop! a");
	    break;
	  }

//          while (holder.mouseIsMoving.get() == true)
//          {
//            Utils.sleep(500);
//          }

	  while (Utils.now() < readyForRiverToRunNano.get()) {
	    //System.out.println("can't run!");
	    //Utils.sleep(100);
	  }
	  System.out.println("running!");
	  long updateTime = Utils.now();

	  for (int s = 0; s < holder.geomStratas.size(); s++) {
	    //System.out.println("s = " + s);
	    GeomStrata strata = holder.geomStratas.get(s);

	    //System.out.println("\n** iteration # " + i + "_" + s);
	    //System.out.println("before ... timeIdx = " + timeIdx + " timeToDataSlice.size() = " + timeToDataSlice.size());

	    DataSlice prevDataSlice = timeToDataSlice.get(timeIdx);
	    timeIdx = (s + i) % (timeToDataSlice.size());
	    DataSlice currDataSlice = timeToDataSlice.get(timeIdx);

	    strata.leftDataSlice = prevDataSlice;
	    strata.dataSlice = currDataSlice;
	    strata.rightDataSlice = timeToDataSlice.get((timeIdx + 1) % (timeToDataSlice.size()));

	    strata.weekLbl.text = "week " + (timeIdx + 1);



	    List<Float> leftSide = timeToDataSlice.get(timeIdx).layerPositions;
	    List<Float> rightSide = timeToDataSlice.get((timeIdx + 1) % timeToDataSlice.size()).layerPositions;

	    if (s == 0) {//(holder.geomStratas.size() -1)){
	      userLabelsUpdate(leftSide.get(1), leftSide.get(2));
	    }

	    List<GeomPoly> stratumPolys = new ArrayList<GeomPoly>();
	    for (int ii = 0; ii < strata.stratums.size(); ii++) {
	      GeomStratum gs = strata.stratums.get(ii);

	      if (gs.behaviorY_lowerLeft != null) {
		gs.behaviorY_lowerLeft.interruptImmediately();
	      }
	      float y0 = leftSide.get(ii);
	      GeomPoint gp0 = gs.vertices.get(0);
	      Point3f p0 = new Point3f(0f, y0 - gp0.anchor.y, 0f);
	      gs.behaviorY_lowerLeft = BehaviorTranslate.translate(gp0, updateTime, UPDATE_SPEED, p0);



	      if (gs.behaviorY_upperLeft != null) {
		gs.behaviorY_upperLeft.interruptImmediately();
	      }
	      float y3 = leftSide.get(ii + 1);
	      GeomPoint gp3 = gs.vertices.get(3);
	      Point3f p3 = new Point3f(0f, y3 - gp3.anchor.y, 0f);
	      gs.behaviorY_upperLeft = BehaviorTranslate.translate(gp3, updateTime, UPDATE_SPEED, p3);


	      if (gs.behaviorY_lowerRight != null) {
		gs.behaviorY_lowerRight.interruptImmediately();
	      }
	      float y1 = rightSide.get(ii);
	      GeomPoint gp1 = gs.vertices.get(1);
	      Point3f p1 = new Point3f(0f, y1 - gp1.anchor.y, 0f);
	      gs.behaviorY_lowerRight = BehaviorTranslate.translate(gp1, updateTime, UPDATE_SPEED, p1);


	      if (gs.behaviorY_upperRight != null) {
		gs.behaviorY_upperRight.interruptImmediately();
	      }
	      float y2 = rightSide.get(ii + 1);
	      GeomPoint gp2 = gs.vertices.get(2);
	      Point3f p2 = new Point3f(0f, y2 - gp2.anchor.y, 0f);
	      gs.behaviorY_upperRight = BehaviorTranslate.translate(gp2, updateTime, UPDATE_SPEED, p2);

	      GeomPoly poly = new GeomPoly(
		      new Point3f(0f, y0, 0f),
		      new Point3f(holder.w * .5f, y1, 0f),
		      new Point3f(holder.w * .5f, y2, 0f),
		      new Point3f(0f, y3, 0f));

	      //System.out.println("poly = " + poly);
	      stratumPolys.add(poly);
	    }

	    if (strata == holder.openStrata) //currentStrata)
	    {
	      //Utils.sleepUntilNano(Utils.nanoPlusMillis(updateTime, UPDATE_SPEED));
	      System.out.println("about to call placeTags()");
	      //then we have to handle tags
	      if (Utils.now() > readyForRiverToRunNano.get()) {
		placeTags(Utils.nanoPlusMillis(updateTime, UPDATE_SPEED), strata,
			prevDataSlice, currDataSlice,
			stratumPolys);
	      } else {
		System.out.println("nope! not placing tags!");
	      }
	    }

	  }

	  if (holder.runRiver == false) {
	    System.out.println("breaking out of loop! b");

	    break;
	  }

	  Utils.sleep(PAUSE_TIME);

	}
      }
    };

    t.start();
  }

  public void updateStrata(List<GeomStrata> stratas, List<List<Float>> stratumPositions) {
    long PAUSE_TIME = 2000L;
    long UPDATE_SPEED = 1250L;
    long baseNano = Utils.nowPlusMillis(2000);
    for (int i = 0; i < 10000; i++) {
      long updateTime = baseNano; //Utils.now();

      for (int s = 0; s < stratas.size(); s++) {
	GeomStrata strata = stratas.get(s);

	int stratamPosIdx = (s + i) % (stratumPositions.size() - 1);
	List<Float> leftSide = stratumPositions.get(stratamPosIdx);
	List<Float> rightSide = stratumPositions.get(stratamPosIdx + 1);

	for (int ii = 0; ii < strata.stratums.size(); ii++) {
	  GeomStratum gs = strata.stratums.get(ii);

	  if (gs.behaviorY_lowerLeft != null) {
	    gs.behaviorY_lowerLeft.interruptImmediately();
	  }

	  GeomPoint gp0 = gs.vertices.get(0);
	  Point3f p0 = new Point3f(0f, (leftSide.get(ii)) - gp0.anchor.y, 0f);
	  gs.behaviorY_lowerLeft = BehaviorTranslate.translate(gp0, updateTime, UPDATE_SPEED, p0);

	  if (gs.behaviorY_upperLeft != null) {
	    gs.behaviorY_upperLeft.interruptImmediately();
	  }

	  GeomPoint gp1 = gs.vertices.get(3);
	  Point3f p1 = new Point3f(0f, (leftSide.get(ii + 1)) - gp1.anchor.y, 0f);
	  gs.behaviorY_upperLeft = BehaviorTranslate.translate(gp1, updateTime, UPDATE_SPEED, p1);

	  if (gs.behaviorY_lowerRight != null) {
	    gs.behaviorY_lowerRight.interruptImmediately();
	  }

	  GeomPoint gp2 = gs.vertices.get(1);
	  Point3f p2 = new Point3f(0f, (rightSide.get(ii)) - gp2.anchor.y, 0f);
	  gs.behaviorY_lowerRight = BehaviorTranslate.translate(gp2, updateTime, UPDATE_SPEED, p2);

	  if (gs.behaviorY_upperRight != null) {
	    gs.behaviorY_upperRight.interruptImmediately();
	  }

	  GeomPoint gp3 = gs.vertices.get(2);
	  Point3f p3 = new Point3f(0f, (rightSide.get(ii + 1)) - gp3.anchor.y, 0f);
	  gs.behaviorY_upperRight = BehaviorTranslate.translate(gp3, updateTime, UPDATE_SPEED, p3);
	}
      }
      Utils.sleep(PAUSE_TIME);
    }
  }

  public void createAllStrata() {
    for (Map.Entry<Integer, DataSlice> entries : timeToDataSlice.entrySet()) {
      int time = entries.getKey();

      DataSlice slice = entries.getValue();
      DataSlice nxtSlice = timeToDataSlice.get((time + 1) % timeToDataSlice.entrySet().size());

      holder.geomStratas.get(time).initializeStrata(
	      createStratums(NUM_LAYERS, slice.layerPositions, nxtSlice.layerPositions));
    }

    addGeom(holder);
  }

  public List<GeomStratum> createStratums(int howMany, List<Float> leftSide, List<Float> rightSide) {
    List<GeomStratum> stratums = new ArrayList<GeomStratum>();

    for (int i = 0; i < howMany; i++) {
      GeomStratum s = new GeomStratum(this.holder);
      s.lowerLeft = leftSide.get(i); //(time * 1f / howMany);
      s.upperLeft = leftSide.get(i + 1); //((time + 1) * 1f / howMany);
      s.lowerRight = rightSide.get(i); //(time * 1f / howMany);
      s.upperRight = rightSide.get(i + 1); //((time + 1) * 1f / howMany);

      stratums.add(s);
    }

    return stratums;
  }

  private void removeDebugGeoms() {
    for (GeomStrata strata : holder.geomStratas) {
      for (GeomStratum stratum : strata.stratums) {
	for (Geom g : stratum.geoms) {
	  if (g instanceof GeomAvailableRects) {
	    stratum.removeGeom(g);
	  }
	}
      }
    }
  }

  private void interruptAndDeactivateAllTagBehaviors() {
    for (int s = 0; s < layerToDataStratum.size(); s++) {
      DataStratum ds = layerToDataStratum.get(s);


      for (DataStratumSlice dss : ds.timeToDataStratumSlice.values()) {
	for (GeomPhoto2 gt2 : dss.geomImages) {
	  if (gt2.behaviorScale != null) {
	    gt2.behaviorScale.interruptImmediately();
	  }
	  if (gt2.behaviorTranslate != null) {
	    gt2.behaviorTranslate.interruptImmediately();
	  }
	  if (gt2.behaviorRGBA != null) {
	    gt2.behaviorRGBA.interruptImmediately();
	  }
	  if (gt2.behaviorIsActive != null) {
	    gt2.behaviorIsActive.interruptImmediately();
	  }


	  gt2.isActive = false;
	  gt2.scale.x = 0f;
	  gt2.scale.y = 0f;
	}
      }


      for (GeomTag2 gt2 : ds.geomTags.values()) {
	if (gt2.behaviorScale != null) {
	  gt2.behaviorScale.interruptImmediately();
	}
	if (gt2.behaviorTranslate != null) {
	  gt2.behaviorTranslate.interruptImmediately();
	}
	if (gt2.behaviorRGBA != null) {
	  gt2.behaviorRGBA.interruptImmediately();
	}
	if (gt2.behaviorIsActive != null) {
	  gt2.behaviorIsActive.interruptImmediately();
	}


	gt2.isActive = false;
	gt2.scale.x = 0f;
	gt2.scale.y = 0f;
      }
    }
  }
  GeomAvailableRects gar;// = new GeomAvailableRects(pa);
  boolean firstDebugTime = true;
  AtomicLong readyForRiverToRunNano = new AtomicLong(0L);

  public void placeTagsWhenBrowsing(final long baseNano, final GeomStrata strata,
	  final DataSlice cds, final List<GeomPoly> polys, final float newStrataX) {
    final long MIN_APPEAR_TIME = 300L;
    final long MAX_APPEAR_TIME = 1000L;
    final long MIN_APPEAR_SPEED = 200L;
    final long MAX_APPEAR_SPEED = 500L;

    //removeDebugGeoms();

    interruptAndDeactivateAllTagBehaviors();

    if (strata != holder.openStrata) {
      return;
    }

    readyForRiverToRunNano.set(Utils.nowPlusMillis(MAX_APPEAR_TIME + MAX_APPEAR_SPEED + 100L));
    //readyForRiverToRunNano.set(Utils.nowPlusMillis(APPEAR_TIME));

    for (int s = 0; s < strata.stratums.size(); s++) {
      GeomStratum stratum = strata.stratums.get(s);
      GeomPoly stratumPoly = polys.get(s);
      Point2D centerPt = GeomUtils.toPoint2D(GeomUtils.centerOfMass(stratumPoly));

      //get current DataStratumSlice
      List<String> cStrings = new ArrayList<String>();
      List<GeomPhoto2> cImages = new ArrayList<GeomPhoto2>();


      if (cds != null) {
	DataStratumSlice css = cds.layerToDataStratumSlice.get(s);
	cStrings.addAll(css.tagToCount.keySet());
	cImages.addAll(css.geomImages);
	Collections.shuffle(cImages);
      }


      DataStratum ds = layerToDataStratum.get(s);
      //int totalTagsInLayer = css.totalCount;

      PackingAlgorithm3 pa = new PackingAlgorithm3(stratumPoly.makePath2DFromPoly());
      //PackingAlgorithm3 pa = new PackingAlgorithm3(stratum.makePath2DFromPoly());

      /*
      if (s == 2 && strata == holder.openStrata)
      {
      gar = new GeomAvailableRects(pa);

      //if (firstDebugTime == true)
      {

      stratum.addGeom(gar);
      firstDebugTime = false;
      }
      }
       */

      //will need to be positioned and then scaled in place
      int imgIdx = 0;
      int tagIdx = 0;

      for (int idx = 0; idx < cImages.size() + cStrings.size(); idx++) {
	if (Utils.randomInt(0, 10) < 5 && imgIdx < cImages.size()) {
	  GeomPhoto2 gt2 = cImages.get(imgIdx);
	  //photo
	  Rectangle2D r2d = gt2.makeRectangle2DFromRect();

	  if (pa.placeRectInClosestAvailableAreaToPoint(r2d, centerPt)) {
	    gt2.scale.x = 0f;
	    gt2.scale.y = 0f;

	    float newX = newStrataX + (float) r2d.getX() + (float) (r2d.getWidth() * .2);
	    float newY = strata.anchor.y + (float) r2d.getY() +  (float) (r2d.getHeight() * .2);
	    //gt2.setPos(strata.anchor.x + (float) r2d.getX(), strata.anchor.y + (float) r2d.getY(), 0f);

	    gt2.setPos(newX, newY, 0.0f);

	    //gt2.setPos(newStrataX + (float) r2d.getX(), strata.anchor.y + (float) r2d.getY(), 0.0f);

	    //gt2.setColor(new Colorf(1f,1f,1f,1f)); //textCOLORS[s]);

	    gt2.setColor(1f, 1f, 1f, 1f);

	    if (gt2.behaviorScale != null) {
	      gt2.behaviorScale.interruptImmediately();
	    }
	    gt2.isActive = true; //just for debugging

	    gt2.behaviorScale = BehaviorScale.scaleTo(gt2,
		    Utils.nowPlusMillis(MIN_APPEAR_TIME, MAX_APPEAR_SPEED),
		    Utils.randomLong(MIN_APPEAR_SPEED, MAX_APPEAR_SPEED),
		    new Point3f(.6f, .6f, 0f));

	    // gt2.isActive = true;
	  } else {
	    gt2.isActive = false;
	  }
	  imgIdx++;
	} else if (tagIdx < cStrings.size()) {
	  //tag
	  GeomTag2 gt2 = ds.geomTags.get(cStrings.get(tagIdx));
	  Rectangle2D r2d = gt2.makeRectangle2DFromRect();

	  if (pa.placeRectInClosestAvailableAreaToPoint(r2d, centerPt)) {
	    gt2.scale.x = 0f;
	    gt2.scale.y = 0f;

	    //gt2.setPos(strata.anchor.x + (float) r2d.getX(), strata.anchor.y + (float) r2d.getY(), 0f);
	    gt2.setPos(newStrataX + (float) r2d.getX(), strata.anchor.y + (float) r2d.getY(), 0.0f);

	    //gt2.setColor(new Colorf(1f,1f,1f,1f)); //textCOLORS[s]);

	    gt2.setColor(textCOLORS[s]);
	    //gt2.setColor(1f, 1f, 1f, 1f);

	    if (gt2.behaviorScale != null) {
	      gt2.behaviorScale.interruptImmediately();
	    }
	    gt2.isActive = true; //just for debugging

	    gt2.behaviorScale = BehaviorScale.scaleTo(gt2,
		    Utils.nowPlusMillis(MIN_APPEAR_TIME, MAX_APPEAR_SPEED),
		    Utils.randomLong(MIN_APPEAR_SPEED, MAX_APPEAR_SPEED),
		    new Point3f(1f, 1f, 0f));

	    // gt2.isActive = true;
	  } else {
	    gt2.isActive = false;
	  }
	  tagIdx++;
	}


      }

      /*
      for (GeomPhoto2 gt2 : cImages) {
      Rectangle2D r2d = gt2.makeRectangle2DFromRect();

      if (pa.placeRectInClosestAvailableAreaToPoint(r2d, centerPt)) {
      gt2.scale.x = 0f;
      gt2.scale.y = 0f;

      //gt2.setPos(strata.anchor.x + (float) r2d.getX(), strata.anchor.y + (float) r2d.getY(), 0f);
      gt2.setPos(newStrataX + (float) r2d.getX(), strata.anchor.y + (float) r2d.getY(), 0.0f);

      //gt2.setColor(new Colorf(1f,1f,1f,1f)); //textCOLORS[s]);

      gt2.setColor(1f,1f,1f,1f);

      if (gt2.behaviorScale != null)
      {
      gt2.behaviorScale.interruptImmediately();
      }
      gt2.isActive = true; //just for debugging

      gt2.behaviorScale = BehaviorScale.scaleTo(gt2,
      Utils.nowPlusMillis(MIN_APPEAR_TIME, MAX_APPEAR_SPEED),
      Utils.randomLong(MIN_APPEAR_SPEED, MAX_APPEAR_SPEED),
      new Point3f(1f, 1f, 0f));

      // gt2.isActive = true;
      }
      else {
      gt2.isActive = false;
      }
      }

      for (String str : cStrings) {
      GeomTag2 gt2 = ds.geomTags.get(str);

      Rectangle2D r2d = gt2.makeRectangle2DFromRect();

      if (pa.placeRectInClosestAvailableAreaToPoint(r2d, centerPt))
      {
      gt2.scale.x = 0f;
      gt2.scale.y = 0f;

      //gt2.setPos(strata.anchor.x + (float) r2d.getX(), strata.anchor.y + (float) r2d.getY(), 0f);
      gt2.setPos(newStrataX + (float) r2d.getX(), strata.anchor.y + (float) r2d.getY(), 0f);

      gt2.setColor(textCOLORS[s]);
      //gt2.setColor(1f,1f,1f,1f);


      if (gt2.behaviorScale != null) {
      gt2.behaviorScale.interruptImmediately();
      }

      gt2.isActive = true; //just for debugging

      gt2.behaviorScale = BehaviorScale.scaleTo(gt2,
      Utils.nowPlusMillis(MIN_APPEAR_TIME, MAX_APPEAR_SPEED),
      Utils.randomLong(MIN_APPEAR_SPEED, MAX_APPEAR_SPEED),
      new Point3f(1f, 1f, 0f));

      // gt2.isActive = true;
      }
      else
      {
      gt2.isActive = false;
      }
      }
       */
    }


  }
  /*
  // get strings in previous slice
  // get strings in current  slice

  //(A, B, C) --> (A, C, D)
  //
  A moves to new position
  B disappears
  C moves to new position
  D appears
   */

  public void placeTags(final long baseNano, final GeomStrata strata,
	  final DataSlice pds, final DataSlice cds, final List<GeomPoly> polys) {
    System.out.println("in placeTags() ");

    final long DISAPPEAR_TIME = 0L;
    final long MOVE_TIME = 400L;
    final long APPEAR_TIME = 2000L;

    final long MIN_DISAPPEAR_SPEED = 250L;
    final long MAX_DISAPPEAR_SPEED = 1000L;
    final long MIN_MOVE_SPEED = 800L;
    final long MAX_MOVE_SPEED = 1200L;
    final long MIN_APPEAR_SPEED = 200L;
    final long MAX_APPEAR_SPEED = 1000L;

    /*
    for (int s = 0; s < strata.stratums.size(); s++)
    {
    DataStratum ds = layerToDataStratum.get(s);

    for (GeomTag2 gt2 : ds.geomTags.values())
    {
    if (gt2.behaviorScale != null)
    {
    gt2.behaviorScale.interruptImmediately();
    }
    }
    }


    if (strata != holder.openStrata)
    {
    return;
    }
     */

    for (int s = 0; s < strata.stratums.size(); s++) {
      if (Utils.now() < readyForRiverToRunNano.get()) {
	System.out.println("INTERRUPTED AT S = " + s);
	return;
      }

      DataStratum ds = layerToDataStratum.get(s);

      //GeomStratum stratum = strata.stratums.get(s);
      GeomPoly stratumPoly = polys.get(s);
      Point2D centerPt = GeomUtils.toPoint2D(GeomUtils.centerOfMass(stratumPoly));

      //get previous DataStratumSlice
      Set<String> pStrings = new HashSet();
      if (pds != null) {
	DataStratumSlice pss = pds.layerToDataStratumSlice.get(s);
	pStrings.addAll(pss.tagToCount.keySet());


      }

      //get current DataStratumSlice
      Set<String> cStrings = new HashSet();
      //Set<Integer> cCount = new HashSet();

      DataStratumSlice css = null;

      if (cds != null) {
	css = cds.layerToDataStratumSlice.get(s);
	cStrings.addAll(css.tagToCount.keySet());
	//cCount.addAll(css.tagToCount.values());
      }



      //these sets should not overlap!
      Set<String> stringsToMove = Utils.intersectSets(pStrings, cStrings);
      Set<String> stringsToDisappear = Utils.complementSets(pStrings, stringsToMove);
      Set<String> stringsToAppear = Utils.complementSets(cStrings, stringsToMove);

      //Angus added the following code:
      List<String> notActiveMoveStrings = new ArrayList<String>();
      for (String checkStr : stringsToMove) {
	if (ds.geomTags.get(checkStr).isActive == false) {
	  notActiveMoveStrings.add(checkStr);
	}
      }

      stringsToMove.removeAll(notActiveMoveStrings);
      stringsToAppear.addAll(notActiveMoveStrings);

      PackingAlgorithm3 pa = new PackingAlgorithm3(stratumPoly.makePath2DFromPoly());
      GeomAvailableRects gar = new GeomAvailableRects(pa);

      DebugTimer timer = new DebugTimer();

      Colorf diff = Colorf.distance(ds.highColor, ds.nrmColor);
      for (String str : stringsToMove) {
	//if(Utils.now() < readyForRiverToRunNano.get()) { return; }

	GeomTag2 gt2 = ds.geomTags.get(str);

	int t = css.tagToCount.get(str);
	float newHeight = (float) Math.log10((double) t);

	newHeight = Math.min(DataStratum.MAX_INIT_HEIGHT_OF_TAGS, newHeight);
	newHeight = Math.max(DataStratum.MIN_INIT_HEIGHT_OF_TAGS, newHeight);


	float perc = newHeight / gt2.h;
	float newWidth = gt2.w * perc;

	Rectangle2D r2d = new Rectangle2D.Double(gt2.x, gt2.y, newWidth, newHeight);



	if (pa.placeRectInClosestAvailableAreaToPoint(r2d, centerPt)) {

	  if (gt2.behaviorTranslate != null) {
	    gt2.behaviorTranslate.interruptImmediately();
	  }


	  gt2.behaviorTranslate = BehaviorTranslate.translateTo(gt2,
		  Utils.nowPlusMillis(MOVE_TIME),
		  Utils.randomLong(MIN_MOVE_SPEED, MAX_MOVE_SPEED),
		  new Point3f(strata.anchor.x + (float) r2d.getX(), strata.anchor.y + (float) r2d.getY(), 0f));

	  if (gt2.behaviorRGBA != null) {
	    gt2.behaviorRGBA.interruptImmediately();
	  }

	  if (gt2.highlight) {
	    //gt2.setColor(ds.nrmColor);
	    gt2.behaviorRGBA = BehaviorRGBA.colorChange(gt2, Utils.now(), Utils.randomLong(MIN_MOVE_SPEED, MAX_MOVE_SPEED), diff);
	    gt2.highlight = false;
	  } else if (gt2.a < 1.0) {
	    gt2.a += 0.1;
	  }

	  if (gt2.behaviorScale != null) {
	    gt2.behaviorScale.interruptImmediately();
	  }
	  gt2.behaviorScale = BehaviorScale.scaleTo(gt2, Utils.now(), Utils.randomLong(MIN_MOVE_SPEED, MAX_MOVE_SPEED),
		  new Point3f(perc, perc, 0f));



	  gt2.isActive = true;

	} else {
	  gt2.isActive = false;
	}
      }



      //will need to be positioned and then scaled in place
      for (String str : stringsToAppear) {
	//if(Utils.now() < readyForRiverToRunNano.get()) { return; }
	GeomTag2 gt2 = ds.geomTags.get(str);


	gt2.setColor(ds.highColor);
	gt2.highlight = true;

	int t = css.tagToCount.get(str);
	float newHeight = (float) Math.log10((double) t);

	newHeight = Math.min(DataStratum.MAX_INIT_HEIGHT_OF_TAGS, newHeight);
	newHeight = Math.max(DataStratum.MIN_INIT_HEIGHT_OF_TAGS, newHeight);
	float perc = newHeight / gt2.h;
	float newWidth = gt2.w * perc;



	Rectangle2D r2d = new Rectangle2D.Double(gt2.x, gt2.y, newWidth, newHeight);

	if (pa.placeRectInClosestAvailableAreaToPoint(r2d, centerPt)) {
	  gt2.setPos(strata.anchor.x + (float) r2d.getX(), strata.anchor.y + (float) r2d.getY(), 0f);

	  if (gt2.behaviorScale != null) {
	    gt2.behaviorScale.interruptImmediately();
	  }

	  gt2.scale.x = 0f;
	  gt2.scale.y = 0f;

	  gt2.behaviorScale = BehaviorScale.scaleTo(gt2, Utils.nowPlusMillis(APPEAR_TIME),
		  Utils.randomLong(MIN_APPEAR_SPEED, MAX_APPEAR_SPEED),
		  new Point3f(perc, perc, 0f));

	  gt2.isActive = true;
	} else {
	  gt2.isActive = false;
	}
      }

      //will need to be scaled in place
      for (String str : stringsToDisappear) {
	//if(Utils.now() < readyForRiverToRunNano.get()) { return; }
	GeomTag2 gt2 = ds.geomTags.get(str);

	if (gt2.behaviorScale != null) {
	  gt2.behaviorScale.interruptImmediately();
	}

	long disappearSpeed = Utils.randomLong(MIN_DISAPPEAR_SPEED, MAX_DISAPPEAR_SPEED);

	gt2.behaviorScale = BehaviorScale.scaleTo(gt2,
		Utils.nowPlusMillis(DISAPPEAR_TIME),
		disappearSpeed,
		new Point3f(0f, 0f, 0f));

	if (gt2.behaviorIsActive != null) {
	  gt2.behaviorIsActive.interruptImmediately();
	}

	gt2.behaviorIsActive = BehaviorIsActive.deactivateAtMillis(gt2,
		Utils.nowPlusMillis(DISAPPEAR_TIME),
		disappearSpeed);
      }

      /*
      for (GeomPhoto2 gt2: ds.geomImages)
      {
      //if(Utils.now() < readyForRiverToRunNano.get()) { return; }
      //GeomTag2 gt2 = ds.geomTags.get(str);

      if (gt2.behaviorScale != null)
      {
      gt2.behaviorScale.interruptImmediately();
      }

      long disappearSpeed = Utils.randomLong(MIN_DISAPPEAR_SPEED, MAX_DISAPPEAR_SPEED);

      gt2.behaviorScale = BehaviorScale.scaleTo(gt2,
      Utils.nowPlusMillis(DISAPPEAR_TIME),
      disappearSpeed,
      new Point3f(0f, 0f, 0f));

      if (gt2.behaviorIsActive != null)
      {
      gt2.behaviorIsActive.interruptImmediately();
      }

      gt2.behaviorIsActive = BehaviorIsActive.deactivateAtMillis(gt2,
      Utils.nowPlusMillis(DISAPPEAR_TIME),
      disappearSpeed);


      }
       */
      //ds.geomImages.clear();




      /*
      List<GeomText2> tags = new ArrayList<GeomText2>();

      for (Map.Entry<String, Integer> entry : dss.tagToCount.entrySet())
      {
      GeomText2 gt2 = ds.geomTags.get(entry.getKey());

      tags.add(new GeomTag(Utils.randomString(4, 10), 1));
      }

      testPackingAlgorithmWithGeomPolyAndTags(stratum, tags);
       */
    }
//      }
//      };
//
//      t.start();
  }

  /*
  private void testPackingAlgorithmWithGeomPolyAndTags(GeomPoly gp, List<GeomTag> tags)
  {
  long PAUSE_BETWEEN_PLACEMENT = 0L;

  for (int i = 0; i < tags.size(); i++)
  {
  float xpos = Utils.randomFloat(-2f, 2f);
  float ypos = Utils.randomFloat(-2f, 2f);

  float initialHeight = tags.get(i).count / 30f; // + 1.0f;
  String txt = tags.get(i).text;

  tags.get(i).setPos(xpos, ypos, 0f, initialHeight);
  }

  Path2D.Float p2d = gp.makePath2DFromPoly();
  PackingAlgorithm2 pa = new PackingAlgorithm2(p2d);

  System.out.println("TAGS SIZE = " + tags.size());
  for (int i = 0; i < tags.size(); i++) //for(int time=0; time<0; time++)
  {
  GeomText2 gt = GeomText2.newGeomTextConstrainedByHeight(tags.get(i).text,
  new Point3f(0f, 0f, 0f), .3f, true);
  gt.backgroundColor = new Colorf();

  float leftSideLength = gp.vertices.get(3).anchor.y - gp.vertices.get(0).anchor.y;
  float rightSideLength = gp.vertices.get(2).anchor.y - gp.vertices.get(1).anchor.y;

  if (leftSideLength > rightSideLength)
  {
  gt.anchor.x = 0f;
  gt.anchor.y = (gp.vertices.get(0).anchor.y + gp.vertices.get(3).anchor.y) / 2f - (gt.h / 2f);
  }
  else
  {
  gt.anchor.x = gp.vertices.get(1).anchor.x - gt.w;
  gt.anchor.y = (gp.vertices.get(2).anchor.y + gp.vertices.get(1).anchor.y) / 2f - (gt.h / 2f);
  }
  //gt.anchor.x = 0.0f;
  //gt.anchor.y = 0.0f;
  gt.anchor.z = 0.0f;

  }
  }
   */
}
