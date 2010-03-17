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

public class timeseries_sketch extends PApplet {

FloatTable data; 
float dataMin, dataMax; 
float plotX1, plotY1; 
float plotX2, plotY2; 
float labelX, labelY; 
int rowCount; 
int columnCount; 
int currentColumn = 0;  
String[] months; 
int monthInterval = 12;
int volumeInterval = 1; 
float volumeIntervalMinor = 0.5f;
float[] tabLeft, tabRight; 
float tabTop, tabBottom; 
float barWidth = 4; 

float tabPad = 10; 
Integrator[] interpolators; 
PFont plotFont; 
public void setup() { 
  size(1000, 500); 
  data = new FloatTable("energy_prices.tsv"); 
  rowCount = data.getRowCount(); 
  columnCount = data.getColumnCount(); 
  months = data.getRowNames(); 
  dataMin = 0; 
  dataMax = ceil(data.getTableMax() / volumeInterval) * volumeInterval; 
  interpolators = new Integrator[rowCount]; 
  for (int row = 0; row < rowCount; row++) { 
    float initialValue = data.getFloat(row, 0); 
    interpolators[row] = new Integrator(initialValue); 
    //interpolators[row].attraction = 0.05; // Set lower than the default 
  } 
  plotX1 = 130; 
  plotX2 = width - 80; 
  labelX = 50; 
  plotY1 = 60; 
  plotY2 = height - 70; 
  labelY = height - 25; 
  plotFont = createFont("SansSerif", 20); 
  textFont(plotFont); 
  smooth(); 
} 

public void draw() { 
  background(224); 
  // Show the plot area as a white box 
  fill(255); 
  rectMode(CORNERS); 
  noStroke(); 
  rect(plotX1, plotY1, plotX2, plotY2); 
  drawTitleTabs(); 
  drawAxisLabels(); 
  for (int row = 0; row < rowCount; row++) { 
    interpolators[row].update(); 
  } 
  drawYearLabels(); 
  drawVolumeLabels(); 
  
  // Draw scattered data points
  //strokeWeight(5); 
  //stroke(#5679C1);   
  //drawDataPoints(currentColumn);
  
  // Draw a line graph
  //strokeWeight(5); 
  //stroke(#5679C1); 
  //noFill(); 
  //drawDataLine(currentColumn); 
  
  // Draw a line graph and scattered points
  //stroke(#5679C1); 
  //strokeWeight(5); 
  //drawDataPoints(currentColumn); 
  //noFill(); 
  //strokeWeight(0.5); 
  //drawDataLine(currentColumn); 
  
  // Draw a curved line graph
  strokeWeight(2); 
  stroke(0xff5679C1); 
  noFill(); 
  drawDataCurve(currentColumn); 
  drawDataHighlight(currentColumn);

  // Draw a bar chart
  //noStroke(); 
  //fill(#5679C1); 
  //drawDataBars(currentColumn); 
  
  // Draw an area plot
  //noStroke(); 
  //fill(#5679C1); 
  //drawDataArea(currentColumn); 
} 

public void drawTitleTabs() { 
  rectMode(CORNERS); 
  noStroke(); 
  textSize(20); 
  textAlign(LEFT); 

  // On first use of this method, allocate space for an array 
  // to store the values for the left and right edges of the tabs 
  if (tabLeft == null) { 
    tabLeft = new float[columnCount]; 
    tabRight = new float[columnCount]; 
  } 
  float runningX = plotX1; 
  tabTop = plotY1 - textAscent() - 15; 
  tabBottom = plotY1; 
  for (int col = 0; col < columnCount; col++) { 
    String title = data.getColumnName(col); 
    tabLeft[col] = runningX; 
    float titleWidth = textWidth(title); 
    tabRight[col] = tabLeft[col] + tabPad + titleWidth + tabPad; 
    // If the current tab, set its background white, otherwise use pale gray 
    fill(col == currentColumn ? 255 : 224); 
    rect(tabLeft[col], tabTop, tabRight[col], tabBottom); 
    // If the current tab, use black for the text, otherwise use dark gray 
    fill(col == currentColumn ? 0 : 64); 
    text(title, runningX + tabPad, plotY1 - 10); 
    runningX = tabRight[col]; 
  } 
} 

// For mouse rollover
public void drawDataHighlight(int col) { 
  for (int row = 0; row < rowCount; row++) { 
    if (data.isValid(row, col)) { 
      float value = data.getFloat(row, col); 
      float x = map(row, 0, rowCount, plotX1, plotX2); 
      float y = map(value, dataMin, dataMax, plotY2, plotY1); 
      if (dist(mouseX, mouseY, x, y) < 3) { 
        strokeWeight(10); 
        point(x, y); 
        fill(0); 
        textSize(10); 
        textAlign(CENTER); 
        text(nf(value, 0, 2) + " (" + months[row] + ")", x, y-8); 
      } 
    } 
  } 
} 


public void mousePressed() { 
  if (mouseY > tabTop && mouseY < tabBottom) { 
    for (int col = 0; col < columnCount; col++) { 
      if (mouseX > tabLeft[col] && mouseX < tabRight[col]) { 
        setCurrent(col); 
      } 
    } 
  } 
} 

public void setCurrent(int col) { 
  currentColumn = col; 
  for (int row = 0; row < rowCount; row++) { 
    interpolators[row].target(data.getFloat(row, col)); 
  } 
} 

public void drawAxisLabels() { 
  fill(0); 
  textSize(13); 
  textLeading(15); 
  textAlign(CENTER, CENTER); 
  text("Average\nRetail Price\n(cents per kW\u00b7h)", labelX, (plotY1+plotY2)/2); 
  textAlign(CENTER); 
  text("Month and Year", (plotX1+plotX2)/2, labelY); 
} 

public void drawYearLabels() { 
  fill(0); 
  textSize(10); 
  textAlign(CENTER); 
  // Use thin, gray lines to draw the grid 
  stroke(224); 
  strokeWeight(0.5f); 

for (int row = 0; row < rowCount; row++) { 
    if (row % monthInterval == 0) { 
      float x = map(row, 0, rowCount, plotX1, plotX2); 
      text(months[row], x, plotY2 + textAscent() + 10); 
      line(x, plotY1, x, plotY2); 
    } 
  } 
} 

public void drawVolumeLabels() { 
  fill(0); 
  textSize(10); 
  textAlign(RIGHT); 
  stroke(128); 
  strokeWeight(1); 
  for (float v = dataMin; v <= dataMax; v += volumeIntervalMinor) { 
    if (v % volumeIntervalMinor == 0) { // If a tick mark 
      float y = map(v, dataMin, dataMax, plotY2, plotY1); 
      if (v % volumeInterval == 0) { // If a major tick mark 
        float textOffset = textAscent()/2; // Center vertically 
        if (v == dataMin) { 
          textOffset = 0; // Align by the bottom 
        } else if (v == dataMax) { 
          textOffset = textAscent(); // Align by the top 
        } 
        text(floor(v), plotX1 - 10, y + textOffset); 
        line(plotX1 - 4, y, plotX1, y); // Draw major tick 
      } else { 
//        line(plotX1 - 2, y, plotX1, y); // Draw minor tick 
      } 
    } 
  } 
} 

// Draw the data as a series of points 
public void drawDataPoints(int col) { 
  int rowCount = data.getRowCount(); 
  for (int row = 0; row < rowCount; row++) { 
    if (data.isValid(row, col)) { 
      float value = interpolators[row].value; 
      float x = map(row, 0, rowCount, plotX1, plotX2); 
      float y = map(value, dataMin, dataMax, plotY2, plotY1); 
      point(x, y); 
    } 
  } 
} 

// Draw the data as a line graph
public void drawDataLine(int col) { 
  beginShape(); 
  int rowCount = data.getRowCount(); 
  for (int row = 0; row < rowCount; row++) { 
    if (data.isValid(row, col)) { 
      float value = interpolators[row].value;  
      float x = map(row, 0, rowCount, plotX1, plotX2); 
      float y = map(value, dataMin, dataMax, plotY2, plotY1); 
      vertex(x, y); 
    } 
  } 
  endShape(); 
} 

// Draw the data as a curved line graph
public void drawDataCurve(int col) { 
  beginShape(); 
  for (int row = 0; row < rowCount; row++) { 
    if (data.isValid(row, col)) { 
      float value = interpolators[row].value;  
      float x = map(row, 0, rowCount, plotX1, plotX2); 
      float y = map(value, dataMin, dataMax, plotY2, plotY1); 
      curveVertex(x, y); 
      // double the curve points for the start and stop 
      if ((row == 0) || (row == rowCount-1)) { 
        curveVertex(x, y); 
      } 
    } 
  } 
  endShape(); 
} 

// Draw the data as a bar chart
public void drawDataBars(int col) { 
  noStroke(); 
  rectMode(CORNERS); 
  for (int row = 0; row < rowCount; row++) { 
    if (data.isValid(row, col)) { 
      float value = interpolators[row].value;  
      float x = map(row, 0, rowCount, plotX1, plotX2); 
      float y = map(value, dataMin, dataMax, plotY2, plotY1); 
      rect(x-barWidth/2, y, x+barWidth/2, plotY2); 
    } 
  } 
} 


// Draw the data as an area plot
public void drawDataArea(int col) { 
  beginShape(); 
  for (int row = 0; row < rowCount; row++) { 
    if (data.isValid(row, col)) { 
      float value = interpolators[row].value; 
      float x = map(row, 0, rowCount, plotX1, plotX2); 
      float y = map(value, dataMin, dataMax, plotY2, plotY1); 
      vertex(x, y); 
    } 
  } 
  vertex(plotX2, plotY2); 
  vertex(plotX1, plotY2); 
  endShape(CLOSE); 
} 
// first line of the file should be the column headers
// first column should be the row titles
// all other values are expected to be floats
// getFloat(0, 0) returns the first data value in the upper lefthand corner
// files should be saved as "text, tab-delimited"
// empty rows are ignored
// extra whitespace is ignored


class FloatTable {
  int rowCount;
  int columnCount;
  float[][] data;
  String[] rowNames;
  String[] columnNames;
  
  
  FloatTable(String filename) {
    String[] rows = loadStrings(filename);
    
    String[] columns = split(rows[0], TAB);
    columnNames = subset(columns, 1); // upper-left corner ignored
    scrubQuotes(columnNames);
    columnCount = columnNames.length;

    rowNames = new String[rows.length-1];
    data = new float[rows.length-1][];

    // start reading at row 1, because the first row was only the column headers
    for (int i = 1; i < rows.length; i++) {
      if (trim(rows[i]).length() == 0) {
        continue; // skip empty rows
      }
      if (rows[i].startsWith("#")) {
        continue;  // skip comment lines
      }

      // split the row on the tabs
      String[] pieces = split(rows[i], TAB);
      scrubQuotes(pieces);
      
      // copy row title
      rowNames[rowCount] = pieces[0];
      // copy data into the table starting at pieces[1]
      data[rowCount] = parseFloat(subset(pieces, 1));

      // increment the number of valid rows found so far
      rowCount++;      
    }
    // resize the 'data' array as necessary
    data = (float[][]) subset(data, 0, rowCount);
  }
  
  
  public void scrubQuotes(String[] array) {
    for (int i = 0; i < array.length; i++) {
      if (array[i].length() > 2) {
        // remove quotes at start and end, if present
        if (array[i].startsWith("\"") && array[i].endsWith("\"")) {
          array[i] = array[i].substring(1, array[i].length() - 1);
        }
      }
      // make double quotes into single quotes
      array[i] = array[i].replaceAll("\"\"", "\"");
    }
  }
  
  
  public int getRowCount() {
    return rowCount;
  }
  
  
  public String getRowName(int rowIndex) {
    return rowNames[rowIndex];
  }
  
  
  public String[] getRowNames() {
    return rowNames;
  }

  
  // Find a row by its name, returns -1 if no row found. 
  // This will return the index of the first row with this name.
  // A more efficient version of this function would put row names
  // into a Hashtable (or HashMap) that would map to an integer for the row.
  public int getRowIndex(String name) {
    for (int i = 0; i < rowCount; i++) {
      if (rowNames[i].equals(name)) {
        return i;
      }
    }
    //println("No row named '" + name + "' was found");
    return -1;
  }
  
  
  // technically, this only returns the number of columns 
  // in the very first row (which will be most accurate)
  public int getColumnCount() {
    return columnCount;
  }
  
  
  public String getColumnName(int colIndex) {
    return columnNames[colIndex];
  }
  
  
  public String[] getColumnNames() {
    return columnNames;
  }


  public float getFloat(int rowIndex, int col) {
    // Remove the 'training wheels' section for greater efficiency
    // It's included here to provide more useful error messages
    
    // begin training wheels
    if ((rowIndex < 0) || (rowIndex >= data.length)) {
      throw new RuntimeException("There is no row " + rowIndex);
    }
    if ((col < 0) || (col >= data[rowIndex].length)) {
      throw new RuntimeException("Row " + rowIndex + " does not have a column " + col);
    }
    // end training wheels
    
    return data[rowIndex][col];
  }
  
  
  public boolean isValid(int row, int col) {
    if (row < 0) return false;
    if (row >= rowCount) return false;
    //if (col >= columnCount) return false;
    if (col >= data[row].length) return false;
    if (col < 0) return false;
    return !Float.isNaN(data[row][col]);
  }
  
  
  public float getColumnMin(int col) {
    float m = Float.MAX_VALUE;
    for (int i = 0; i < rowCount; i++) {
      if (!Float.isNaN(data[i][col])) {
        if (data[i][col] < m) {
          m = data[i][col];
        }
      }
    }
    return m;
  }

  
  public float getColumnMax(int col) {
    float m = -Float.MAX_VALUE;
    for (int i = 0; i < rowCount; i++) {
      if (isValid(i, col)) {
        if (data[i][col] > m) {
          m = data[i][col];
        }
      }
    }
    return m;
  }

  
  public float getRowMin(int row) {
    float m = Float.MAX_VALUE;
    for (int i = 0; i < columnCount; i++) {
      if (isValid(row, i)) {
        if (data[row][i] < m) {
          m = data[row][i];
        }
      }
    }
    return m;
  } 

  
  public float getRowMax(int row) {
    float m = -Float.MAX_VALUE;
    for (int i = 1; i < columnCount; i++) {
      if (!Float.isNaN(data[row][i])) {
        if (data[row][i] > m) {
          m = data[row][i];
        }
      }
    }
    return m;
  }
  
  
  public float getTableMin() {
    float m = Float.MAX_VALUE;
    for (int i = 0; i < rowCount; i++) {
      for (int j = 0; j < columnCount; j++) {
        if (isValid(i, j)) {
          if (data[i][j] < m) {
            m = data[i][j];
          }
        }
      }
    }
    return m;
  }

  
  public float getTableMax() {
    float m = -Float.MAX_VALUE;
    for (int i = 0; i < rowCount; i++) {
      for (int j = 0; j < columnCount; j++) {
        if (isValid(i, j)) {
          if (data[i][j] > m) {
            m = data[i][j];
          }
        }
      }
    }
    return m;
  }
}
int NUMSTEPS = 35;

class Integrator {

  float value, _start, _target;
  int _t;
  
  final int NUM_STEPS = NUMSTEPS;
  final float STEP_SIZE = 1.0f / (float)(NUM_STEPS);
  float _normalization;

  boolean _targeting;

  Integrator(float value) {
    value = value;
    _t = 0;
    _targeting = true;
    _target = value;
    
    // compute the normalization variable
    float total = 0.0f;
    for ( int i = 0; i <= NUM_STEPS; i++ ) {
      total += f( (float)i*STEP_SIZE );
    }
    _normalization = 1.0f/total;
  }
  
  public float f( float x ) {
   return (1.0f - (2.0f*x-1.0f)*(2.0f*x-1.0f)); 
   //return 1.0;
  }

  public void update() {
    if ( _targeting ) {
      value += f( (float)_t*STEP_SIZE )*_normalization*( _target - _start );
      ++_t;
      
      if ( _t > NUM_STEPS ) {
        noTarget();
      }
    }    
  }

  public float value() {
    return value; }

  public void target(float t) {
    _start = value;
    _t = 0;
    _targeting = true;
    _target = t;
  }


  public void noTarget() {
    _targeting = false;
  }
}

class ColorIntegrator {

  int value, _start, _target;
  int _t;
  float _time;
  
  final int NUM_STEPS = NUMSTEPS;
  final float STEP_SIZE = 1.0f / (float)(NUM_STEPS);
  float _normalization;

  boolean _targeting;

  ColorIntegrator(int value) {
    value = value;
    _t = 0;
    _time = 0.0f;
    
    // compute the normalization variable
    float total = 0.0f;
    for ( int i = 0; i <= NUM_STEPS; i++ ) {
      total += f( (float)i*STEP_SIZE );
    }
    _normalization = 1.0f/total;
  }
  
  public float f( float x ) {
   return (1.0f - (2.0f*x-1.0f)*(2.0f*x-1.0f)); 
   //return 1.0;
  }

  public void update() {
    if ( _targeting ) {
      //value += color(f( (float)_t*STEP_SIZE )*_normalization*( _target - _start ));
      _time += f( (float)_t*STEP_SIZE )*_normalization;
      value = lerpColor( _start, _target, _time );
      ++_t;
      
      if ( _t > NUM_STEPS ) {
        noTarget();
      }
    }    
  }

  public int value() {
    return value; }

  public void target(int t) {
    _start = value;
    _t = 0;
    _targeting = true;
    _target = t;
    _time = 0.0f;
  }


  public void noTarget() {
    _targeting = false;
  }
}

  static public void main(String args[]) {
    PApplet.main(new String[] { "--bgcolor=#ffffff", "timeseries_sketch" });
  }
}
