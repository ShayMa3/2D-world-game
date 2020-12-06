* user may have to import javalib library prior to running
# 2D World Game
This is a 2D, top-down, tile-based game in which the player can explore randomly generated worlds and capture creatures.

## Design Document
### Classes and Data Structures
MapRender Class: generates the world (draws rooms and hallways)

Point Class: represents a point on the grid
- xPos: the x position
- yPos: the y position

Room Class: represents rooms
- lL: the lowerLeft Point of the room/hall
- uR: the upperRight Point of the room/hall
- up: true if top of room is connected to a hall
- down: true if bottom of room is connected to a hall
- left: true if left of room is connected to a hall
- right: true if right of room is connected to a hall

Hallway Class: represents hallways, subclass of Room
- open: an integer corresponding with of the hallway that is not already connected to a room

### Algorithms
generateMap(): 

Note: Only draw if the room/hall fits inside the grid and does not overlap with any other rooms/halls.
1. Generate one randomly sized room at a random location. 
2. Draw a hallway connected to a randomly selected and unmarked side of the room. Mark that side of the room. 
    - If all sides are marked, apply step 2 on the previously drawn room.
    - Randomly select if the drawn hallway should have a corner.
3. If space permits, draw a random room that connects to the other end off the hall.
    - If not, select the previously drawn room and begin at step 2.
4. Repeat steps 2 and 3 until all rooms and hallways are generated.
