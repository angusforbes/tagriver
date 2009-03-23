

import behaviorism.BehaviorismDriver;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import worlds.WorldGeom;

import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import java.io.*;
import java.util.Collections;

public class TagRiverDataParser extends WorldGeom
{
  //private List<String> groupNames = Arrays.asList("Rammstein+Fans", "Psychedelic+Rock", "trance", "Acid+Jazz"); 
  private List<String> groupNames = Arrays.asList("aydozz", "orcungogus", "uberbjork", "kaanna"); 
  private List<String> charts = new ArrayList<String>();

  public static void main(String[] args)
  {
    //load in application specific properties
    Properties properties = loadPropertiesFile("behaviorism.properties");

    //create the world
    WorldGeom world = new TagRiverDataParser();

    //create an instance of the behaviorism framework set to the world
    new BehaviorismDriver(world, properties);
  }

  public void setUpWorld()
  {
     
        try {
            getTimeTags();
        } catch (JDOMException ex) {
            Logger.getLogger(TagRiverDataParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TagRiverDataParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        parseTagsAndOutput(21);
  }
  
  public void getTimeTags() throws JDOMException, IOException{
      
      SAXBuilder builder = new SAXBuilder();
      Document doc = builder.build("http://ws.audioscrobbler.com/2.0/?method=group.getweeklychartlist&group=alternative&api_key=b25b959554ed76058ac220b7b2e0a026");
      Element root = doc.getRootElement();
      
       //To get a list of all its child elements:
      Element chartList = (Element) root.getChildren().get(0);
      List allChildren = chartList.getChildren();
      System.out.println("all Time Tags Size = "+allChildren.size());
      
      for(int t=0; t<allChildren.size(); t++){
          Element chart = (Element) allChildren.get(t);
        
          String frm = chart.getAttributeValue("from");
         
          //System.out.println("from = "+frm);
          charts.add(frm);
      }
      
  }
  
 
  
  public void getArtistsPerGroupByDate(String groupName, String from, String to, String fileName)throws IOException, JDOMException{
      
     
     String webpath = "http://ws.audioscrobbler.com/2.0/?method=group.getweeklyartistchart&group="+
                        groupName+
                        "&from="+
                        from+
                        "&to="+
                        to+
                        "&api_key=b25b959554ed76058ac220b7b2e0a026";
      SAXBuilder builder = new SAXBuilder();
      Document doc = builder.build(webpath);
      Element root = doc.getRootElement();
           
      Element wa = (Element) root.getChildren().get(0); // <weeklyartistchart group="mnml" from="1169380800" to="1169985600">
      List allChildren = wa.getChildren();
           
      System.out.println("groupName = "+groupName);
      System.out.println("to - from = "+to+" "+from);
      FileWriter outFile = new FileWriter(fileName);
      PrintWriter out = new PrintWriter(outFile);
          
      for(int nmArts=0; nmArts<allChildren.size(); nmArts++){
          Element artst = (Element) allChildren.get(nmArts);
          Element neym = (Element) artst.getChildren("name").get(0);
          //System.out.println("name = "+neym.getText()); //name of the artist
          Element count = (Element) artst.getChildren("playcount").get(0);
          //System.out.println("count = "+count.getText()); //count of the artist
          
          String line = count.getText()+" "+neym.getText();
          
          
          out.println(line);
      }
      
      out.close();
  }
  
  public void getArtistsPerUserByDate(String userName, String from, String to, String fileName)throws IOException, JDOMException{
     String webpath = "http://ws.audioscrobbler.com/2.0/?method=user.getweeklyartistchart&user="+
                        userName+
                        "&from="+
                        from+
                        "&to="+
                        to+
                        "&api_key=b25b959554ed76058ac220b7b2e0a026";
      SAXBuilder builder = new SAXBuilder();
      Document doc = builder.build(webpath);
      Element root = doc.getRootElement();
           
      Element wa = (Element) root.getChildren().get(0); // <weeklyartistchart group="mnml" from="1169380800" to="1169985600">
      List allChildren = wa.getChildren();
           
      System.out.println("userName = "+userName);
      System.out.println("to - from = "+to+" "+from);
      FileWriter outFile = new FileWriter(fileName);
      PrintWriter out = new PrintWriter(outFile);
          
      for(int nmArts=0; nmArts<allChildren.size(); nmArts++){
          Element artst = (Element) allChildren.get(nmArts);
          Element neym = (Element) artst.getChildren("name").get(0);
          //System.out.println("name = "+neym.getText()); //name of the artist
          Element count = (Element) artst.getChildren("playcount").get(0);
          //System.out.println("count = "+count.getText()); //count of the artist
          
          String line = count.getText()+" "+neym.getText();
          
          
          out.println(line);
      }
      
      out.close();
  }
  
   public void parseTagsAndOutput(int limit) {
      Collections.reverse(charts);
      for(int gn=0; gn<groupNames.size(); gn++){
          //for(int c=0; c<charts.size()-1; c++){
          for(int c=0; c<limit; c++){
              String outputFileName = "riverData2/week"+c+"_group"+gn+".txt";
              //the charts are reversed
              String from = charts.get(c+1);
              String to = charts.get(c);
              String group = groupNames.get(gn);
                try {
                    //getArtistsPerGroupByDate(group, from, to, outputFileName);
                    getArtistsPerUserByDate(group, from, to, outputFileName);
                } catch (IOException ex) {
                    Logger.getLogger(TagRiverDataParser.class.getName()).log(Level.SEVERE, null, ex);
                } catch (JDOMException ex) {
                    Logger.getLogger(TagRiverDataParser.class.getName()).log(Level.SEVERE, null, ex);
                }           
          }
      }
   }
 
}
