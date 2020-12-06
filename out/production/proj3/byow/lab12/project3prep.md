# Project 3 Prep

**For tessellating hexagons, one of the hardest parts is figuring out where to place each hexagon/how to easily place hexagons on screen in an algorithmic way.
After looking at your own implementation, consider the implementation provided near the end of the lab.
How did your implementation differ from the given one? What lessons can be learned from it?**

Answer: In my implementation, I had the position be the topleft coordinate of each hexagon and I tried to brute force my approach, whereas in the lab solution, they had a Hexagon class with a position attribute and were able to efficiently use abstraction to their advantage.

-----

**Can you think of an analogy between the process of tessellating hexagons and randomly generating a world using rooms and hallways?
What is the hexagon and what is the tesselation on the Project 3 side?**

Answer: Tessellating hexagons would be the same process as connecting rooms and hallways together. The hexagon is the individual rooms/hallways and tesselation is connection.

-----
**If you were to start working on world generation, what kind of method would you think of writing first? 
Think back to the lab and the process used to eventually get to tessellating hexagons.**

Answer: I would likely think of a method to first generate a single hallway or room, then worry about connecting them and forming larger structures later.

-----
**What distinguishes a hallway from a room? How are they similar?**

Answer: Hallways are narrowly surrounded by walls, whereas rooms are more wide. They are similar because they both must contain floor surrounded by walls and they both connect to each other. 
