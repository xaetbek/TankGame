package main.java.tankrotationexample;

import java.awt.*;

public class Menu {


    public void drawImage(Graphics grph) {
        Font f = new Font("Verdana", Font.BOLD, 43);
        grph.setColor(Color.GREEN);
        grph.setFont(f);
        grph.drawString("START", 380, 470);
        grph.drawString("CONTROLS", 320, 560);
        grph.drawString("EXIT", 397, 650);
        grph.setColor(Color.white);
        grph.drawRoundRect(360, 416, 210, 70, 20, 20);
        grph.drawRoundRect(310, 416 + 90, 300, 70, 20, 20);
        grph.drawRoundRect(360, 416 + 90 + 90, 210, 70, 20, 20);
    }
}
