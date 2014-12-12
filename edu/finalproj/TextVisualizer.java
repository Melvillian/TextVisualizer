package edu.finalproj;


import javax.swing.*;


/**
 * Created by Alex Melville on 12/1/14.
 */
public class TextVisualizer {

    private GraphicsApplet display;


    public TextVisualizer(String sentiPath){
    	WordNet wordnet = new WordNet(sentiPath);
        System.out.println("Finished loading SentiWordNet DB");
        
        JFrame application = new JFrame("TextTisualizer");
		application.setResizable(false);
		application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GraphicsApplet panel = new GraphicsApplet(wordnet);
		application.add(panel);
		application.pack();
		application.setVisible(true);
    }

    public static void main(String[] args) {
        String sentiPath;
        TextVisualizer tv; 
        if (args.length == 0) System.out.println("Usage: java TextVisualizer sentiWordNetPath");
        else {
            sentiPath = args[0];
            tv =  new TextVisualizer(sentiPath);
        }
		
        
    }
}
