package org.dyndns.dandydot.jtowers;

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.util.Stack;
import java.util.Vector;

import javax.swing.JPanel;

public class JCardStack extends JPanel implements CardStack{
    private static final long serialVersionUID = 1L;
    private Stack<Card> stack = new Stack<Card>();
    private Vector reserve = new Vector();
    private Card lastCard = null;
    private int maxSize = 0;
    private int xOffset = 0;
    private int yOffset = 22;
    private int xPosition = 0;
    private int yPosition = 0;
    private String cardPosition;

    public JCardStack(){
        setLayout(null);
    }
    public JCardStack(int maxSize){
        setLayout(null);
        this.maxSize = maxSize;
    }
    public JCardStack(int xOffset,int yOffset,int maxSize){
        setLayout(null);
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.maxSize = maxSize;
    }

    public JCardStack(int xOffset,int yOffset,int xPosition,int yPosition,int maxSize,String cardPosition){
        setLayout(null);
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.maxSize = maxSize;
        this.cardPosition = cardPosition;
    }


    public void setMaxSize(int maxSize){
        this.maxSize = maxSize;
    }

    public int getMaxSize(){
        return this.maxSize;
    }

    public void setCardPosition(String cardPosition){
        this.cardPosition = cardPosition;
    }

    public String getCardPosition(){
        return this.cardPosition;
    }

    public void push(Card card){
        int reservedCount = 0;
        if(reserve.indexOf(card) != -1){
            reserve.remove(card);
            reservedCount = reserve.size();
        } else {
            if(!canPush() || card == null){
                return;
            }
            stack.push(card);
            lastCard = card;
        }
        add("" + card.getSuit() + card.getNumber(),(JCard)card);

        /* FIXME : pushReserve and push has some problem originated by multi thread */
        if(getCardPosition().equals("left")){
            for(int i = 0; i < stack.size();i++){
                Card c = stack.get(i);
                setComponentZOrder((JCard)c,0);
            }
        } else {
            setComponentZOrder((JCard)card,0);
        }
        card.setLocation(xPosition + (xOffset * (stack.size())),yPosition + (yOffset * (stack.size() - reservedCount)));
    }
    
    public void pushReserve(Card card) {
        if(!canPush() || card == null){
            return;
        }
        stack.push(card);
        reserve.add(card);
        lastCard = card;
    }

    public boolean canPush(){
        if(maxSize < (stack.size() + 1)){
            return false;
        }
        return true;
    }

    public Card pop(){
        if(stack.size() == 0){
            return null;
        }
        JCard lastElement = (JCard)stack.lastElement();
        if(lastElement.getSuit() == JCard.Blank && lastElement.getNumber() == JCard.Blank){
            return null;
        }
        JCard card = (JCard)stack.pop();
        remove(card);
        if(!stack.empty()){
            lastCard = (JCard)stack.lastElement();
        } else {
            lastCard = null;
        }
        card.repaint();
        return card;
    }

    public void moveCard(Card card){
        Container c = getRootPane();
        c.add((JCard)card);
        c.setComponentZOrder((JCard)card,0);

        for(int i = 0;i < 10;i++){
            card.setLocation(i*20,80);
            c.repaint(1);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public Card pop(Card card){
        if(stack.size() == 0){
            return null;
        }
        if(!canPop(card)){
            return null;
        }
        return pop();
    }

    public boolean canPop(Card card){
        if((JCard)stack.lastElement() == card){
            return true;
        } else {
            return false;
        }
    }

    public Card getLastCard(){
        return lastCard;
    }
    
    public Point getLastCardLocation(){
        Point p;
        JCard card = (JCard)getLastCard();
        MovePanelsAccessor mpa = MovePanelsAccessor.getInstance();

        if(card != null){
            p = card.getLocationOnScreen();
            p.x -= mpa.getRootLocation().getX();
            p.y -= mpa.getRootLocation().getY();
            return p;
        }
        
        p = getLocationOnScreen();
        p.x += xPosition + (xOffset * (stack.size()));
        p.y += yPosition + (yOffset * (stack.size()));


        p.x -= mpa.getRootLocation().getX();
        p.y -= mpa.getRootLocation().getY();
        return p;
    }

    public void removeAll(){
        Component cmps[] = getComponents();
        for(int i = 0;i < cmps.length;i++){
            JCard card = (JCard)cmps[i];
            if(card.getSuit() != 0 && card.getNumber() != 0){
                remove(card);
            }
        }
        stack.clear();
        lastCard = null;
    }
}
