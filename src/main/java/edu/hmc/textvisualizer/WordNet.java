package edu.finalproj;

/**
 * Created by alex on 12/5/14.
 */

//import com.sun.tools.corba.se.idl.toJavaPortable.Helper;
//import com.sun.xml.internal.fastinfoset.util.StringArray;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * The WordNet class encapsulates all of the logic
 * that deals with parsing and storing WordNet databases
 * as well as providing methods for access to the underlying
 * Wordnet data. Note that the private class 'SentiMap' in contained
 * within the WordNet class so that the 'cnt' global variable can be shared
 */
public class WordNet {
    SentiMap sentimentMap;
    private static Integer cnt = 0;


    WordNet(String sentiPath) {
        System.out.println("Initializing Wordnet database...");
        sentimentMap = new SentiMap();

        // create BufferedReader
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(sentiPath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // add pos/neg sentiment words
        String line;
        int cnt = 0;
        try {

            while ((line = br.readLine()) != null) {
                cnt++;
                if (line.substring(0, 1).equals("#")) { // skip commented out lines
                    continue;
                } else {
                    String[] sentiLine = line.split("\t");
                    Double posVal = Double.valueOf(sentiLine[2]);
                    Double negVal = Double.valueOf(sentiLine[3]);
                    String synsetWords = sentiLine[4];
                    ArrayList<String> synsetWordList = getSynsetWords(synsetWords);
                    for (String word : synsetWordList) {
                        addSentiWord(word, posVal, negVal);
                    }

                }
            }
        } catch (IOException e) {
            System.out.println("Error in reading WordNet database");
            e.printStackTrace();
        }
    }


    /**
     * Helper method to print a String array
     * Helper Method to Print the String Array
     * @param
     */
    private static void printSplitWord(String[] splitWord) {
        StringBuilder splitWords = new StringBuilder();
        for (String w : splitWord) {
            splitWords.append(w + " ");
        }
        splitWord.toString();
    }

    /**
     * Helper method for adding a SentiWord to the sentiMap
     * which handles possible duplicate SentiWords
     * @param String word the string under consideration (may have 1-4 words);
     * @param Double posVal - the associated positive sentiment value
     * @param Double negVal - the associated negative sentiment value
     */  
    private void addSentiWord(String word, Double posVal, Double negVal) {
        String[] splitWord = word.split("_");
        //printSplitWord(splitWord);
        if (splitWord.length == 1) sentimentMap.put(splitWord[0], null, null, null, posVal, negVal);
        else if (splitWord.length == 2) sentimentMap.put(splitWord[0], splitWord[1], null, null, posVal, negVal);
        else if (splitWord.length == 3) sentimentMap.put(splitWord[0], splitWord[1], splitWord[2], null, posVal, negVal);
        else sentimentMap.put(splitWord[0], splitWord[1], splitWord[2], splitWord[3], posVal, negVal);
    }


    /**
     * Helper method for extracting words from SynsetWords column
     * of the WordNet database
     * @param String words from a single line (which share meaning)
     * @return ArrayList<String> array of synonyms for the given wordset
     */  
    private static ArrayList<String> getSynsetWords(String words) {
        ArrayList<String> wordList = new ArrayList<String>();

        String[] tabSepWords = words.split(" ");

        for (String word : tabSepWords) {
            int poundInd = word.indexOf("#");
            wordList.add(word.substring(0, poundInd)); // remove WordNet's pound encoding
        }

        return wordList;
    }


    /**
     * Helper method for displaying the pos/neg counts
     * of all 4th Level words from the Wordnet DB
     */  
    private void test4thLevel() {
        for (Map.Entry<String, HashMap<String, HashMap<String, HashMap<String, SentiWord>>>> map2 : this.sentimentMap.sentiMap.entrySet()) {
            String word1 = map2.getKey();
            for (Map.Entry<String, HashMap<String, HashMap<String, SentiWord>>> map3 : map2.getValue().entrySet()) {
                String word2 = map3.getKey();
                for (Map.Entry<String, HashMap<String, SentiWord>> map4 : map3.getValue().entrySet()) {
                    String word3 = map4.getKey();
                    for (Map.Entry<String, SentiWord> entry : map4.getValue().entrySet()) {
                        if (entry.getKey().length() > 0) {
                            Integer size = entry.getValue().posVals.size();
                            System.out.println(word1 + " " + word2 + " " + word3 + " " + entry.getKey());
                            System.out.println(entry.getValue().getWC());
                            for (int i = 0; i < size; i++) {
                            	
                                System.out.println(entry.getValue().posVals.get(i) + "\t" + entry.getValue().negVals.get(i));
                            }
                            System.out.println("\n");
                        }
                    }
                }
            }
        }
    }
    
    public SentiWord test(String w1, String w2, String w3, String w4){
    	return this.sentimentMap.get(w1,w2,w3,w4);
    }
    
    /**
     * Main
     * @param String[] input
     */
    public static void main(String[] args) {

        String sentiPath;
        if (args.length == 0) System.out.println("Usage: java WordNet sentiWordNetPath");
        else {
            sentiPath = args[0];
            WordNet wd = new WordNet(sentiPath);
            System.out.println("Finished loading SentiWordNet DB");
            wd.test4thLevel();
        }
    }






}
