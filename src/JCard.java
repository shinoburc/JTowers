package org.dyndns.dandydot.jtowers;

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.Timer;



public class JCard extends JLabel implements Card, MouseListener{

    public static final int Spade = 0;
    public static final int Club = 1;
    public static final int Heart = 2;
    public static final int Dia = 3;

    public static final int Blank = 0;

    private String imgDir = "images/middle/";

    private static final long serialVersionUID = 1L;
    private int suit;
    private int number;
    private History history = History.getInstance();

    private JCard movingCard = null;
    private Container content = null;
    private int cardX = 0;
    private int cardY = 0;

    private Timer t = null;

    private boolean fillPiles = false;

    public JCard(){
        setSuit(Blank);
        setNumber(Blank);
        ImageIcon imageIcon = genImageIcon();
        setIcon(imageIcon);
        setSize(imageIcon.getIconWidth(),imageIcon.getIconHeight());
    }

    public JCard(int suit,int number){
        setSuit(suit);
        setNumber(number);
        ImageIcon imageIcon = genImageIcon();
        setIcon(imageIcon);
        setSize(imageIcon.getIconWidth(),imageIcon.getIconHeight());
    }

    private ImageIcon genImageIcon(){
        if(suit == Blank && number == Blank){
            /* for Java Web Start */
            ClassLoader cl = getClass().getClassLoader();
            return new ImageIcon(cl.getResource(imgDir + "00.png"));
        }
        String filename;
        String strSuit = "";
        switch(suit){
        case Spade :
            strSuit = "s";
            break;
        case Club :
            strSuit = "c";
            break;
        case Heart :
            strSuit = "h";
            break;
        case Dia :
            strSuit = "d";
            break;
        }
        if(number < 10){
            filename = imgDir + strSuit + "0" + number + ".png";
        } else {
            filename = imgDir + strSuit + number + ".png";
        }
        /* for Java Web Start */
        ClassLoader cl = getClass().getClassLoader();
        return new ImageIcon(cl.getResource(filename));
    }

    public void setSuit(int suit){
        this.suit = suit;
    }

    public int getSuit(){
        return suit;
    }

    public void setNumber(int number){
        this.number = number;
    }

    public int getNumber(){
        return number;
    }
    
    public int getWidth(){
        return getIcon().getIconWidth();
    }
    
    public int getHeight(){
        return getIcon().getIconHeight();
    }

    public String getPosition() {
        if(getParent() instanceof JRootPane){
            return "left";
        }
        CardStack c = (CardStack)getParent();
        return c.getCardPosition();
    }
    
    public void setFillPiles(boolean fillPiles){
        this.fillPiles = fillPiles;
    }
    
    public boolean getFillPiles(){
        return this.fillPiles;
    }

    public String toString(){
        return "suit = " + suit + ",number = " + number;
    }

    public void reset(){
        if(getMouseListeners().length == 0){
            addMouseListener(this);
        }
        if(t != null){
            if(t.isRunning()){
                t.stop();
            }
        }
    }
    
    public CardStack getParentCardStack(){
        return (CardStack)getParent();
    }

    public void mouseClicked(MouseEvent e) {
    }
    
    public void mousePressed(MouseEvent e) {
        if(getParent() instanceof JRootPane){
            return;
        }
        CardStack c = (CardStack)getParent();
        Card lastCard = c.getLastCard();
        if(lastCard == null){
            return;
        }

        boolean moved = false;
        if(!lastCard.getPosition().equals("left")){
            moved = moveLeftPanel(lastCard,false) ? true :
                    moveContentPanel(lastCard) ? true :
                    moveRightPanel(lastCard) ? true : false;
        }
        if(moved){
            //movedUpdate();
        }
    }

    public void movedUpdate(){
        JPanel c = (JPanel)getParent();
        if(c != null){
            c.repaint();
        }

        Component cc = c.getParent();
        if(cc != null){
            cc.repaint();
        }

        Component ccc = cc.getParent();
        if(ccc != null){
            ccc.repaint();
        }

        Container rc = getRootPane();
        if(rc != null){
            rc.repaint();
        }
    }

    private void cardAnimation(CardStack fromPanel,CardStack toPanel){
        movingCard = this;
        content = getRootPane();

        class TimerListener implements ActionListener{
            private Timer timer = null;
            private boolean running = true;
            private Point currentLocation;
            private Point toLocation;
            private CardStack toPanel;
            private double moveX;
            private double moveY;
            public TimerListener(CardStack fromPanel,CardStack toPanel){
                this.toPanel = toPanel;
                currentLocation = fromPanel.getLastCardLocation();
                toLocation = toPanel.getLastCardLocation();
                fromPanel.pop();
                content.add(movingCard);
                movingCard.setLocation(currentLocation);
                content.setComponentZOrder(movingCard,0);
                moveX = (toLocation.getX() - currentLocation.getX()) / 10;
                moveY = (toLocation.getY() - currentLocation.getY()) / 10;
                toPanel.pushReserve(movingCard);
            }
            public void actionPerformed(ActionEvent e){
                running = true;
                currentLocation.x += moveX;
                currentLocation.y += moveY;
                movingCard.setLocation(currentLocation);
                content.repaint();
                if(Math.abs(currentLocation.getX() - toLocation.getX()) < movingCard.getWidth() / 2 && Math.abs(currentLocation.getY() - toLocation.getY()) < movingCard.getHeight() / 2){
                    running = false;
                    content.remove(movingCard);
                    toPanel.push(movingCard);
                    if(movingCard.getFillPiles()){
                        movingCard.fillPiles();
                    }
                    timer.stop();
                }
            }
            public void setTimer(Timer timer){
                this.timer = timer;
            }
        }
        TimerListener tl = new TimerListener(fromPanel,toPanel);
        t = new Timer(20,tl);
        tl.setTimer(t);
        t.start();
    }

    public void move(CardStack fromPanel,CardStack toPanel){
        
        /* TODO : read animation settings somewhere */
        if(true){
            cardAnimation(fromPanel,toPanel);
        } else {
            fromPanel.pop();
            //setLocation(toPanel.getLocation());
            toPanel.push(this);
            movedUpdate();
        }
        history.move(fromPanel,toPanel);
    }
    
    private void fillPiles(){
        MovePanelsAccessor mpa = MovePanelsAccessor.getInstance();
        Component cmps[] = mpa.getRightPanel().getComponents();
        for(int i = 0;i < cmps.length;i++){
            CardStack csp = (CardStack)cmps[i];
            Card card = csp.getLastCard();

            if(card != null){
                if(card.getPosition().equals("left")){
                    continue;
                }
                if(card.moveLeftPanel(card,true)){
                    break;
                }
            }
        }

        CardStack css[] = mpa.getContentPanel();
        for(int i = 0;i < css.length;i++){
            CardStack csp = css[i];
            Card card = csp.getLastCard();
            if(card != null){
                if(card.getPosition().equals("left")){
                    continue;
                }
                if(card.moveLeftPanel(card,true)){
                    break;
                }
            }
        }
        setFillPiles(false);
    }

    public boolean moveContentPanel(Card card){
        CardStack nonStack = null;

        if(card.getPosition().equals("right")){
            MovePanelsAccessor mpa = MovePanelsAccessor.getInstance();
            CardStack csps[] = mpa.getContentPanel();
            for(int i = 0;i < csps.length;i++){
                CardStack csp = (CardStack)csps[i];
                Card lastCard = csp.getLastCard();
                if(lastCard == null){
                    if(card.getNumber() == 13){
                        nonStack = csp;
                    }
                    continue;
                }
                if(card.getSuit() == lastCard.getSuit() && card.getNumber() == lastCard.getNumber() - 1){
                    CardStack parentPanel = card.getParentCardStack();
                    card.move(parentPanel,csp);
                    return true;
                }
            }
            if(nonStack != null){
                CardStack parentPanel = card.getParentCardStack();
                card.move(parentPanel,nonStack);
                return true;
            }
            return false;
        }
        JPanel c = (JPanel)getParent();

        JPanel p = (JPanel)c.getParent();
        Component cmps[] = p.getComponents();
        for(int i = 0;i < cmps.length;i++){
            CardStack csp = (CardStack)cmps[i];
            Card lastCard = csp.getLastCard();
            if(lastCard == null){
                if(card.getNumber() == 13){
                    nonStack = csp;
                }
                continue;
            }
            if(card.getSuit() == lastCard.getSuit() && card.getNumber() == lastCard.getNumber() - 1){
                CardStack parentPanel = card.getParentCardStack();
                card.move(parentPanel,csp);
                return true;
            }
        }
        if(nonStack != null){
            CardStack parentPanel = card.getParentCardStack();
            card.move(parentPanel,nonStack);
            return true;
        }
        return false;
    }

    public boolean moveLeftPanel(Card card,boolean fillPiles){
        this.fillPiles = fillPiles;
        MovePanelsAccessor mpa = MovePanelsAccessor.getInstance();
        JPanel leftPanel = mpa.getLeftPanel();
        Component cmps[] = leftPanel.getComponents();
            CardStack csp = (CardStack)cmps[card.getSuit()];
            Card lastCard = csp.getLastCard();

            if(lastCard == null){
                return false;
                //continue;
            }
            //if card number = 1
            if((card.getNumber() == 1) && (lastCard.getSuit() == Blank) && (lastCard.getNumber() == Blank)){
                CardStack parentPanel = card.getParentCardStack();
                card.move(parentPanel,csp);

                return true;
            }

            // if (card number - 1) = last card number
            if((card.getSuit() == lastCard.getSuit()) && (card.getNumber() == lastCard.getNumber() + 1)){
                CardStack parentPanel = card.getParentCardStack();
                card.move(parentPanel,csp);

                return true;
            }
        return false;
    }

    public boolean moveRightPanel(Card card){
        MovePanelsAccessor mpa = MovePanelsAccessor.getInstance();
        JPanel rightPanel = mpa.getRightPanel();
        Component cmps[] = rightPanel.getComponents();
        for(int i = 0;i < cmps.length;i++){
            CardStack csp = (CardStack)cmps[i];
            Card lastCard = csp.getLastCard();
            if(lastCard.getSuit() != Blank || lastCard.getNumber() != Blank){
                continue;
            }
            CardStack parentPanel = card.getParentCardStack();
            card.move(parentPanel,csp);

            return true;
        }
        return false;
    }
    public void mouseReleased(MouseEvent e) {
    }
    public void mouseEntered(MouseEvent e) {
    }
    public void mouseExited(MouseEvent e) {
    }
}
