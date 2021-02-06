package com.a2.crazyEights;

import java.util.ArrayList;

public class Game {

    // Accessible data members
    int numPlayers;
    boolean reversed = false;
    CardStack deck = new CardStack(true);
    CardStack discard = new CardStack(false);
    ArrayList<Player> players = new ArrayList<>();

    // Private data members
    private int activePlayer;

    public Game(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    public void roundForceStartNoDeal(Rank r, Suit s) {
        activePlayer = 1;
        discard.add(deck.get(r, s));
    }

    public void roundStart() {
        activePlayer = 1;
        discard.add(deck.draw());
        deal();
    }

    public PlayResult play(Card card) {
        PlayResult result = PlayResult.OK;

        // Check if the card can be played
        if (!isValidPlay(card))
            return PlayResult.INVALID_PLAY;

        // Play card from player's hand
        discard.add(players.get(activePlayer-1).playCard(card));

        // Go to next player
        int increment;
        switch (card.getRank()) {
            case ACE:
                result = PlayResult.REVERSE;
                reversed = !reversed;
                increment = reversed ? -1 : 1;
                break;
            case EIGHT:
                result = PlayResult.WILD;
                increment = reversed ? -1 : 1;
                break;
            case QUEEN:
                result = PlayResult.SKIP;
                increment = reversed ? -2 : 2;
                break;
            default:
                increment = reversed ? -1 : 1;
        }
        activePlayer = activePlayer + increment;
        if (activePlayer > numPlayers) activePlayer -= numPlayers;
        if (activePlayer < 1) activePlayer += numPlayers;

        return result;
    }

    private boolean isValidPlay(Card card) {
        Card current = discard.peek();
        return (
                current.getRank() == card.getRank()
                || current.getSuit() == card.getSuit()
                || card.getRank() == Rank.EIGHT
        );
    }

    public int getActivePlayer() {
        return activePlayer;
    }

    public void deal() {
        // Deal cards to players
        for (int i = 0; i < 5; i++) { // Deal 5 cards one at a time to each player
            for (int k = 0; k < numPlayers; k++) {
                players.get(k).addCard(deck.draw());
            }
        }
    }
}

enum PlayResult {
    OK,
    INVALID_PLAY,
    SKIP,
    REVERSE,
    WILD,
    TWO,
    DOUBLE_TWO,
    TRIPLE_TWO,
    QUAD_TWO,
    MUST_DRAW,
    ROUND_WIN
}
