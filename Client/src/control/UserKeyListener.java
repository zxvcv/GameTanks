package control;

import data.GameMessage;

import java.awt.Dimension;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;

public class UserKeyListener extends JFrame implements java.awt.event.KeyListener {
    private GameManager gameManager = Game.getGameManager();
    private int playerIndex;

    public UserKeyListener(int index) {
        super("KeyListener Test");
        setPreferredSize(new Dimension(300, 100));
        addKeyListener(this);

        pack();
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        playerIndex = index;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        char c = e.getKeyChar();
        synchronized (gameManager) {
            switch (c) {
                case 's':
                    gameManager.getMessageQueue().add(new GameMessage("SHOOT", playerIndex));
                    break;
                case 'a':
                    gameManager.getMessageQueue().add(new GameMessage("SHOW", playerIndex));
                    break;
                default:
                    break;
            }
        }
    }

}
