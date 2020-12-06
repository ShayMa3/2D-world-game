package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static byow.Core.Engine.RANDOM;

public class MapRender implements Serializable {
    //private static final int WORLD_WIDTH = 90;
    //private static final int WORLD_HEIGHT = 50;
    private static final int ROOM_MAX_SIZE = 8;
    private static final int HALL_MAX_LENGTH = 3;
    private static final int UP = 0;
    private static final int DOWN = 1;
    private static final int LEFT = 2;
    private static final int RIGHT = 3;
    static boolean loadGame = false;
    static boolean interactWithString = false;
    static Point avatar;
    //private static final long SEED = 2873123;
    //public static final Random RANDOM = new Random(SEED);


    /**
     * Draws a randomly sized room. If it is the first room, it is drawn in a random valid location.
     * Otherwise, draw the room connected to the open end of a hallway. If no space exists, draw
     * another hallway.
     * @param world the grid of tiles
     * @param rooms list of existing rooms
     * @param halls list of existing hallways
     */
    public static void drawRoom(TETile[][] world, List<Room> rooms, List<Hallway> halls) {
        int width = 3 + RANDOM.nextInt(ROOM_MAX_SIZE); // add 3 to make sure there is floor
        int height = 3 + RANDOM.nextInt(ROOM_MAX_SIZE);
        int xPos, yPos;
        Point lL;
        Point uR;
        Hallway prev = null;
        Room curr = null;

        if (halls.isEmpty()) {
            // find valid lowerLeft and upperRight points for first room
            do {
                xPos = 1 + RANDOM.nextInt(Engine.WIDTH - 1); // add 1 to avoid edges
                yPos = 1 + RANDOM.nextInt(Engine.HEIGHT - 1);
                lL = new Point(xPos, yPos);
                uR = new Point(xPos + width, yPos + height);
                curr = new Room(lL, uR);
            }
            while (!validPoints(prev, curr, rooms, halls));
        } else {
            /** attempt to add to end of hallway, if it doesn't work,
             * end function to draw another hallway
             */
            prev = halls.get(halls.size() - 1);
            if (prev.getOpen() == UP) {
                curr = prev.getRoomUp(width, height);
                curr.setDown(true);
            } else if (prev.getOpen() == DOWN) {
                curr = prev.getRoomDown(width, height);
                curr.setUp(true);
            } else if (prev.getOpen() == LEFT) {
                curr = prev.getRoomLeft(width, height);
                curr.setRight(true);
            } else if (prev.getOpen() == RIGHT) {
                curr = prev.getRoomRight(width, height);
                curr.setLeft(true);
            }
            lL = curr.getlL();
            uR = curr.getuR();
            if (!validPoints(prev, curr, rooms, halls)) {
                return;
            }
        }

        // draw room (make the edges WALL tiles and insides FLOOR tiles)
        for (int x = lL.getX(); x <= uR.getX(); x += 1) {
            for (int y = lL.getY(); y <= uR.getY(); y += 1) {
                if (x == lL.getX() || x == uR.getX() || y == lL.getY() || y == uR.getY()) {
                    world[x][y] = Tileset.WALL;
                } else {
                    world[x][y] = Tileset.FLOOR;
                }
            }
        }
        rooms.add(curr);

        // draw the character in the first room
        if (halls.isEmpty() && !loadGame) {
            avatar = new Point((lL.getX() + uR.getX()) / 2, (lL.getY() + uR.getY()) / 2);
            world[avatar.getX()][avatar.getY()] = Tileset.AVATAR;
        }

        // set overlapping tile into floor
        if (prev != null) {
            if (prev.getOpen() == UP) {
                world[prev.getlL().getX() + 1][prev.getuR().getY()] = Tileset.FLOOR;
            } else if (prev.getOpen() == DOWN) {
                world[prev.getlL().getX() + 1][prev.getlL().getY()] = Tileset.FLOOR;
            } else if (prev.getOpen() == LEFT) {
                world[prev.getlL().getX()][prev.getlL().getY() + 1] = Tileset.FLOOR;
            } else if (prev.getOpen() == RIGHT) {
                world[prev.getuR().getX()][prev.getlL().getY() + 1] = Tileset.FLOOR;
            }
            prev.setOpen(-1);
        }
    }

    /**
     * Check if the given room or hallway is a valid.
     * @param prev if curr is a hallway: the previous room that curr branch out of OR
     *             if curr is a room: the previous hallway that curr should be connected to
     * @param curr the current room or hallway that we are checking
     * @param rooms list of existing rooms
     * @param halls list of existing halls
     * @return whether the room or hallway is valid
     */
    private static boolean validPoints(Room prev, Room curr, List<Room> rooms,
                                       List<Hallway> halls) {
        // check if room will go out of bounds
        if (curr.getuR().getX() >= Engine.WIDTH - 1 || curr.getuR().getY() >= Engine.HEIGHT - 3
                || curr.getlL().getX() <= 0 || curr.getlL().getY() <= 0) {
            return false;
        }
        // check if room/hall overlaps any currently drawn rooms or halls
        boolean isValid = true;
        for (Room r: rooms) {
            if (prev != null && r == prev) {
                continue;
            }
            if (curr.overlaps(r)) {
                isValid = false;
            }
        }
        for (Hallway h: halls) {
            if (prev != null && h == prev) {
                continue;
            }
            if (curr.overlaps(h)) {
                isValid = false;
            }
        }
        return isValid;
    }

    public static boolean moveUp(TETile[][] world) {
        if (world[avatar.getX()][avatar.getY() + 1] == Tileset.FLOOR) {
            world[avatar.getX()][avatar.getY()] = Tileset.FLOOR;
            avatar.setY(avatar.getY() + 1);
            world[avatar.getX()][avatar.getY()] = Tileset.AVATAR;
        } else if (world[avatar.getX()][avatar.getY() + 1] == Tileset.CREATURE) {
            world[avatar.getX()][avatar.getY()] = Tileset.FLOOR;
            avatar.setY(avatar.getY() + 1);
            world[avatar.getX()][avatar.getY()] = Tileset.AVATAR;
            return true;
        }
        return false;
    }

    public static boolean moveDown(TETile[][] world) {
        if (world[avatar.getX()][avatar.getY() - 1] == Tileset.FLOOR) {
            world[avatar.getX()][avatar.getY()] = Tileset.FLOOR;
            avatar.setY(avatar.getY() - 1);
            world[avatar.getX()][avatar.getY()] = Tileset.AVATAR;
        } else if (world[avatar.getX()][avatar.getY() - 1] == Tileset.CREATURE) {
            world[avatar.getX()][avatar.getY()] = Tileset.FLOOR;
            avatar.setY(avatar.getY() - 1);
            world[avatar.getX()][avatar.getY()] = Tileset.AVATAR;
            return true;
        }
        return false;
    }

    public static boolean moveLeft(TETile[][] world) {
        if (world[avatar.getX() - 1][avatar.getY()] == Tileset.FLOOR) {
            world[avatar.getX()][avatar.getY()] = Tileset.FLOOR;
            avatar.setX(avatar.getX() - 1);
            world[avatar.getX()][avatar.getY()] = Tileset.AVATAR;
        } else if (world[avatar.getX() - 1][avatar.getY()] == Tileset.CREATURE) {
            world[avatar.getX()][avatar.getY()] = Tileset.FLOOR;
            avatar.setX(avatar.getX() - 1);
            world[avatar.getX()][avatar.getY()] = Tileset.AVATAR;
            return true;
        }
        return false;
    }

    public static boolean moveRight(TETile[][] world) {
        if (world[avatar.getX() + 1][avatar.getY()] == Tileset.FLOOR) {
            world[avatar.getX()][avatar.getY()] = Tileset.FLOOR;
            avatar.setX(avatar.getX() + 1);
            world[avatar.getX()][avatar.getY()] = Tileset.AVATAR;
        } else if (world[avatar.getX() + 1][avatar.getY()] == Tileset.CREATURE) {
            world[avatar.getX()][avatar.getY()] = Tileset.FLOOR;
            avatar.setX(avatar.getX() + 1);
            world[avatar.getX()][avatar.getY()] = Tileset.AVATAR;
            return true;
        }
        return false;
    }

    private static void generateCreatures(int num, TETile[][] world, List<Room> rooms) {
        while (num > 0) {
            int roomNum = 1 + RANDOM.nextInt(rooms.size() - 1);
            Room creatureRoom = rooms.get(roomNum);
            int x = creatureRoom.getlL().getX()
                    + RANDOM.nextInt(creatureRoom.getuR().getX() - creatureRoom.getlL().getX());
            int y = creatureRoom.getlL().getY()
                    + RANDOM.nextInt(creatureRoom.getuR().getY() - creatureRoom.getlL().getY());
            Point creature = new Point(x, y);
            if (world[creature.getX()][creature.getY()].description().equals("floor")) {
                world[creature.getX()][creature.getY()] = Tileset.CREATURE;
            }
            num -= 1;
        }
    }

    /**
     * Draw a hallway of random length connecting to a random side of the previously drawn room.
     * If no valid hallway exists, draw a hallway coming out of the previous room.
     * @param world the grid of tiles
     * @param rooms list of existing rooms
     * @param halls list of existing halls
     */
    public static void drawHallway(TETile[][] world, List<Room> rooms, List<Hallway> halls) {
        // random location --> random available side of room
        int length = 3 + RANDOM.nextInt(HALL_MAX_LENGTH);
        int orientation = 0;
        boolean valid = false;
        Hallway curr = null;
        Room prev = rooms.get(rooms.size() - 1);

        while (!valid) {
            if (checkFull(prev)) {
                rooms.add(0, rooms.remove(rooms.size() - 1));
                prev = rooms.get(rooms.size() - 1);
            }
            int side = RANDOM.nextInt(4);
            switch (side) {
                case 0: // up
                    curr = prev.getHallUp(length);
                    orientation = UP;
                    prev.setUp(true);
                    break;
                case 1: // down
                    curr = prev.getHallDown(length);
                    orientation = DOWN;
                    prev.setDown(true);
                    break;
                case 2: // left
                    curr = prev.getHallLeft(length);
                    orientation = LEFT;
                    prev.setLeft(true);
                    break;
                case 3: // right
                    curr = prev.getHallRight(length);
                    orientation = RIGHT;
                    prev.setRight(true);
                    break;
                default:
                    curr = prev.getHallUp(length);
                    orientation = UP;
                    prev.setUp(true);
                    break;
            }
            if (validPoints(prev, curr, rooms, halls)) {
                valid = true;
            }
        }
        Point lL = curr.getlL();
        Point uR = curr.getuR();
        drawStraightHall(world, lL, uR, orientation);

        /** Set missing tile in hallway to be a wall, set hall's open side,
         * mark room's side as filled.*/
        if (orientation == UP) {
            world[lL.getX() + 1][uR.getY()] = Tileset.WALL;
            curr.setOpen(0);
            prev.setUp(true);
        } else if (orientation == DOWN) {
            world[lL.getX() + 1][lL.getY()] = Tileset.WALL;
            curr.setOpen(1);
            prev.setDown(true);
        } else if (orientation == LEFT) {
            world[lL.getX()][lL.getY() + 1] = Tileset.WALL;
            curr.setOpen(2);
            prev.setLeft(true);
        } else if (orientation == RIGHT) {
            world[uR.getX()][lL.getY() + 1] = Tileset.WALL;
            curr.setOpen(3);
            prev.setRight(true);
        }
        halls.add(curr);
    }

    /**
     * Checks if Room r can have a hallway appended to it.
     * @param r Room to be checked
     * @return whether r is full or not
     */
    private static boolean checkFull(Room r) {
        return r.markedUp() && r.markedDown() && r.markedLeft() && r.markedRight();
    }

    /**
     * Draws a valid hallway, given points such that the hall is connected to a room.
     * @param world the grid of tiles
     * @param lL the lower left point of the hall
     * @param uR the upper right point of the hall
     * @param orientation direction of hall corresponding to constants
     */
    public static void drawStraightHall(TETile[][] world, Point lL, Point uR, int orientation) {
        // set outside tiles to walls, inside strip to floor
        for (int x = lL.getX(); x <= uR.getX(); x += 1) {
            for (int y = lL.getY(); y <= uR.getY(); y += 1) {
                if (orientation > 1) {
                    if (y == lL.getY() || y == lL.getY() + 2) {
                        world[x][y] = Tileset.WALL;
                    } else {
                        world[x][y] = Tileset.FLOOR;
                    }
                } else {
                    if (x == lL.getX() || x == lL.getX() + 2) {
                        world[x][y] = Tileset.WALL;
                    } else {
                        world[x][y] = Tileset.FLOOR;
                    }
                }
            }
        }
    }

    /**
     * Generate the randomly generated map of rooms and halls.
     * @param ter tile renderer
     * @param world grid of tiles
     */
    public static void generateMap(TERenderer ter, TETile[][] world) {
        for (int x = 0; x < Engine.WIDTH; x += 1) {
            for (int y = 0; y < Engine.HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }

        // randomly generate, connect hallways and rooms
        List<Room> rooms = new ArrayList<>();
        List<Hallway> halls = new ArrayList<>();
        int numCalls = 30;
        while (numCalls > 0) {
            drawRoom(world, rooms, halls);
            drawHallway(world, rooms, halls);
            numCalls -= 1;
        }

        if (!loadGame) {
            generateCreatures(10, world, rooms);
        }
    }

    public static void main(String[] args) {
        //TETile[][] main = Engine.interactWithInputString("");

        // Initialize empty world

        TERenderer ter = new TERenderer();
        ter.initialize(Engine.WIDTH, Engine.HEIGHT);
        //TETile[][] world = new TETile[Engine.WIDTH][Engine.HEIGHT];
        TETile[][] world = Engine.interactWithInputString("n7685817615627686380s:q");
        ter.renderFrame(world);
        //TETile[][] world = Engine.interactWithInputString("lsssaaaa");
        /* for (int x = 0; x < Engine.WIDTH; x += 1) {
            for (int y = 0; y < Engine.HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        } */

        // Generate rooms and hallways
        generateMap(ter, world);
    }
}
