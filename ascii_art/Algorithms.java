package ascii_art;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Algorithms
{
    /**
     * a function that check how many times a specific number duplicates in an array
     * @param numList array of numbers
     * @return the number of duplications
     */
    public static int findDuplicate (int[] numList)
    {
        int flag = numList[numList[0]];
        int duplicates = numList[0];
        while (flag != duplicates)
        {
            flag = numList[numList[flag]];
            duplicates = numList[duplicates];
        }
        flag = 0;
        for (int i = 0; i < numList.length; i++)
        {
            if (flag != duplicates)
            {
                flag = numList[flag];
                duplicates = numList[duplicates];
            }
            else
            {
                return duplicates;
            }
        }
        return duplicates;
    }

    /**
     * a function that creates and translates the English letters to Morse code
     * @return map of the letters translated to morse
     */
    private static HashMap<Character, String> mapFactory ()
    {
        HashMap<Character, String> charsMap = new HashMap<>();
        charsMap.put('a', ".-"); charsMap.put('b', "-..."); charsMap.put('c', "-.-.");
        charsMap.put('d', "-.."); charsMap.put('e', "."); charsMap.put('f', "..-.");
        charsMap.put('g', "--."); charsMap.put('h', "...."); charsMap.put('i', "..");
        charsMap.put('j', ".---"); charsMap.put('k', "-.-"); charsMap.put('l', ".-..");
        charsMap.put('m', "--"); charsMap.put('n', "-."); charsMap.put('o', "---");
        charsMap.put('p', ".--."); charsMap.put('q', "--.-"); charsMap.put('r', ".-.");
        charsMap.put('s', "..."); charsMap.put('t', "-"); charsMap.put('u', "..-");
        charsMap.put('v', "...-"); charsMap.put('w', ".--"); charsMap.put('x', "-..-");
        charsMap.put('y', "-.--"); charsMap.put('z', "--..");
        return charsMap;
    }

    /**
     * a function that checks the number of unique Morse sequences in a specific list of words
     * @param words an array of String words
     * @return the number of unique codes
     */
    public static int uniqueMorseRepresentations(String[] words)
    {
        HashMap<Character, String> charsMap;
        charsMap = mapFactory();
        HashSet<String> translationArr = new HashSet<>();
        StringBuilder morseToWord;
        for (int i = 0; i < words.length; i++)
        {
            morseToWord = new StringBuilder();
            for (int j = 0; j < words[i].length(); j++)
            {
                morseToWord.append(charsMap.get(words[i].charAt(j)));
            }
            translationArr.add(morseToWord.toString());
        }
        return translationArr.size();
    }
}
