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

    /**
     * Return a fuzzy string-regex (with extra, substituted(or transposed) or missing
     * characters) from an original string.
     * The word to fuzzify  must be at least one character longer than fuzzy
     * (because fuzzy characters will be removed from it)
     * otherwise extended search based only on the first character is performed.
     *
     * @param word
     * @param fuzzy
     * @return
     */
    public static String fuzzyfy(String word, int fuzzy, String head) {//TODO stain the words smartly
        System.out.println("\nmot=" + word + " &fuzzy=" + fuzzy);//debug
        if (fuzzy == 0) return head + word;//strict match (can be incomplete but not fuzzy)
        if (word.length() == 0) return ".{0,}"; //anything
        if (!(word.length() > fuzzy)) return head + word.charAt(0);
        StringBuilder fuzzyfied = new StringBuilder();
        //i begin at 1(not 0) because :
        //-substring is end exclusive
        //-beginning by i=0 would be too large :
        //(searching "tuo" would be equivalent to search (l<->(tu=..)ol)), so :
        //-the first character must be harder than rock (it define the direction)
        for (int i = 1; i <= word.length() - (fuzzy); i++) {
            String prefix = word.substring(0, i);
            System.out.print("prefix : " + prefix + "  ");//debug
            String trunk = word.substring(i, i + fuzzy);
            System.out.print("trunk : " + trunk + "  ");//debug
            String suffix = word.substring(i + fuzzy);
            System.out.print("suffix : " + suffix + " ");//debug

            if (i == word.length() - (fuzzy))
                fuzzyfied.append(head + prefix + ".{0,}"); //last fuzzyfying is "open end"
            else {
                fuzzyfied.append(head + prefix/*+trunk.substring(0,j)*/
                        + ".{0," + (fuzzy) + "}" + suffix + ".{0,}" + "|");
                /*@Failure : fuzzyfy is Not really generic (TODO).
				  it is too specific for fuzzy=2 especially at this 2 followings 
				  lines that consider trunk's size to 1 
				  ((.replace(j,j+1,) instead of .replace(j,j+k))
				  .replace(j,j+k)) is generic for(k=1,k<=fuzzy;k++)
				  but it creates too much repetitions of some patterns
				  by eg the line before has to repeat only once
				  and the line after "fuzzy" times . 
				  How to conciliate that (line before run for trunk's size of)
				  How to have multistage that repeat only modified trunk size 
				  time for each. By eg the line before modify trunk.length chars
				  so it must repeat once the line after modify 1 char in trunk 
				  so it must repeat trunk.length times TODO later (no more time)*/
                for (int j = 0; j < trunk.length(); j++)
                    //< instead of <= to avoid duplication on last replacement
                    //(replace "" by .{0,}): the second for do the same
                    fuzzyfied.append(head + prefix + new StringBuilder(trunk)
                            .replace(j, j + 1, ".{0," + (fuzzy) + "}") + suffix + ".{0,}" + "|");
                for (int j = 0; j <= trunk.length(); j++)
                    fuzzyfied.append(head + prefix + new StringBuilder(trunk)
                            .replace(j, j, ".{0," + (fuzzy) + "}") + suffix + ".{0,}" + "|");
                //StringBuilder do more precise job than substring here because
                //it makes it possible to replace the first character of the trunk
                //instead of /*+trunk.substring(0,j)*/ that doesn't
            }
            System.out.println(" --> fuzzyfied : " + fuzzyfied); //debug
        }
        return fuzzyfied.toString();
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