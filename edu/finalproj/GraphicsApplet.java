package edu.nlp.finalproj;



/**
 * Created by alex on 12/1/14.
 */

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.Random;


/**
 * This class exists simply to play around with Java's 2DGraphics library
 * so that eventually we'll be able to integrate it into the final project
 */
public class GraphicsTester extends JPanel implements ActionListener {
		
	
	// Strings (For labelling radio buttons, and also internal not!enums).
	static String sentimentStr = "Good V. Bad";
	static String stemStr = "Stemming";
	static String etyStr = "Etymology";	
	
	int pageGrain = 500; //specifies how many expected words per page
	int bookGrain = 1000; //specifies how many expected pages per book
	
	String fileName;
	String genType;
	JTextField source;
	JPanel paint;
	RepaintManager painter;
	
	public GraphicsTester(){
		
		//Setting up the radio first, because it takes the most code. 
		
		//individual buttons. 
		JRadioButton rad_goodbad = new JRadioButton(sentimentStr);
		rad_goodbad.setActionCommand(sentimentStr);
		rad_goodbad.setSelected(true); // By default we set this one to be selected. 
		genType = sentimentStr; 		// That lets us ensure this field is not null.
		
		JRadioButton rad_stem = new JRadioButton(stemStr);
		rad_stem.setActionCommand(stemStr);
		
		JRadioButton rad_ety = new JRadioButton(etyStr);
		rad_ety.setActionCommand(etyStr);
		
		//then we add them to the button group
		ButtonGroup group = new ButtonGroup();
		group.add(rad_goodbad);
		group.add(rad_stem);
		group.add(rad_ety);
		
		// The image panel must be defined as well
		paint = new JPanel();
		paint.setPreferredSize(new Dimension(1000,250));
		paint.setBackground(Color.DARK_GRAY);

		//Also, the file filter and the 'run' command button.
		source = new JTextField(20);
		source.setActionCommand("File");
		JButton button = new JButton("Run TextVisualizer");
		button.setActionCommand("Run");
		
        JPanel radioPanel = new JPanel( new GridLayout(0,1));
        JPanel sourcePanel = new JPanel();
        JPanel selectPanel = new JPanel();
        
        radioPanel.add(new JLabel("Selection Method:"));
		radioPanel.add(rad_goodbad);
		radioPanel.add(rad_stem);
		radioPanel.add(rad_ety);
		
		sourcePanel.add(new JLabel("Input File Name:"));
		sourcePanel.add(source);
		
		selectPanel.add(radioPanel);
		selectPanel.add(sourcePanel);
		selectPanel.add(button);

		this.setLayout(new GridLayout(2,1));
		add(paint, BorderLayout.CENTER); // Puts the painting object at the top of our applet;
		add(selectPanel, BorderLayout.PAGE_END);
		
		rad_goodbad.addActionListener(this);
		rad_stem.addActionListener(this);
		rad_ety.addActionListener(this);
		button.addActionListener(this);
		source.addActionListener(this); //Current janky implementation requires hitting enter. Fix?
	}
	
	public void actionPerformed(ActionEvent e){
		if (e.getActionCommand().equals("File")){
			fileName = source.getText();
		} else if (e.getActionCommand().equals(sentimentStr)){
			genType = sentimentStr;
		} else if (e.getActionCommand().equals(stemStr)){
			genType = stemStr;
		} else if (e.getActionCommand().equals(etyStr)){
			genType = etyStr;
		} else if (e.getActionCommand().equals("Run")){
			// Call main implementation
			// and make sure there or here we catch for file errors!
			paintBook(paint);
		}
 
	}
	
	public void paintBook(JPanel panel){
		Graphics canvas = panel.getGraphics();
		canvas.setPaintMode();
		
		//while in bounds
		int maxX = paint.getWidth();
		int maxY = paint.getHeight();
		int curX = 0;
		int xShift = maxX/bookGrain+1; //we define how large our rectangles are
		int yShift = maxY/pageGrain+1; // by the book/page granularity, offset by one for int division.

		Random rand = new Random(); //for testing
		
		while (curX < maxX){
			int curY = 0;

			while (curY < maxY){
				int[] xs = {curX, curX, curX+xShift, curX+xShift}; 
				int[] ys = {curY, curY+yShift,curY+yShift, curY};
				
				
				 //for testing
				Float zero = new Float(0.0);
				if ( genType.equals(stemStr)){
					canvas.setColor(new Color(zero, rand.nextFloat(), zero));
				} else if (genType.equals(etyStr)){
					canvas.setColor(new Color(zero,  zero, rand.nextFloat()));
				} else { //if genType.equals(Sentiment){ 
					canvas.setColor(new Color(rand.nextFloat(), zero,zero));
				}
				canvas.fillPolygon(xs, ys, 4);
				curY+=yShift+1;
			};
			curX+=xShift+1;
		};
		
		
		
	}
	
	public static void main(String[] args){

		JFrame application = new JFrame("TextTisualizer");
		application.setResizable(false);
		application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		GraphicsTester panel = new GraphicsTester();
		application.add(panel);
		application.pack();

		application.setVisible(true);
		
	}
}
