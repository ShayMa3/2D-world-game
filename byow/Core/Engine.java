package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;
//import byow.TileEngine.Tileset;

//import java.util.ArrayList;
//import java.util.List;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;

import org.junit.Assert;
import org.junit.Assert.*;
import org.junit.Test;


import static byow.Core.MapRender.*;

/**
 * The interactivity of the world. Includes methods that change the layout
 * of the world based on user input.
 * @author Shay Ma and @author Amber Fang
 */
public class Engine implements Serializable {
    public static final int WIDTH = 90;
    public static final int HEIGHT = 50;
    static final Random RANDOM = new Random();


    /**
     * Method used for exploring a fresh world. This method should
     * handle all inputs, including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        startGameScreen();
        boolean started = false;
        while (!started) {
            if (StdDraw.hasNextKeyTyped()) {
                Character firstInput = StdDraw.nextKeyTyped();

                // if New Game, get seed input and generate new world.
                if (firstInput == 'N' || firstInput == 'n') {
                    started = true;
                    String name = drawStartScreen();
                    StdDraw.clear(Color.black);
                    StdDraw.text((double) WIDTH / 2, (double) HEIGHT / 2 + (double) HEIGHT / 7,
                            "Please enter a random seed. Press (S) to confirm.");
                    StdDraw.show();
                    String seedString = "";
                    boolean finishedSeed = false;
                    while (!finishedSeed) {
                        if (StdDraw.hasNextKeyTyped()) {
                            Character seedInput = StdDraw.nextKeyTyped();
                            if (!seedString.equals("") && (seedInput == 'S' || seedInput == 's')) {
                                finishedSeed = true;
                            } else if (seedInput >= 48 && seedInput < 58) {
                                seedString += seedInput;
                                StdDraw.clear(Color.BLACK);
                                StdDraw.text((double) WIDTH / 2,
                                        (double) HEIGHT / 2 + (double) HEIGHT / 7,
                                        "Please enter a random seed. Press (S) to confirm.");
                                StdDraw.text((double) WIDTH / 2, (double) HEIGHT / 2, seedString);
                                StdDraw.show();
                            }
                        }
                    }
                    Long seed = Long.valueOf(seedString);
                    RANDOM.setSeed(seed);

                    // Generate world with seed
                    Font font = new Font("Monaco", Font.BOLD, 14);
                    StdDraw.setFont(font);
                    TERenderer ter = new TERenderer();
                    TETile[][] world = renderInitialWorld(ter);
                    playGame(ter, world, name, seed, 0);
                } else if (firstInput == 'L' || firstInput == 'l') {
                    started = true;
                    loadGame = true;
                    File prevWorld = new File("saveWorldInfo.txt");
                    long seed = 0;
                    int numCaptures = 0;
                    String prevTile = "";
                    String name = "";
                    ArrayList<Point> creaturePos = new ArrayList<>();
                    try {
                        ObjectInputStream inp =
                                new ObjectInputStream(new FileInputStream(prevWorld));
                        seed = (long) inp.readObject();
                        numCaptures = (int) inp.readObject();
                        prevTile = (String) inp.readObject();
                        name = (String) inp.readObject();
                        avatar = (Point) inp.readObject();
                        creaturePos = (ArrayList<Point>) inp.readObject();
                        inp.close();
                    } catch (IOException | ClassNotFoundException excp) {
                        System.out.println("Object not found");
                    }
                    RANDOM.setSeed(seed);
                    Font font = new Font("Monaco", Font.BOLD, 14);
                    StdDraw.setFont(font);
                    TERenderer ter = new TERenderer();
                    ter.initialize(Engine.WIDTH, Engine.HEIGHT);
                    TETile[][] world = renderSavedWorld(ter, avatar, creaturePos);
                    updateCurrState(ter, world, numCaptures, prevTile, name);
                    playGame(ter, world, name, seed, numCaptures);
                } else if (firstInput == 'Q' || firstInput == 'q') {
                    started = true;
                    System.exit(0);
                }
            }
        }
    }

    public static String drawStartScreen() {
        StdDraw.clear(Color.black);
        StdDraw.text((double) WIDTH / 2, (double) HEIGHT / 2 + (double) HEIGHT / 7,
                "Please enter your name. Press (Enter) to confirm.");
        StdDraw.show();
        String name = "";
        boolean finishedName = false;
        while (!finishedName) {
            if (StdDraw.hasNextKeyTyped()) {
                Character nameInput = StdDraw.nextKeyTyped();
                if (!name.equals("") && nameInput == 10) {
                    finishedName = true;
                } else if ((nameInput >= 65 && nameInput <= 90)
                        || (nameInput >= 97 && nameInput < 123) || nameInput == 32) {
                    name += nameInput;
                    StdDraw.clear(Color.BLACK);
                    StdDraw.text((double) WIDTH / 2, (double) HEIGHT / 2 + (double) HEIGHT / 7,
                            "Please enter your name. Press (Enter) to confirm.");
                    StdDraw.text((double) WIDTH / 2, (double) HEIGHT / 2, name);
                    StdDraw.show();
                }
            }
        }
        return name;
    }

    public static void startGameScreen() {
        // Set up Canvas
        StdDraw.setCanvasSize(WIDTH * 16, HEIGHT * 16);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.enableDoubleBuffering();
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.white);
        // Set up Start Menu
        Font title = new Font("Monaco", Font.BOLD, 50);
        StdDraw.setFont(title);
        StdDraw.text((double) WIDTH / 2, (double) HEIGHT / 2 + (double) HEIGHT / 7,
                "CS 61B: THE GAME");
        Font menuItems = new Font("Monaco", Font.PLAIN, 30);
        StdDraw.setFont(menuItems);
        StdDraw.text((double) WIDTH / 2, (double) HEIGHT / 2 - 1, "New Game (N)");
        StdDraw.text((double) WIDTH / 2, (double) HEIGHT / 2 - 4, "Load Game (L)");
        StdDraw.text((double) WIDTH / 2, (double) HEIGHT / 2 - 7, "Quit (Q)");
        StdDraw.show();
    }

    /** @source Hilfinger Spring2020 Lab12 */
    private static void playGame(TERenderer ter, TETile[][] world, String name,
                                 long seed, int numCaptures) {
        String prevTile = "";
        boolean playing = true;
        boolean capUpdate = true;
        boolean potentialQ = false;
        while (playing) {
            // Display HUD over hover
            int xPos = (int) StdDraw.mouseX();
            int yPos = (int) StdDraw.mouseY();
            if (xPos >= 0 && xPos < WIDTH && yPos >= 0 && yPos < HEIGHT) {
                String tileType = world[xPos][yPos].description();
                // If the new tile is different from prevTile, set new prevTile and render it once.
                if (!tileType.equals(prevTile)) {
                    StdDraw.clear(Color.black);
                    prevTile = tileType;
                    updateCurrState(ter, world, numCaptures, prevTile, name);
                }
            }

            // If you captured a creature, update.
            if (capUpdate) {
                updateCaptures(numCaptures, ter, world);
                capUpdate = false;
            }

            // Allow avatar to move
            if (StdDraw.hasNextKeyTyped()) {
                Character seedInput = StdDraw.nextKeyTyped();
                if (seedInput == 'W' || seedInput == 'w') {
                    if (potentialQ) {
                        potentialQ = false;
                    }
                    if (moveUp(world)) {
                        displayEncounter();
                        numCaptures += 1;
                        capUpdate = true;
                    }
                    updateCurrState(ter, world, numCaptures, prevTile, name);
                } else if (seedInput == 'S' || seedInput == 's') {
                    if (potentialQ) {
                        potentialQ = false;
                    }
                    if (moveDown(world)) {
                        displayEncounter();
                        numCaptures += 1;
                        capUpdate = true;
                    }
                    updateCurrState(ter, world, numCaptures, prevTile, name);
                } else if (seedInput == 'A' || seedInput == 'a') {
                    if (potentialQ) {
                        potentialQ = false;
                    }
                    if (moveLeft(world)) {
                        displayEncounter();
                        numCaptures += 1;
                        capUpdate = true;
                    }
                    updateCurrState(ter, world, numCaptures, prevTile, name);
                } else if (seedInput == 'D' || seedInput == 'd') {
                    if (potentialQ) {
                        potentialQ = false;
                    }
                    if (moveRight(world)) {
                        displayEncounter();
                        numCaptures += 1;
                        capUpdate = true;
                    }
                    updateCurrState(ter, world, numCaptures, prevTile, name);
                } else if (seedInput == ':') {
                    potentialQ = true;
                } else if (potentialQ && Character.toUpperCase(seedInput) == 'Q') {
                    updateCurrState(ter, world, numCaptures, prevTile, name);
                    avatar = new Point(avatar.getX(), avatar.getY());
                    saveGameInfo(world, seed, numCaptures, prevTile, name);
                    System.exit(0);
                }
            }
        }
    }

    private static void saveGameInfo(TETile[][] world, long seed, int numCaptures,
                                     String prevTile, String name) {
        File saveWorldInfo = new File("saveWorldInfo.txt");
        ArrayList<Point> creatures = creaturePos(world);
        if (interactWithString) {
            try {
                ObjectOutputStream out =
                        new ObjectOutputStream(new FileOutputStream(saveWorldInfo));
                out.writeObject(seed);
                out.writeObject(avatar);
                out.writeObject(creatures);
                out.close();
            } catch (IOException excp) {
                System.out.println("Object error");
            }
        } else {
            try {
                ObjectOutputStream out =
                        new ObjectOutputStream(new FileOutputStream(saveWorldInfo));
                out.writeObject(seed);
                out.writeObject(numCaptures);
                out.writeObject(prevTile);
                out.writeObject(name);
                out.writeObject(avatar);
                out.writeObject(creatures);
                out.close();
            } catch (IOException excp) {
                System.out.println("Object error");
            }
        }
    }

    private static ArrayList<Point> creaturePos(TETile[][] currWorld) {
        ArrayList<Point> creatures = new ArrayList<>();
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (currWorld[x][y] == Tileset.CREATURE) {
                    creatures.add(new Point(x, y));
                }
            }
        }
        return creatures;
    }

    private static void updateCaptures(int numCaptures, TERenderer ter, TETile[][] world) {
        StdDraw.setPenColor(Color.white);
        StdDraw.text(25, HEIGHT - 1, "Creatures Captured: " + numCaptures);
        StdDraw.show();
    }

    private static void updateTile(String prevTile) {
        StdDraw.setPenColor(Color.white);
        StdDraw.text(8, HEIGHT - 1, "Current Tile: " + prevTile);
        StdDraw.show();
    }

    private static void displayName(String name) {
        if (!name.equals("")) {
            StdDraw.setPenColor(Color.white);
            StdDraw.text(40, HEIGHT - 1, "Name: " + name);
            StdDraw.show();
        }
    }

    private static void updateCurrState(TERenderer ter, TETile[][] world,
                                        int numCaptures, String prevTile, String name) {
        ter.renderFrame(world);
        updateCaptures(numCaptures, ter, world);
        if (!interactWithString) {
            updateTile(prevTile);
        }
        displayName(name);
    }


    private static void displayEncounter() {
        boolean display = true;
        while (display) {
            StdDraw.clear(Color.black);

            StdDraw.setPenColor(Color.white);
            Font encounterFont = new Font("Monaco", Font.BOLD, 30);
            StdDraw.setFont(encounterFont);
            StdDraw.text((double) WIDTH / 2, (double) HEIGHT / 2 + (double) HEIGHT / 7,
                    "You have encountered a creature.");
            StdDraw.text((double) WIDTH / 2 - 4, (double) HEIGHT / 2, "@");

            StdDraw.setPenColor(Color.red);
            StdDraw.text((double) WIDTH / 2 + 4, (double) HEIGHT / 2, "O");

            StdDraw.setPenColor(Color.white);
            Font captureFont = new Font("Monaco", Font.PLAIN, 22);
            StdDraw.setFont(captureFont);
            StdDraw.text((double) WIDTH / 2, (double) HEIGHT / 2 - 5, "Capture creature (C)");
            StdDraw.show();

            if (StdDraw.hasNextKeyTyped()) {
                char input = StdDraw.nextKeyTyped();
                if (input == 'C' || input == 'c') {
                    Font font = new Font("Monaco", Font.BOLD, 14);
                    StdDraw.setFont(font);
                    display = false;
                }
            }
        }
    }

    /**
     * Method used for autograding and testing your code.
     * The input string will be a series of characters
     * (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww".
     * The engine should behave exactly as if the user typed
     * these characters into the engine using interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quit save.
     * For example, if we do interactWithInputString("n123sss:q"),
     * we expect the game to run the first 7 commands (n123sss)
     * and then quit and save. If we then do interactWithInputString("l"),
     * we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public static TETile[][] interactWithInputString(String input) {
        interactWithString = true;
        StringToSeed string = new StringToSeed(input);
        String seedString = "";
        ArrayList<Point> creaturePos = new ArrayList<>();

        /* Takes input string and generates seed.*/
        while (string.nextInput()) {
            char next = string.nextKey();
            if (next == 'N' || next == 'n') {
                continue;
            } else if (next == 'L' || next == 'l') {
                loadGame = true;
                long seed = 0;
                File prevWorld = new File("saveWorldInfo.txt");
                try {
                    ObjectInputStream inp =
                            new ObjectInputStream(new FileInputStream(prevWorld));
                    seed = (long) inp.readObject();
                    avatar = (Point) inp.readObject();
                    creaturePos = (ArrayList<Point>) inp.readObject();
                    inp.close();
                } catch (IOException | ClassNotFoundException excp) {
                    System.out.println("Object not found");
                }
                seedString = Long.toString(seed);
                break;
            } else if (next == 'S' || next == 's') {
                break;
            }
            seedString = seedString + next;
        }

        /* Converts the seed string into a long int value. */
        Long seed = new Long(seedString).longValue();
        RANDOM.setSeed(seed);
        TERenderer ter = new TERenderer();
        TETile[][] world;

        if (!loadGame) {
            world = renderInitialWorld(ter);
        } else {
            world = renderSavedWorld(ter, avatar, creaturePos);
        }

        /* Moves avatar and will quit and save if ":Q" is entered.*/
        boolean potentialQ = false;
        while (string.nextInput()) {
            loadGame = false;
            char next = string.nextKey();
            if (next == 'W' || next == 'w') {
                if (potentialQ) {
                    potentialQ = false;
                }
                moveUp(world);
            } else if (next == 'A' || next == 'a') {
                if (potentialQ) {
                    potentialQ = false;
                }
                moveLeft(world);
            } else if (next == 'S' || next == 's') {
                if (potentialQ) {
                    potentialQ = false;
                }
                moveDown(world);
            } else if (next == 'D' || next == 'd') {
                if (potentialQ) {
                    potentialQ = false;
                }
                moveRight(world);
            } else if (next == ':') {
                potentialQ = true;
            } else if (potentialQ && Character.toUpperCase(next) == 'Q') {
                avatar = new Point(avatar.getX(), avatar.getY());
                saveGameInfo(world, seed, 0, "", "");
                break;
            }
        }
        return world;
    }

    public static TETile[][] renderInitialWorld(TERenderer ter) {

        TETile[][] genWorldFrame = new TETile[WIDTH][HEIGHT];
        generateMap(ter, genWorldFrame);
        TETile[][] finalWorldFrame = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                finalWorldFrame[x][y] = genWorldFrame[x][y];
            }
        }
//        ter.renderFrame(finalWorldFrame);
        return finalWorldFrame;
    }

    /** Initializes saved world and then adds creatures and avatar
    * back to their previous positions.*/
    public static TETile[][] renderSavedWorld(TERenderer ter, Point avatar,
                                              ArrayList<Point> creatures) {
        TETile[][] prevWorldMap = renderInitialWorld(ter);
        int avatarXx = avatar.getX();
        int avatarYy = avatar.getY();
        prevWorldMap[avatarXx][avatarYy] = Tileset.AVATAR;

        for (int c = 0; c < creatures.size(); c++) {
            int x = creatures.get(c).getX();
            int y = creatures.get(c).getY();
            prevWorldMap[x][y] = Tileset.CREATURE;
        }

        return prevWorldMap;
    }

    /** Class that converts a string to an iterator.*/
    private static class StringToSeed {
        private String input;
        private int index;

        StringToSeed(String s) {
            input = s;
            index = 0;
        }

        /** Returns the next character in the string. */
        public char nextKey() {
            char next = input.charAt(index);
            index += 1;
            return next;
        }

        /** Checks if there are more characters in the string. */
        public boolean nextInput() {
            return index < input.length();
        }
    }

    @Test
    public static void main(String[] args) {
        Engine engine = new Engine();

        // TEST InputString
        interactWithInputString("n5113456583806962379ssd:q");
        interactWithInputString("lsw:q");
        TETile[][] world1 = interactWithInputString("lss");
//        engine.interactWithInputString("lsss");
        TETile[][] world2 = interactWithInputString("n5113456583806962379ssdswss");

//        TETile[][] testQuit = interactWithInputString("n5113456583806962379ssd:q");
//        TETile[][] testLoad = interactWithInputString("l:q");
//        TETile[][] testLoad2 = interactWithInputString("lswss:q");
//        TETile[][] expected = interactWithInputString("n5113456583806962379ssdswss");

//        Assert.assertArrayEquals(testQuit, testLoad);
//        Assert.assertArrayEquals(expected, testLoad2);

        Assert.assertArrayEquals(world2, world1);

        // TEST Keyboard
//        engine.interactWithKeyboard();
    }
}
