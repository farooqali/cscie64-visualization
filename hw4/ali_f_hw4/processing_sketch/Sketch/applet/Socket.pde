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
    
    stroke(#888888);
    strokeWeight(1);
    fill(120, 0, 120);
    rectMode(CORNER);
    textFont(settings.tooltipFont);
    
    float tooltipTopLeftX = mouseX+5+tooltipWidth > chartBottomRightX ? mouseX-5-tooltipWidth : mouseX+5;
    float toolipTopLeftY = mouseY+5+tooltipHeight > chartBottomRightY-70 ? mouseY-5-tooltipHeight : mouseY+5;
    
    rect(tooltipTopLeftX, toolipTopLeftY, tooltipWidth, tooltipHeight);

    //draw text
    fill(#FFFFFF);
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
