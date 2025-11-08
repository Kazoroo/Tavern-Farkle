# Tavern Farkle
Tavern Farkle is an Android dice game inspired by the mini-game from <a href="https://store.steampowered.com/app/379430/Kingdom_Come_Deliverance/">Kingdome Come: Deliverance</a>. </br>
The game is varied with special dice and multiplayer mode. The gameplay is about rolling several dice and choosing that ones, which gives the most points. You have to outscore the opponent, and be careful to not end up with no scoring dice.

<p align="left">
  <img alt="Tavern Farkle icon" height="80" src="app/src/main/ic_launcher-playstore.png" />
    
  <a href="https://play.google.com/store/apps/details?id=pl.kazoroo.tavernFarkle">
    <img alt="Get it on Google Play" height="100" src="docs/google-badge.png" />
  </a>
</p>

<h3>Key features:</h3>
<ul>
    <li>Singleplayer with opponent gameplay algorithm</li>
    <li>Multiplayer using Firebase Realtime Database for state storage</li>
    <li>Special dice allowing to compose a strategy</li>
    <li>AdMob ads for bonus coins</li>
    <li>Betting system to win or lose coins on games</li>
    <li>Music and sounds effects utilizing SoundPlayer and MediaPlayer</li>
    <li>Onboarding tutorial for new players</li>
    <li>Translations to 6 languages</li>
</ul>

<p align="left">
    <img alt="Tavern Farkle icon" height="500" src="https://github.com/user-attachments/assets/74e2b644-6569-47e9-859b-31d8dedd784f" />
    <img alt="Tavern Farkle icon" height="500" src="https://github.com/user-attachments/assets/5f976390-626c-4586-9ab9-fbcbc110b0a7" />
</p>

### Architecture

The app follows a clean architecture pattern.  
A single `GameViewModel` observes the game state stored in a `GameRepository`, which has two implementations:
- `LocalGameRepository` for singleplayer mode  
- `RemoteGameRepository` for multiplayer mode  

In multiplayer, the game state is stored both locally and in Firebase.  
When a player takes an action, they immediately see the result, while the opponent's view updates in real-time by listening to the same database node.

The flow is simple:
1. The host creates a lobby visible to all players.  
2. Another player joins it from the lobby screen.  
3. Once the game ends, the lobby node is deleted, and the winner receives the reward.


<img width="1266" height="449" alt="obraz" src="https://github.com/user-attachments/assets/b57ac2c6-31c5-4a0a-9c7c-cc1de048c0bf" /></br>


Feel free to contribute!



