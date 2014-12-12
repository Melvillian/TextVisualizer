package edu.finalproj;

/**
 * Created by alex on 12/5/14.
 */

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
     * Returns a list with 2 elements, the first the positive value for the given phrase
     * and the second the negative value for the given phrase
     * @param word1
     * @param word2
     * @param word3
     * @param word4
     * @return
     */
    public ArrayList<Double> getPosNegValues(String word1, String word2, String word3, String word4) {
        return this.sentimentMap.get(word1, word2, word3, word4);
    }

    /**
     * Helper method to print a String array
     *
     * @param splitWord
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
     * of all 4thLevel words from the Wordnet DB
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


    /**
     * SentiMap stores a quadruple nested HashMap, each level of the HashMap contains a word as a key and a deeper HashMap
     * as its value, until the 4th level at last stores the SentiWord. The idea behind this data structure is that TextVisualizer
     * will traverse through a PDF at most 4 words per iteration, looking for those 4 consecutive words in the Wordnet database. If
     * a phrase will all 4 consecutive words exists, it returns the sentiment value for that phrase and increments the global traversal
     * counter by 4. If only i words of the phrase exist, where 1 <= i <= 3, then the sentiment value of those i words of the phrase
     * are returned and the counter is incremented by i. If no phrase is found we treat the sentiment as 0.
     **/
    private class SentiMap {
        HashMap<String, HashMap<String, HashMap<String, HashMap<String, SentiWord>>>> sentiMap;

        SentiMap() {
            this.sentiMap = new HashMap<String, HashMap<String, HashMap<String, HashMap<String, SentiWord>>>>();
        }


        /**
         * returns the SentiWord for a given depth of word. We always pass
         * in 4 words and we let the function decide how to increase the cnt
         *
         * @param word1
         * @param word2
         * @param word3
         * @param word4
         * @return
         */
        public ArrayList<Double> get(String word1, String word2, String word3, String word4) {
            if (sentiMap.containsKey(word1)) {
                if (sentiMap.get(word1).containsKey(word2)) {
                    if (sentiMap.get(word1).get(word2).containsKey(word3)) {
                        if (sentiMap.get(word1).get(word2).get(word3).containsKey(word4)) {
                            cnt += 4;
                            SentiWord sw = sentiMap.get(word1).get(word2).get(word3).get(word4);
                            ArrayList<Double> posNegVals = sw.get();
                            sentiMap.get(word1).get(word2).get(word3).put(word4, sw);
                            return posNegVals;
                        } else {
                            cnt += 3;
                            SentiWord sw = sentiMap.get(word1).get(word2).get(word3).get("");
                            ArrayList<Double> posNegVals = sw.get();
                            sentiMap.get(word1).get(word2).get(word3).put("", sw);
                            return posNegVals;
                        }
                    } else {
                        cnt += 2;
                        SentiWord sw = sentiMap.get(word1).get(word2).get("").get("");
                        ArrayList<Double> posNegVals = sw.get();
                        sentiMap.get(word1).get(word2).get("").put("", sw);
                        return posNegVals;
                    }

                } else {
                    cnt++;
                    HashMap<String, HashMap<String, HashMap<String, SentiWord>>> test = sentiMap.get(word1);
                    HashMap<String, HashMap<String, SentiWord>> testinner = sentiMap.get(word1).get("");
                    HashMap<String, SentiWord> testinnerinner = sentiMap.get(word1).get("").get("");
                    SentiWord sw = sentiMap.get(word1).get("").get("").get("");
                    ArrayList<Double> posNegVals = sw.get();
                    sentiMap.get(word1).get("").get("").put("", sw);
                    return posNegVals;
                }
            } else {
                cnt++;
                ArrayList<Double> tuple = new ArrayList<Double>();
                tuple.add(0.0);
                tuple.add(0.0);
                return tuple;
            }
        }


        public void put(String word1, String word2, String word3, String word4, Double posVal, Double negVal) {
            SentiWord s = new SentiWord(posVal, negVal);


            HashMap<String, HashMap<String, HashMap<String, SentiWord>>> map2 = new HashMap<String, HashMap<String, HashMap<String, SentiWord>>>();
            HashMap<String, HashMap<String, SentiWord>> map3 = new HashMap<String, HashMap<String, SentiWord>>();
            HashMap<String, SentiWord> map4 = new HashMap<String, SentiWord>();

            if (sentiMap.containsKey(word1)) {
                HashMap<String, HashMap<String, HashMap<String, SentiWord>>> smap2 = sentiMap.get(word1);
                if (word2 != null) {
                    if (smap2.containsKey((word2))) {
                        HashMap<String, HashMap<String, SentiWord>> smap3 = sentiMap.get(word1).get(word2);
                        if (word3 != null) {
                            if (smap3.containsKey(word3)) {
                                HashMap<String, SentiWord> smap4 = sentiMap.get(word1).get(word2).get(word3);
                                if (word4 != null) {
                                    if (smap4.containsKey(word4)) {
                                        SentiWord se = sentiMap.get(word1).get(word2).get(word3).get(word4);
                                        se.add(posVal, negVal);
                                        smap4.put(word4, se);
                                        smap3.put(word3, smap4);
                                        smap2.put(word2, smap3);
                                        sentiMap.put(word1, smap2);
                                    } else {
                                        smap4.put(word4, s);
                                        smap3.put(word3, map4);
                                        smap2.put(word2, smap3);
                                        sentiMap.put(word1, smap2);
                                    }

                                } else {
                                    if (smap4.containsKey("")) {
                                        SentiWord se = smap4.get("");
                                        se.add(posVal, negVal);
                                        smap4.put("", se);
                                        smap3.put(word3, smap4);
                                        smap2.put(word2, smap3);
                                        sentiMap.put(word1, smap2);
                                    } else {
                                        smap4.put("", s);
                                        smap3.put(word3, smap4);
                                        smap2.put(word2, smap3);
                                        sentiMap.put(word1, smap2);
                                    }
                                }
                            } else {
                                if (word4 != null) {
                                    map4.put(word4, s);
                                    smap3.put(word3, map4);
                                    smap2.put(word2, smap3);
                                    sentiMap.put(word1, smap2);
                                } else {
                                    map4.put("", s);
                                    smap3.put(word3, map4);
                                    smap2.put(word2, smap3);
                                    sentiMap.put(word1, smap2);
                                }

                            }

                        } else {
                            if (smap3.containsKey("")) {
                                HashMap<String, SentiWord> smap4 = smap3.get("");
                                if (smap4.containsKey("")) {
                                    SentiWord se = smap4.get("");
                                    se.add(posVal, negVal);
                                    smap4.put("", se);
                                    smap3.put("", smap4);
                                    smap2.put(word2, smap3);
                                    sentiMap.put(word1, smap2);

                                } else {
                                    smap4.put("", s);
                                    smap3.put("", smap4);
                                    smap2.put(word2, smap3);
                                    sentiMap.put(word1, smap2);
                                }
                            } else {
                                map4.put("", s);
                                smap3.put("", map4);
                                smap2.put(word2, smap3);
                                sentiMap.put(word1, smap2);

                            }
                        }
                    } else {
                        if (word3 != null) {
                            if (word4 != null) {
                                map4.put(word4, s);
                                map3.put(word3, map4);
                                smap2.put(word2, map3);
                                sentiMap.put(word1, smap2);
                            } else {
                                map4.put("", s);
                                map3.put(word3, map4);
                                smap2.put(word2, map3);
                                sentiMap.put(word1, smap2);
                            }
                        } else {
                            map4.put("", s);
                            map3.put("", map4);
                            smap2.put(word2, map3);
                            sentiMap.put(word1, smap2);
                        }
                    }
                } else {
                    if (smap2.containsKey("")) {
                        HashMap<String, HashMap<String, SentiWord>> smap3 = sentiMap.get(word1).get("");
                        if (smap3.containsKey("")) {
                            HashMap<String, SentiWord> smap4 = smap3.get("");
                            if (smap4.containsKey("")) {
                                SentiWord se = smap4.get("");
                                se.add(posVal, negVal);
                                smap4.put("", se);
                                smap3.put("", smap4);
                                smap2.put("", smap3);
                                sentiMap.put(word1, smap2);
                            } else {
                                smap4.put("", s);
                                smap3.put("", smap4);
                                smap2.put("", smap3);
                                sentiMap.put(word1, smap2);
                            }

                        } else {
                            map4.put("", s);
                            smap3.put("", map4);
                            smap2.put("", smap3);
                            sentiMap.put(word1, smap2);
                        }

                    } else {
                        map4.put("", s);
                        map3.put("", map4);
                        smap2.put("", map3);
                        sentiMap.put(word1, smap2);
                    }
                }
            } else {
                if (word2 != null) {
                    if (word3 != null) {
                        if (word4 != null) {
                            map4.put(word4, s);
                            map3.put(word3, map4);
                            map2.put(word2, map3);
                            sentiMap.put(word1, map2);
                        } else {
                            map4.put("", s);
                            map3.put(word3, map4);
                            map2.put(word2, map3);
                            sentiMap.put(word1, map2);
                        }
                    } else {
                        map4.put("", s);
                        map3.put("", map4);
                        map2.put(word2, map3);
                        sentiMap.put(word1, map2);
                    }
                } else {
                    map4.put("", s);
                    map3.put("", map4);
                    map2.put("", map3);
                    sentiMap.put(word1, map2);
                }
            }
        }
    }

    public void put(String word1, String word2, String word3, String word4, Double posVal, Double negVal) {
        this.sentimentMap.put(word1, word2, word3, word4, posVal, negVal);
    }


    public static void main(String[] args) {

        String sentiPath;
        if (args.length == 0) System.out.println("Usage: java WordNet sentiWordNetPath");
        else {
            sentiPath = args[0];
            System.out.println("Starting loading SentiWordNetDB...");
            WordNet wd = new WordNet(sentiPath);
            System.out.println("Finished loading SentiWordNet DB");
        }
    }
}