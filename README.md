# Visualizing PDF Sentiment #

## How to Run TextVisualizer ##

Assuming you have an update to date version of maven installed on your machine, while in the top
level directory run

    mvn package

Note that it took 2 minutes on my Macbook 2009 to complete the build. Once the build has finished, 
run the following line to start TextVisualizer

    java -Xmx1G -cp target/textvisualizer-TextVisualizer.jar  edu.finalproj.TextVisualizer
 
Place the PDF you would like to visualize in the "pdfs" directory, and then load the PDF by entering pdfs/name_of_pdf.
Choose which type of visualization method you would like. Note that the Positive vs. Negative method
takes around 10 seconds to run, while the Sentence Parsing method takes up to a minute to run.

## What is TextVisualizer? ##

TextVisualizer is a Java applet that allows a user to select a PDF on their
computer and visualize its contents. Thus far TextVisualizer performs two
types of visualizations:

* **Positive vs Negative**

  Using blue for positive and red for negative, each page in the PDF is mapped
  to a column in the visualizer. A page's sentiment is determined by summing the
  sentiment values for every phrase in a sentence according to the values in SentiWordNet.txt.
  Normally TextVisualizer will calculate a sentiment value for the page as a whole.
  If "Use Normalized Sentiment Values" is checked, then every 4 words within a page will
  receive its own coloring, allowing the user to see a text's sentiment at a much higher granularity. 

* **Sentence Parsing**

  For each sentence in a page (by default the 2nd page) TextVisualizer paints the parse tree of
  the sentence using the parts of speech groups given in colored font. The idea behind this
  visualization method to let the user identify patterns in a text's syntactic structure.
