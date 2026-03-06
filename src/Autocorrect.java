import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Autocorrect
 * <p>
 * A command-line tool to suggest similar words when given one not in the dictionary.
 * </p>
 * @author Zach Blick
 * @author YOUR NAME HERE
 */
public class Autocorrect {

    /**
     * Constucts an instance of the Autocorrect class.
     * @param words The dictionary of acceptable words.
     * @param threshold The maximum number of edits a suggestion can have.
     */
    private String[] words;
    private int threshold;
    public Autocorrect(String[] words, int threshold) {
        this.words = words;
        this.threshold = threshold;
    }

    /**
     * Runs a test from the tester file, AutocorrectTester.
     * @param typed The (potentially) misspelled word, provided by the user.
     * @return An array of all dictionary words with an edit distance less than or equal
     * to threshold, sorted by edit distnace, then sorted alphabetically.
     */
    public String[] runTest(String typed) {
        ArrayList<Pair> validWords = new ArrayList<Pair>();
        int currWordInDict = 0;
        while (currWordInDict < words.length) {
            int dist = findDist(words[currWordInDict], typed);
            if (dist <= threshold) {
                validWords.add(new Pair(words[currWordInDict], dist));
            }
            currWordInDict ++;
        }
        validWords.sort(Comparator.comparing(Pair::getWord));
        validWords.sort(Comparator.comparing(Pair::getEditDistance));
        String[] finalWords = new String[validWords.size()];
        for (int i = 0; i < finalWords.length; i++) {
            finalWords[i] = validWords.get(i).getWord();
        }
        return finalWords;
    }

    public int findDist(String word1, String word2) {
        int[][] levenshtein = new int[word1.length() + 1][word2.length() + 1];
        int tailBoth = 0;
        int tail1 = 0;
        int tail2 = 0;
        for (int i = 0; i < word1.length() + 1; i++) {
            levenshtein[i][0] = i;
        }
        for (int i = 0; i < word2.length() + 1; i++) {
            levenshtein[0][i] = i;
        }
        for (int i = 1; i < word1.length() + 1; i++) {
            for (int j = 1; j < word2.length() + 1; j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    levenshtein[i][j] = levenshtein[i - 1][j - 1];
                }
                else {
                    tailBoth = levenshtein[i - 1][j - 1] + 1;
                    tail1   = levenshtein[i - 1][j] + 1;
                    tail2   = levenshtein[i][j - 1] + 1;
                    levenshtein[i][j] = Math.min(Math.min(tailBoth, tail1), tail2);
                }
            }
        }
        return levenshtein[word1.length()][word2.length()];
    }




    /**
     * Loads a dictionary of words from the provided textfiles in the dictionaries directory.
     * @param dictionary The name of the textfile, [dictionary].txt, in the dictionaries directory.
     * @return An array of Strings containing all words in alphabetical order.
     */
    private static String[] loadDictionary(String dictionary)  {
        try {
            String line;
            BufferedReader dictReader = new BufferedReader(new FileReader("dictionaries/" + dictionary + ".txt"));
            line = dictReader.readLine();

            // Update instance variables with test data
            int n = Integer.parseInt(line);
            String[] words = new String[n];

            for (int i = 0; i < n; i++) {
                line = dictReader.readLine();
                words[i] = line;
            }
            return words;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}