package com.a2.crazyEights;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class GameServer implements Serializable {

    // Static members
    private static final long serialVersionUID = 1L;
    private static int playerCapacity;

    // Accessible data members
    Game game;
    int numPlayers;
    int turns = 0;
    boolean isPlaying = true;

    // Private data members
    private final ArrayList<Server> playerServers = new ArrayList<>();
    private ServerSocket ss;

    public GameServer() {
        System.out.println("Starting game server");
        numPlayers = 0;

        try {
            ss = new ServerSocket(3333);
        } catch (IOException e) {
            System.out.println("Server Failed to open");
        }
    }

    public static void main(String[] args) {
        Scanner sin = new Scanner(System.in);
        int n;
        do {
            System.out.print("How many players? ");
            n = sin.nextInt();
        } while (n != 3 && n != 4);
        playerCapacity = n;

        GameServer sr = new GameServer();
        sr.acceptConnections();
        sr.gameLoop();
    }

    /* NON-STATIC METHODS */
    public void acceptConnections() {
        try {
            game = new Game(playerCapacity);
            System.out.println("Waiting for " + playerCapacity + " players to join...");
            while (numPlayers < playerCapacity) {
                Socket s = ss.accept();
                numPlayers++;

                Server server = new Server(s, numPlayers);

                // send the player number
                server.dOut.writeInt(server.playerId);
                server.dOut.flush();

                // get the player object
                Player newPlayer = server.receivePlayer();
                System.out.println("Player " + server.playerId + " ~ " + newPlayer.getName() + " ~ has joined");

                game.players.add(newPlayer);
                playerServers.add(server);
            }
            System.out.println(numPlayers + " players have joined the game");

            // start the server threads
            for (Server playerServer : playerServers) {
                Thread t = new Thread(playerServer);
                t.start();
            }

        } catch (IOException e) {
            System.out.println("Could not connect players");
        }
    }

    public void gameLoop() {
        try {
            PlayResult result = PlayResult.OK;
            for (int i = 0; i < numPlayers; i++)
                playerServers.get(i).sendSignal(0); // Activate players
            game.roundStart();
            while (game.champion == null) {
                turns++;

                String activePlayerName = game.players.get(game.getActivePlayer()-1).getName();
                String nextPlayerName = game.players.get(game.getNextPlayer()-1).getName();

                // Send all data to players
                for (int i = 0; i < numPlayers; i++) {
                    Server playerSever = playerServers.get(i);
                    playerSever.sendOpponents(game.players, game.players.get(i));
                    playerSever.sendOpponentNames(game.players, game.players.get(i));
                    playerSever.sendOpponentHandCounts(game.players, game.players.get(i));
                    playerSever.sendOpponentScores(game.players, game.players.get(i));
                    playerSever.sendHand(game.players.get(i).getHand());
                    playerSever.sendPlayerName(activePlayerName);
                    playerSever.sendPlayerName(nextPlayerName);
                    playerSever.sendCard(game.discard.peek());
                    playerSever.sendSignal(game.round);
                    playerSever.sendSignal(turns);
                    playerSever.sendBool(game.reversed);
                }

                // Handle player actions
                Server activePlayerServer = playerServers.get(game.getActivePlayer()-1);
                Player activePlayer;
                activePlayerServer.sendSignal(result.ordinal());
                boolean gettingActions = true;
                do {
                    activePlayer = game.players.get(game.getActivePlayer()-1);
                    activePlayerServer.sendSignal(activePlayer.numTimesDrawn);
                    // Send options to active player
                    if (result == PlayResult.TWO) {
                        activePlayerServer.sendBool( game.canDenyTwo() != null);
                    } else {
                        activePlayerServer.sendBool(game.canPlay());
                        activePlayerServer.sendBool(game.canDraw());
                    }

                    // Get the player's action
                    Action action = Action.values()[activePlayerServer.receiveSignal()];
                    Card card;
                    Suit suit = null;
                    switch (action) {
                        case PLAY:
                            do { // Let the player keep trying to play cards until they stop being dumb and pick a valid card
                                card = activePlayerServer.receiveCard();
                                if (card.getRank() == Rank.EIGHT) {
                                    suit = Suit.values()[activePlayerServer.receiveSignal()];
                                    result = game.playEight(card, suit);
                                }
                                else
                                    result = game.play(card);
                                // Send result to player
                                activePlayerServer.sendSignal(result.ordinal());
                            } while (result == PlayResult.INVALID_PLAY);
                            switch (result) {
                                case OK:
                                    notifyNonActivePlayers(activePlayer, activePlayer.getName() + " plays the " + card, true);
                                    break;
                                case SKIP:
                                    notifyNonActivePlayers(activePlayer, activePlayer.getName() + " plays the " + card + ", causing " + game.players.get(game.getNextPlayerFrom(activePlayer.getPlayerId())-1).getName() + " to skip their turn!", true);
                                    break;
                                case REVERSE:
                                    notifyNonActivePlayers(activePlayer, activePlayer.getName() + " plays the " + card + " causing the play order to reverse!", true);
                                    break;
                                case WILD:
                                    notifyNonActivePlayers(activePlayer, activePlayer.getName() + " plays the " + card + " declaring " + suit + " as the active suit!", true);
                                    break;
                                case TWO:
                                    notifyNonActivePlayers(activePlayer, activePlayer.getName() + " plays the " + card + " causing " + game.players.get(game.getNextPlayerFrom(activePlayer.getPlayerId())-1).getName() + " to either draw, deny, or respond!", true);
                                    break;
                                case ROUND_WIN:
                                    notifyNonActivePlayers(activePlayer, activePlayer.getName() + " plays the " + card + " emptying their hand and winning the round!", true);
                                    break;
                                case STALEMATE:
                                    notifyNonActivePlayers(activePlayer, activePlayer.getName() + " plays the " + card + " causing a stalemate!", true);
                                    break;
                            }
                            gettingActions = false;
                            break;
                        case CHAIN_TWO:
                            do { // Let the player keep trying to play cards until they stop being dumb and pick a valid Two
                                card = activePlayerServer.receiveCard();
                                result = game.play(card);
                                // Send result to player
                                activePlayerServer.sendSignal(result.ordinal());
                            } while (result == PlayResult.INVALID_PLAY);
                            notifyNonActivePlayers(activePlayer, activePlayer.getName() + " has played the " + card + ", continuing a Two Chain!", true);
                            gettingActions = false;
                            break;
                        case DRAW:
                            card = game.draw();
                            activePlayerServer.sendCard(card);
                            if (card != null)
                                notifyNonActivePlayers(activePlayer, activePlayer.getName() + " has drawn the " + card, false);
                            else
                                notifyNonActivePlayers(activePlayer, activePlayer.getName() + " attempted to draw, but no more cards could be drawn!", false);
                            break;
                        case DRAW_FROM_TWO:
                            ArrayList<Card> cardsDrawn = game.drawFromTwo();
                            activePlayerServer.sendHand(cardsDrawn);
                            if (!cardsDrawn.isEmpty())
                                notifyNonActivePlayers(activePlayer, activePlayer.getName() + " has drawn " + cardsDrawn + " as a result of the Two Chain!", false);
                            else
                                notifyNonActivePlayers(activePlayer, activePlayer.getName() + " attempted to draw, but no more cards could be drawn!", false);
                            result = PlayResult.OK;
                            break;
                        case DENY_TWO:
                            ArrayList<Card> sequence;
                            do { // Let the player keep trying to play card sequences until they pick a valid sequence
                                sequence = activePlayerServer.receivePlaySequence();
                                result = game.denyTwo(sequence);
                                if (sequence.get(1).getRank() == Rank.EIGHT) { // If the second card is an Eight, then the player needs to declare a suit
                                    suit = Suit.values()[activePlayerServer.receiveSignal()];
                                    game.declareSuit(suit);
                                }
                                // Send result to player
                                activePlayerServer.sendSignal(result.ordinal());
                            } while (result == PlayResult.INVALID_PLAY);
                            notifyNonActivePlayers(activePlayer, activePlayer.getName() + " ended the Two Chain by playing the " + sequence.get(0) + " and the " + sequence.get(1) + "!", false);
                            switch (result) {
                                case OK:
                                    notifyNonActivePlayers(activePlayer, activePlayer.getName() + "'s " + sequence.get(1) + " is on top of the discard.", true);
                                    break;
                                case SKIP:
                                    notifyNonActivePlayers(activePlayer, activePlayer.getName() + "'s " + sequence.get(1) + " causes " + game.players.get(game.getNextPlayerFrom(activePlayer.getPlayerId())-1).getName() + " to skip their turn!", true);
                                    break;
                                case REVERSE:
                                    notifyNonActivePlayers(activePlayer, activePlayer.getName() + "'s " + sequence.get(1) + " causes the play order to reverse!", true);
                                    break;
                                case WILD:
                                    notifyNonActivePlayers(activePlayer, activePlayer.getName() + "'s " + sequence.get(1) + " causes them to change the active suit to " + suit + "!", true);
                                    break;
                                case TWO:
                                    notifyNonActivePlayers(activePlayer, activePlayer.getName() + "'s " + sequence.get(1) + " causes " + game.players.get(game.getNextPlayerFrom(activePlayer.getPlayerId())-1).getName() + " to either draw, deny, or respond!", true);
                                    break;
                                case ROUND_WIN:
                                    notifyNonActivePlayers(activePlayer, activePlayer.getName() + "'s " + sequence.get(1) + " causes them to empty their hand, winning the round!", true);
                                    break;
                                case STALEMATE:
                                    notifyNonActivePlayers(activePlayer, activePlayer.getName() + "'s " + sequence.get(1) + " causes a stalemate!", true);
                                    break;
                            }
                            gettingActions = false;
                            break;
                        case PASS:
                            game.pass();
                            notifyNonActivePlayers(activePlayer, activePlayer.getName() + " passed the turn", true);
                            gettingActions = false;
                            break;
                    }
                } while (gettingActions);

                // Check if the round has ended
                if (result == PlayResult.ROUND_WIN || result == PlayResult.STALEMATE) {
                    // Build the scoreboard
                    StringBuilder scoreboard = new StringBuilder("\nSCORE:");
                    ArrayList<Player> players = game.players;
                    for (int i = 0; i < players.size(); i++) {
                        Player p = players.get(i);
                        scoreboard.append("\n").append(p.getName()).append(": ").append(game.getPlayerScore(i));
                    }
                    scoreboard.append("\n");
                    // Notify players
                    for (int i = 0; i < numPlayers; i++) {
                        Server playerServer = playerServers.get(i);
                        playerServer.sendBool(true);
                        playerServer.sendSignal(game.getPlayerScore(i));
                        playerServer.sendNotification(activePlayer.getName() + " has ended the round due to " + (result == PlayResult.ROUND_WIN ? "winning the round!" : "stalemate.") + scoreboard);
                    }
                    game.roundStart();
                    turns = 0;
                } else
                    for (int i = 0; i < numPlayers; i++)
                        playerServers.get(i).sendBool(false);

                // Check if the game is over
                isPlaying = game.champion == null;
                for (int i = 0; i < numPlayers; i++)
                    playerServers.get(i).sendBool(isPlaying);
                if (!isPlaying) {
                    for (int i = 0; i < numPlayers; i++)
                        playerServers.get(i).sendNotification(game.champion.getName() + " WINS THE GAME!");
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void notifyNonActivePlayers(Player active, String notification, boolean activation) {
        for (int i = 0; i < numPlayers; i++) {
            if (i != active.getPlayerId()-1) {
                playerServers.get(i).sendNotification(notification);
                playerServers.get(i).sendBool(activation);
            }
        }
    }

    /* EXTRA CLASSES */
    public class Server implements Runnable {
        private ObjectInputStream dIn;
        private ObjectOutputStream dOut;
        private final int playerId;

        public Server(Socket s, int pId){
            playerId = pId;
            try {
                dOut = new ObjectOutputStream(s.getOutputStream());
                dIn = new ObjectInputStream(s.getInputStream());
            } catch (IOException e) {
                System.out.println("Server Connection Failed");
            }
        }

        /* INTERFACE REQUIREMENTS */
        public void run() {
            try {
                //noinspection StatementWithEmptyBody
                while (true) {
                }
            } catch (Exception e) {
                System.out.println("Run failed");
                e.printStackTrace();
            }
        }

        /* NON-STATIC METHODS */
        // Send the number of player objects to other players
        public void sendNumOpponents(int numPlayers) {
            try {
                dOut.writeInt(numPlayers);
                dOut.flush();
            } catch (IOException e) {
                System.out.println("Could not send number of players");
                e.printStackTrace();
            }
        }

        // Send all player objects to other players
        public void sendOpponents(ArrayList<Player> plist, Player sentTo) {
            try {
                // Send the number of players being sent
                sendNumOpponents(plist.size()-1);
                for (Player p : plist) {
                    if (p != sentTo) { // Do not send player self data as opponent
                        dOut.writeObject(p);
                        dOut.flush();
                    }
                }
            } catch (IOException e) {
                System.out.println("Could not send player objects");
                e.printStackTrace();
            }
        }

        // Send all player objects' scores to other players
        public void sendOpponentScores(ArrayList<Player> plist, Player sentTo) {
            try {
                // Send the number of player hand counts being sent
                sendNumOpponents(plist.size()-1);
                for (int i = 0; i < numPlayers; i++) {
                    Player p = plist.get(i);
                    if (p != sentTo) { // Do not send player self data as opponent
                        dOut.writeInt(p.score);
                        dOut.flush();
                    }
                }
            } catch (IOException e) {
                System.out.println("Could not send player hand counts");
                e.printStackTrace();
            }
        }

        // Send all player objects' names to other players
        public void sendOpponentNames(ArrayList<Player> plist, Player sentTo) {
            try {
                // Send the number of player hand counts being sent
                sendNumOpponents(plist.size()-1);
                for (int i = 0; i < numPlayers; i++) {
                    Player p = plist.get(i);
                    if (p != sentTo) { // Do not send player self data as opponent
                        dOut.writeUTF(p.getName());
                        dOut.flush();
                    }
                }
            } catch (IOException e) {
                System.out.println("Could not send player hand counts");
                e.printStackTrace();
            }
        }

        // Send all player objects' hand count to other players
        public void sendOpponentHandCounts(ArrayList<Player> plist, Player sentTo) {
            try {
                // Send the number of player hand counts being sent
                sendNumOpponents(plist.size()-1);
                for (int i = 0; i < numPlayers; i++) {
                    Player p = plist.get(i);
                    if (p != sentTo) { // Do not send player self data as opponent
                        dOut.writeInt(p.getHandSize());
                        dOut.flush();
                    }
                }
            } catch (IOException e) {
                System.out.println("Could not send player hand counts");
                e.printStackTrace();
            }
        }

        // Send a cardstack object to other players
        public void sendCardStack(CardStack cardStack) {
            try {
                dOut.writeObject(cardStack);
                dOut.flush();
            } catch (IOException e) {
                System.out.println("Could not send card stack");
                e.printStackTrace();
            }
        }

        // Send a card object to other players
        public void sendCard(Card card) {
            try {
                dOut.writeObject(card);
                dOut.flush();
            } catch (IOException e) {
                System.out.println("Could not send card object");
                e.printStackTrace();
            }
        }

        // Send the number of card objects to be received by a player
        public void sendNumCards(int numCards) {
            try {
                dOut.writeInt(numCards);
                dOut.flush();
            } catch (IOException e) {
                System.out.println("Could not send number of cards");
                e.printStackTrace();
            }
        }

        // Send all card objects to a player representing the player's hand
        public void sendHand(ArrayList<Card> clist) {
            try {
                // Send the number of cards being sent
                sendNumCards(clist.size());
                for (Card c : clist) {
                    dOut.writeObject(c);
                    dOut.flush();
                }
            } catch (IOException e) {
                System.out.println("Could not send card objects");
                e.printStackTrace();
            }
        }

        // Send the a player's name from the server to a client
        public void sendPlayerName(String s) {
            try {
                dOut.writeUTF(s);
                dOut.flush();
            } catch (IOException e) {
                System.out.println("Could not send player name");
                e.printStackTrace();
            }
        }

        // Send a notification from the server to a client
        public void sendNotification(String s) {
            try {
                dOut.writeUTF(s);
                dOut.flush();
            } catch (IOException e) {
                System.out.println("Could not send notification");
                e.printStackTrace();
            }
        }

        // Send a boolean from the server to a client
        public void sendBool(boolean b) {
            try {
                dOut.writeBoolean(b);
                dOut.flush();
            } catch (IOException e) {
                System.out.println("Could not send boolean");
                e.printStackTrace();
            }
        }

        // Send a generic signal to a player
        public void sendSignal(int sig) {
            try {
                dOut.writeInt(sig);
                dOut.flush();
            } catch (IOException e) {
                System.out.println("Could not send number of players");
                e.printStackTrace();
            }
        }

        // Receive a Player object
        public Player receivePlayer() {
            try {
                return (Player) dIn.readObject();
            } catch (IOException e) {
                System.out.println("Player not received");
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                System.out.println("Class not found");
                e.printStackTrace();
            }
            return null;
        }

        // Receive a Card object
        public Card receiveCard() {
            try {
                return (Card) dIn.readObject();
            } catch (IOException e) {
                System.out.println("Card not received");
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                System.out.println("Class not found");
                e.printStackTrace();
            }
            return null;
        }

        // Receive the number of cards in a player's hand
        public int receiveHandCount() {
            try {
                return dIn.readInt();
            } catch (IOException e) {
                System.out.println("Hand count not received");
                e.printStackTrace();
            }
            return 0;
        }

        // Receive a signal from the player
        public int receiveSignal() {
            try {
                return dIn.readInt();
            } catch (IOException e) {
                System.out.println("Signal not received");
                e.printStackTrace();
            }
            return -1;
        }

        // Receive the number of cards to be sent to the player
        public int receiveNumCards() {
            try {
                return dIn.readInt();
            } catch (IOException e) {
                System.out.println("Number of cards not received");
                e.printStackTrace();
            }
            return 0;
        }

        // Receive a sequence of cards from a player
        public ArrayList<Card> receivePlaySequence() {
            // Get the number of cards being received
            int numCards = receiveNumCards();
            ArrayList<Card> clist = new ArrayList<>();
            try {
                for (int i = 0; i < numCards; i++) {
                    clist.add((Card) dIn.readObject());
                }
            } catch (IOException e) {
                System.out.println("Hand not received");
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                System.out.println("Class not found");
                e.printStackTrace();
            }
            return clist;
        }
    }
}
