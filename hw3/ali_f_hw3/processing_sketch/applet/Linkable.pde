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
