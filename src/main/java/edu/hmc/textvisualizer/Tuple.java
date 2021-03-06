package edu.finalproj;

import java.awt.*;


/**
 * Created by alex on 12/11/14.
 *
 * Tuple stores the part of speech (pos), color, and the index
 * within a sentence of a word, and supplies getter methods for
 * those data members
 */
public class Tuple {
    public String pos;
    public String label;
    public int cnt;
    

    public Tuple(String pos, String label, int cnt) {
        this.pos = pos;
        this.label = label;
        this.cnt = cnt;
    }
    
    public String getPOS(){
    	return this.pos;
    }
    
    public int getCnt(){
    	return this.cnt;
    }
    

    public Color getColor(){
    	if (pos.equals("MISC")){
    		return Color.GRAY;
    	}
    	else if (pos.equals("PREP")){
    		return Color.PINK;
    	}
    	else if (pos.equals("DET")){
    		return Color.LIGHT_GRAY;    		
    	}
    	else if (pos.equals("ADJECT")){
    		return Color.BLUE;
    	}
    	else if (pos.equals("NOUNS")){
    		return Color.GREEN;
    	}
    	else if (pos.equals("ADVERBS")){
    		return  Color.MAGENTA;
    	}
    	else if (pos.equals("VERBS")){
    		return  Color.RED;
    	}
    	else return Color.WHITE;
    }
}
