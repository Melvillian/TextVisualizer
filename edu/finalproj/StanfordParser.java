package edu.finalproj;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;


/**
 * Created by alex on 12/11/14.
 *
 * StanfordParser
 */
public class StanfordParser {

    private static final int MAXDEPTH = 10;  // Maximum height to which we search for parts of speech in our parser.
                                              // Increasing this number will create smaller vertical rectangles in the
                                              // visualizer.

    private StanfordCoreNLP pipeline;
    private static final HashSet<String> MISC = new HashSet<String>();
    private static final HashSet<String> PREP = new HashSet<String>();
    private static final HashSet<String> DET = new HashSet<String>();
    private static final HashSet<String> ADJECT = new HashSet<String>();
    private static final HashSet<String> NOUNS = new HashSet<String>();
    private static final HashSet<String> ADVERBS = new HashSet<String>();
    private static final HashSet<String> VERBS = new HashSet<String>();


    StanfordParser(String parserType) {
        if (parserType.equals("parser")) {
            Properties props = new Properties();
            props.put("annotators", "tokenize, ssplit, parse");
            pipeline = new StanfordCoreNLP(props);
            initializeSets();
        }
        else {
            Properties props = new Properties();
            props.put("annotators", "tokenize, ssplit");
            pipeline = new StanfordCoreNLP(props);
            initializeSets();
        }
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
        NOUNS.add("NP");
        NOUNS.add("NNP");
        NOUNS.add("NNPS");
        NOUNS.add("NNS");
        NOUNS.add("PRP$");
        NOUNS.add("PRP");

        ADVERBS.add("RBR");
        ADVERBS.add("RBS");
        ADVERBS.add("RB");

        VERBS.add("VB");
        VERBS.add("VP");
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

        if (tree.childIsTerminal()) {
            String pos = getPOS(tree.getLabel());
            Tuple tup = new Tuple(pos, tree.getLabel(), 1);
            counts.add(tup);
            return counts;
        }
        else if (depth == MAXDEPTH) {  // we've reached the depth we're willing to go
            if (tree.getLabel().equals("S") == false) { // if it's not a sentence
                for (ParseTree subtree : tree.getChildren()) {
                    String label = subtree.getLabel();
                    int subtreeTerminalNum = subtree.getTerminalNum();
                    String pos = getPOS(label);
                    Tuple tup = new Tuple(pos, label, subtreeTerminalNum);
                    counts.add(tup);
                }
                return counts;
            } else { // if it is a sentence, continue parsing
                return countParse(tree, 0);

            }
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
            System.out.println(tree);

            ParseTree ps = new ParseTree(tree.toString());
            ps = ps.getChild(0); // disregard root node, now root is S
            sentenceParses.add(countParse(ps, 0));

        }
        return sentenceParses;
    }




    private void testParseText() {
        String TEXT = "A beginning is the time for taking the most delicate care that the balances are \n" +
                "correct";
        StanfordParser sp = new StanfordParser("parser");
        ArrayList<ArrayList<Tuple>> parsedText = sp.parseText(TEXT);
        assert parsedText.size() == 0; // should correspond to only 1 sentence
        ArrayList<Tuple> sentence = parsedText.get(0);
        System.out.println(TEXT);
        for (Tuple tup : sentence) {
            System.out.println(tup.pos);
            System.out.println(tup.cnt);
            System.out.println(" ");
        }


        TEXT = "This every sister of the Bene Gesserit knows";
        parsedText = sp.parseText(TEXT);
        assert parsedText.size() == 0; // should correspond to only 1 sentence
        sentence = parsedText.get(0);
        System.out.println(TEXT);
        for (Tuple tup : sentence) {
            System.out.println(tup.pos);
            System.out.println(tup.cnt);
            System.out.println(" ");
        }

        TEXT = "to begin your study of the life of Muad'Dib, then, take care that you first place him in his time: born in the 57th year of the Padishah Emperor, Shaddam IV";
        parsedText = sp.parseText(TEXT);
        assert parsedText.size() == 0; // should correspond to only 1 sentence
        sentence = parsedText.get(0);
        System.out.println(TEXT);
        for (Tuple tup : sentence) {
            System.out.println(tup.pos);
            System.out.println(tup.cnt);
            System.out.println(" ");
        }

        StanfordParser spsplitter = new StanfordParser("sentence-splitter");
        TEXT = "A beginning is the time for taking the most delicate care that the balances are correct. This every sister of the Bene Gesserit knows. To begin your study of the life of Muad'Dib, then, take care that you first place him in his time: born in the 57th year of the Padishah Emperor, Shaddam IV. And take the most special care that you locate Muad'Dib in his place: the planet Arrakis. Do not be deceived by the fact that he was born on Caladan and lived his first fifteen years there. Arrakis, the planet known as Dune, is forever his place.\n" +
                "-from \"Manual of Muad'Dib\" by the Princess Irulan\n" +
                "In the week before their departure to Arrakis, when all the final scurrying about had reached a nearly unbearable frenzy, an old crone came to visit the mother of the boy, Paul.\n" +
                "It was a warm night at Castle Caladan, and the ancient pile of stone that had served the Atreides family as home for twenty-six generations bore that cooled-sweat feeling it acquired before a change in the weather.\n" +
                "The old woman was let in by the side door down the vaulted passage by Paul's room and she was allowed a moment to peer in at him where he lay in his bed.\n" +
                "By the half-light of a suspensor lamp, dimmed and hanging near the floor, the awakened boy could see a bulky female shape at his door, standing one step ahead of his mother. The old woman was a witch shadow -- hair like matted spiderwebs, hooded 'round darkness of features, eyes like glittering jewels.\n" +
                "\"Is he not small for his age, Jessica?\" the old woman asked. Her voice wheezed and twanged like an untuned baliset.\n" +
                "Paul's mother answered in her soft contralto: \"The Atreides are known to start late getting their growth, Your Reverence.\"\n" +
                "\"So I've heard, so I've heard,\" wheezed the old woman. \"Yet he's already fifteen.\"\n" +
                "    \"Yes, Your Reverence.\"\n" +
                "\"He's awake and listening to us,\" said the old woman. \"Sly little rascal.\" She chuckled. \"But royalty has need of slyness. And if he's really the Kwisatz Haderach . . . well . . .\"\n" +
                "Within the shadows of his bed, Paul held his eyes open to mere slits. Two bird-bright ovals -- the eyes of the old woman -- seemed to expand and glow as they stared into his.";
        Annotation document = new Annotation(TEXT);
        spsplitter.pipeline.annotate(document);
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);

        ArrayList<ArrayList<Tuple>> sentenceParses = new ArrayList<ArrayList<Tuple>>();
        for(CoreMap sent: sentences) {
            // this is the parse tree of the current sentence
            System.out.println(sent);

        }
    }


    public static void main(String[] args) {
        StanfordParser sp = new StanfordParser("parser");
        sp.testParseText();
    }
}
