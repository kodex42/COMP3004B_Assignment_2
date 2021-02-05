package com.a2.crazyEights;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Player implements Serializable {

    private static final long serialVersionUID = 1L;
    public String name;

    int playerId = 0;

    static Client clientConnection;

    ArrayList<Player> opponents = new ArrayList<>();
    ArrayList<Card> hand = new ArrayList<>();

    public Player(String n) {
        name = n;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("What is your name? ");
        String name = scanner.next();

        Player p = new Player(name);
        p.connectToClient();
        p.startGame();

        scanner.close();
    }

    /* NON-STATIC METHODS */

    public void startGame() {
        // Receive players once for names
        opponents = clientConnection.receiveOpponents();
        for (int i = 0; i < 5; i++) {
            hand.add(clientConnection.receiveCard());
        }
        System.out.println("Game Start!");
        //noinspection LoopStatementThatDoesntLoop
        while (true) {
            // Game Loop
            clientConnection.sendHandCount();
            ArrayList<Integer> handCounts = clientConnection.receiveOpponentHandCounts();
            for (int i = 0; i < opponents.size(); i++) {
                System.out.println("Opponent " + opponents.get(i).getName() + " has " + handCounts.get(i) + " cards remaining.");
            }
            System.out.println(hand);
            break;
        }
    }

    private String getName() {
        return name;
    }

    public Player getPlayer() {
        return this;
    }

    /* OVERRIDES */
    public boolean equals(Player other) {
        if (other == null) return false;
        return this.playerId == other.playerId;
    }

    /* NETWORKING */
    public void connectToClient() {
        clientConnection = new Client();
    }

    public void connectToClient(int port) {
        clientConnection = new Client(port);
    }

    /* EXTRA CLASSES */
    private class Client {
        Socket socket;
        private ObjectInputStream dIn;
        private ObjectOutputStream dOut;

        public Client() {
            try {
                socket = new Socket("localhost", 3333);
                dOut = new ObjectOutputStream(socket.getOutputStream());
                dIn = new ObjectInputStream(socket.getInputStream());

                playerId = dIn.readInt();

                System.out.println("Connected as " + playerId);
                sendPlayer();

            } catch (IOException e) {
                System.out.println("Client failed to open");
            }
        }

        public Client(int portId) {
            try {
                socket = new Socket("localhost", portId);
                dOut = new ObjectOutputStream(socket.getOutputStream());
                dIn = new ObjectInputStream(socket.getInputStream());

                playerId = dIn.readInt();

                System.out.println("Connected as " + playerId);
                sendPlayer();

            } catch (IOException e) {
                System.out.println("Client failed to open");
            }
        }

        // Sends the Player object to the server
        public void sendPlayer() {
            try {
                dOut.writeObject(getPlayer());
                dOut.flush();
            } catch (IOException e) {
                System.out.println("Player not sent");
                e.printStackTrace();
            }
        }

        // Sends the size of the player's hand
        public void sendHandCount() {
            try {
                dOut.writeInt(hand.size());
                dOut.flush();
            } catch (IOException e) {
                System.out.println("Hand count not sent");
                e.printStackTrace();
            }
        }

        // Receive the number of total players for fetching all players
        public int receiveNumOpponents() {
            try {
                return dIn.readInt();
            } catch (IOException e) {
                System.out.println("Number of players not received");
                e.printStackTrace();
            }
            return 0;
        }

        // Receive player objects of opponents for names
        public ArrayList<Player> receiveOpponents() {
            // Get the number of opponents being received
            int numOpponents = receiveNumOpponents();
            ArrayList<Player> plist = new ArrayList<>();
            try {
                for (int i = 0; i < numOpponents; i++) {
                    plist.add((Player) dIn.readObject());
                }
            } catch (IOException e) {
                System.out.println("Players not received");
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                System.out.println("Class not found");
                e.printStackTrace();
            }
            return plist;
        }

        // Receive hand counts of opponents
        public ArrayList<Integer> receiveOpponentHandCounts() {
            // Get the number of opponents being received
            int numOpponents = receiveNumOpponents();
            ArrayList<Integer> handCounts = new ArrayList<>();
            try {
                for (int i = 0; i < numOpponents; i++) {
                    handCounts.add(dIn.readInt());
                }
            } catch (IOException e) {
                System.out.println("Hand counts not received");
                e.printStackTrace();
            }
            return handCounts;
        }

        // Receive card object to populate player hand
        public Card receiveCard() {
            try {
                Card card = (Card) dIn.readObject();
                System.out.println("You draw " + card);
                return card;
            } catch (IOException e) {
                System.out.println("Card not received");
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                System.out.println("Class not found");
                e.printStackTrace();
            }
            return null;
        }
    }
}
