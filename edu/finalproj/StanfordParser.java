package edu.finalproj;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;


/**
 * Created by alex on 12/11/14.
 */
public class StanfordParser {

    private static final int MAXDEPTH = 3;  // we put MAXDEPTH 3, but this will cause us to get all POS's from
                                            // a maximum depth of 4 as calculated in countParse()

    private StanfordCoreNLP pipeline;
    private static final HashSet<String> MISC = new HashSet<String>();
    private static final HashSet<String> PREP = new HashSet<String>();
    private static final HashSet<String> DET = new HashSet<String>();
    private static final HashSet<String> ADJECT = new HashSet<String>();
    private static final HashSet<String> NOUNS = new HashSet<String>();
    private static final HashSet<String> ADVERBS = new HashSet<String>();
    private static final HashSet<String> VERBS = new HashSet<String>();


    StanfordParser() {
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, parse");
        pipeline = new StanfordCoreNLP(props);
        initializeSets();
    }

    /**
     * Sets up the sets we'll use to check what POS type a given label is
     */
    private void initializeSets() {
        MISC.add("CC");
        MISC.add("CD");
        MISC.add("EX");
        MISC.add("FW");
        MISC.add("LS");
        MISC.add("MD");
        MISC.add("PDT");
        MISC.add("POS");
        MISC.add("SYM");
        MISC.add("TO");
        MISC.add("UH");
        MISC.add("WDT");
        MISC.add("WP");
        MISC.add("WP$");
        MISC.add("WRB");

        PREP.add("IN");

        DET.add("DT");

        ADJECT.add("JJS");
        ADJECT.add("JJR");
        ADJECT.add("JJ");

        NOUNS.add("NN");
        NOUNS.add("NNP");
        NOUNS.add("NNPS");
        NOUNS.add("NNS");
        NOUNS.add("PRP$");
        NOUNS.add("PRP");

        ADVERBS.add("RBR");
        ADVERBS.add("RBS");
        ADVERBS.add("RB");

        VERBS.add("VB");
        VERBS.add("VBD");
        VERBS.add("VBG");
        VERBS.add("VBN");
        VERBS.add("VBP");
        VERBS.add("VBZ");
    }

    /**
     * Given a label from a parsetree node, return which of our 7
     * POS types it is a member of
     * @param label
     * @return
     */
    private String getPOS(String label) {
        if (ADJECT.contains(label)) {
            return "ADJECT";
        }

        else if (DET.contains(label)) {
            return "DET";
        }

        else if (PREP.contains(label)) {
            return "PREP";
        }

        else if (NOUNS.contains(label)) {
            return "NOUNS";
        }

        else if (VERBS.contains(label)) {
            return "VERBS";
        }

        else if (ADVERBS.contains(label)) {
            return "ADVERBS";
        }

        else {
            return "MISC";
        }
    }

    /**
     * Given a nested parse tree, return the counts of the 7 types of
     * parts of speech at either the MAXDEPTH depth (where the root is defined
     * as depth 0) or an earlier depth is the tree does not extend that far.
     * Also we need to store the words that exist underneath each Tuple.
     * The values are stored as a list of tuples in order to preserve the
     * ordering of the POS's.
     * @param tree
     * @param depth
     * @return
     */
    private ArrayList<Tuple> countParse(ParseTree tree, int depth) {
        ArrayList<Tuple> counts = new ArrayList<Tuple>();

        if (tree.isTerminal()) {
            String pos = getPOS(tree.getLabel());
            Tuple tup = new Tuple(pos, 1);
            counts.add(tup);
            return counts;
        }
        else if (depth == MAXDEPTH) {  // we've reached the depth we're willing to go
            for (ParseTree subtree : tree.getChildren()) {
                String label = subtree.getLabel();
                int subtreeTerminalNum = subtree.getTerminalNum();
                String pos = getPOS(label);
                Tuple tup = new Tuple(pos, subtreeTerminalNum);
                counts.add(tup);
            }
            return counts;
        }
        else { // we still need to traverse nodes
            for (ParseTree subtree : tree.getChildren()) {
                ArrayList<Tuple> subtreeTuples = countParse(subtree, depth + 1);
                counts.addAll(subtreeTuples);
            }

            return counts;
        }
    }

    /**
     * Given some page text, returns a list of lists, where the inner list
     * contains the tuple counts for the 7 parts of speech
     * @param text
     * @return
     */
    public ArrayList<ArrayList<Tuple>> parseText(String text) {
        Annotation document = new Annotation(text);
        pipeline.annotate(document);
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);

        ArrayList<ArrayList<Tuple>> sentenceParses = new ArrayList<ArrayList<Tuple>>();
        for(CoreMap sentence: sentences) {
            // this is the parse tree of the current sentence
            Tree tree = sentence.get(TreeAnnotation.class);

            ParseTree ps = new ParseTree(tree.toString());
            ps = ps.getChild(0); // disregard root node, now root is S
            sentenceParses.add(countParse(ps, 0));

        }

        return sentenceParses;
    }
    
    private ArrayList<Tuple> getConstituents(Tree tree, int depth){
    	ArrayList<Tuple> ret = new ArrayList<Tuple>();
    	int iter = tree.numChildren();
    	ArrayList<Tree> treelist = (ArrayList<Tree>)tree.getChildrenAsList();
    	while ((iter > 0) && (depth > 0)){
    		Tree next = treelist.remove(0);
    		if (!next.isLeaf()){ //we only add kids if kid exist.
    			iter += next.numChildren();
	    		ArrayList<Tree> subtrees = (ArrayList<Tree>) next.getChildrenAsList();
	    		for (Tree branch : subtrees){
	    			treelist.add(branch);
	    		};
    		};
    		depth--;
    	};
    	for (Tree branch : treelist){
    		Tuple tuple = new Tuple(branch.value(), branch.getLeaves().size());
    		ret.add(tuple);
    	}
    	return ret;
    }


    public static void main(String[] args) {
        try {

            StanfordParser sp = new StanfordParser();
            PDDocument book = PDDocument.load(new File("/Users/alex/Desktop/CodingFolder/workspace/NLP/pdfs/Dune.pdf"));
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setStartPage(2);
            stripper.setEndPage(3);
            String pageText = stripper.getText(book);
            System.out.println("TEXT:\n" + pageText + "\n");





        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
