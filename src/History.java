package org.dyndns.dandydot.jtowers;

import java.util.Stack;

public class History {
    private static History singleton = new History();
    private Stack<CardStack[]> history;


    private History(){
        history = new Stack<CardStack[]>();
    }

    public static History getInstance(){
        return singleton;
    }

    public void move(CardStack fromPanel,CardStack toPanel){
        CardStack event[] = new CardStack[2];
        event[0] = fromPanel;
        event[1] = toPanel;
        history.push(event);
    }

    public void undo(){
        if(history.empty()){
            return;
        }
        CardStack event[] = history.pop();
        Card card = event[1].pop();
        if(card != null){
            event[0].push(card);
            card.movedUpdate();
        }
    }

    public void clear(){
        history.clear();
    }

}
