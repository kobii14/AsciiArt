package ascii_art;
import image.Image;
import java.util.logging.Logger;

/**
 * the class that runs the program
 */
public class Driver {
    private static final String USAGE_ERR = "USAGE: java asciiArt ";
    private static final String IMAGE_ERR = "Failed to open image file ";
    public static void main(String[] args) throws Exception {
            if (args.length != 1) {
                System.err.println(USAGE_ERR);
                return;
            }
            Image img = Image.fromFile(args[0]);
            if (img == null) {
                Logger.getGlobal().severe(IMAGE_ERR + args[0]);
                return;
            }
            new Shell(img).run();
        }
}
