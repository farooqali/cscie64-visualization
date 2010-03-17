public class Legend
{
  private java.util.List items;
  
  public Legend()
  {
    items = new java.util.ArrayList();
  }
  
  public void addItem(String label, Socket[] sockets, int itemColor)
  {
    items.add(new LegendItem(label, sockets, itemColor));
  }
  
  public void draw()
  {
    textFont(settings.regularFont);
    int rectHeight = items.size() * 30 + 5;
    int rectWidth = 300;
    int topLeftX = 19;
    int topLeftY = chartBottomRightY - rectHeight - 23;
    fill(#333333);
    stroke(#FFFFFF);
    strokeWeight(1);
    rectMode(CORNER);
    rect(topLeftX, topLeftY, rectWidth, rectHeight);
    fill(#FFFFFF);
    text("Legend", topLeftX + 10, topLeftY + 20);
    int trailingY = topLeftY + 10;
    for(int i = 0; i < sockets.length; i++)
      sockets[i].setIsHighlighted(false);    
    for(int i = 0; i < items.size(); i++)
    {
      LegendItem item = (LegendItem) items.get(i);
      item.drawRelativeToLegend(topLeftX, trailingY);
      trailingY += 28;
    }
  }
}

public class LegendItem
{
  public String label;
  public Socket[] sockets;
  public int itemColor;
  int boxTopLeftX, boxTopLeftY;
  
  public LegendItem(String label, Socket[] sockets, int itemColor)
  {
    this.label = label;
    this.sockets = sockets;
    this.itemColor = itemColor;
  }
  
  public void drawRelativeToLegend(int topLeftX, int trailingY)
  {
    boxTopLeftX = topLeftX + 10;
    boxTopLeftY = trailingY;
    
    if(this.isMouseOver())
    {
      strokeWeight(2);
      stroke(settings.highlightColor);
      setHighlightingForAllSocketsTo(true);
    }
    else if(isMouseOverLinkedItem())
    {
      strokeWeight(2);
      stroke(settings.highlightColor);
    }
    else
    {
      strokeWeight(1);
      stroke(#FFFFFF);
    }
    
    fill(itemColor);
    rect(boxTopLeftX, boxTopLeftY, 100, 20);
    text(label, boxTopLeftX + 100 + 5, boxTopLeftY + 15);
  }
  
  public void setHighlightingForAllSocketsTo(boolean isHighlighted)
  {
    String x = "";
    for(int i = 0; i < this.sockets.length; i++)
    {
      this.sockets[i].setIsHighlighted(isHighlighted);
      x += "," + this.sockets[i].id;
    }
  }
  
  public boolean isMouseOver()
  {   
    return mouseX > boxTopLeftX && mouseX < boxTopLeftX+100 && mouseY > boxTopLeftY && mouseY < boxTopLeftY + 20;
  }
  
  public boolean isMouseOverLinkedItem()
  {
    for(int i = 0; i < this.sockets.length; i++)
      if(this.sockets[i].isMouseOver())
        return true;
    return false;
  }
}
