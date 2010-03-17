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

void setup()
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

void draw() {
  background(settings.backgroundColor);
  smooth();
  drawChart();
  drawLegend();
}

void drawChart()
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

float getLanguageLabelXForLanguageIndex(int i)
{
  return (i*65) + 25;
}

void drawLegend()
{
  // Legend container
  int legendY = languageLabelY;
  noStroke();
  fill(#EEEEEE);
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

void createStudentCircles()
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


