##Backlog draft version

*OOPP group 33*

_Must have:_

- Quiz game to increase energy use awareness.
- Database with all possible questions(1).
- Leaderboard.
- Display who got a question right.
- Multiple choice questions (3+ answers per question).
- 20 questions per game (randomly selected from the database).
- Single player mode.
    - client server communication, past results of previous games.
- Multiplayer mode (with no player limit), support as many players at least 6.
    - Waiting area before the game showing how many others are waiting.
    - Everyone should have a unique name.
    - Start button to start the game.
    - Synchronous questions.

_Should have:_

- Jokers in the game, one of them should eliminate a wrong answer, one should decrease time (absolute or relative) for others, one for double points when sure about the answer. More than three jokers is not necessary. Use each one once. Possible to use these after you answer and before the time runs out.
- Every question should have an image.
- Score is visible throughout the game.
- Randomized answers in a close range.
- Something to represent how much time is left to answer the question.

_Could have:_

- Emojis to send during game.
- Extra points for quick answers.
- Sound effects.
- Live leaderboard during the multiplayer game.
    - 10 questions, leaderboard, 10 questions.

_Won&#39;t:_

- Authentication (signin/signup).
- Save data in multiplayer mode.
- Chat.

_(1):_

- Question - an activity that takes energy.

Answer - choose one other activity that takes the same amount of energy (three options).

- Question - guess how much energy something takes (a number within a range).
- Question - what activity takes the most energy out of three.