package edu.finalproj;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Alex Melville on 12/1/14.
 */
public class TextVisualizer {


    private WordNet wordnet;
    PDDocument pdDoc;
    PDFTextStripper stripper;;

    /**
     * Loads Wordnet data from the file located at sentiPath
     * @param sentiPath
     */
    TextVisualizer(String sentiPath) {

        this.wordnet = new WordNet(sentiPath);
    }


    /**
     * Loads the PDF document data for the file located at
     * pathTopDF and creates the text stripper to be used
     * for that document
     * @param pathToPDF
     */
    private void readInPDF(String pathToPDF) {
        try {
            this.pdDoc = PDDocument.load(new File(pathToPDF));
            this.stripper = new PDFTextStripper();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Given the length of some text that we're going to iterate through in
     * intervals of 4, determine how much we need to subtract from our total
     * length so we don't get an ArrayOutOfBoundsException
     * @param textLen
     * @return
     */
    private static int determineOffset(int textLen) {
        if (textLen % 4 == 1) {
            return 1;
        }
        else if (textLen % 4 == 2) {
            return 2;
        }
        else if (textLen % 4 == 3) {
            return 3;
        }
        else return 0;
    }

    /**
     * Reads through a page and returns the summed
     * positive and negative values for that page
     * @param startPage
     * @param endPage
     * @return
     */
    private void readPage(int startPage, int endPage) {
        stripper.setStartPage( startPage );
        stripper.setEndPage( endPage );
        try {
            // format page's text
            String text = stripper.getText(pdDoc);
            text = text.trim().replaceAll(System.getProperty("line.separator"), "").replaceAll("\\p{Punct}", "");
            String[] splitText = text.split("\\s");

            int textLen = splitText.length;
            int wordOffset = determineOffset(textLen);
            for (int i = 0; i < splitText.length - wordOffset; i+=4) {
                if (splitText[i].equals("")) { // hack to skip blank 'words'
                    continue;
                }
                ArrayList<Double> posNegValues = wordnet.getPosNegValues(splitText[i],
                                                splitText[i + 1], splitText[i + 2], splitText[i + 3]);
                System.out.println("phrase: " + splitText[i] + " " + splitText[i + 1] + " " + splitText[i + 2] + " " + splitText[i + 3]);
                System.out.println("positive value: " + posNegValues.get(0));
                System.out.println("negative value: " + posNegValues.get(1));
                System.out.println("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        TextVisualizer tv = new TextVisualizer(args[0]);
        tv.readInPDF(args[1]);

        for (int i = 1; i < 2; i += 2) {
            tv.readPage(i, i + 1);
        }
    }

}
