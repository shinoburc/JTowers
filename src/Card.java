package org.dyndns.dandydot.jtowers;

public interface Card {
    public CardStack getParentCardStack();
    public void setSuit(int suit);
    public int getSuit();
    public void setNumber(int number);
    public int getNumber();
    public int getWidth();
    public int getHeight();
    public void setLocation(int x,int y);
    public String getPosition();
    public void reset();
    public void movedUpdate();
    public void move(CardStack fromPanel,CardStack toPanel);
    public boolean moveContentPanel(Card card);
    public boolean moveLeftPanel(Card card,boolean fillPiles);
    public boolean moveRightPanel(Card card);
}
