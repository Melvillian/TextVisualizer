package edu.nlp.finalproj;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import java.io.File;

import java.io.IOException;

/**
 * Created by alex on 12/1/14.
 */


/**
 This class exists simply to play around with Apache's PDFBox library
 so that eventually we'll be able to integrate it into the final project
 **/
public class PDFBoxTester {

    // This class demonstrates how to use PDFBox to read a pdf file in from
    // the pdfs directory, and read the text data from the first page and
    // the second page
    public static void main(String[] args) {
        try {
            PDDocument document = PDDocument.load(new File("pdfs/Dune.pdf"));
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setStartPage( 1 );
            stripper.setEndPage( 2 );
            String text = stripper.getText(document);
            System.out.println(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
