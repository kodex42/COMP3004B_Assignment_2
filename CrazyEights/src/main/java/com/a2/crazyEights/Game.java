package com.a2.crazyEights;

import java.util.ArrayList;

public class Game {

    // Accessible data members
    int round = 0;
    int numPlayers;
    boolean reversed = false;
    boolean gameOver = false;
    CardStack deck = new CardStack(true);
    CardStack discard = new CardStack(false);
    ArrayList<Player> players = new ArrayList<>();
    Player champion = null;

    // Private data members
    private int activePlayer;

    public Game(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    private int getNumStackedTwos() {
        int i;
        for (i = 0; i < discard.size(); i++) {
            if (discard.get(i).getRank() != Rank.TWO)
                break;
        }
        return i;
    }

    private void resolveWin() {
        Player roundWinner = players.get(activePlayer-1);
        resolveRound();
    }

    private void resolveStalemate() {
        resolveRound();
    }

    private void resolveRound() {
        for (Player p : players) {
            for (Card c : p.getHand()) {
                switch (c.getRank()) {
                    case KING: case QUEEN: case JACK:
                        p.score += 10;
                        break;
                    case EIGHT:
                        p.score += 50;
                        break;
                    default:
                        p.score += c.getRank().ordinal() + 1;
                        break;
                }
            }
            if (p.score >= 100) gameOver = true;
        }
        
        if (gameOver) {
            Player winner = null;
            int lowest = 1000;
            for (Player p : players) {
                if (p.score < lowest) {
                    lowest = p.score;
                    winner = p;
                }
            }
            champion = winner;
        }
    }

    public void roundStart() {
        reversed = false;
        activePlayer = 1;
        for (int i = 0; i < round; i++) {
            activePlayer = getNextPlayer();
        }
        round++;
        for (Player p : players) {
            p.emptyHand();
        }
        deck = new CardStack(true);
        discard = new CardStack(false);
        discard.add(deck.draw());
        deal();
    }

    public PlayResult playEight(Card card, Suit newSuit) {
        Player player = players.get(activePlayer-1);
        // Play card from player's hand and reset draw count
        // This is done server side to prevent loss of cards through the socket
        discard.add(player.playCard(card));
        player.numTimesDrawn = 0;

        // Change the suit of the card
        declareSuit(newSuit);

        // Check for win condition
        if (player.getHandSize() == 0)
            return PlayResult.ROUND_WIN;

        activePlayer = getNextPlayer();

        return PlayResult.WILD;
    }

    public PlayResult play(Card card) {
        Player player = players.get(activePlayer-1);
        PlayResult result = PlayResult.OK;

        // Check if the card can be played
        if (!isValidPlay(card))
            return PlayResult.INVALID_PLAY;
        else
            player.numTimesDrawn = 0; // Reset the player's draw count since their play is valid

        // Play card from player's hand
        // This is done server side to prevent loss of cards through the socket
        discard.add(player.playCard(card));

        // Go to next player
        switch (card.getRank()) {
            case ACE:
                result = PlayResult.REVERSE;
                reversed = !reversed; // Reverse player order
                break;
            case TWO:
                result = PlayResult.TWO;
                break;
            case QUEEN:
                result = PlayResult.SKIP;
                activePlayer = getNextPlayer(); // Skip a player
                break;
        }

        // Check for win condition
        if (player.getHandSize() == 0) {
            resolveWin();
            return PlayResult.ROUND_WIN;
        }

        // Check for stalemate
        if (checkForStalemate()) {
            resolveStalemate();
            return PlayResult.STALEMATE;
        }

        activePlayer = getNextPlayer();

        return result;
    }

    public PlayResult denyTwo(ArrayList<Card> sequence) {
        Card c1 = sequence.get(0);
        Card c2 = sequence.get(1);
        Player player = players.get(activePlayer-1);
        PlayResult result = PlayResult.OK;

        /*-- CARD ONE --*/
        // Check if the card can be played
        if (!isValidPlaySequence(c1, c2))
            return PlayResult.INVALID_PLAY;

        // Play card from player's hand
        // This is done server side to prevent loss of cards through the socket
        discard.add(player.playCard(c1));

        // Go to next player
        switch (c1.getRank()) {
            case ACE:
                reversed = !reversed; // Reverse player order
                break;
            case QUEEN:
                activePlayer = getNextPlayer(); // Skip a player
                break;
        }

        /*-- CARD TWO --*/
        // Play card from player's hand
        // This is done server side to prevent loss of cards through the socket
        discard.add(player.playCard(c2));

        // Go to next player
        switch (c2.getRank()) {
            case ACE:
                result = PlayResult.REVERSE;
                reversed = !reversed; // Reverse player order
                break;
            case TWO:
                result = PlayResult.TWO;
                break;
            case EIGHT:
                result = PlayResult.WILD;
                break;
            case QUEEN:
                result = PlayResult.SKIP;
                activePlayer = getNextPlayer(); // Skip a player
                break;
        }

        // Check for win condition
        if (player.getHandSize() == 0) {
            resolveWin();
            return PlayResult.ROUND_WIN;
        }

        // Check for stalemate
        if (checkForStalemate()) {
            resolveStalemate();
            return PlayResult.STALEMATE;
        }

        activePlayer = getNextPlayer();

        return result;
    }

    public void pass() {
        Player p = players.get(activePlayer-1);
        p.numTimesDrawn = 0;
        activePlayer = getNextPlayer();
    }

    public void deal() {
        // Deal cards to players
        for (int i = 0; i < 5; i++) { // Deal 5 cards one at a time to each player
            for (int k = 0; k < numPlayers; k++) {
                players.get(k).addCard(deck.draw());
            }
        }
    }

    public boolean checkForStalemate() {
        for (Player p : players) {
            if (canDraw(p.getPlayerId()) || canPlay(p.getPlayerId()))
                return false;
        }
        return true;
    }

    public void declareSuit(Suit s) {
        Card c = discard.peek();
        discard.set(0, c.getRank(), s);
    }

    public boolean isValidPlaySequence(Card c, Card k) {
        return !c.equals(k) && (c.getRank() == k.getRank() || c.getSuit() == k.getSuit() || k.getRank() == Rank.EIGHT || c.getRank() == Rank.EIGHT);
    }

    public boolean isValidPlay(Card card) {
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

    public int getNextPlayer() {
        int increment = reversed ? -1 : 1;
        int next = activePlayer + increment;
        if (next > numPlayers) next -= numPlayers;
        if (next < 1) next += numPlayers;
        return next;
    }

    public int getNextPlayerFrom(int id) {
        int increment = reversed ? -1 : 1;
        int next = id + increment;
        if (next > numPlayers) next -= numPlayers;
        if (next < 1) next += numPlayers;
        return next;
    }

    public int getPlayerScore(int i) {
        return players.get(i).score;
    }

    public boolean deckHasCards() {
        return deck.size() > 0;
    }

    public boolean nextPlayerHasTwo() {
        Player next = players.get(getNextPlayer());
        return next.handContainsTwo();
    }

    public ArrayList<Card> drawFromTwo() {
        ArrayList<Card> cardsDrawn = new ArrayList<>();
        Player p = players.get(activePlayer-1);
        // draw two cards per number of stacked twos
        for (int i = 0; i < getNumStackedTwos(); i++) {
            Card card;
            if (deckHasCards()) {
                card = deck.draw();
                p.addCard(card);
                cardsDrawn.add(card);
            }
            if (deckHasCards()) {
                card = deck.draw();
                p.addCard(card);
                cardsDrawn.add(card);
            }
        }
        return cardsDrawn;
    }

    public ArrayList<Card> canDenyTwo() {
        Player p = players.get(activePlayer-1);
        ArrayList<Card> playableCardSequence = new ArrayList<>();
        for (Card c : p.getHand()) {
            if (isValidPlay(c))
                for (Card k : p.getHand()) {
                    if (isValidPlaySequence(c, k)) {
                        playableCardSequence.add(c);
                        playableCardSequence.add(k);
                        return playableCardSequence;
                    }
                }
        }
        return null;
    }

    public boolean canPlay() {
        Player p = players.get(activePlayer-1);
        if (p.numTimesDrawn > 0) return isValidPlay(p.getHand().get(p.getHandSize()-1));
        else {
            for (Card card : players.get(activePlayer - 1).getHand()) {
                if (isValidPlay(card)) return true;
            }
            return false;
        }
    }

    public boolean canPlay(int id) {
        for (Card card : players.get(id-1).getHand()) {
            if (isValidPlay(card)) return true;
        }
        return false;
    }

    public boolean canDraw() {
        Player p = players.get(activePlayer-1);
        return (p.numTimesDrawn == 0 || (p.numTimesDrawn < 3 && !canPlay())) && deckHasCards();
    }

    public boolean canDraw(int id) {
        Player p = players.get(id-1);
        return p.numTimesDrawn < 3 && deckHasCards();
    }

    public Card draw() {
        Player p = players.get(activePlayer-1);
        if (canDraw()) {
            Card card = deck.draw();
            p.addCard(card);
            p.numTimesDrawn++;
            return card;
        }
        return null;
    }

    /* ONLY FOR TESTING */
    public void roundForceStartNoDeal(Rank r, Suit s) {
        reversed = false;
        activePlayer = 1;
        for (int i = 0; i < round; i++) {
            activePlayer = getNextPlayer();
        }
        reversed = false;
        for (Player p : players) {
            p.emptyHand();
        }
        deck = new CardStack(true);
        discard = new CardStack(false);
        discard.add(deck.get(r, s));
    }

    public void riggedDraw(Rank r, Suit s) {
        Player p = players.get(activePlayer-1);
        if (canDraw()) {
            p.addCard(deck.get(r, s));
            p.numTimesDrawn++;
        }
    }

    public void riggedDrawFromTwo(Rank r1, Suit s1, Rank r2, Suit s2) {
        Player p = players.get(activePlayer-1);
        // draw two specific cards
        p.addCard(deck.get(r1, s1));
        p.addCard(deck.get(r2, s2));
    }
}

enum PlayResult {
    OK,
    INVALID_PLAY,
    SKIP,
    REVERSE,
    WILD,
    TWO,
    ROUND_WIN,
    STALEMATE
}

enum Action {
    PLAY,
    CHAIN_TWO,
    DRAW,
    DRAW_FROM_TWO,
    DENY_TWO,
    PASS
}
