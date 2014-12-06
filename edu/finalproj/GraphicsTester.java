package edu.finalproj;



/**
 * Created by alex on 12/1/14.
 */


import javax.swing.*;
import java.awt.*;

/**
 * This class exists simply to play around with Java's 2DGraphics library
 * so that eventually we'll be able to integrate it into the final project
 */

/**
 * Set up graphics window
 */
public class GraphicsTester extends JPanel {



    public GraphicsTester()                       // set up graphics window
    {
        super();
        setBackground(Color.WHITE);
    }

    public void paintComponent(Graphics g)  // draw graphics in the panel
    {
        int width = getWidth();             // width of window in pixels
        int height = getHeight();           // height of window in pixels

        super.paintComponent(g);            // call superclass to make panel display correctly

        g.drawString("Hello, World", 100, 150);


        // Drawing code goes here
    }

    public static void main(String[] args)
    {
        GraphicsTester panel = new GraphicsTester();                            // window for drawing
        JFrame application = new JFrame();                            // the program itself

        application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   // set frame to exit
        // when it is closed

        application.add(panel);


        JPanel paintPanel = new JPanel();
        JPanel textPanel = new JPanel();


        application.setSize(500, 400);         // window is 500 pixels wide, 400 high
        application.setVisible(true);
    }
}
