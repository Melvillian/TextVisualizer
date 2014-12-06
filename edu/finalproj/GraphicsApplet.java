package edu.finalproj;



/**
 * Created by alex on 12/1/14.
 */

import javax.swing.*;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.Random;


/**
 * This class exists simply to play around with Java's 2DGraphics library
 * so that eventually we'll be able to integrate it into the final project
 */
public class GraphicsApplet extends JPanel implements ActionListener {
		


	static String sentimentStr = "Good V. Bad";
	static String stemStr = "Stemming";
	static String etyStr = "Etymology";
	static String runStr = "Run TextVisualizer";
	static String loadStr = "Load Book";
	
	WordNet wordnet; 
	PDDocument book;
	
	int pageGrain = 500; //specifies how many expected words per page
	int bookGrain = 1000; //specifies how many expected pages per book
	
	//These dont' have to be variables, but it makes the communication
	// easier than using tags on objects in the container....
	String fileName;
	String genType;
	JButton but_run;
	JTextField source;
	JPanel paint;
	RepaintManager painter;
	
	public GraphicsApplet(){
		this.display();
	};
	
	public GraphicsApplet(WordNet wn){
		this.display();
	}
	
	public void display(){
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
		but_run = new JButton(runStr);
		but_run.setActionCommand(runStr);
		but_run.setEnabled(false);
		
		JButton but_load = new JButton(loadStr);
		but_load.setActionCommand(loadStr);
		
        JPanel radioPanel = new JPanel( new GridLayout(0,1));
        JPanel sourcePanel = new JPanel();
        JPanel selectPanel = new JPanel();
        
        radioPanel.add(new JLabel("Selection Method:"));
		radioPanel.add(rad_goodbad);
		radioPanel.add(rad_stem);
		radioPanel.add(rad_ety);
		
		sourcePanel.add(new JLabel("Input File Name:"));
		sourcePanel.add(source);
		sourcePanel.add(but_load);
		
		selectPanel.add(radioPanel);
		selectPanel.add(sourcePanel);
		selectPanel.add(but_run);

		this.setLayout(new GridLayout(2,1));
		add(paint, BorderLayout.CENTER); // Puts the painting object at the top of our applet;
		add(selectPanel, BorderLayout.PAGE_END);
		
		rad_goodbad.addActionListener(this);
		rad_stem.addActionListener(this);
		rad_ety.addActionListener(this);
		but_run.addActionListener(this);
		but_load.addActionListener(this);
		source.addActionListener(this);
		}
	
	public void actionPerformed(ActionEvent event){
		if (event.getActionCommand().equals("File")){
			fileName = source.getText();
		} else if (event.getActionCommand().equals(loadStr)){
			fileName = source.getText();
		// create BufferedReader
        try {
            book = PDDocument.load(new File(fileName));
            bookGrain = book.getNumberOfPages();
            
            but_run.setEnabled(true);
        } catch (IOException e) { //
        	// if file not found don't care but don't let us run. 
        	System.out.println("Can't find file: ");
        	System.out.println(fileName);
        };
			
		} else if (event.getActionCommand().equals(sentimentStr)){
			genType = sentimentStr;
		} else if (event.getActionCommand().equals(stemStr)){
			genType = stemStr;
		} else if (event.getActionCommand().equals(etyStr)){
			genType = etyStr;
		} else if (event.getActionCommand().equals(runStr)){
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
		int curPage = 0, curWord = 0, curX = 0, curY = 0;
		int xShift = maxX/bookGrain+1; //we define how large our rectangles are
	
		Random rand = new Random(); //for testing
		PDFTextStripper stripper;
		while (curPage < bookGrain){ //while on page i
			
			//reset iterators for internal loop
			curY = 0;
			curWord = 0; 
			
			try { 
				stripper = new PDFTextStripper();
	            stripper.setStartPage(curPage);
	            stripper.setEndPage(curPage);
	
	            String[] page = stripper.getText(book).split(" ");
				pageGrain = page.length;
				int yShift = maxY/pageGrain+1; // by the book/page granularity, offset by one for int division.

				while (curWord < pageGrain){
					int[] xs = {curX, curX, curX+xShift, curX+xShift}; 
					int[] ys = {curY, curY+yShift,curY+yShift, curY};
					
					Float zero = new Float(0.0);
					if ( genType.equals(stemStr)){
						//double[] colors = wordnet.getColors();
						canvas.setColor(new Color(zero, rand.nextFloat(), zero));
					} else if (genType.equals(etyStr)){
						canvas.setColor(new Color(zero,  zero, rand.nextFloat()));
					} else { //if genType.equals(Sentiment){ 
						canvas.setColor(new Color(rand.nextFloat(), zero,zero));
					}
					canvas.fillPolygon(xs, ys, 4);
					curY+=yShift+1;
					//curWord += 1;
				};
			} catch (IOException e) { e.printStackTrace(); }
			curX+=xShift+1;
			curPage+=1;
		};
		
		
		
	}
	
	public static void main(String[] args){

		JFrame application = new JFrame("TextTisualizer");
		application.setResizable(false);
		application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		GraphicsApplet panel = new GraphicsApplet();
		application.add(panel);
		application.pack();

		application.setVisible(true);
		
	}
}
