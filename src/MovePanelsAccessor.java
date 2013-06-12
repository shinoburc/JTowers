package org.dyndns.dandydot.jtowers;

import java.awt.Point;

import javax.swing.JPanel;

public class MovePanelsAccessor {
    private static MovePanelsAccessor singleton = new MovePanelsAccessor();
    private JPanel leftPanel = null;
    private JPanel rightPanel = null;
    private CardStack[] content = null;
    private Point rootLocation = null;

    private MovePanelsAccessor() {
    }

    public static MovePanelsAccessor getInstance(){
        return singleton;
    }

    public void setLeftPanel(JPanel leftPanel){
        this.leftPanel = leftPanel;
    }

    public JPanel getLeftPanel(){
        return leftPanel;
    }

    public void setRightPanel(JPanel rightPanel){
        this.rightPanel = rightPanel;
    }

    public JPanel getRightPanel(){
        return rightPanel;
    }

    public void setContentPanel(CardStack[] content){
        this.content = content;
    }

    public CardStack[] getContentPanel(){
        return this.content;
    }

    public void setRootLocation(Point rootLocation){
        this.rootLocation = rootLocation;
    }

    public Point getRootLocation(){
        return this.rootLocation;
    }
    
    public void allRepaint(){
        leftPanel.repaint();
        rightPanel.repaint();
    }
}
