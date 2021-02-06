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
    int turns;
    boolean isPlaying = true;

    // Private data members
    private ArrayList<Server> playerServers = new ArrayList<>();
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
            for (int i = 0; i < numPlayers; i++)
                playerServers.get(i).sendSignal(0); // Activate players
            game.roundStart();
            while (isPlaying) {
                turns++;

                for (int i = 0; i < numPlayers; i++) {
                    // Send opponents for names and hand count, and send hand to each player
                    playerServers.get(i).sendOpponents(game.players, game.players.get(i));
                    playerServers.get(i).sendHand(game.players.get(i).getHand());
                }

                System.out.println("****************************************");
                System.out.println("Turn " + turns);

                isPlaying = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* EXTRA CLASSES */
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

        // Send all player objects' hand count to other players
        public void sendOpponentHandCounts(ArrayList<Player> plist, Player sentTo, int[] handSizes) {
            try {
                // Send the number of player hand counts being sent
                sendNumOpponents(plist.size());
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
