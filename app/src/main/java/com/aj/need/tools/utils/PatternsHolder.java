package com.aj.need.tools.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * https://openclassrooms.com/courses/concevez-votre-site-web-avec-php-et-mysql/memento-des-expressions-regulieres
 *
 * @author ANAGBLA  Joan
 */
public class PatternsHolder {

    //public static String space="\\s"; //" " //Espace blanc (correspond à\t\n\r) //use instead blank
    public static final String blank = "\\s+"; //"    "
    public static final String notBlank = "\\S+";//^"     "
    public static final String nonaccentuedchar = "\\w"; //[a-zA-Z0-9_]
    public static final String notnonaccentuedchar = "\\W"; // [^a-zA-Z0-9_]
    public static final String aword = "\\w+"; //[a-zA-Z0-9_]+
    public static final String notWord = "\\W+"; //[^a-zA-Z0-9_]+
    public static final String num = "\\d"; //[0-9]
    public static final String notNum = "\\D"; //[^0-9]
    public static final String nums = "\\d+"; //[0-9]+
    public static final String notNums = "\\D+"; //[^0-9]+
    public static final String email = "(.+)@(.+)\\.(.+)"; //RFC is : .+@.+
    public static final String pass = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,})";
    public static final String uname = "((?=.*[a-z])^[a-zA-Z](\\w{2,}))";

    public static final Map<String, String> accents = new HashMap<String, String>();

    static {
        accents.put("e", "[éêèëe]");
        accents.put("a", "[àâäa]");
        accents.put("u", "[ùûüu]");
        accents.put("o", "[ôöo]");
        accents.put("i", "[îïi]");
        accents.put("y", "[ÿy]");
        accents.put("c", "[çc]");
    }


    public static boolean isValidUsername(String input) {
        return /*isValidWord(input) &&*/(Pattern.compile(
                PatternsHolder.uname
        ).matcher(input).matches());
    }


    /**
     * Check if the {input} is a valid word (contains only letters and numbers)
     * and contains almost more than one letter or number
     *
     * @param input
     * @return
     */
    public static boolean isValidWord(String input) {
        return (!Pattern.compile(
                PatternsHolder.notWord
        ).matcher(input).matches())
                &&
                (Pattern.compile(
                        PatternsHolder.aword
                ).matcher(input).matches());
    }


    /**
     * Check if the {input} is a valid word (contains only letters and numbers)
     * and contains almost more than one letter or number
     *
     * @param input
     * @return
     */
    public static boolean isValidEmail(String input) {
        return Pattern.compile(
                PatternsHolder.email
        ).matcher(input).matches();
    }


    public static boolean isValidPass(String input) {
        return Pattern.compile(
                PatternsHolder.pass
        ).matcher(input).matches();
    }


    /**
     * Replace an accented word by a non-accented word (including ç)
     *
     * @param word
     * @return
     */
    public static String refine(String word) {
        for (Entry<String, String> entry : accents.entrySet())
            word = word.trim().toLowerCase().replaceAll(entry.getValue(), entry.getKey());
        return word;
    }

    /**
     * Replace a word by an accented string-regex built from the word
     *
     * @param word
     * @return
     */
    public static String stain(String word) {
        word = refine(word);
        for (Entry<String, String> entry : accents.entrySet())
            word = word.replaceAll(entry.getKey(), entry.getValue());
        return word;
    }


    /**
     * Return a set of words contained in a string
     * (only one occurence of a word)
     *
     * @param string
     * @param pattern
     * @return
     */
    public static Set<String> wordSet(String string, String pattern) {
        return new HashSet<String>(
                Arrays.asList(string.trim().toLowerCase().split(pattern)));
    }


    /**
     * Return a list of words contained in a string
     * (only one occurence of a word)
     *
     * @param string
     * @param pattern
     * @return
     */
    public static List<String> wordList(String string, String pattern) {
        return Arrays.asList(string.trim().toLowerCase().split(pattern));
    }


    /**
     * Return a String built from a collection of Strings
     *
     * @param wordsList
     * @param old
     * @param neew
     * @return
     */
    public static String stringOfColl(Collection<String> wordsList, String old, String neew) {
        return (wordsList.toString()
                .substring(1, wordsList.toString().length() - 1)).replace(old, neew);
    }


    /**
     * Return a String built from a collection of Patterns
     *
     * @param list
     * @param old
     * @param neew
     * @return
     */
    public static String stringOfColl(List<Pattern> list, String old, String neew) {
        return (list.toString()
                .substring(1, list.toString().length() - 1)).replace(old, neew);
    }


    public static void main(String[] args) {
		/*System.out.println(Pattern.matches(word, "574ythtgtrg"));
		System.out.println(Pattern.matches(word, "574ythétgtrg"));
		System.out.println(wordSet("57:y4_y,thét;gt-rg!ujh",notWord));*/

		/*List<String> words= Arrays.asList("","y","AB","JOE","NOEL","JOANE","JOANNE","TTANCK");
		for(String word : words)
			System.out.println("fuzzyfy("+word+"): "+fuzzyfy(word,2));*/

        //System.out.println(refine("héêàlèônÿçç"));
        List<String> d1 = wordList(refine("Son nom est célébré par le bocage qui frémit, et par le ruisseau qui murmure, les vents lemportent jusquà larc céleste, larc de grâce et de consolation que sa main tendit dans les nuages."), notWord);
        System.out.println("d1=" + d1);
        System.out.println("d1_size=" + d1.size());

        List<String> d2 = wordList(refine("À peine distinguait-on deux buts à lextrémité de la carrière : des chênes ombrageaient lun, autour de lautre des palmiers se dessinaient dans léclat du soir."), notWord);
        System.out.println("d2=" + d2);
        System.out.println("d2_size=" + d2.size());

        List<String> d3 = wordList(refine("Ah ! le beau temps de mes travaux poétiques ! les beaux jours que jai passés près de toi ! Les premiers, inépuisables de joie, de paix et de liberté ; les derniers, empreints dune mélancolie qui eut bien aussi ses charmes."), notWord);
        System.out.println("d3=" + d3);
        System.out.println("d3_size=" + d3.size());
        //System.out.println(stain("vélo"));
        //System.out.println(stain("héêàlèônÿçç"));
    }

}