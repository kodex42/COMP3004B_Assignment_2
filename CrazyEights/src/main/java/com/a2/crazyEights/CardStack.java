package com.a2.crazyEights;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class CardStack {

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

    /* OVERRIDES */
    public boolean equals(Card other) {
        if (other == null) return false;
        return this.rank == other.rank && this.suit == other.suit;
    }

    public String toString() {
        String s, r;
        switch (suit) {
            case CLUBS:
                s = "Clubs";
                break;
            case DIAMONDS:
                s = "Diamonds";
                break;
            case HEARTS:
                s = "Hearts";
                break;
            case SPADES: default:
                s = "Spades";
                break;
        }
        switch (rank) {
            case ACE:
                r = "Ace";
                break;
            case TWO:
                r = "Two";
                break;
            case THREE:
                r = "Three";
                break;
            case FOUR:
                r = "Four";
                break;
            case FIVE:
                r = "Five";
                break;
            case SIX:
                r = "Six";
                break;
            case SEVEN:
                r = "Seven";
                break;
            case EIGHT:
                r = "Eight";
                break;
            case NINE:
                r = "Nine";
                break;
            case TEN:
                r = "Ten";
                break;
            case JACK:
                r = "Jack";
                break;
            case QUEEN:
                r = "Queen";
                break;
            case KING: default:
                r = "King";
                break;
        }
        return r + " of " + s;
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