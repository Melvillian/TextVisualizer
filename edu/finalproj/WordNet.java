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

import edu.finalproj.SentiWord;

/**
 * The WordNet class encapsulates all of the logic
 * that deals with parsing and storing WordNet databases
 * as well as providing an API for access to the underlying
 * Wordnet data
 */
public class WordNet {
    SentiMap sentiMap;
    private static Integer cnt = 0;


    WordNet(String sentiPath) {
        sentiMap = new SentiMap();

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
        if (splitWord.length == 1) sentiMap.put(splitWord[0], null, null, null, posVal, negVal);
        else if (splitWord.length == 2) sentiMap.put(splitWord[0], splitWord[1], null, null, posVal, negVal);
        else if (splitWord.length == 3) sentiMap.put(splitWord[0], splitWord[1], splitWord[2], null, posVal, negVal);
        else sentiMap.put(splitWord[0], splitWord[1], splitWord[2], splitWord[3], posVal, negVal);
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
        SentiMap sm = this.sentiMap;
        for (Map.Entry<String, HashMap<String, HashMap<String, HashMap<String, SentiWord>>>> map2 : this.sentiMap.sentiMap.entrySet()) {
            String word1 = map2.getKey();
            for (Map.Entry<String, HashMap<String, HashMap<String, SentiWord>>> map3 : map2.getValue().entrySet()) {
                String word2 = map3.getKey();
                for (Map.Entry<String, HashMap<String, SentiWord>> map4 : map3.getValue().entrySet()) {
                    String word3 = map4.getKey();
                    for (Map.Entry<String, SentiWord> entry: map4.getValue().entrySet()) {
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







    /**
     * Stores the quadruple nested HashMap of words and their pos/neg values
     */
    private class SentiMap {
        HashMap<String, HashMap<String, HashMap<String, HashMap<String, SentiWord>>>> sentiMap;

        SentiMap() {
            this.sentiMap = new HashMap<String, HashMap<String, HashMap<String, HashMap<String, SentiWord>>>>();
        }


       /**
         * returns the SentiWord for a given depth of word. We always pass
         * in 4 words and we let the function decide how to increase the cnt
         * @param word1
         * @param word2
         * @param word3
         * @param word4
         * @return
         */
        public SentiWord get(String word1, String word2, String word3, String word4) {
        	SentiWord se = new SentiWord(0.0 ,0.0, 1);
            if (sentiMap.containsKey(word1)) {
                if (sentiMap.get(word1).containsKey(word2)) {
                    if (sentiMap.get(word1).get(word2).containsKey(word3)) {
                        if (sentiMap.get(word1).get(word2).get(word3).containsKey(word4)) {
                            se = sentiMap.get(word1).get(word2).get(word3).get(word4);
                        } else {
                        	if (sentiMap.get(word1).get(word2).get(word3).containsKey("")){
                        		se = sentiMap.get(word1).get(word2).get(word3).get("");
                        	}
                        }
                    } else {
                      	if (sentiMap.get(word1).get(word2).containsKey("")){
                            if (sentiMap.get(word1).get(word2).get("").containsKey("")){
                            	se = sentiMap.get(word1).get(word2).get("").get("");
                            }
                    	}
                    }

                } else {
                	if (sentiMap.get(word1).containsKey("")){
	                	if (sentiMap.get(word1).get("").containsKey("")){
		                    if (sentiMap.get(word1).get("").get("").containsKey("")){
		                    	se = sentiMap.get(word1).get("").get("").get("");
		                    }
	                	}
                	}
                }
            }
            return se;
        }

        /**
         * returns the SentiWord for a given depth of word. We always pass
         * in 4 words and we let the function decide how to increase the cnt
         * @param word[] 
         * @return
         */
        public SentiWord get(String[] words) {
        	SentiWord se = new SentiWord(0.0 ,0.0, 1);
            if (sentiMap.containsKey(words[0])) {
                if (sentiMap.get(words[0]).containsKey(words[1])) {
                    if (sentiMap.get(words[0]).get(words[1]).containsKey(words[3])) {
                        if (sentiMap.get(words[0]).get(words[1]).get(words[2]).containsKey(words[3])) {
                            se = sentiMap.get(words[0]).get(words[1]).get(words[2]).get(words[3]);
                        } else {
                        	if (sentiMap.get(words[0]).get(words[1]).get(words[2]).containsKey("")){
                        		se = sentiMap.get(words[0]).get(words[1]).get(words[2]).get("");
                        	}
                        }
                    } else {
                    	if (sentiMap.get(words[0]).get(words[1]).containsKey("")){
                            if (sentiMap.get(words[0]).get(words[1]).get("").containsKey("")){
                            	se = sentiMap.get(words[0]).get(words[1]).get("").get("");
                            }
                    	}
                    }

                } else {
                	if (sentiMap.get(words[0]).containsKey("")){
	                	if (sentiMap.get(words[0]).get("").containsKey("")){
		                    if (sentiMap.get(words[0]).get("").get("").containsKey("")){
		                    	se = sentiMap.get(words[0]).get("").get("").get("");
		                    }
	                	}
                	}
                }
            }
            return se;
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
                            }

                            else {
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

                        }

                        else {
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
                    }

                    else {
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
                }

                else {
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
            }


            else {
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
}
