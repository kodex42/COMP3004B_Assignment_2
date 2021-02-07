package com.a2.crazyEights;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Player implements Serializable {

    // Static members
    private static final long serialVersionUID = 1L;
    static Client clientConnection;

    // Available data members
    int numTimesDrawn = 0;
    int score = 0;

    // Private data members
    private int playerId = 0;
    private String name;
    private ArrayList<Player> opponents = new ArrayList<>();
    private ArrayList<Card> hand = new ArrayList<>();

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
        System.out.println("Waiting for game to start...");
        clientConnection.receiveSignal();
        System.out.println("\n******* Game Start! *******\n");
        //noinspection LoopStatementThatDoesntLoop
        while (true) {
            // Receive opponents for names and hand counts
            opponents = clientConnection.receiveOpponents();
            hand = clientConnection.receiveHand();
            for (Player p : opponents) {
                System.out.println(p.getName() + " has " + p.getHandSize() + " cards left.");
            }
            System.out.println("\nYour hand is: " + hand);
            // Game Loop
            break;
        }
    }

    public void addCard(Card c) {
        hand.add(c);
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public Card playCard(Card c) {
        Card val = null;
        int i;

        for (i = 0; i < hand.size(); i++) {
            Card card = hand.get(i);
            if (card.equals(c)) {
                val = card;
                break;
            }
        }
        if (i != hand.size())
            hand.remove(i);

        return val;
    }

    public Card playCard(Rank r, Suit s) {
        Card c = new Card(r, s);
        Card val = null;
        int i;

        for (i = 0; i < hand.size(); i++) {
            Card card = hand.get(i);
            if (card.equals(c)) {
                val = card;
                break;
            }
        }
        if (i != hand.size())
            hand.remove(i);

        return val;
    }

    public Card getCard(Rank r, Suit s) {
        Card c = new Card(r, s);

        for (Card card : hand) {
            if (card.equals(c)) {
                return card;
            }
        }

        return null;
    }

    public int getHandSize() {
        return hand.size();
    }

    public String getName() {
        return name;
    }

    public int getPlayerId() {
        return playerId;
    }

    public Player getPlayer() {
        return this;
    }

    public ArrayList<Card> getHand() {
        return hand;
    }

    public void emptyHand() {
        hand.clear();
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

                setPlayerId(dIn.readInt());

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

        /* NON-STATIC METHODS */
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

        // Receive card objects representing the player's hand
        public ArrayList<Card> receiveHand() {
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

        // Receive a generic signal from the server
        public int receiveSignal() {
            try {
                return dIn.readInt();
            } catch (IOException e) {
                System.out.println("Signal not received");
                e.printStackTrace();
            }
            return 0;
        }
    }
}
