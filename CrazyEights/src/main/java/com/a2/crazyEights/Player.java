package com.a2.crazyEights;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Player implements Serializable {

    // Static members
    private static final long serialVersionUID = 1L;
    static Client clientConnection;

    // Available data members
    int numTimesDrawn = 0;
    int score = 0;

    private int playerId = 0;
    private String name;
    private ArrayList<Card> hand = new ArrayList<>();

    public Player(String n) {
        name = n;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("What is your name? ");
        String name = scanner.nextLine();

        Player p = new Player(name);
        p.connectToClient();
        p.startGame();

        scanner.close();
    }

    /* NON-STATIC METHODS */
    public void startGame() {
        ArrayList<Action> availableActions = new ArrayList<>();
        System.out.println("Waiting for game to start...");
        clientConnection.receiveSignal();
        System.out.println("\n******* Round 1 Start! *******\n");
        while (true) {
            // Receive opponents for names and hand counts
            ArrayList<Player> opponents = clientConnection.receiveOpponents();
            ArrayList<String> names = clientConnection.receiveOpponentNames();
            ArrayList<Integer> handCounts = clientConnection.receiveOpponentHandCounts();
            ArrayList<Integer> scores = clientConnection.receiveOpponentScores();
            for (int i = 0; i < opponents.size(); i++) {
                Player opponent = opponents.get(i);
                opponent.name = names.get(i);
                opponent.hand.clear();
                for (int j = 0; j < handCounts.get(i); j++) {
                    opponent.addCard(new Card(Rank.ACE, Suit.SPADES)); // dummy cards to match opponent's hand sizes
                }
                opponent.score = scores.get(i);
            }
            hand = clientConnection.receiveHand();
            String activePlayer = clientConnection.receivePlayerName();
            String nextPlayer = clientConnection.receivePlayerName();
            Card discardTop = clientConnection.receiveCard();
            int roundNumber = clientConnection.receiveSignal();
            int turnNumber = clientConnection.receiveSignal();
            boolean reversed = clientConnection.receiveBool();

            // Check if you are currently the active player
            final String statusBanner = "[ROUND: " + roundNumber + "] [TURN: " + turnNumber + "] [ACTIVE PLAYER: " + activePlayer + "] [NEXT PLAYER: " + nextPlayer + "]" + " [PLAY DIRECTION: " + (reversed ? "Right]" : "Left]");
            if (activePlayer.equals(name)) {
                System.out.println("\n******* Turn Start! *******");
                // Get result of last play
                PlayResult result = PlayResult.values()[clientConnection.receiveSignal()];
                boolean choosingActions = true;
                do { // Enter action prompt loop
                    // Print game state
                    System.out.println(statusBanner);
                    for (Player p : opponents) {
                        System.out.println(p.getName() + " has " + p.getHandSize() + " cards left and a score of " + p.score);
                    }
                    System.out.println("\nThe " + discardTop + " is on top of the discard pile.");
                    System.out.println("Your hand is: " + hand);
                    numTimesDrawn = clientConnection.receiveSignal();
                    if (result == PlayResult.TWO) { // A two was played
                        if (clientConnection.receiveBool()) // Can deny two
                            availableActions.add(Action.DENY_TWO); // Deny the current two chain
                        if (handContainsTwo())
                            availableActions.add(Action.CHAIN_TWO); // Redirect the two chain and chain it further
                        availableActions.add(Action.DRAW_FROM_TWO); // Draw from last two card played
                    } else { // Usual gameplay
                        boolean canPlay = clientConnection.receiveBool();
                        boolean canDraw = clientConnection.receiveBool();
                        if (canPlay)
                            availableActions.add(Action.PLAY);
                        if (canDraw)
                            availableActions.add(Action.DRAW);
                        if (!canPlay && !canDraw)
                            availableActions.add(Action.PASS); // Can pass the turn
                    }

                    // Have player choose an available action and send it to the server
                    int choice = getActionChoice(availableActions);
                    Action action = availableActions.get(choice-1);
                    clientConnection.sendAction(action);

                    switch (action) {
                        case PLAY:
                            do { // Have the player choose a card to play until they pick a valid one
                                Card playingCard = getPlayingCard(discardTop);
                                clientConnection.sendCard(playingCard);
                                if (playingCard.getRank() == Rank.EIGHT)// Handle eights
                                    clientConnection.sendSignal(declareSuit().ordinal());
                                // Get result from server
                                result = PlayResult.values()[clientConnection.receiveSignal()];
                                if (result == PlayResult.INVALID_PLAY)
                                    System.out.println("That is not a valid play!");
                                else {
                                    System.out.println("You played the " + playingCard);
                                    break;
                                }
                            } while (true);
                            choosingActions = false;
                            break;
                        case CHAIN_TWO:
                            do { // Have the player choose a Two card to play until they pick a valid one
                                Card playingCard = getTwoCard(discardTop);
                                clientConnection.sendCard(playingCard);
                                // Get result from server
                                result = PlayResult.values()[clientConnection.receiveSignal()];
                                if (result == PlayResult.INVALID_PLAY)
                                    System.out.println("That is not a valid play!");
                                else {
                                    System.out.println("You played the " + playingCard);
                                    break;
                                }
                            } while (true);
                            choosingActions = false;
                            break;
                        case DRAW:
                            Card card = clientConnection.receiveCard();
                            if (card != null) {
                                System.out.println("You draw the " + card);
                                hand.add(card);
                            } else {
                                System.out.println("You try to draw, but the deck is empty!");
                            }
                            break;
                        case DRAW_FROM_TWO:
                            ArrayList<Card> cardsDrawn = clientConnection.receiveHand();
                            if (!cardsDrawn.isEmpty())
                                System.out.println("You draw " + cardsDrawn);
                            else
                                System.out.println("You tried to draw, but the deck is empty!");
                            hand.addAll(cardsDrawn);
                            result = PlayResult.OK; // No repeated double drawing
                            break;
                        case DENY_TWO:
                            do { // Have the player choose 2 cards to play in sequence until they pick a valid sequence
                                ArrayList<Card> sequence = getDenyTwoSequence(discardTop);
                                clientConnection.sendHand(sequence);
                                if (sequence.get(1).getRank() == Rank.EIGHT) // If the second card is an Eight, then the player needs to declare a suit
                                    clientConnection.sendSignal(declareSuit().ordinal());
                                // Get result from server
                                result = PlayResult.values()[clientConnection.receiveSignal()];
                                if (result == PlayResult.INVALID_PLAY)
                                    System.out.println("That is not a valid play!");
                                else {
                                    System.out.println("You played the " + sequence.get(0) + " and the " + sequence.get(1) + ", ending the Two Chain!");
                                    choosingActions = false;
                                    break;
                                }
                            } while (true);
                            break;
                        case PASS:
                            choosingActions = false;
                            break;
                    }

                    // Reset actions
                    availableActions.clear();
                    if (!choosingActions)
                        System.out.println("******* Turn Over! *******");
                } while (choosingActions);
            } else {
                System.out.println(statusBanner);                
                do {
                    System.out.println(clientConnection.receiveNotification()); // Get updates about the game state
                } while (!clientConnection.receiveBool()); // While not activated
            }

            // Check if the round is over
            if (clientConnection.receiveBool()) {
                score = clientConnection.receiveSignal();
                System.out.println(clientConnection.receiveNotification());
                System.out.println("******* Round Over! *******");
                System.out.println("\n******* Round " + (roundNumber+1) + " Start! *******\n");
            }

            // Check if the game is over
            boolean isPlaying = clientConnection.receiveBool();
            if (!isPlaying) {
                System.out.println(clientConnection.receiveNotification());
                break;
            }
        }
    }

    private ArrayList<Card> getDenyTwoSequence(Card discardTop) {
        ArrayList<Card> sequence = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        boolean gettingCard = true;
        do {
            System.out.println("\nThe " + discardTop + " is on top of the discard pile.");
            System.out.println("Your hand is: " + hand);
            System.out.print("Play which card first? ");
            String card = scanner.nextLine();
            for (Card c : hand) {
                if (c.toString().equalsIgnoreCase(card)) {
                    sequence.add(c);
                    gettingCard = false;
                }
            }
            if (gettingCard)
                System.out.println("Please choose a valid card.");
        } while (gettingCard);

        gettingCard = true;
        do {
            System.out.println("Your first card in sequence is the " + sequence.get(0));
            System.out.println("Your hand is: " + hand);
            System.out.print("Play which card? ");
            String card = scanner.nextLine();
            for (Card c : hand) {
                if (c.toString().equalsIgnoreCase(card) && !sequence.get(0).toString().equalsIgnoreCase(card)) {
                    sequence.add(c);
                    gettingCard = false;
                }
            }
            if (gettingCard)
                System.out.println("Please choose a valid card.");
        } while (gettingCard);

        return sequence;
    }

    private Suit declareSuit() {
        Scanner scanner = new Scanner(System.in);
        do {
            System.out.println("Choose a Suit...");
            System.out.println("[Clubs]");
            System.out.println("[Diamonds]");
            System.out.println("[Hearts]");
            System.out.println("[Spades]");
            String suit = scanner.next();
            if (suit.equalsIgnoreCase("clubs")) return Suit.CLUBS;
            if (suit.equalsIgnoreCase("diamonds")) return Suit.DIAMONDS;
            if (suit.equalsIgnoreCase("hearts")) return Suit.HEARTS;
            if (suit.equalsIgnoreCase("spades")) return Suit.SPADES;
            else System.out.println("Please choose a valid suit.");
        } while (true);
    }

    private void displayActions(ArrayList<Action> availableActions) {
        System.out.println("Choose an Action...");
        for (int i = 0; i < availableActions.size(); i++) {
            Action action = availableActions.get(i);
            int c = i+1;
            switch (action) {
                case PLAY:
                    System.out.println(c + " -> [Play a Card]");
                    break;
                case DRAW:
                    System.out.println(c + " -> [Draw a Card]");
                    break;
                case PASS:
                    System.out.println(c + " -> [Pass the Turn]");
                    break;
                case DENY_TWO:
                    System.out.println(c + " -> [Play 2 Cards to End the Two Chain]");
                    break;
                case CHAIN_TWO:
                    System.out.println(c + " -> [Chain the Two]");
                    break;
                case DRAW_FROM_TWO:
                    System.out.println(c + " -> [Draw from Two Chain]");
                    break;
            }
        }
    }

    private Card getPlayingCard(Card discardTop) {
        Scanner scanner = new Scanner(System.in);
        do {
            System.out.println("\nThe " + discardTop + " is on top of the discard pile.");
            System.out.println("Your hand is: " + hand);
            System.out.print("Play which card? ");
            String card = scanner.nextLine();
            if (numTimesDrawn > 0) {
                Card lastDrawn = hand.get(getHandSize()-1);
                if (lastDrawn.toString().equalsIgnoreCase(card))
                    return lastDrawn;
                System.out.println("You can only play your last drawn card.");
            } else {
                for (Card c : hand) {

                    if (c.toString().equalsIgnoreCase(card))
                        return c;
                }
                System.out.println("Please choose a valid card.");
            }
        } while (true);
    }

    private Card getTwoCard(Card discardTop) {
        Scanner scanner = new Scanner(System.in);
        do {
            System.out.println("\nThe " + discardTop + " is on top of the discard pile.");
            System.out.println("Your hand is: " + hand);
            System.out.print("Chain with which card? ");
            String card = scanner.nextLine();
            for (Card c : hand) {
                if (c.toString().equalsIgnoreCase(card) && c.getRank() == Rank.TWO)
                    return c;
            }
            System.out.println("Please choose a valid card.");
        } while (true);
    }

    private int getActionChoice(ArrayList<Action> availableActions) {
        Scanner scanner = new Scanner(System.in);
        do {
            try {
                displayActions(availableActions);
                System.out.print("Action: ");
                int choice = scanner.nextInt();
                if (choice < 1 || choice > availableActions.size())
                    throw new InputMismatchException();
                else
                    return choice;
            } catch (InputMismatchException e) {
                System.out.println("Please choose a valid action.");
                scanner.nextLine();
            }
        } while (true);
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

    public boolean handContainsTwo() {
        for (Card card : hand) {
            if (card.getRank() == Rank.TWO)
                return true;
        }
        return false;
    }

    public ArrayList<Card> getHand() {
        return hand;
    }

    public void emptyHand() {
        hand.clear();
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

        // Send the number of card objects to be received by the server
        public void sendNumCards(int numCards) {
            try {
                dOut.writeInt(numCards);
                dOut.flush();
            } catch (IOException e) {
                System.out.println("Could not send number of cards");
                e.printStackTrace();
            }
        }

        // Send and arraylist of card objects to the server
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

        // Sends a Card object to the server
        public void sendCard(Card card) {
            try {
                dOut.writeObject(card);
                dOut.flush();
            } catch (IOException e) {
                System.out.println("Card not sent");
                e.printStackTrace();
            }
        }

        public void sendSignal(int signal) {
            try {
                dOut.writeInt(signal);
                dOut.flush();
            } catch (IOException e) {
                System.out.println("Signal not sent");
                e.printStackTrace();
            }
        }

        // Sends the requested action
        public void sendAction(Action action) {
            try {
                dOut.writeInt(action.ordinal());
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

        // Receive scores of opponents
        public ArrayList<Integer> receiveOpponentScores() {
            // Get the number of opponents being received
            int numOpponents = receiveNumOpponents();
            ArrayList<Integer> scores = new ArrayList<>();
            try {
                for (int i = 0; i < numOpponents; i++) {
                    scores.add(dIn.readInt());
                }
            } catch (IOException e) {
                System.out.println("Hand counts not received");
                e.printStackTrace();
            }
            return scores;
        }

        // Receive names of opponents
        public ArrayList<String> receiveOpponentNames() {
            // Get the number of opponents being received
            int numOpponents = receiveNumOpponents();
            ArrayList<String> names = new ArrayList<>();
            try {
                for (int i = 0; i < numOpponents; i++) {
                    names.add(dIn.readUTF());
                }
            } catch (IOException e) {
                System.out.println("Hand counts not received");
                e.printStackTrace();
            }
            return names;
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

        // Get the name of a player
        public String receivePlayerName() {
            try {
                return dIn.readUTF();
            } catch (IOException e) {
                System.out.println("Could not get player name");
                e.printStackTrace();
            }
            return "";
        }

        // Get a notification from the server
        public String receiveNotification() {
            try {
                return dIn.readUTF();
            } catch (IOException e) {
                System.out.println("Could not get notification");
                e.printStackTrace();
            }
            return "";
        }

        // Get a boolean from the server
        public boolean receiveBool() {
            try {
                return dIn.readBoolean();
            } catch (IOException e) {
                System.out.println("Could not get boolean");
                e.printStackTrace();
            }
            return false;
        }

        // Receive a generic signal from the server
        public int receiveSignal() {
            try {
                return dIn.readInt();
            } catch (IOException e) {
                System.out.println("Signal not received");
                e.printStackTrace();
            }
            return -1;
        }
    }
}
