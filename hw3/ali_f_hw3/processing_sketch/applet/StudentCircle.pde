class StudentCircle extends Linkable
{
  private float centerX, centerY, diameter;
  private Student student;
  
  public StudentCircle(Student student, float centerX, float centerY, float diameter)
  {
    super();
    this.student = student;
    this.centerX = centerX;
    this.centerY = centerY;
    this.diameter = diameter;
  }
  
  public void draw()
  {
    int circleColor = settings.getColorForExperienceLevel(student.getExperience());
    fill(circleColor);
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
    
    ellipseMode(CENTER);
    ellipse(centerX, centerY, diameter, diameter);
  }
  
  public void drawToolTip()
  {
    String tooltipText = getExperience().getDescription() + " of experience" + "\n"
                       + "Comfort level of " + getComfortLevel() + "\n"
                       + "Codes " + student.getCodingFrequency().toLowerCase() + "\n"
                       + "Concentrating in " + student.getConcentration() + "\n"
                       + "Uses " + student.getOperatingSystem();

    float tooltipWidth = textWidth(tooltipText) + 10;
    float tooltipHeight = tooltipText.split("\n").length * 17 + 10;
    
    stroke(#888888);
    strokeWeight(1);
    fill(120, 0, 120);
    rectMode(CORNER);
    textFont(settings.tooltipFont);
    
    float tooltipTopLeftX = mouseX+5+tooltipWidth > chartBottomRightX ? mouseX-5-tooltipWidth : mouseX+5;
    float toolipTopLeftY = mouseY+5+tooltipHeight > chartBottomRightY ? mouseY-5-tooltipHeight : mouseY+5;
    
    rect(tooltipTopLeftX, toolipTopLeftY, tooltipWidth, tooltipHeight);
    fill(#FFFFFF);
    textAlign(LEFT);
    text(tooltipText, tooltipTopLeftX + 5, toolipTopLeftY + settings.tooltipFont.size * 2);
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
  
  // ^ Implements Linkable.isMouseOver()
  public boolean isMouseOver()
  {
    return dist(mouseX, mouseY, centerX, centerY) < diameter/2;
  }
  
  public int getComfortLevel()
  {
   return student.getComfortLevel(); 
  }
  
  public Experience getExperience()
  {
    return student.getExperience();
  }
  
  public String getProgrammingLanguage()
  {
    return student.getProgrammingLanguage();
  }
  
}
