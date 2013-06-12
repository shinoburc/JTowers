package org.dyndns.dandydot.jtowers;

import java.util.HashMap;

import javax.swing.JLabel;

public class CardFactory {
    private HashMap<String, Card> pool = new HashMap<String, Card>();
    private static CardFactory singleton = new CardFactory();

    private CardFactory() {
    }

    public static CardFactory getInstance() {
        return singleton;
    }
    
    public synchronized Card getCard(int suit,int number) {
        Card card = pool.get("" + suit + number);
        if (card == null) {
            card = new JCard(suit,number);
            ((JLabel)card).setDoubleBuffered(true);
            pool.put("" + suit + number,card);
        }
        return card;
    }
}
