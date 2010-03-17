import processing.core.*; 
import processing.xml.*; 

import java.applet.*; 
import java.awt.*; 
import java.awt.image.*; 
import java.awt.event.*; 
import java.io.*; 
import java.net.*; 
import java.text.*; 
import java.util.*; 
import java.util.zip.*; 
import java.util.regex.*; 

public class Sketch extends PApplet {

PImage mapImage;
Socket[] sockets;
Legend legend;
Settings settings = new Settings();
int chartBottomRightX = 992;
int chartBottomRightY = 589;

public void setup()
{
  //map is 992x589
  size(chartBottomRightX, chartBottomRightY);
  fill(0);
  textFont(settings.regularFont);
  mapImage = loadImage("housemap.png");
  cursor(CROSS);
  buildSockets();
  buildLinkedHighlightingLegend();
}

public void draw()
{
  smooth();
  image(mapImage, 0, 0);
  legend.draw();
  for(int i = 0; i < sockets.length; i++)
    sockets[i].draw();
  for(int i = 0; i < sockets.length; i++)
    if(sockets[i].isMouseOver()) sockets[i].drawToolTip();
}

public void buildSockets()
{
  Table names = new Table("housenames.tsv");
  Table locations = new Table("locations.tsv");

  sockets = new Socket[names.getRowCount()];
  for(int i = 0; i < names.getRowCount(); i++)
  {
    String id = names.getString(i, 0);
    int totalSockets = names.getInt(i, 1);
    int availableSockets = names.getInt(i, 2);
    String socketDescription = names.getString(i, 3);
    String locationDescription = names.getString(i, 4);
    int locationX = locations.getInt(i, 1);
    int locationY = locations.getInt(i, 2);
    sockets[i] = new Socket(id, locationX, locationY, totalSockets, availableSockets, socketDescription, locationDescription, settings);
    System.out.println("Built socket - " + sockets[i]);
  }
}

public void buildLinkedHighlightingLegend()
{
  legend = new Legend();
  legend.addItem("Available sockets", getAvailableSockets(),  settings.availableSocketsColor);
  legend.addItem("Unavailable sockets", getTakenSockets(), settings.takenSocketsColor);
}

public Socket[] getAvailableSockets()
{
  java.util.List availableSockets = new java.util.ArrayList();
  for(int i = 0; i < sockets.length; i++)
  {
    if(sockets[i].hasAvailableSlots())
    {
      availableSockets.add(sockets[i]);
    }
  }
  return (Socket[]) availableSockets.toArray(new Socket[availableSockets.size()]);
}

public Socket[] getTakenSockets()
{
  java.util.List takenSockets = new java.util.ArrayList();
  for(int i = 0; i < sockets.length; i++)
  {
    if(sockets[i].hasTakenSlots())
      takenSockets.add(sockets[i]);
  }
  return (Socket[]) takenSockets.toArray(new Socket[takenSockets.size()]);
}
public class Legend
{
  private java.util.List items;
  
  public Legend()
  {
    items = new java.util.ArrayList();
  }
  
  public void addItem(String label, Socket[] sockets, int itemColor)
  {
    items.add(new LegendItem(label, sockets, itemColor));
  }
  
  public void draw()
  {
    textFont(settings.regularFont);
    int rectHeight = items.size() * 30 + 5;
    int rectWidth = 300;
    int topLeftX = 19;
    int topLeftY = chartBottomRightY - rectHeight - 23;
    fill(0xff333333);
    stroke(0xffFFFFFF);
    strokeWeight(1);
    rectMode(CORNER);
    rect(topLeftX, topLeftY, rectWidth, rectHeight);
    fill(0xffFFFFFF);
    text("Legend", topLeftX + 10, topLeftY + 20);
    int trailingY = topLeftY + 10;
    for(int i = 0; i < sockets.length; i++)
      sockets[i].setIsHighlighted(false);    
    for(int i = 0; i < items.size(); i++)
    {
      LegendItem item = (LegendItem) items.get(i);
      item.drawRelativeToLegend(topLeftX, trailingY);
      trailingY += 28;
    }
  }
}

public class LegendItem
{
  public String label;
  public Socket[] sockets;
  public int itemColor;
  int boxTopLeftX, boxTopLeftY;
  
  public LegendItem(String label, Socket[] sockets, int itemColor)
  {
    this.label = label;
    this.sockets = sockets;
    this.itemColor = itemColor;
  }
  
  public void drawRelativeToLegend(int topLeftX, int trailingY)
  {
    boxTopLeftX = topLeftX + 10;
    boxTopLeftY = trailingY;
    
    if(this.isMouseOver())
    {
      strokeWeight(2);
      stroke(settings.highlightColor);
      setHighlightingForAllSocketsTo(true);
    }
    else if(isMouseOverLinkedItem())
    {
      strokeWeight(2);
      stroke(settings.highlightColor);
    }
    else
    {
      strokeWeight(1);
      stroke(0xffFFFFFF);
    }
    
    fill(itemColor);
    rect(boxTopLeftX, boxTopLeftY, 100, 20);
    text(label, boxTopLeftX + 100 + 5, boxTopLeftY + 15);
  }
  
  public void setHighlightingForAllSocketsTo(boolean isHighlighted)
  {
    String x = "";
    for(int i = 0; i < this.sockets.length; i++)
    {
      this.sockets[i].setIsHighlighted(isHighlighted);
      x += "," + this.sockets[i].id;
    }
  }
  
  public boolean isMouseOver()
  {   
    return mouseX > boxTopLeftX && mouseX < boxTopLeftX+100 && mouseY > boxTopLeftY && mouseY < boxTopLeftY + 20;
  }
  
  public boolean isMouseOverLinkedItem()
  {
    for(int i = 0; i < this.sockets.length; i++)
      if(this.sockets[i].isMouseOver())
        return true;
    return false;
  }
}
class Settings
{
  public int backgroundColor = 0xffFFFFFF;
  public PFont regularFont = createFont("SansSerif", 12);
  public PFont tooltipFont = createFont("SansSerif", 10);
  public int availableSocketsColor = 0xffC51B8A;
  public int takenSocketsColor = 0xffFA95B5;
  public int highlightColor = 0xff0000FF;
  
  public Settings()
  {
  }
}
public class Socket
{
  private Settings settings;
  private String id, socketDescription, locationDescription;
  private int centerX, centerY, totalSockets, availableSockets;
  private boolean isHighlighted;

  public Socket(String id, int centerX, int centerY, int totalSockets, int availableSockets, String socketDescription, String locationDescription, Settings settings)
  {
      this.id = id;
      this.centerX = centerX;
      this.centerY = centerY;
      this.totalSockets = totalSockets;
      this.availableSockets = availableSockets;
      this.socketDescription = socketDescription;
      this.locationDescription = locationDescription;
      this.settings = settings;
  }
  
  public void draw()
  {
      noStroke();
      ellipseMode(CENTER);
      drawArcForTakenSockets();
      drawArcForAvailableSockets();
      if(isMouseOver() || isHighlighted) drawHighlight();
  }
  
  public void drawArcForAvailableSockets()
  {
      int availableSocketsColor = settings.availableSocketsColor;
      float totalSocketsStartAngle = getTakenSocketsEndAngle();
      float totalSocketsEndAngle = 270;
      fill(availableSocketsColor);
      noStroke();
      arc(centerX, centerY, getDiameter(), getDiameter(), radians(totalSocketsStartAngle), radians(totalSocketsEndAngle));    
  }
  
  public void drawArcForTakenSockets()
  {
      int takenSocketsColor = settings.takenSocketsColor;
      float takenSocketsStartAngle = -90;
      float takenSocketsEndAngle = getTakenSocketsEndAngle();
      fill(takenSocketsColor);
      arc(centerX, centerY, getDiameter(), getDiameter(), radians(takenSocketsStartAngle), radians(takenSocketsEndAngle));
  }
  
  public void drawHighlight()
  {
    strokeWeight(2);
    stroke(settings.highlightColor);
    noFill();
    ellipse(centerX, centerY, getDiameter(), getDiameter());
  }
  
  public void drawToolTip()
  {
    String tooltipText = wordWrap(totalSockets + " power sockets" + "\n" 
                                 + (availableSockets == totalSockets ? "All" : (availableSockets == 0 ? "None" : availableSockets)) + " sockets usually available" + "\n"
                                 + "Location: " + locationDescription + "\n"
                                 + "Detail: " + socketDescription, 70);
    PImage picture = loadImage(id + ".JPG");
    float descriptionWidth = textWidth(tooltipText);
    float descriptionHeight = tooltipText.split("\n").length * 17;
    float tooltipWidth = max(descriptionWidth, picture.width) + 10;
    float tooltipHeight = descriptionHeight + picture.height + 10 + 10;
    
    stroke(0xff888888);
    strokeWeight(1);
    fill(120, 0, 120);
    rectMode(CORNER);
    textFont(settings.tooltipFont);
    
    float tooltipTopLeftX = mouseX+5+tooltipWidth > chartBottomRightX ? mouseX-5-tooltipWidth : mouseX+5;
    float toolipTopLeftY = mouseY+5+tooltipHeight > chartBottomRightY-70 ? mouseY-5-tooltipHeight : mouseY+5;
    
    rect(tooltipTopLeftX, toolipTopLeftY, tooltipWidth, tooltipHeight);

    //draw text
    fill(0xffFFFFFF);
    textAlign(LEFT);
    text(tooltipText, tooltipTopLeftX + 5, toolipTopLeftY + 20);
    
    //draw picture of socket
    image(picture, tooltipTopLeftX + 5, toolipTopLeftY + descriptionHeight + 5 + 10);
  }
  
  public String wordWrap(String s, int numberOfChars)
  {
    if(s == null) return "";
    StringBuffer wrapped = new StringBuffer();
    String[] words = s.split(" ");
    int currentLineLength = 0;
    for(int i = 0; i < words.length; i++)
    {
      if(currentLineLength + words[i].length() > numberOfChars)
      {
        wrapped.append("\n" + words[i] + " ");
        currentLineLength = words[i].length();
      }
      else
      {
        wrapped.append(words[i] + " ");
        currentLineLength += words[i].length();
      }
    }
    
    return wrapped.toString();
  }
  
  public void setIsHighlighted(boolean highlighted)
  {
    this.isHighlighted = highlighted;
  }

  public boolean isMouseOver()
  {
    return dist(mouseX, mouseY, centerX, centerY) < getDiameter()/2;
  }
  
  public float getTakenSocketsEndAngle()
  {
    return ( (float)getTakenSockets() / (float)totalSockets ) * 360 - 90;
  }
  
  public int getTakenSockets()
  {
    return totalSockets - availableSockets;
  }
  
  public boolean hasAvailableSlots()
  {
    return availableSockets > 0;
  }
  
  public boolean hasTakenSlots()
  {
    return getTakenSockets() > 0;
  }
  
  public int getDiameter()
  {
    return totalSockets * 15;
  }
  
  public String toString()
  {
    return "id: " + id + ", centerX: " + centerX + ", centerY: " + centerY + ", totalSocks: " + totalSockets + ", availSocks: " + availableSockets + ", sockDesc: " + socketDescription + ", locDesc: " + locationDescription;
  }
}
class Table {
  String[][] data;
  int rowCount;
  
  
  Table() {
    data = new String[10][10];
  }

  
  Table(String filename) {
    String[] rows = loadStrings(filename);
    data = new String[rows.length][];
    
    for (int i = 0; i < rows.length; i++) {
      if (trim(rows[i]).length() == 0) {
        continue; // skip empty rows
      }
      if (rows[i].startsWith("#")) {
        continue;  // skip comment lines
      }
      
      // split the row on the tabs
      //String[] pieces = split(rows[i], ',');
      String[] pieces = splitTokens(rows[i], "\t" );
      // copy to the table array
      data[rowCount] = pieces;
      rowCount++;
      
      // this could be done in one fell swoop via:
      //data[rowCount++] = split(rows[i], TAB);
    }
    // resize the 'data' array as necessary
    data = (String[][]) subset(data, 0, rowCount);
  }


  public int getRowCount() {
    return rowCount;
  }
  
  
  // find a row by its name, returns -1 if no row found
  public int getRowIndex(String name) {
    for (int i = 0; i < rowCount; i++) {
      if (data[i][0].equals(name)) {
        return i;
      }
    }
    println("No row named '" + name + "' was found");
    return -1;
  }
  
  
  public String getRowName(int row) {
    return getString(row, 0);
  }


  public String getString(int rowIndex, int column) {
    return data[rowIndex][column];
  }

  
  public String getString(String rowName, int column) {
    return getString(getRowIndex(rowName), column);
  }

  
  public int getInt(String rowName, int column) {
    return parseInt(getString(rowName, column));
  }

  
  public int getInt(int rowIndex, int column) {
    return parseInt(getString(rowIndex, column));
  }

  
  public float getFloat(String rowName, int column) {
    return parseFloat(getString(rowName, column));
  }

  
  public float getFloat(int rowIndex, int column) {
    return parseFloat(getString(rowIndex, column));
  }
  
  
  public void setRowName(int row, String what) {
    data[row][0] = what;
  }


  public void setString(int rowIndex, int column, String what) {
    data[rowIndex][column] = what;
  }

  
  public void setString(String rowName, int column, String what) {
    int rowIndex = getRowIndex(rowName);
    data[rowIndex][column] = what;
  }

  
  public void setInt(int rowIndex, int column, int what) {
    data[rowIndex][column] = str(what);
  }

  
  public void setInt(String rowName, int column, int what) {
    int rowIndex = getRowIndex(rowName);
    data[rowIndex][column] = str(what);
  }

  
  public void setFloat(int rowIndex, int column, float what) {
    data[rowIndex][column] = str(what);
  }


  public void setFloat(String rowName, int column, float what) {
    int rowIndex = getRowIndex(rowName);
    data[rowIndex][column] = str(what);
  }
  
  
  // Write this table as a TSV file
  public void write(PrintWriter writer) {
    for (int i = 0; i < rowCount; i++) {
      for (int j = 0; j < data[i].length; j++) {
        if (j != 0) {
          writer.print(TAB);
        }
        if (data[i][j] != null) {
          writer.print(data[i][j]);
        }
      }
      writer.println();
    }
    writer.flush();
  }
}

  static public void main(String args[]) {
    PApplet.main(new String[] { "--bgcolor=#ffffff", "Sketch" });
  }
}
