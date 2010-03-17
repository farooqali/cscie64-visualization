class Settings
{
  private Hashtable comfortLevelDiameters;
  private Hashtable experienceLevelColors;
  public int backgroundColor = #FFFFFF;
  public int comfortCircleHighlightStrokeColor = #FF0000;
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
    experienceLevelColors.put(Experience.OVER_THREE_YEARS, #225ea8);
    experienceLevelColors.put(Experience.ONE_TO_THREE_YEARS, #41b6c3);
    experienceLevelColors.put(Experience.BETWEEN_SIX_MONTHS_AND_ONE_YEAR, #a1dab4);
    experienceLevelColors.put(Experience.LESS_THAN_SIX_MONTHS, #ffffcc);
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
