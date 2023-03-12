package ascii_art;

import ascii_art.img_to_char.BrightnessImgCharMatcher;
import ascii_output.AsciiOutput;
import ascii_output.ConsoleAsciiOutput;
import ascii_output.HtmlAsciiOutput;
import image.Image;

import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class Shell {

    private static final int MIN_PIXELS_PER_CHAR = 2;
    private Set<Character> charSet = new HashSet<>();
    private static final int INITIAL_CHARS_IN_ROW = 64;
    private static final String CMD_EXIT = "exit";
    private final int minCharsInRow;
    private final int maxCharsInRow;
    private int charsInRow;
    private static final String FONT_NAME = "Courier New";
    private static final String OUTPUT_FILENAME = "out.html";
    private BrightnessImgCharMatcher charMatcher;
    private AsciiOutput output;
    private int consoleFlag;
    private ConsoleAsciiOutput consoleAsciiOutput = new ConsoleAsciiOutput();

    private static final String ROW = ">>> ";
    private static final String CHARS = "chars";
    private static final String RES = "res";
    private static final String UP = "up";
    private static final String DOWN = "down";
    private static final String RENDER = "render";
    private static final String CONSOLE = "console";
    private static final String SPACE = "space";
    private static final String ADD = "add";
    private static final String REMOVE = "remove";
    private static final String ALL = "all";
    private static final String WRONG_VALUE = "WRONG VALUE, MISSING OF ARGUMENT";
    private static final String WRONG_ADD = "WRONG VALUE TO BE ADDED!!!";
    private static final String WRONG_REMOVE = "WRONG VALUE TO BE REMOVED!!!";
    private static final String WRONG_ARGUMENT = "WRONG ARGUMENT";
    private static final String MAX_REACHED = "MAXIMUM RESOLUTION HAS BEEN REACHED";
    private static final String MIN_REACHED = "MINIMUM RESOLUTION HAS BEEN REACHED";
    private static final String WIDTH = "Width set to ";
    private static final char ASCII_START = ' ';
    private static final char ASCII_END = '~';
    private static final char SEPARATOR = '-';

    /**
     * a constructor to the Shell
     * @param img the image given by the user
     */
    Shell(Image img) {
        Collections.addAll(charSet, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9');
        minCharsInRow = Math.max(1, img.getWidth()/img.getHeight());
        maxCharsInRow = img.getWidth() / MIN_PIXELS_PER_CHAR;
        charsInRow = Math.max(Math.min(INITIAL_CHARS_IN_ROW, maxCharsInRow), minCharsInRow);
        charMatcher = new BrightnessImgCharMatcher(img, FONT_NAME);
        output = new HtmlAsciiOutput(OUTPUT_FILENAME, FONT_NAME);
        consoleFlag = 0;
    }

    /**
     * a public function that runs a whole asciiArt image and receives inputs from the user
     */
    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.print(ROW);
        String cmd = scanner.nextLine().trim();
        String[] words = cmd.split("\\s+");
        while (!words[0].equals(CMD_EXIT))
        {
            // prints all the chars that are being used to represent the image
            if (words[0].equals(CHARS) && words.length == 1)
            {
                showChars();
            }
            // add more chars
            else if (words[0].equals(ADD))
            {
                if (words.length != 2)
                {
                    System.out.println(WRONG_VALUE);
                }
                else
                {
                    addChars(words[1]);
                }
            }
            //Remove chars
            else if (words[0].equals(REMOVE))
            {
                if (words.length != 2)
                {
                    System.out.println(WRONG_VALUE);
                }
                else
                {
                    removeChars(words[1]);
                }
            }
            // raise or lower the resolution level
            else if (words[0].equals(RES))
            {
                if (words.length != 2)
                {
                    System.out.println(WRONG_VALUE);
                }
                else if (words[1].equals(UP))
                {
                    resChange(UP);
                }
                else if (words[1].equals(DOWN))
                {
                    resChange(DOWN);
                }
                else
                    System.out.println(WRONG_VALUE);
            }
            // rending the output
            else if (words[0].equals(RENDER) && words.length == 1)
            {
                if (charSet.size() != 0)
                {
                    render();
                }

            }
            // switching the render to console
            else if (words[0].equals(CONSOLE) && words.length == 1)
            {
                consoleFlag = 1;
            }
            else if (words[0].equals(""))
            {
                String param = "";
                if (words.length > 1)
                {
                    param = words[1];
                }
            }
            else
            {
                System.out.println(WRONG_ARGUMENT);
            }
            System.out.print(ROW);
            cmd = scanner.nextLine().trim();
            words = cmd.split("\\s+");
        }
    }

    /**
     * a function the changes the resolution level of the image
     * @param s "up" for raising the level, "down" to low the level
     */
    private void resChange(String s)
    {
        if (s.equals(UP))
        {
            if (this.charsInRow * 2 > this.maxCharsInRow)
            {
                System.out.println(MAX_REACHED);
                return;
            }
            this.charsInRow *= 2;
        }
        else if (s.equals(DOWN))
        {
            if (this.charsInRow / 2 < this.minCharsInRow)
            {
                System.out.println(MIN_REACHED);
                return;
            }
            this.charsInRow /= 2;
        }
        System.out.println(WIDTH + this.charsInRow);
    }

    /**
     * a function that prints out the chars that are used to represent the image
     */
    private void showChars()
    {
        charSet.stream().sorted().forEach(c -> System.out.print(c + " "));
        System.out.println();
    }

    /**
     * add chars to the chars set, the valid input will be chars that have ASCII representation
     * @param s the range of the chars to be added
     */
    private void addChars(String s) {
        char[] range = parseCharRange(s);
        if(range != null)
        {
            Stream.iterate(range[0], c -> c <= range[1], c -> (char)((int)c+1)).forEach(charSet::add);
        }
        else
        {
            System.out.println(WRONG_ADD);
        }
    }

    /**
     * remove chars from the chars set, the valid input will be chars that have ASCII representation
     * @param s the range of the chars to be removed
     */
    private void removeChars(String s) {
        char[] range = parseCharRange(s);
        if(range != null)
        {
            Stream.iterate(range[0], c -> c <= range[1], c -> (char)((int)c+1)).forEach(charSet::remove);
        }
        else
        {
            System.out.println(WRONG_REMOVE);
        }
    }

    /**
     * a function that checks if a char has an ASCII representation
     * @param input a char to check
     * @return true or false if the char has ASCII
     */
    private static boolean isASCII(char input) {
        if (input > 0x7F)
        {
            return false;
        }
        return true;
    }

    /**
     * a function that parses an input from the user to a valid range of chars represented by ASCII numbers
     * @param param a string of a given range or a specific tab
     * @return 2 chars array of a range of chars
     */
    private static char[] parseCharRange(String param)
    {
        char [] charsArr = new char[2];
        if (param.length() == 1)
        {
            if  (isASCII(param.charAt(0)))
            {
                charsArr[0] = param.charAt(0); charsArr[1] = param.charAt(0);
            }
            else
                return null;
        }
        else if (param.equals(ALL))
        {
            charsArr[0] = ASCII_START; charsArr[1] = ASCII_END;
        }
        else if (param.equals(SPACE))
        {
            charsArr[0] = ' '; charsArr[1] = ' ';
        }
        else if ((param.charAt(1)) == SEPARATOR && isASCII(param.charAt(0))
                && param.length() == 3 && isASCII(param.charAt(2)))
        {
            if (param.charAt(0) > param.charAt(2))
            {
                charsArr[0] = param.charAt(2); charsArr[1] = param.charAt(0);
            }
            else if (param.charAt(0) < param.charAt(2))
            {
                charsArr[0] = param.charAt(0); charsArr[1] = param.charAt(2);
            }
            else if (param.charAt(0) == param.charAt(2))
            {
                charsArr[0] = param.charAt(0); charsArr[1] = param.charAt(0);
            }
        }
        else
        {
            return null;
        }
        return charsArr;
    }

    /**
     * a function that renders the output of an image represented by chars
     */
    private void render()
    {
        Character [] charsSet = new Character [charSet.size()];
        charSet.toArray(charsSet);
        char[][] chars = charMatcher.chooseChars(charsInRow, charsSet);
        if (consoleFlag == 0)
        {
            output.output(chars);
        }
        else if (consoleFlag == 1)
        {
            consoleAsciiOutput.output(chars);
        }
    }
}
