package com.a2.crazyEights;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class GameServer implements Serializable {

    private static final long serialVersionUID = 1L;

    ArrayList<Server> playerServers = new ArrayList<>();
    ArrayList<Player> players = new ArrayList<>();
    CardStack deck = new CardStack(true);
    CardStack discard = new CardStack(false);

    ServerSocket ss;

    int numPlayers;
    int turns;
    boolean isPlaying = true;

    public static void main(String[] args) {
        GameServer sr = new GameServer();

        sr.acceptConnections();
        sr.gameLoop();
    }

    public GameServer() {
        System.out.println("Starting game server");
        numPlayers = 0;

        try {
            ss = new ServerSocket(3333);
        } catch (IOException e) {
            System.out.println("Server Failed to open");
        }
    }

    public void acceptConnections() {
        try {
            System.out.println("Waiting for players...");
            while (numPlayers < 3) {
                Socket s = ss.accept();
                numPlayers++;

                Server server = new Server(s, numPlayers);

                // send the player number
                server.dOut.writeInt(server.playerId);
                server.dOut.flush();

                // get the player object
                Player newPlayer = server.receivePlayer();
                System.out.println("Player " + server.playerId + " ~ " + newPlayer.name + " ~ has joined");

                players.add(newPlayer);
                playerServers.add(server);

            }
            System.out.println("Three players have joined the game");

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
            // Send players for names
            for (int i = 0; i < numPlayers; i++) {
                playerServers.get(i).sendOpponents(players, players.get(i));
            }
            // Deal cards to players
            for (int i = 0; i < 5; i++) { // Deal 5 cards one at a time to each player
                for (int k = 0; k < numPlayers; k++) {
                    playerServers.get(k).sendCard(deck.draw());
                }
            }

            while (isPlaying) {
                turns++;

                System.out.println("****************************************");
                System.out.println("Turn " + turns);

                int[] handSizes = new int[numPlayers];
                // Get each player's hand sizes
                for (int i = 0; i < numPlayers; i++) {
                    handSizes[i] = playerServers.get(i).receiveHandCount();
                }
                // Send player hand sizes
                for (int i = 0; i < numPlayers; i++) {
                    playerServers.get(i).sendOpponentHandCounts(players, players.get(i), handSizes);
                }

                isPlaying = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class Server implements Runnable {
        private Socket socket;
        private ObjectInputStream dIn;
        private ObjectOutputStream dOut;
        private int playerId;

        public Server(Socket s, int pId){
            socket = s;
            playerId = pId;
            try {
                dOut = new ObjectOutputStream(socket.getOutputStream());
                dIn = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                System.out.println("Server Connection Failed");
            }
        }

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

        // Send the number of player objects to other players
        public void sendNumOpponents() {
            try {
                dOut.writeInt(numPlayers-1);
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
                sendNumOpponents();
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

        // Send all player objects' hand count to other players
        public void sendOpponentHandCounts(ArrayList<Player> plist, Player sentTo, int[] handSizes) {
            try {
                // Send the number of player hand counts being sent
                sendNumOpponents();
                for (int i = 0; i < numPlayers; i++) {
                    Player p = plist.get(i);
                    if (p != sentTo) { // Do not send player self data as opponent
                        dOut.writeInt(handSizes[i]);
                        dOut.flush();
                    }
                }
            } catch (IOException e) {
                System.out.println("Could not send player hand counts");
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

        // Receive a player object
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
    }
}
