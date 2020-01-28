package jchess.game.clock;

import java.awt.*;

/*
 * Class to generate the view of game clock
 * */

public class GameClockView extends AbstractGameClockView {

    private String whiteClock, blackClock, grayClock;
    private String whitePlayerName, blackPlayerName, grayPlayerName;

    public GameClockView() {
        this.updateClocks(new int[]{0, 0, 0});
    }

    public void setPlayerNames(String whitePlayerName, String blackPlayerName, String grayPlayerName) {
        this.whitePlayerName = whitePlayerName;
        this.blackPlayerName = blackPlayerName;
        this.grayPlayerName = grayPlayerName;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Font font = new Font(Font.SERIF, Font.ITALIC, 20);

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

        font = new Font(Font.SERIF, Font.ITALIC, 14);

        g2d.setFont(font);
        g.setColor(Color.BLACK);
        g.drawString(whitePlayerName, 10, 50);

        g.setColor(Color.WHITE);
        g.drawString(blackPlayerName, 90, 50);
        g.setColor(Color.WHITE);
        g.drawString(grayPlayerName, 170, 50);
        g2d.setFont(font);
        g.setColor(Color.BLACK);
        g2d.drawString(whiteClock, 10, 80);
        g2d.drawString(blackClock, 90, 80);
        g2d.drawString(grayClock, 170, 80);
    }

    public void updateClocks(int[] timeSpentByPlayers) {
        int whiteMinutes = timeSpentByPlayers[0] / 60;
        int whiteSeconds = timeSpentByPlayers[0] % 60;
        whiteClock = String.format("%02d", whiteMinutes) + ":" + String.format("%02d", whiteSeconds);
        int blackMinutes = timeSpentByPlayers[1] / 60;
        int blackSeconds = timeSpentByPlayers[1] % 60;
        blackClock = String.format("%02d", blackMinutes) + ":" + String.format("%02d", blackSeconds);
        int grayMinutes = timeSpentByPlayers[2] / 60;
        int graySeconds = timeSpentByPlayers[2] % 60;
        grayClock = String.format("%02d", grayMinutes) + ":" + String.format("%02d", graySeconds);
        repaint();
    }

}
