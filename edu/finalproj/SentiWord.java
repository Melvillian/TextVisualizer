package edu.finalproj;

/**
 * Created by alex on 12/5/14.
 */

import java.util.ArrayList;

/**
 * SentiWord represents a word from the SentiWordNet database.
 * Because words that are spelled the same may have different
 * meanings, and thus different pos/neg scores, SentiWord holds
 * all of a word's pos/neg scores and chooses which pos/neg
 * score to use in a round-robin fashion
 */
public class SentiWord {
    ArrayList<Double> posVals;
    ArrayList<Double> negVals;
    int scoreInd;
    int wordCount;
    

    SentiWord(Double posVal, Double negVal) {

        this.posVals = new ArrayList<Double>();
        this.posVals.add(posVal);
        this.negVals = new ArrayList<Double>();
        this.negVals.add(negVal);
        scoreInd = 0;
        wordCount = 1;   
    }
    
    SentiWord(Double posVal, Double negVal, int wordCount) {

        this.posVals = new ArrayList<Double>();
        this.posVals.add(posVal);
        this.negVals = new ArrayList<Double>();
        this.negVals.add(negVal);
        scoreInd = 0;
        this.wordCount = wordCount;
    }
    
    public void add(Double posVal, Double negVal) {
        this.posVals.add(posVal);
        this.negVals.add(negVal);

    }
    
    public void setWC(int wc) {
        this.wordCount = wc;
    }
    
    public int getWC(){
    	return this.wordCount;
    }

    /**
     * Returns an ArrayList with 2 elements. The first
     * is a positive SentiWord value and the second is
     * the negative SentiWord value
     * @return
     */
    public ArrayList<Double> get() {
        assert this.posVals != null;

        ArrayList<Double> outputArr = new ArrayList<Double>();
        outputArr.add(posVals.get(this.scoreInd));
        outputArr.add(negVals.get(this.scoreInd));

        this.scoreInd++;
        if (posVals.size() == this.scoreInd) this.scoreInd = 0;
        return outputArr;
    }
    
    /**
     * Returns an double[] with 2 elements. The first
     * is a positive SentiWord value and the second is
     * the negative SentiWord value
     * @return
     */
    public double[] get() {
        assert this.posVals != null;

        double[] outputArr = {posVals.get(this.scoreInd), negVals.get(this.scoreInd)};

        this.scoreInd++;
        if (posVals.size() == this.scoreInd) this.scoreInd = 0;
        return outputArr;
    }
}
