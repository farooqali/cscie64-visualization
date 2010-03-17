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
float volumeIntervalMinor = 0.5;
float[] tabLeft, tabRight; 
float tabTop, tabBottom; 
float barWidth = 4; 

float tabPad = 10; 
Integrator[] interpolators; 
PFont plotFont; 
void setup() { 
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

void draw() { 
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
  stroke(#5679C1); 
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

void drawTitleTabs() { 
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
void drawDataHighlight(int col) { 
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


void mousePressed() { 
  if (mouseY > tabTop && mouseY < tabBottom) { 
    for (int col = 0; col < columnCount; col++) { 
      if (mouseX > tabLeft[col] && mouseX < tabRight[col]) { 
        setCurrent(col); 
      } 
    } 
  } 
} 

void setCurrent(int col) { 
  currentColumn = col; 
  for (int row = 0; row < rowCount; row++) { 
    interpolators[row].target(data.getFloat(row, col)); 
  } 
} 

void drawAxisLabels() { 
  fill(0); 
  textSize(13); 
  textLeading(15); 
  textAlign(CENTER, CENTER); 
  text("Average\nRetail Price\n(cents per kW·h)", labelX, (plotY1+plotY2)/2); 
  textAlign(CENTER); 
  text("Month and Year", (plotX1+plotX2)/2, labelY); 
} 

void drawYearLabels() { 
  fill(0); 
  textSize(10); 
  textAlign(CENTER); 
  // Use thin, gray lines to draw the grid 
  stroke(224); 
  strokeWeight(0.5); 

for (int row = 0; row < rowCount; row++) { 
    if (row % monthInterval == 0) { 
      float x = map(row, 0, rowCount, plotX1, plotX2); 
      text(months[row], x, plotY2 + textAscent() + 10); 
      line(x, plotY1, x, plotY2); 
    } 
  } 
} 

void drawVolumeLabels() { 
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
void drawDataPoints(int col) { 
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
void drawDataLine(int col) { 
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
void drawDataCurve(int col) { 
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
void drawDataBars(int col) { 
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
void drawDataArea(int col) { 
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
