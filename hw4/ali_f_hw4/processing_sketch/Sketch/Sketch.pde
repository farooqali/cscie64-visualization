PImage mapImage;
Socket[] sockets;
Legend legend;
Settings settings = new Settings();
int chartBottomRightX = 992;
int chartBottomRightY = 589;

void setup()
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

void draw()
{
  smooth();
  image(mapImage, 0, 0);
  legend.draw();
  for(int i = 0; i < sockets.length; i++)
    sockets[i].draw();
  for(int i = 0; i < sockets.length; i++)
    if(sockets[i].isMouseOver()) sockets[i].drawToolTip();
}

void buildSockets()
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

void buildLinkedHighlightingLegend()
{
  legend = new Legend();
  legend.addItem("Available sockets", getAvailableSockets(),  settings.availableSocketsColor);
  legend.addItem("Unavailable sockets", getTakenSockets(), settings.takenSocketsColor);
}

Socket[] getAvailableSockets()
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

Socket[] getTakenSockets()
{
  java.util.List takenSockets = new java.util.ArrayList();
  for(int i = 0; i < sockets.length; i++)
  {
    if(sockets[i].hasTakenSlots())
      takenSockets.add(sockets[i]);
  }
  return (Socket[]) takenSockets.toArray(new Socket[takenSockets.size()]);
}
