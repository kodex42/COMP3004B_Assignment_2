package com.a2.crazyEights;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class CardStack implements Serializable {

    private ArrayList<Card> cards = new ArrayList<>();
    private boolean faceUp;

    public CardStack(boolean startFilled) {
        if (startFilled) {
            // Initialize deck
            for (Rank r : Rank.values()) {
                for (Suit s : Suit.values()) {
                    cards.add(new Card(r, s));
                }
            }

            // Shuffle
            shuffle();
        }
        faceUp = !startFilled; // Decks are face down, discard piles are face up
    }

    /* NON-STATIC METHODS */
    public void shuffle() {
        Collections.shuffle(cards);
    }

    public void set(int i, Rank r, Suit s) {
        cards.set(i, new Card(r, s));
    }

    public Card get(int i) {
        if (faceUp) {
            return cards.get(i);
        } else return null;
    }

    public Card get(Rank r, Suit s) {
        Card c = new Card(r, s);
        Card val = null;
        for (Card card : cards) {
            if (card.equals(c)) {
                val = card;
                break;
            }
        }
        if (!faceUp) shuffle();
        return val;
    }

    /* STACK-LIKE LIFO OPPERATIONS */
    public Card draw() {
        return cards.remove(0);
    }

    public void add(Card card) {
        cards.add(0, card);
    }

    public Card peek() {
        return faceUp ? cards.get(0) : null;
    }

    public int size() {
        return cards.size();
    }
}

class Card implements Serializable {

    private static final long serialVersionUID = 1L;

    private Suit suit;
    private Rank rank;

    public Card(Rank r, Suit s) {
        suit = s;
        rank = r;
    }

    /* NON-STATIC METHODS */
    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    public void setRank(Rank r) {
        rank = r;
    }

    public void setSuit(Suit s) {
        suit = s;
    }

    /* OVERRIDES */
    public boolean equals(Card other) {
        if (other == null) return false;
        return this.rank == other.rank && this.suit == other.suit;
    }

    public String toString() {
        String s, r;
        switch (suit) {
            case CLUBS:
                s = "C";
                break;
            case DIAMONDS:
                s = "D";
                break;
            case HEARTS:
                s = "H";
                break;
            case SPADES: default:
                s = "S";
                break;
        }
        switch (rank) {
            case ACE:
                r = "A";
                break;
            case TWO:
                r = "2";
                break;
            case THREE:
                r = "3";
                break;
            case FOUR:
                r = "4";
                break;
            case FIVE:
                r = "5";
                break;
            case SIX:
                r = "6";
                break;
            case SEVEN:
                r = "7";
                break;
            case EIGHT:
                r = "8";
                break;
            case NINE:
                r = "9";
                break;
            case TEN:
                r = "T";
                break;
            case JACK:
                r = "J";
                break;
            case QUEEN:
                r = "Q";
                break;
            case KING: default:
                r = "K";
                break;
        }
        return r + s;
    }
}

enum Suit {
    CLUBS,
    DIAMONDS,
    HEARTS,
    SPADES
}

enum Rank {
    ACE,
    TWO,
    THREE,
    FOUR,
    FIVE,
    SIX,
    SEVEN,
    EIGHT,
    NINE,
    TEN,
    JACK,
    QUEEN,
    KING
}