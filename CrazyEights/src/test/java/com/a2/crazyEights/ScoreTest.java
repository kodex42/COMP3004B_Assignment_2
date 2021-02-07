package com.a2.crazyEights;

import junit.framework.TestCase;

public class ScoreTest extends TestCase {
    public void testScoreAfterRiggedGame() {
        // Declare objects
        Game game = new Game(4);
        Player p1 = new Player("P1");
        Player p2 = new Player("P2");
        Player p3 = new Player("P3");
        Player p4 = new Player("P4");
        game.players.add(p1);
        game.players.add(p2);
        game.players.add(p3);
        game.players.add(p4);

        // Set the player id (normally done automatically by the server)
        p1.setPlayerId(1);
        p2.setPlayerId(2);
        p3.setPlayerId(3);
        p4.setPlayerId(4);

        // Start the round with king of clubs
        game.roundForceStartNoDeal(Rank.KING, Suit.SPADES);

        // Populate hands with specific cards
        p1.addCard(game.deck.get(Rank.ACE, Suit.SPADES));
        p1.addCard(game.deck.get(Rank.THREE, Suit.SPADES));
        p2.addCard(game.deck.get(Rank.FOUR, Suit.SPADES));
        p3.addCard(game.deck.get(Rank.EIGHT, Suit.HEARTS));
        p3.addCard(game.deck.get(Rank.JACK, Suit.HEARTS));
        p3.addCard(game.deck.get(Rank.SIX, Suit.HEARTS));
        p3.addCard(game.deck.get(Rank.KING, Suit.HEARTS));
        p3.addCard(game.deck.get(Rank.KING, Suit.SPADES));
        p4.addCard(game.deck.get(Rank.EIGHT, Suit.CLUBS));
        p4.addCard(game.deck.get(Rank.EIGHT, Suit.DIAMONDS));
        p4.addCard(game.deck.get(Rank.TWO, Suit.DIAMONDS));

        // Player 1 starts with a three of spades
        assertEquals(1, game.getActivePlayer());
        PlayResult result = game.play(p1.getCard(Rank.THREE, Suit.SPADES));
        assertEquals(PlayResult.OK, result);
        // Player 2 finishes the round with the four of spades
        assertEquals(2, game.getActivePlayer());
        result = game.play(p2.getCard(Rank.FOUR, Suit.SPADES));
        assertEquals(PlayResult.ROUND_WIN, result);
        // Check scores
        assertEquals(1, p1.score);
        assertEquals(0, p2.score);
        assertEquals(86, p3.score);
        assertEquals(102, p4.score);
    }
}
