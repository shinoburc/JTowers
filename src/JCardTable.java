package org.dyndns.dandydot.jtowers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowListener;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.KeyEvent;
import java.awt.Toolkit;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.BorderFactory;

public class JCardTable extends JFrame implements CardTable, WindowListener, ComponentListener, KeyListener, ActionListener, MouseListener  {
    private static final long serialVersionUID = 1L;
    private JPanel cardPanels  = null;
    private JPanel upPanels = null;

    private JPanel leftPanel = null;
    private JPanel rightPanel = null;
    private JLabel messagePanel = null;
    private MovePanelsAccessor mpa = MovePanelsAccessor.getInstance();

    private JLabel movingCard = null;
    private Container content = null;
    private int cardX = 0;
    private int cardY = 0;

    private History history = History.getInstance();

    public JCardTable(String title) {
        super(title);
    }

    public void execute(){
        setJTowersIcon();
        addWindowListener(this);
        this.addComponentListener(this);
        setBackground(Color.WHITE);
        getContentPane().setBackground(Color.WHITE);
        getContentPane().setLayout(null);
        setJMenuBar(genJMenuBar());
        genContent(true);
        messagePanel = new JLabel();
        messagePanel.setText("Have fun!");
        getContentPane().add(messagePanel);
    }

    private void setJTowersIcon(){
        ClassLoader cl = getClass().getClassLoader();
        setIconImage(Toolkit.getDefaultToolkit().createImage(cl.getResource("images/JTowers.jpg")));
    }

    private void genContent(boolean resize){
        if(cardPanels != null){
            getContentPane().remove(cardPanels);

            Component cmps[];
            cmps = leftPanel.getComponents();
            for(int i = 0;i < cmps.length;i++){
                JPanel csp = (JPanel)cmps[i];
                csp.removeAll();
            }
            cmps = rightPanel.getComponents();
            for(int i = 0;i < cmps.length;i++){
                JPanel csp = (JPanel)cmps[i];
                csp.removeAll();
            }
            upPanels.remove(leftPanel);
            upPanels.remove(rightPanel);

            getContentPane().remove(upPanels);
            getContentPane().repaint();
        }
        Iterator it = CardGenerator.randomCardGenerate();
        Card upCard1 = (Card)it.next();
        Card upCard2 = (Card)it.next();
        upCard1.reset();
        upCard2.reset();

        JPanel[] panels = genCardPanels(it);
        cardPanels = new JPanel();

        cardPanels.setLayout(new GridLayout(1,10));
        for(int i = 0;i < panels.length;i++){
            cardPanels.add(panels[i]);
        }

        mpa.setRootLocation(this.getLocation());

        upPanels = new JPanel();
        upPanels.setLayout(new GridLayout(1,2,upCard1.getWidth(),0));
        upPanels.add(genLeftPanel());
        upPanels.add(genRightPanel(upCard1,upCard2));

        getContentPane().add(upPanels);
        getContentPane().add(cardPanels);

        // width = card.width * 10 + 50
        // height = card.height
        upPanels.setBounds(10,10,upCard1.getWidth() * 10 + 50,upCard1.getHeight());
        // width = card.width * 10 + 50
        // height = card.height * 6
        cardPanels.setBounds(10,10,upCard1.getWidth() * 10 + 50,upCard1.getHeight() * 6);

        if(resize){
            setSize(upCard1.getWidth() * 10 + 70,upCard1.getHeight() * 6);
        }
        setVisible(true);
        content = getContentPane();
        movingCard = new JLabel();
        /* for Java Web Start */
        ClassLoader cl = getClass().getClassLoader();
        movingCard.setIcon(new ImageIcon(cl.getResource("images/junbi.png")));
        movingCard.setBounds(cardX,cardY,100,100);
    }

    private JPanel genLeftPanel(){
        leftPanel = new JPanel();
        leftPanel.setLayout(new GridLayout(1,1));
        leftPanel.setBackground(Color.WHITE);
        JCardStack panels[] = new JCardStack[4];
        for(int i = 0;i < 4;i++){
            panels[i] = new JCardStack(0,0,0,0,14,"left");
            panels[i].setBackground(Color.WHITE);
            Card card = new JCard();
            panels[i].push(card);
        }
        for(int i = 0;i < panels.length;i++){
            leftPanel.add(panels[i]);
        }
        mpa.setLeftPanel(leftPanel);
        return leftPanel;
    }

    private JPanel genRightPanel(Card upCard1,Card upCard2){
        rightPanel = new JPanel();
        rightPanel.setLayout(new GridLayout(1,1));
        rightPanel.setBackground(Color.WHITE);
        JCardStack panels[] = new JCardStack[4];
        for(int i = 0;i < 4;i++){
            panels[i] = new JCardStack(0,0,0,0,2,"right");
            panels[i].setBackground(Color.WHITE);
            Card card = new JCard();
            panels[i].push(card);
            if(i == 1){
                panels[i].push((JCard)upCard1);
            } else if(i == 2){
                panels[i].push((JCard)upCard2);
            }
        }
        for(int i = 0;i < panels.length;i++){
            rightPanel.add(panels[i]);
        }
        mpa.setRightPanel(rightPanel);
        return rightPanel;
    }

    private JCardStack[] genCardPanels(Iterator it){
        JCardStack panels[] = new JCardStack[10];

        int i = 0;
        int index = -1;
        while(it.hasNext()){
            Card card = (Card)it.next();
            if(i % 5 == 0){
                index++;
                panels[index] = new JCardStack(0,card.getHeight()/3,0,card.getHeight()+10,18,"content");
                panels[index].setBackground(Color.WHITE);
            }
            card.reset();
            panels[index].push((JCard)card);
            i++;
        }
        mpa.setContentPanel(panels);
        return panels;
    }

    private JMenuBar genJMenuBar(){
        JMenuBar menuBar=new JMenuBar();

        //NewGame menu
        menuBar.add(genJMenuItem("NewGame",KeyEvent.VK_N));
        //Undo menu
        menuBar.add(genJMenuItem("Undo",KeyEvent.VK_U));
        //Hint menu
        menuBar.add(genJMenuItem("Hint",KeyEvent.VK_H));
        //Score menu
        menuBar.add(genJMenuItem("Score",KeyEvent.VK_S));
        //FillPiles menu
        menuBar.add(genJMenuItem("FillPiles",KeyEvent.VK_F));
        //quit menu
        menuBar.add(genJMenuItem("Quit",KeyEvent.VK_Q));
        //menuBar.add(genJMenuItem("Save",KeyEvent.VK_S));
        //menuBar.add(genJMenuItem("Restore",KeyEvent.VK_R));

        return menuBar;
    }

    private JMenuItem genJMenuItem(String name,int shortcut){
        JMenuItem menuItem = new JMenuItem(name);
        menuItem.setMnemonic(shortcut);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(shortcut,0));
        menuItem.addActionListener(this);
        return menuItem;
    }

    private void fillPiles(){
        boolean moved = false;
        Component cmps[];
        cmps = rightPanel.getComponents();
        for(int i = 0;i < cmps.length;i++){
            CardStack csp = (CardStack)cmps[i];
            Card card = csp.getLastCard();

            if(card != null){
                if(card.getPosition().equals("left")){
                    continue;
                }
                if(card.moveLeftPanel(card,true)){
                    //card.movedUpdate();
                    moved = true;
                    break;
                }
            }
        }

        cmps = cardPanels.getComponents();
        for(int i = 0;i < cmps.length;i++){
            CardStack csp = (CardStack)cmps[i];
            Card card = csp.getLastCard();
            if(card != null){
                if(card.getPosition().equals("left")){
                    continue;
                }
                if(card.moveLeftPanel(card,true)){
                    //card.movedUpdate();
                    moved = true;
                    break;
                }
            }
        }

        if(moved){
            fillPiles();
        }
    }

    private void cardAnimation(){
        getContentPane().add(movingCard);
        getContentPane().setComponentZOrder(movingCard,0);
        cardX = 0;
        cardY = 100;

        class TimerListener implements ActionListener{
            private Timer timer = null;
            public void actionPerformed(ActionEvent ae){
                cardX += 10;
                movingCard.setLocation(cardX,cardY);
                content.repaint();
                if(cardX > 600){
                    content.remove(movingCard);
                    timer.stop();
                }
            }

            public void setTimer(Timer timer){
                this.timer = timer;
            }
        }
        TimerListener tl = new TimerListener();
        Timer t = new Timer(20,tl);
        tl.setTimer(t);
        t.start();
    }

    public void windowClosing(WindowEvent e) {
        System.exit(0);
    }
    public void windowActivated(WindowEvent e) {
    }
    public void windowClosed(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowIconified(WindowEvent e) {}
    public void windowOpened(WindowEvent e) {}

    public void keyPressed(KeyEvent e){
        System.out.println("Pressed");
    }
    public void keyReleased(KeyEvent e){
        System.out.println("Released");
    }
    public void keyTyped(KeyEvent e){
        System.out.println("Typed");
    }

    public void actionPerformed(ActionEvent e){
        String actionCommand = e.getActionCommand();
        if(actionCommand.equals("Quit")){
            System.exit(0);
        } else if(actionCommand.equals("NewGame")){
            genContent(false);
        } else if(actionCommand.equals("FillPiles")){
            fillPiles();
        } else if(actionCommand.equals("Undo")){
            history.undo();
        } else if(actionCommand.equals("Hint")){
            cardAnimation();
        } else if(actionCommand.equals("Score")){
            cardAnimation();
        } else {
            System.out.println(actionCommand);
        }
    }

    public void mouseClicked(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {
        System.out.println("Pressed:" + e);
    }
    public void mouseReleased(MouseEvent e) {
        System.out.println("Released:" + e.getPoint());
    }
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

	public void componentResized(ComponentEvent e) {
	}

	public void componentMoved(ComponentEvent e) {
        mpa.setRootLocation(this.getLocation());
	}

	public void componentShown(ComponentEvent e) {
	}

	public void componentHidden(ComponentEvent e) {
	}
}
