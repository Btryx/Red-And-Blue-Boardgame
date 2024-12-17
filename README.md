# Red And Blue BoardGame
A boardgame made with javaFX for my second semester subject: Software development in practice
## 🎮 How to Play
There are 22 red, and 3 blue pieces on the 5x5 sized controller. One player moves with red, the other, with blue. The blue player begins the game.
Every step consists of moving one piece.

- **Movements**:
  - The **Red Player** can only move to a neighbouring field.
  - The **Blue Player** can only move to a neighbouring field that is already occupied by a red piece. Then, this red piece will be removed.

- **Winning Conditions**:
  - The **Red Player** wins if all **Blue Pieces** are aligned in the same row or column.
  - The **Blue Player** wins if no more valid moves are possible for the Blue pieces.

## 🎥 Demo
    demo/BoardGame.exe

## 🛠️ Technologies Used
**Language**: Java

**UI Framework**: JavaFX

**Important Plugins**: Maven, JUnit, Checkstyle, JavaDoc, Tinylog, Gson, Lombok


## 📸 Screenshots
### At the start of the game, you can set the player names.

![kép](https://github.com/user-attachments/assets/0c000e1f-26df-43d2-afe6-ee0b76e3ff0a)


### Blue player starts. Click a blue circle to select it, than click a valid field to move it. The selected piece will have a slightly darker color.

![kép](https://github.com/user-attachments/assets/d2e5c2f7-fa57-4539-829f-8084ae258a2c)



### When a player wins, the game will display that player's name.

![kép](https://github.com/user-attachments/assets/d85c62f4-b343-497b-ba37-97bf7ab398c9)


### Check out the leaderboard to see the winners. The top ten will appear, based on how many moves it took to win.

![kép](https://github.com/user-attachments/assets/315f954f-898c-4c37-ad86-0c194191c3fb)







