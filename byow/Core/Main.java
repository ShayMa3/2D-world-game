package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;

/** This is the main entry point for the program. This class simply parses
 *  the commnd line inputs, and lets the byow.Core.Engine class take over
 *  in either keyboard or input string mode.
 */
public class Main {
    public static void main(String[] args) {
        if (args.length > 1) {
            System.out.println("Can only have one argument - the input string");
            System.exit(0);
        } else if (args.length == 1) {
            TERenderer ter = new TERenderer();
            ter.initialize(Engine.WIDTH, Engine.HEIGHT);
            Engine engine = new Engine();
            TETile[][] world = engine.interactWithInputString(args[0]);
            ter.renderFrame(world);
            System.out.println(engine.toString());
        } else {
            Engine engine = new Engine();
            engine.interactWithKeyboard();
        }
    }
}
