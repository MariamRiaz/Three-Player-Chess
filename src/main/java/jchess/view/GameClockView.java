package jchess.view;

import jchess.Game;
import jchess.Settings;
import jchess.helper.Clock;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/*
 * Class to generate the view of game clock
 * */

public class GameClockView extends JPanel {

    private Settings settings;
    private BufferedImage background;
    private String whiteClock, blackClock, grayClock;

    public GameClockView(Game game){
        this.settings = game.getSettings();
    }

    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(this.background, 0, 0, this);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Font font = new Font("Serif", Font.ITALIC, 20);
        g2d.drawImage(this.background, 0, 0, this);

        g2d.setColor(Color.WHITE);
        g2d.fillRect(5, 30, 80, 30);
        g2d.setFont(font);

        g2d.setColor(Color.BLACK);
        g2d.fillRect(85, 30, 80, 30);

        g2d.setColor(Color.GRAY);
        g2d.fillRect(165, 30, 80, 30);

        g2d.drawRect(5, 30, 240, 30);
        g2d.drawRect(5, 60, 240, 30);

        g2d.drawLine(85, 30, 85, 90);
        g2d.drawLine(165, 30, 165, 90);

        font = new Font("Serif", Font.ITALIC, 14);

        g2d.drawImage(this.background, 0, 0, this);

        g2d.setFont(font);
        g.setColor(Color.BLACK);
        g.drawString(settings.getPlayerWhite().getName(), 10, 50);

        g.setColor(Color.WHITE);
        g.drawString(settings.getPlayerBlack().getName(), 90, 50);
        g.drawString(settings.getPlayerGray().getName(), 170, 50);
        g2d.setFont(font);
        g.setColor(Color.BLACK);
        g2d.drawString(whiteClock, 10, 80);
        g2d.drawString(blackClock, 90, 80);
        g2d.drawString(grayClock, 170, 80);
    }

    public void update(Graphics g) {
        paint(g);
    }

    public void updateClocks(int[] timeSpentByPlayers){
        this.repaint();
        whiteClock = Integer.toString(timeSpentByPlayers[0]);
        blackClock = Integer.toString(timeSpentByPlayers[1]);
        grayClock = Integer.toString(timeSpentByPlayers[2]);
    }

}
