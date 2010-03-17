class ComfortLevelLegendCircle extends Linkable
{
  private float centerX, centerY, diameter;
  private int comfortLevel;
  
  public ComfortLevelLegendCircle(int comfortLevel, float centerX, float centerY, float diameter)
  {
    super();
    this.comfortLevel = comfortLevel;
    this.centerX = centerX;
    this.centerY = centerY;
    this.diameter = diameter;
  }
  
  public void draw()
  {  
    fill(85, 142, 213);
    if(shouldHighlight())
    {
      strokeWeight(3);
      stroke(settings.comfortCircleHighlightStrokeColor);
    }
    else
    {
      strokeWeight(1);
      stroke(#888888);
    }
    ellipse(centerX, centerY, diameter, diameter);
    fill(0);
  }
  
  // ^ Implements Linkable.isMouseOver()
  public boolean isMouseOver()
  {
    return dist(mouseX, mouseY, centerX, centerY) < diameter/2;
  }
  
  public int getComfortLevel()
  {
   return comfortLevel; 
  }
}
