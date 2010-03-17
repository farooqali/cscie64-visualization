class ExperienceLegendBox extends Linkable
{
  private float topLeftX, topLeftY, bottomRightX, bottomRightY;
  private Experience experience;
  
  public ExperienceLegendBox(Experience experience, float topLeftX, float topLeftY, float bottomRightX, float bottomRightY)
  {
    super();
    this.experience = experience;
    this.topLeftX = topLeftX;
    this.topLeftY = topLeftY;
    this.bottomRightX = bottomRightX;
    this.bottomRightY = bottomRightY;
  }
  
  public void draw()
  {  
    if(shouldHighlight())
    {
      strokeWeight(3);
      stroke(settings.comfortCircleHighlightStrokeColor);
    }
    else
    {
      noStroke();
    }
    fill(settings.getColorForExperienceLevel(experience));
    rectMode(CORNERS);
    rect(topLeftX, topLeftY, bottomRightX, bottomRightY);
    fill(0);
    textAlign(LEFT);
    text(experience.getDescription(), topLeftX + 5, topLeftY + 15);
  }
  
  // ^ Implements Linkable.isMouseOver()
  public boolean isMouseOver()
  {
    return (mouseX >= topLeftX && mouseX <= bottomRightX) && (mouseY >= topLeftY && mouseY <= bottomRightY);
  }
  
  public Experience getExperience()
  {
   return experience;
  }
}
