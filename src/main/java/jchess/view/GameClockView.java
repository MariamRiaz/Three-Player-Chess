package jchess.view;

import jchess.Game;
import jchess.Settings;
import jchess.helper.Clock;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class GameClockView extends JPanel {

    private Settings settings;
    private BufferedImage background;
    private String white_clock, black_clock, gray_clock;
    public Clock clock1;
    public Clock clock2;
    public Clock clock3;

    public GameClockView(Game game){
        this.settings = game.settings;
    }

    public void paint(Graphics g) {
        // Log.log("rysuje zegary");
        super.paint(g);
        white_clock = this.clock1.prepareString();
        black_clock = this.clock2.prepareString();
        gray_clock = this.clock3.prepareString();
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
        g.drawString(settings.playerWhite.getName(), 10, 50);

        g.setColor(Color.WHITE);
        g.drawString(settings.playerBlack.getName(), 90, 50);
        g.drawString(settings.playerGray.getName(), 170, 50);
        g2d.setFont(font);
        g.setColor(Color.BLACK);
        g2d.drawString(white_clock, 10, 80);
        g2d.drawString(black_clock, 90, 80);
        g2d.drawString(gray_clock, 170, 80);
    }

    public void update(Graphics g) {
        paint(g);
    }

}
