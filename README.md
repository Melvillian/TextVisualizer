# Visualizing PDF Sentiment #

## How to Use TextVisualizer ##

Include in your Classpath the libraries listed in the Java Dependencies section. Then
run the 'TextVisualizer' file with between 1GB and 2GB of memory (the Stanford parser really likes its memory!).
Next load a PDF using a path to a PDF file relative to the directory TextVisualizer was run from.
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
  


### Java Dependencies ###

* pdfbox = v1.8.7
* fontbox = v1.8.7
* commons-logging >= v1.1.3
* stanford-corenlp = v3.4.1 (download at http://nlp.stanford.edu/software/stanford-corenlp-full-2014-08-27.zip)
