package byow.lab13;

import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

public class MemoryGame {
    private int width;
    private int height;
    private int round;
    private Random rand;
    private boolean gameOver;
    private boolean playerTurn;
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
            "You got this!", "You're a star!", "Go Bears!",
            "Too easy for you!", "Wow, so impressive!"};

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        long seed = Long.parseLong(args[0]);
        MemoryGame game = new MemoryGame(40, 40, seed);
        game.startGame();
        System.out.println(game.generateRandomString(7));
    }

    public MemoryGame(int width, int height, long seed) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
        StdDraw.setPenColor(Color.white);
        StdDraw.text((double) width/2, (double) height/2, "Test");
        StdDraw.show();

        rand = new Random(seed);
    }

    public String generateRandomString(int n) {
        String s = "";
        while (n > 0) {
            s = s + CHARACTERS[rand.nextInt(25)];
            n -= 1;
        }
        return s;
    }

    public void drawFrame(String s) {
        StdDraw.clear();
        Font font = new Font("Arial", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setPenColor(Color.black);
        StdDraw.text((double) width/2, (double) height/2, s);
        StdDraw.show();
    }

    public void flashSequence(String letters) {
        char[] charLetters = letters.toCharArray();
        for (char letter: charLetters) {
            drawFrame(Character.toString(letter));
            StdDraw.pause(1000);
            StdDraw.clear();
            StdDraw.show();
            StdDraw.pause(500);
        }
    }

    public String solicitNCharsInput(int n) {
        String currString = "";
        int numChars = 0;
        while (numChars < n && StdDraw.hasNextKeyTyped()) {
            currString += Character.toString(StdDraw.nextKeyTyped());
            drawFrame(currString);
            numChars += 1;
            if (numChars == n) {
                return currString;
            }
        }
        return currString;
    }

    public void startGame() {
        round = 2;
        gameOver = false;
        while (!gameOver) {
            drawFrame("Round: " + round);
            StdDraw.pause(500);
            String currString = generateRandomString(round);

            flashSequence(currString);
            StdDraw.pause(2000);

            String answer = solicitNCharsInput(round);
            StdDraw.pause(3000);

            if (!answer.equals(currString)) {
                gameOver = true;
                drawFrame("Game over! You made it to round " + round + "! The correct word was " + currString);
            }
            round += 1;
        }

    }

}