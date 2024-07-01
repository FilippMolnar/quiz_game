# Before each commit we should run the following tests



### If something breaks, message on the group🚨!!
<hr/>

### Process
**Basic tests**
1. Run the server again 
2. Run 2 clients 
3. Join with one name 
4. For the other client try to join with same name and see if you are not allowed to this 
5. After that join with a different name the waiting room
6. Start game 
7. Test if the emojis are sent from one player to another player 
8. Test if both players ran out of time the timer does not do weird things
9. Test clicking on different answers shows the good statistics 
10. Close both clients and repeat the same process steps from 2 to 9 again 

**Bugs that should not appear**

| Bug description   |      How to test for it      |  Status of dev | Person who knows about it |
|-------------------|:-----------------------------:|----------------:| ---:|
| People stay in the waiting room after the game has started. |  Check this by adding a client to the game and do a GET request to the`localhost:8080/api/wait` (just open a browser) | Not fixed |   Nicolae  |
| Negative seconds for the timer  |    Just run a game   |  Sometimes appears |   Nicolae  |
| Rematch the game crashes (in general) | Try a rematch after running a game   | Fixed for single player |  Izzy | 




**UI Tests**
1. Back button from waiting room leaves the waiting room 
2. Only buttons should be clickable (Score should be not clickable)
3. Start game has a hover effect 