package edu.finalproj;

import java.util.HashMap;



/**
 * Stores the quadruple nested HashMap of words and their pos/neg values
 */
public class SentiMap {
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
    protected SentiWord get(String word1, String word2, String word3, String word4) {
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
    protected SentiWord get(String[] words) {
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


    protected void put(String word1, String word2, String word3, String word4, Double posVal, Double negVal) {

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
                                	
                                    smap4.put(word4, new SentiWord(posVal, negVal, 4));
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
                                    smap4.put("", new SentiWord(posVal, negVal, 3));
                                    smap3.put(word3, smap4);
                                    smap2.put(word2, smap3);
                                    sentiMap.put(word1, smap2);
                                }
                            }
                        }

                        else {
                            if (word4 != null) {
                                map4.put(word4, new SentiWord(posVal, negVal, 4));
                                smap3.put(word3, map4);
                                smap2.put(word2, smap3);
                                sentiMap.put(word1, smap2);
                            } else {
                                map4.put("", new SentiWord(posVal, negVal, 3));
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
                                smap4.put("", new SentiWord(posVal, negVal, 2));
                                smap3.put("", smap4);
                                smap2.put(word2, smap3);
                                sentiMap.put(word1, smap2);
                            }
                        } else {
                            map4.put("", new SentiWord(posVal, negVal, 2));
                            smap3.put("", map4);
                            smap2.put(word2, smap3);
                            sentiMap.put(word1, smap2);

                        }
                    }
                }

                else {
                    if (word3 != null) {
                        if (word4 != null) {
                            map4.put(word4, new SentiWord(posVal, negVal, 4));
                            map3.put(word3, map4);
                            smap2.put(word2, map3);
                            sentiMap.put(word1, smap2);
                        } else {
                            map4.put("", new SentiWord(posVal, negVal, 3));
                            map3.put(word3, map4);
                            smap2.put(word2, map3);
                            sentiMap.put(word1, smap2);
                        }
                    } else {
                        map4.put("", new SentiWord(posVal, negVal, 2));
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
                            smap4.put("", new SentiWord(posVal, negVal, 1));
                            smap3.put("", smap4);
                            smap2.put("", smap3);
                            sentiMap.put(word1, smap2);
                        }

                    } else {
                        map4.put("", new SentiWord(posVal, negVal, 1));
                        smap3.put("", map4);
                        smap2.put("", smap3);
                        sentiMap.put(word1, smap2);
                    }

                } else {
                    map4.put("", new SentiWord(posVal, negVal, 1));
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
                        map4.put(word4, new SentiWord(posVal, negVal, 4));
                        map3.put(word3, map4);
                        map2.put(word2, map3);
                        sentiMap.put(word1, map2);
                    } else {
                        map4.put("", new SentiWord(posVal, negVal, 3));
                        map3.put(word3, map4);
                        map2.put(word2, map3);
                        sentiMap.put(word1, map2);
                    }
                } else {
                    map4.put("", new SentiWord(posVal, negVal, 2));
                    map3.put("", map4);
                    map2.put(word2, map3);
                    sentiMap.put(word1, map2);
                }
            } else {
                map4.put("", new SentiWord(posVal, negVal, 1));
                map3.put("", map4);
                map2.put("", map3);
                sentiMap.put(word1, map2);
            }
        }
    }
}