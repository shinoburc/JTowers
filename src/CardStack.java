package org.dyndns.dandydot.jtowers;

import java.awt.Point;

public interface CardStack {
    public void setMaxSize(int maxSize);
    public int getMaxSize();
    public String getCardPosition();
    public void push(Card card);
    public void pushReserve(Card card);
    public boolean canPush();
    public Card pop();
    public Card pop(Card card);
    public boolean canPop(Card card);
    public Card getLastCard();
    public Point getLastCardLocation();
    public Point getLocation();
}
