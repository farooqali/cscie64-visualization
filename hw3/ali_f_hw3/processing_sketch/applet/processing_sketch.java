import processing.core.*; 
import processing.xml.*; 

import junit.extensions.*; 
import junit.swingui.*; 
import junit.textui.*; 
import junit.runner.*; 
import junit.framework.*; 
import junit.awtui.*; 

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

public class processing_sketch extends PApplet {

Survey survey;
String[] languages;
Settings settings;
PFont plotFont;
int canvasX = 0;
int canvasY = 10;
int languageLabelY = canvasY + 10;
float legendWidth = 200;
float legendMarginRight = 10;
float legendX = 1000 - legendWidth - legendMarginRight;
StudentCircle[] studentCircles;
float chartBottomRightX = legendX - legendMarginRight;
float chartBottomRightY = 830;

public void setup()
{
  survey = new Survey(loadStrings("survey.csv"));
  languages = survey.getLanguages();
  settings = new Settings();
  studentCircles = new StudentCircle[survey.getStudents().length];
  size(1000, 830);
  plotFont = createFont("SansSerif", 12);
  textFont(plotFont);
  createStudentCircles();
}

public void draw() {
  background(settings.backgroundColor);
  smooth();
  drawChart();
  drawLegend();
}

public void drawChart()
{
  textAlign(CENTER);
  for(int i = 0; i < languages.length; i++)
  {
    // Language label
    String language = languages[i];
    float languageLabelX = getLanguageLabelXForLanguageIndex(i);
    fill(0);
    text(language, languageLabelX, languageLabelY);
  }
  
  for(int j = 0; j < studentCircles.length; j++)
    studentCircles[j].draw();
  
  for(int j = 0; j < studentCircles.length; j++)
    if(studentCircles[j].isMouseOver())
      studentCircles[j].drawToolTip();
}

public float getLanguageLabelXForLanguageIndex(int i)
{
  return (i*65) + 25;
}

public void drawLegend()
{
  // Legend container
  int legendY = languageLabelY;
  noStroke();
  fill(0xffEEEEEE);
  rectMode(CORNER);
  rect(legendX, legendY, legendWidth, (languages.length * 20) + 100);
  float paddingTop = 5;
  float runningY = legendY + 15;
  
  // Programming experience legend
  textAlign(CENTER);
  fill(0);
  text("Programming Experience", legendX + (legendWidth / 2), runningY);
  runningY += 10;
  
  for(int i = 0; i < Experience.ALL.length; i++)
  {
    Experience experience = Experience.ALL[i];
    float rectTopLeftX = legendX + 5;
    float rectTopLeftY = runningY;
    float rectBottomRightX = legendX + legendWidth - 5;
    float rectBottomRightY = runningY + 20;
    ExperienceLegendBox experienceBox = new ExperienceLegendBox(experience, rectTopLeftX, rectTopLeftY, rectBottomRightX, rectBottomRightY);

    //cycle through all previously created circles and establish links if applicable
    for(int studentCircleIndex = 0; studentCircleIndex < studentCircles.length; studentCircleIndex++)
    {
      StudentCircle studentCircle = studentCircles[studentCircleIndex];
      if(experienceBox.getExperience().equals(studentCircle.getExperience()))
           experienceBox.linkTo(studentCircle);
    }
    experienceBox.draw();
    runningY += 25;
  }
  
  // Section break
  strokeWeight(6);
  stroke(settings.backgroundColor);
  line(legendX, runningY, legendX + legendWidth, runningY);
  runningY += 20;
  
  // Programming comfort level legend
  textAlign(CENTER);
  fill(0);
  text("Programming Comfort Level", legendX + (legendWidth / 2), runningY);
  runningY += 30;
  
  for(int comfortLevel = 5; comfortLevel >= 1; comfortLevel--)
  {
    float comfortLevelLegendCircleX = legendX + (legendWidth / 2);
    float comfortLevelLegendCircleY = runningY;
    float comfortLevelLegendCircleDiameter = settings.getDiameterForComfortLevel(comfortLevel);
    ComfortLevelLegendCircle legendCircle = new ComfortLevelLegendCircle(comfortLevel, comfortLevelLegendCircleX, comfortLevelLegendCircleY, comfortLevelLegendCircleDiameter);
    
    //cycle through all previously created circles and establish links if applicable
    for(int studentCircleIndex = 0; studentCircleIndex < studentCircles.length; studentCircleIndex++)
    {
      StudentCircle studentCircle = studentCircles[studentCircleIndex];
      if(legendCircle.getComfortLevel() == studentCircle.getComfortLevel())
           legendCircle.linkTo(studentCircle);
    }
    legendCircle.draw();
    text(comfortLevel, legendX + (legendWidth / 2) - 20, runningY + 5);
    runningY += comfortLevelLegendCircleDiameter + 15;
  }
}

public void createStudentCircles()
{
  int studentCircleIndex = 0;
  for(int i = 0; i < languages.length; i++)
  {
    String language = languages[i];
    // Circles for students using language
    Student[] students = survey.getStudentsUsingProgrammingLanguage(language);
    float columnMiddleX = getLanguageLabelXForLanguageIndex(i);
    float runningCircleY = languageLabelY + 12;
    for(int studentIdx = 0; studentIdx < students.length; studentIdx++, studentCircleIndex++)
    {
      // Circle for student
      Student student = students[studentIdx];
      int comfortLevel = student.getComfortLevel();
      float circleDiameter = settings.getDiameterForComfortLevel(comfortLevel);
      float circleCenterY = runningCircleY + (circleDiameter/2);
      StudentCircle studentCircle = new StudentCircle(student, columnMiddleX, circleCenterY, circleDiameter);
      studentCircles[studentCircleIndex] = studentCircle;
      runningCircleY += (circleDiameter) + 5;
      
      //cycle through all previously created circles and establish links if applicable
      for(int circleIndex = 0; circleIndex < studentCircleIndex; circleIndex++)
      {
        StudentCircle ithCircle = studentCircles[circleIndex];
        if(studentCircle != null && 
           ithCircle.getComfortLevel() == studentCircle.getComfortLevel() &&
           ithCircle.getExperience().equals(studentCircle.getExperience()) &&
           ithCircle.getProgrammingLanguage().equals(studentCircle.getProgrammingLanguage()))
             studentCircle.linkTo(ithCircle);
      }
    }
  }
}


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
      stroke(0xff888888);
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
abstract class Linkable
{
  private java.util.List links;
  
  public Linkable()
  {
    links = new java.util.ArrayList(); 
  }

  public boolean shouldHighlight()
  {
    if(isMouseOver()) return true;
    for(int i = 0; i < links.size(); i++)
      if(((Linkable) links.get(i)).isMouseOver()) return true;
    return false;
  }

  public void linkTo(Linkable link)
  {
    if(!links.contains(link))
    {
      links.add(link);
      link.linkTo(this);
    }
  }

  public abstract boolean isMouseOver();
}
class Settings
{
  private Hashtable comfortLevelDiameters;
  private Hashtable experienceLevelColors;
  public int backgroundColor = 0xffFFFFFF;
  public int comfortCircleHighlightStrokeColor = 0xffFF0000;
  public PFont tooltipFont = createFont("SansSerif", 10);
  
  public Settings()
  {
    comfortLevelDiameters = new Hashtable();
    comfortLevelDiameters.put(1, 4);
    comfortLevelDiameters.put(2, 8);
    comfortLevelDiameters.put(3, 16);
    comfortLevelDiameters.put(4, 20);
    comfortLevelDiameters.put(5, 25);
    
    experienceLevelColors = new Hashtable();
    experienceLevelColors.put(Experience.OVER_THREE_YEARS, 0xff225ea8);
    experienceLevelColors.put(Experience.ONE_TO_THREE_YEARS, 0xff41b6c3);
    experienceLevelColors.put(Experience.BETWEEN_SIX_MONTHS_AND_ONE_YEAR, 0xffa1dab4);
    experienceLevelColors.put(Experience.LESS_THAN_SIX_MONTHS, 0xffffffcc);
  }
  
  public int getDiameterForComfortLevel(int comfortLevel)
  {
    return ((Integer) comfortLevelDiameters.get(comfortLevel)).intValue();
  }
  
  public int getColorForExperienceLevel(Experience experience)
  {
    return ((Integer) experienceLevelColors.get(experience)).intValue();
  }
}
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
      stroke(0xff888888);
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
    
    stroke(0xff888888);
    strokeWeight(1);
    fill(120, 0, 120);
    rectMode(CORNER);
    textFont(settings.tooltipFont);
    
    float tooltipTopLeftX = mouseX+5+tooltipWidth > chartBottomRightX ? mouseX-5-tooltipWidth : mouseX+5;
    float toolipTopLeftY = mouseY+5+tooltipHeight > chartBottomRightY ? mouseY-5-tooltipHeight : mouseY+5;
    
    rect(tooltipTopLeftX, toolipTopLeftY, tooltipWidth, tooltipHeight);
    fill(0xffFFFFFF);
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

  static public void main(String args[]) {
    PApplet.main(new String[] { "--bgcolor=#ffffff", "processing_sketch" });
  }
}
