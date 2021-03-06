package edu.finalproj;



/**
 * Created by alex on 12/1/14.
 */

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This is the top level class for Alex and Mari's NLP project. It provides a user interface to
 * display the visualization of text data's parsing and basic positive/negative sentiment analysis
 */
public class TextVisualizer extends JPanel implements ActionListener, ItemListener, DocumentListener {
		
	private static final long serialVersionUID = -4200617947533784654L; //Just to get Eclipse to shush.
	
	static String sentimentStr = "Positive vs Negative";
	static String stemStr = "Sentence Parsing";
	static String blockStr = "Use Word Precision";
	static String runStr = "Run TextVisualizer";
	static String loadStr = "Load PDF";

	static int pageStart = 3;
	static int pageEnd = 4;
	
	WordNet wordnet; 
	StanfordParser sp;
	PDDocument book;
	
	int pageGrain = 500; //specifies how many expected words per page
	int bookGrain = 1000; //specifies how many expected pages per book

	int posCount = 0;
	int negCount = 0;
	
	//These dont' have to be variables, but it makes the communication
	// easier than using tags on objects in the container....
	String fileName;
	String genType;
	String gramType = "page";
	
	JCheckBox check_norm;
	JCheckBox check_block;
	boolean flag_block;
	
	JButton but_run;
	JTextField source;
	JPanel paint;
	JPanel label;
	RepaintManager painter;
	
	
	
	
	public TextVisualizer(){
        this.wordnet = new WordNet("SentiWordNet.txt");
        this.sp  = new StanfordParser("parser");
		this.display();
	};
	
	public TextVisualizer(WordNet wn){
		this.wordnet = wn;
		this.sp = new StanfordParser("parser");
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
		
		//then we add them to the button group
		ButtonGroup group = new ButtonGroup();
		group.add(rad_goodbad);
		group.add(rad_stem);
		
		//We need to define two check boxes: one for globalized normalization
		// and the other for determining page or ngram based images.
		check_block = new JCheckBox(blockStr);
		
		// The image panel must be defined as well
		paint = new JPanel();
		paint.setPreferredSize(new Dimension(1000,250));
		paint.setBackground(Color.DARK_GRAY);

		//Also, the file filter and the 'run' command button.
		source = new JTextField(20);
		source.getDocument().addDocumentListener(this);
		but_run = new JButton(runStr);
		but_run.setActionCommand(runStr);
		but_run.setEnabled(false);
		
		JButton but_load = new JButton(loadStr);
		but_load.setActionCommand(loadStr);
		
        JPanel radioPanel = new JPanel( new GridLayout(0,1));
        JPanel sourcePanel = new JPanel();
        JPanel selectPanel = new JPanel();
        JPanel runPanel = new JPanel( new GridLayout(0,1) );
        
        radioPanel.add(new JLabel("Visualization Method:"));
		radioPanel.add(rad_goodbad);
		radioPanel.add(rad_stem);
		
		sourcePanel.add(new JLabel("Input File Name:"));
		sourcePanel.add(source);
		sourcePanel.add(but_load);
		
		runPanel.add(check_block);
		runPanel.add(sourcePanel);

		label = new JPanel();
		JLabel l_blue = new JLabel("Adjectives");
		l_blue.setForeground(Color.BLUE);
		label.add(l_blue);
		JLabel l_red = new JLabel("Verbs");
		l_red.setForeground(Color.RED);
		label.add(l_red);
		
		JLabel l_green = new JLabel("Nouns");
		l_green.setForeground(Color.GREEN);
		label.add(l_green);
		
		JLabel l_magenta = new JLabel("Adverbs");
		l_magenta.setForeground(Color.MAGENTA);
		label.add(l_magenta);
		JLabel l_pink = new JLabel("Prepositions");
		l_pink.setForeground(Color.PINK);
		label.add(l_pink);
		JLabel l_silver = new JLabel("Determinants");
		l_silver.setForeground(Color.LIGHT_GRAY);
		label.add(l_silver);
		JLabel l_gray = new JLabel("Miscellaneous");
		l_gray.setForeground(Color.GRAY);
		label.add(l_gray);
		JLabel l_white = new JLabel("Unknown");		
		l_white.setForeground(Color.WHITE);
		label.add(l_white);
		
		selectPanel.add(radioPanel);
		selectPanel.add(sourcePanel);
		selectPanel.add(runPanel);
		selectPanel.add(but_run);
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(label, BorderLayout.PAGE_START);
		panel.add(selectPanel, BorderLayout.PAGE_END);
		

		this.setLayout(new BorderLayout());
		add(paint, BorderLayout.PAGE_START); // Puts the painting object at the top of our applet;
		add(panel, BorderLayout.PAGE_END);
		
		rad_goodbad.addActionListener(this);
		rad_stem.addActionListener(this);
		but_run.addActionListener(this);
		but_load.addActionListener(this);
		source.addActionListener(this);
		
		check_block.addItemListener(this);
		}
	
	public void itemStateChanged(ItemEvent e){
		Object source = e.getItemSelectable();
		
		if (source == check_block){
			flag_block = (e.getStateChange() == ItemEvent.SELECTED) ? true : false;
		};
	}
	
	public void changedUpdate(DocumentEvent e){
		Document doc = e.getDocument();
		try {
			fileName = doc.getText(0,doc.getLength());
		} catch (BadLocationException e1) {
			//
		}
	};
	
	public void insertUpdate(DocumentEvent e){
		Document doc = (Document)e.getDocument();
		try {
			fileName = doc.getText(0,doc.getLength());
		} catch (BadLocationException e1) {
			//
		}
	};
	public void removeUpdate(DocumentEvent e){
		Document doc = (Document)e.getDocument();
		try {
			fileName = doc.getText(0,doc.getLength());
		} catch (BadLocationException e1) {
			//
		}
	};
	
	public void actionPerformed(ActionEvent event){
		
		if (event.getActionCommand().equals(loadStr)){
			// create BufferedReader
	        try {
	            book = PDDocument.load(new File(fileName));
	            bookGrain = book.getNumberOfPages();
	            but_run.setEnabled(true);
	        } catch (IOException e) { //
	        	// if file not found don't care but don't let us run. 
	        	System.out.printf("Can't find file: %s.\n", fileName);
	        };
	        
	    //These handle the radio  button (for now).
		} else if (event.getActionCommand().equals(sentimentStr)){
			genType = sentimentStr;
			label.setVisible(false);
			check_block.setEnabled(true);
		} else if (event.getActionCommand().equals(stemStr)){
			genType = stemStr;			
			label.setVisible(true);
			check_block.setEnabled(false);
		//Then, if we trigger the run event, we should run!
		} else if (event.getActionCommand().equals(runStr)){
			
			negCount = 0;
			posCount = 0;
			if (genType.equals(stemStr)){
				paintPage(paint, pageStart, pageEnd);
			}
			else {
				float[] maxs = analyzeBook();
				paintBook(paint, maxs[0], maxs[1]);
			};
			
//			try{ //And, because of IO, we have to try to close the pdf. 
//				book.close();
//			} catch (IOException e){};
		}
 
	}


	public void paintPage(JPanel panel, int first, int last){
		Graphics canvas = panel.getGraphics();
		canvas.setPaintMode();
		
		//while in bounds
		int maxX = paint.getWidth();
		int maxY = paint.getHeight();
		
		PDFTextStripper stripper;
		try {
			stripper = new PDFTextStripper();
	        stripper.setStartPage(first);
	        stripper.setEndPage(last);
	        
	        ArrayList< ArrayList<Tuple> > pageParse = sp.parseText(stripper.getText(book));

			int xShift = maxX/pageParse.size()+1; //we define how large our bars are
			int curX = 0;
			
	        for (ArrayList<Tuple> senParse : pageParse){
	        	int senLength = 0;
	        	for (Tuple phrase : senParse){
	        		senLength+= phrase.getCnt();
	        	}
	        	int yShift = Math.round((float)maxY/senLength);
	        	int curY = 0;
	        	
	        	for (Tuple phrase : senParse){
	        		int wc = phrase.getCnt()*yShift;

					int[] xs = {curX, curX, curX+xShift, curX+xShift}; 
					int[] ys = {curY, curY+wc,curY+wc, curY};
									
					canvas.setColor(phrase.getColor() );
					canvas.fillPolygon(xs, ys, 4);
					curY+=wc;
	        	};
	        curX+=xShift; 
	        };
		} catch (IOException e1) {};
	};


	/**
	 * Draws
	 * @param panel
	 * @param maxPos
	 * @param maxNeg
	 */
	public void paintBook(JPanel panel, float maxPos, float maxNeg){
		Graphics canvas = panel.getGraphics();
		canvas.setPaintMode();
		
		//while in bounds
		int maxX = paint.getWidth();
		int maxY = paint.getHeight();
		int curPage = 0, curWord = 0, curX = 0, curY = 0;
		int xShift = maxX/bookGrain+1; //we define how large our rectangles are

		PDFTextStripper stripper;
		while (curPage <= bookGrain){ //while on page i
			
			//reset iterators for internal loop
			curY = 0;
			curWord = 0; 
			
			try { 
				stripper = new PDFTextStripper();
	            stripper.setStartPage(curPage);
	            stripper.setEndPage(curPage);
	
	            String[] page = stripper.getText(book).split("[^A-Za-z]+");
	            
				pageGrain = page.length;
				if (pageGrain != 0){
					
					if (flag_block){ //If we're using word granularity
						int yShift = maxY/pageGrain+1; // by the book/page granularity, offset by one for int division.
						while (curWord < pageGrain - 4 ){
							int[] xs = {curX, curX, curX+xShift, curX+xShift}; 
							int[] ys = {curY, curY+yShift,curY+yShift, curY};
							
								SentiWord wordColor = wordnet.test(page[curWord], page[curWord+1], 
																	page[curWord+2], page[curWord+3]);
								curWord += wordColor.getWC();
								double[] colors = wordColor.get();
								float neg = (float)colors[1];
								float pos = (float)colors[0];
								if (neg > pos){
									float val = neg/maxNeg;
									canvas.setColor(new Color( val, val, 0 ));
									negCount++;
								} else if (pos > neg ) {
									float val = pos/maxPos;
									canvas.setColor(new Color( 0, val, val ));
									posCount++;
								} else {
									canvas.setColor(new Color( 0.5f, 0.5f, 0.5f));
								}
							
							canvas.fillPolygon(xs, ys, 4);
							curY+=yShift;
						};
					} else { //if page-based granularity
						double[] colors = {0.0, 0.0};
						int[] xs = {curX, curX, curX+xShift, curX+xShift}; 
						int[] ys = {curY, maxY, maxY, curY};
						int seenPos = 0;
						int seenNeg = 0;
						while (curWord < pageGrain - 4  ){
								SentiWord wordColor = wordnet.test(page[curWord], page[curWord+1], 
																	page[curWord+2], page[curWord+3]);
								curWord += wordColor.getWC();
								double[] newcolors = wordColor.get();
								colors[0] += newcolors[0];
								if (newcolors[0] > 0){ 
									seenPos++;
								}
								
								colors[1] += newcolors[1];
								if (newcolors[1] > 0){ 
									seenNeg++;
								}
						};
							
						float neg = (float)colors[1]/seenNeg;
						float pos = (float)colors[0]/seenPos;
						
						 //this scales according to worst and best we've seen. 
							if (neg > pos){
								float val = neg/maxNeg;
								canvas.setColor(new Color( val, 0, 0 ));
								negCount++;
							} else if (pos > neg ) {
								float val = pos/maxPos;
								canvas.setColor(new Color( 0, val*.35f, val));
								posCount++;
							} else {
								canvas.setColor(new Color( 0.5f, 0.5f, 0.5f));
							}
							canvas.fillPolygon(xs, ys, 4);
						
					};
				};
			} catch (IOException e) { e.printStackTrace(); }
			curX+=xShift;
			curPage+=1;
		};	

		System.out.printf("Using the method %s, the following counts were found:\n", gramType);
		System.out.printf("Positive Blocks (Blue): %d\n", posCount);
		System.out.printf("Negative Blocks (Red): %d\n", negCount);
	}

	/**
	 * Used when normalized coloring is selected, and it will scan through the inputted
	 * book calculating the max positive and negative values for the entire book. It returns
	 * a float array with 2 elements, the first is the maximum positive value and the second
	 * is the maximum negative value
	 * @return
	 */
	public float[] analyzeBook(){
		PDFTextStripper stripper;
		float posMax = 0;
		float negMax = 0;
		
		int curPage = 0;
		while (curPage <= bookGrain){ //while on page i
			try { 
				stripper = new PDFTextStripper();
	            stripper.setStartPage(curPage);
	            stripper.setEndPage(curPage);
	            int curWord = 0;
	            
	            String[] page = stripper.getText(book).split("[^A-Za-z]+");
				pageGrain = page.length;							
					if (flag_block){
						while (curWord < pageGrain - 4  ){
							SentiWord wordColor = wordnet.test(page[curWord], page[curWord+1], 
									page[curWord+2], page[curWord+3]);
							
							curWord+=wordColor.getWC();
							double[] colors = wordColor.get();
							
							if (colors[0] > posMax){ posMax = (float)colors[0]; }
							if (colors[1] > negMax){ negMax = (float)colors[1]; }
						};
					} else {

						while (curWord < pageGrain - 4  ){
							SentiWord wordColor = wordnet.test(page[curWord], page[curWord+1], 
									page[curWord+2], page[curWord+3]);
							
							curWord+=wordColor.getWC();
							double[] colors = wordColor.get();
							if (colors[0] > posMax){ posMax = (float)colors[0]; }
							if (colors[1] > negMax){ negMax = (float)colors[1]; }
						};
					};
						
			} catch (IOException e){
				//
			};
			curPage++;
		};
		return new float[]{posMax, negMax};
	}
	
	public static void main(String[] args){

		JFrame application = new JFrame("TextVisualizer");
		//application.setResizable(false);
		application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		TextVisualizer panel = new TextVisualizer();
		application.add(panel);
		application.pack();

		application.setVisible(true);
	}
}
