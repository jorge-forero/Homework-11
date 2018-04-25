

import java.io.File;
import java.util.*;

/*
 * SD2x Homework #11
 * Improve the efficiency of the code below according to the guidelines in the assignment description.
 * Please be sure not to change the signature of the detectPlagiarism method!
 * However, you may modify the signatures of any of the other methods as needed.
 */

public class PlagiarismDetector {

    public static Map<String, Integer> detectPlagiarism(String dirName, int windowSize, int threshold) {
        File dirFile = new File(dirName);
        String[] files = dirFile.list();

        Map<String, Integer> numberOfMatches = new HashMap<>();

        Map<String, Set<String>> storedPhrases = new HashMap<>();
        StringBuilder phrase;
        for(String file: files) {
            if (file != null) {
                phrase = new StringBuilder(dirName);
                phrase.append("/");
                phrase.append(file);
                storedPhrases.put(file, createPhrases(phrase.toString(), windowSize));
            }
        }

        Set<String> phrase1, phrase2;
        int matches;
        StringBuilder matchesPhrases;

        for (int i = 0; i < files.length -1; i++) {
            for (int j = i + 1; j < files.length && !files[i].equals(files[j]) ; j++) {

                phrase1 = storedPhrases.get(files[i]);
                if (phrase1 == null) return null;

                phrase2 = storedPhrases.get(files[j]);

                if (phrase2 == null) return null;

                matches = findMatches(phrase1, phrase2).size();
                if (matches > threshold) {
                    matchesPhrases = new StringBuilder(files[i]);
                    matchesPhrases.append("-");
                    matchesPhrases.append(files[j]);
                    numberOfMatches.put(matchesPhrases.toString(), matches);
                }
            }
        }

        return sortResults(numberOfMatches);

    }


    /*
     * This method reads the given file and then converts it into a Collection of Strings.
     * It does not include punctuation and converts all words in the file to uppercase.
     */
    protected static List<String> readFile(String filename) {
        if (filename == null) return null;

        List<String> words = new ArrayList<>();

        try {
            Scanner in = new Scanner(new File(filename));
            while (in.hasNext()) {
                words.add(in.next().replaceAll("[^a-zA-Z]", "").toUpperCase());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return words;
    }


    /*
     * This method reads a file and converts it into a Set/List of distinct phrases,
     * each of size "window". The Strings in each phrase are whitespace-separated.
     */
    protected static Set<String> createPhrases(String filename, int window) {
        if (filename == null || window < 1) return null;

        List<String> words = readFile(filename);

        Set<String> phrases = new HashSet<>();

        StringBuilder phrase, word;
        for (int i = 0; i < words.size() - window + 1; i++) {
            phrase = new StringBuilder();
            for (int j = 0; j < window; j++) {
                word = new StringBuilder(words.get(i + j));
                word.append(" ");
                phrase.append(word.toString());
            }

            phrases.add(phrase.toString());

        }

        return phrases;
    }


    /*
     * Returns a Set of Strings that occur in both of the Set parameters.
     * However, the comparison is case-insensitive.
     */
    protected static Set<String> findMatches(Set<String> myPhrases, Set<String> yourPhrases) {

        Set<String> matches = new HashSet<>();

        if (myPhrases != null && yourPhrases != null) {

            for (String mine : myPhrases) {
                if (yourPhrases.contains(mine)) {
                    matches.add(mine);
                }
            }
        }
        return matches;
    }

    /*
     * Returns a LinkedHashMap in which the elements of the Map parameter
     * are sorted according to the value of the Integer, in non-ascending order.
     */
    protected static LinkedHashMap<String, Integer> sortResults(Map<String, Integer> possibleMatches) {

        // Because this approach modifies the Map as a side effect of printing
        // the results, it is necessary to make a copy of the original Map
        Map<String, Integer> copy = new HashMap<>(possibleMatches);

        LinkedHashMap<String, Integer> list = new LinkedHashMap<>();

        for (int i = 0; i < copy.size(); i++) {
            int maxValue = 0;
            String maxKey = null;
            for (String key : copy.keySet()) {
                if (copy.get(key) > maxValue) {
                    maxValue = copy.get(key);
                    maxKey = key;
                }
            }

            list.put(maxKey, maxValue);

            copy.put(maxKey, -1);
        }

        return list;
    }

    /*
     * This method is here to help you measure the execution time and get the output of the program.
     * You do not need to consider it for improving the efficiency of the detectPlagiarism method.
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please specify the name of the directory containing the corpus.");
            System.exit(0);
        }
        String directory = args[0];
        long start = System.currentTimeMillis();
        System.out.println("start");
        Map<String, Integer> map = PlagiarismDetector.detectPlagiarism(directory, 4, 5);
        long end = System.currentTimeMillis();
        double timeInSeconds = (end - start) / (double) 1000;
        System.out.println("Execution time (wall clock): " + timeInSeconds + " seconds");
        Set<Map.Entry<String, Integer>> entries = map.entrySet();
        for (Map.Entry<String, Integer> entry : entries) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

}


