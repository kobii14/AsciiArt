package ascii_art.img_to_char;

import ascii_output.AsciiOutput;
import ascii_output.HtmlAsciiOutput;
import image.Image;
import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;

/**
 * the class that matches between the char's brightness to the image brightness
 */
public class BrightnessImgCharMatcher {

    private Image image;
    private Character [] chars;
    private int numCharsInRow;
    private double [] charsBrightnessLvl;
    private String font;
    private final HashMap<Image, Double> cache = new HashMap<>();
    private static final int DEFAULT_PIXELS = 16;



    /**
     * a constructor to the class
     * @param image Image object of the original image
     * @param font font type
     */
    public BrightnessImgCharMatcher(Image image, String font)
    {
        this.image = image;
        this.font = font;
    }

    /**
     * helper function to calculate the minimum and maximum of a doubles array
     * @param numbers the doubles array
     * @return an array - minimum value: [0], maximum value: [1]
     */
    private static double[] findMinMax (double [] numbers)
    {
        double max = 0;
        double min = 1;

        for (int i = 0; i < numbers.length; i++)
        {
                if (numbers[i] > max)
                {
                    max = numbers[i];
                }
                if (numbers[i] < min)
                {
                    min = numbers[i];
                }
        }
        return new double[]{min, max};
    }


    /**
     * helper function to calculate the brightness level
     * @param boolArr boolean array the represents the white and black pixels
     * @return the ratio between true to the whole array
     */
    private double calculateTrueFalseBrightness (boolean [][] boolArr)
    {
        double counterTotal = 0;
        double counterTrue = 0;
        for (int i = 0; i < boolArr.length; i++)
        {
            for (int j = 0; j < boolArr[i].length; j++)
            {
                counterTotal ++;
                if (boolArr[i][j])
                    counterTrue ++;
            }
        }
        return counterTrue / counterTotal;
    }


    /**
     * a function that calculates the brightness level of the chars that were given in the public methode
     */
    private void charsBrightnessLevel ()
    {
        this.charsBrightnessLvl = new double[chars.length];
        boolean [][] boolValues;
        for (int i = 0; i < chars.length; i++)
        {
            boolValues = CharRenderer.getImg(chars[i], DEFAULT_PIXELS, font);
            charsBrightnessLvl[i] = calculateTrueFalseBrightness(boolValues);
        }
    }


    /**
     * a function that normalizes the values of the brightness level of the chars
     */
    private void linearNormalStretching ()
    {
        double [] minMax = BrightnessImgCharMatcher.findMinMax(charsBrightnessLvl);
        for (int i = 0; i < charsBrightnessLvl.length; i++)
        {
            charsBrightnessLvl[i] = (charsBrightnessLvl[i] - minMax[0]) / (minMax[1] - minMax[0]);
        }
    }


    /**
     * a function that calculates the image's brightness level of grey
     * @param image sub image to check
     * @return the average brightness
     */
    private double subImageBrightnessAverage (Image image)
    {
        double greyPixel;
        double sum = 0;
        double counter = 0;
        for (Color pixel : image.pixels())
        {
            greyPixel = pixel.getRed() * 0.2126 + pixel.getGreen() * 0.7152 + pixel.getBlue() * 0.0722;
            sum += (greyPixel / 255);
            counter ++;
        }
        cache.put(image, sum / counter);
        return sum / counter ;
    }

    /**
     * a function that creates a doubles array and inserts the brightness value of sub images of an image
     * @return 2 dimensions array of the brightness level of the sub-images
     */
    private double [][] allImageBrightnessCalculation ()
    {
        int pixels = image.getWidth() / numCharsInRow;
        Image [] imagesArr = new Image[(image.getHeight() / pixels) * (image.getWidth() / pixels)];
        int k = 0;
        for(Image subImage : image.squareSubImagesOfSize(pixels))
        {
            imagesArr[k] = subImage;
            k ++;
        }
        double [][] asciiArtBeforeMatch = new double[image.getHeight() / pixels][image.getWidth() / pixels];
        for (int i = 0; i < asciiArtBeforeMatch.length; i++)
        {
            for (int j = 0; j < asciiArtBeforeMatch[i].length; j++)
            {
                int i1 = (i * asciiArtBeforeMatch[i].length) + j;
                if (cache.containsKey(imagesArr[i1]))
                {
                    asciiArtBeforeMatch[i][j] = cache.get(imagesArr[i1]);
                }
                else
                {
                    asciiArtBeforeMatch[i][j] = subImageBrightnessAverage(imagesArr[i1]);
                }
            }
        }
        return asciiArtBeforeMatch;
    }

    /**
     * a function that finds the minimal different between a double (brightness level of a sub-image)
     * and an array of doubles (the chars brightnesses array)
     * @param charsBrightnessLevelArr the chars brightnesses array
     * @param imageBrightness brightness level of a sub-image
     * @return the index of the closest char by value of its brightness level
     */
    private static int findClosest (double [] charsBrightnessLevelArr, double imageBrightness)
    {
        double minValue = 1;
        int minIndex = 0;
        for (int i = 0; i < charsBrightnessLevelArr.length; i++)
        {
            if (Math.abs(charsBrightnessLevelArr[i] - imageBrightness) < minValue)
            {
                minValue = Math.abs(charsBrightnessLevelArr[i] - imageBrightness);
                minIndex = i;
            }
        }
        return minIndex;
    }

    /**
     * a function that converting a whole image to ascii letters by separating the image to
     * sub-images and calculates the brightness level
     * @return a 2 dimensions array of ascii that will be used to represent the image
     */
    private char[][] convertImageToAscii()
    {
        charsBrightnessLevel();
        linearNormalStretching();
        int pixels = image.getWidth() / numCharsInRow;
        char[][] asciiArt = new char[image.getHeight() / pixels][image.getWidth() / pixels];
        double [][] asciiArtBeforeMatch = allImageBrightnessCalculation();
        double brightnessLevel = 0;
        for (int i = 0; i < asciiArt.length; i++)
        {
            for (int j = 0; j < asciiArt[i].length; j++)
            {
                brightnessLevel = asciiArtBeforeMatch[i][j];
                asciiArt[i][j] = chars[findClosest(charsBrightnessLvl, brightnessLevel)];
            }
        }
        return asciiArt;
    }

    /**
     * a function that chooses the chars that are the most suitable to represent a given image
     * @param numCharsInRow number of chars in each row
     * @param charSet a set of chars to choose the correct chars from
     * @return a 2 dimensions array of ascii that will be used to represent the image
     */
    public char[][] chooseChars(int numCharsInRow, Character[] charSet)
    {
        this.chars = charSet;
        this.numCharsInRow = numCharsInRow;
        return convertImageToAscii();
    }

}


