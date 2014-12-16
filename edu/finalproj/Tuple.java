package edu.finalproj;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashSet;


/**
 * Created by alex on 12/11/14.
 */
public class Tuple {
    public String pos;
    public int cnt;
    

    public Tuple(String pos, int cnt) {
    	char prefix = pos.charAt(0);
    	switch (prefix){
    		case 'V':
    			pos = "VERB";
    			break;
    		case 'J':
    			pos = "ADJECT";
    			break;
    		case 'R':
    			pos = "ADVERB";
    			break;
    		case 'D':
    			pos = "DET";
    			break;
    		case 'I': 
    			pos = "PREP";
    			break;
    		case 'N':
    			pos = "NOUN";
    			break;
    		case 'P':
    			switch (pos.charAt(1)){
    				case 'R': 
    					pos = "NOUN";
    			};
    			break;
    		default: pos = "MISC";
    	};
    	
        this.pos = pos;
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
    	else if (pos.equals("NOUN")){
    		return Color.GREEN;
    	}
    	else if (pos.equals("ADVERB")){
    		return  Color.MAGENTA;
    	}
    	else if (pos.equals("VERB")){
    		return  Color.RED;
    	}
    	else return Color.WHITE;
    }
}
