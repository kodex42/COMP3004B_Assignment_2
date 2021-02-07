package com.a2.crazyEights;

import junit.framework.TestCase;

public class FullRiggedGameTest extends TestCase {
    public void testFullGame() {
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
        PlayResult result;

        // Set the player id (normally done automatically by the server)
        p1.setPlayerId(1);
        p2.setPlayerId(2);
        p3.setPlayerId(3);
        p4.setPlayerId(4);

        // Start the round with king of clubs
        game.roundForceStartNoDeal(Rank.FOUR, Suit.DIAMONDS);

        // Populate hands
        // Player 1
        p1.addCard(game.deck.get(Rank.FOUR, Suit.HEARTS));
        p1.addCard(game.deck.get(Rank.SEVEN, Suit.SPADES));
        p1.addCard(game.deck.get(Rank.FIVE, Suit.DIAMONDS));
        p1.addCard(game.deck.get(Rank.SIX, Suit.DIAMONDS));
        p1.addCard(game.deck.get(Rank.NINE, Suit.DIAMONDS));
        // Player 2
        p2.addCard(game.deck.get(Rank.FOUR, Suit.SPADES));
        p2.addCard(game.deck.get(Rank.SIX, Suit.SPADES));
        p2.addCard(game.deck.get(Rank.KING, Suit.CLUBS));
        p2.addCard(game.deck.get(Rank.EIGHT, Suit.HEARTS));
        p2.addCard(game.deck.get(Rank.TEN, Suit.DIAMONDS));
        // Player 3
        p3.addCard(game.deck.get(Rank.NINE, Suit.SPADES));
        p3.addCard(game.deck.get(Rank.SIX, Suit.CLUBS));
        p3.addCard(game.deck.get(Rank.NINE, Suit.CLUBS));
        p3.addCard(game.deck.get(Rank.JACK, Suit.DIAMONDS));
        p3.addCard(game.deck.get(Rank.THREE, Suit.HEARTS));
        // Player 4
        p4.addCard(game.deck.get(Rank.SEVEN, Suit.DIAMONDS));
        p4.addCard(game.deck.get(Rank.JACK, Suit.HEARTS));
        p4.addCard(game.deck.get(Rank.QUEEN, Suit.HEARTS));
        p4.addCard(game.deck.get(Rank.KING, Suit.HEARTS));
        p4.addCard(game.deck.get(Rank.FIVE, Suit.CLUBS));

        /* ROUND 1 START */
        assertEquals(1, game.getActivePlayer());
        assertTrue(game.canPlay());
        result = game.play(p1.getCard(Rank.FOUR, Suit.HEARTS));
        assertEquals(PlayResult.OK, result);

        assertEquals(2, game.getActivePlayer());
        assertTrue(game.canPlay());
        result = game.play(p2.getCard(Rank.FOUR, Suit.SPADES));
        assertEquals(PlayResult.OK, result);

        assertEquals(3, game.getActivePlayer());
        assertTrue(game.canPlay());
        result = game.play(p3.getCard(Rank.NINE, Suit.SPADES));
        assertEquals(PlayResult.OK, result);

        assertEquals(4, game.getActivePlayer());
        assertFalse(game.canPlay());
        assertTrue(game.canDraw());
        game.riggedDraw(Rank.TWO, Suit.CLUBS);
        assertFalse(game.isValidPlay(p4.getCard(Rank.TWO, Suit.CLUBS)));
        assertTrue(game.canDraw());
        game.riggedDraw(Rank.THREE, Suit.CLUBS);
        assertFalse(game.isValidPlay(p4.getCard(Rank.THREE, Suit.CLUBS)));
        assertTrue(game.canDraw());
        game.riggedDraw(Rank.FOUR, Suit.CLUBS);
        assertFalse(game.canPlay());
        assertFalse(game.isValidPlay(p4.getCard(Rank.FOUR, Suit.CLUBS)));
        game.pass();

        assertEquals(1, game.getActivePlayer());
        assertTrue(game.canPlay());
        result = game.play(p1.getCard(Rank.SEVEN, Suit.SPADES));
        assertEquals(PlayResult.OK, result);

        assertEquals(2, game.getActivePlayer());
        assertTrue(game.canPlay());
        result = game.play(p2.getCard(Rank.SIX, Suit.SPADES));
        assertEquals(PlayResult.OK, result);

        assertEquals(3, game.getActivePlayer());
        assertTrue(game.canPlay());
        result = game.play(p3.getCard(Rank.SIX, Suit.CLUBS));
        assertEquals(PlayResult.OK, result);

        assertEquals(4, game.getActivePlayer());
        assertTrue(game.canPlay());
        result = game.play(p4.getCard(Rank.TWO, Suit.CLUBS));
        assertEquals(PlayResult.TWO_NO_CHAIN, result);

        assertEquals(1, game.getActivePlayer());
        assertNull(game.canDenyTwo());
        game.riggedDrawFromTwo(Rank.TEN, Suit.CLUBS, Rank.JACK, Suit.CLUBS);
        assertTrue(game.canPlay());
        result = game.play(p1.getCard(Rank.JACK, Suit.CLUBS));
        assertEquals(PlayResult.OK, result);

        assertEquals(2, game.getActivePlayer());
        assertTrue(game.canPlay());
        result = game.play(p2.getCard(Rank.KING, Suit.CLUBS));
        assertEquals(PlayResult.OK, result);

        assertEquals(3, game.getActivePlayer());
        assertTrue(game.canPlay());
        result = game.play(p3.getCard(Rank.NINE, Suit.CLUBS));
        assertEquals(PlayResult.OK, result);

        assertEquals(4, game.getActivePlayer());
        assertTrue(game.canPlay());
        result = game.play(p4.getCard(Rank.THREE, Suit.CLUBS));
        assertEquals(PlayResult.OK, result);

        assertEquals(1, game.getActivePlayer());
        assertTrue(game.canDraw());
        game.riggedDraw(Rank.SEVEN, Suit.CLUBS);
        assertTrue(game.isValidPlay(p1.getCard(Rank.SEVEN, Suit.CLUBS)));
        result = game.play(p1.getCard(Rank.SEVEN, Suit.CLUBS));
        assertEquals(PlayResult.OK, result);

        assertEquals(2, game.getActivePlayer());
        assertTrue(game.canPlay());
        result = game.playEight(p2.getCard(Rank.EIGHT, Suit.HEARTS), Suit.DIAMONDS);
        assertEquals(PlayResult.WILD, result);

        assertEquals(3, game.getActivePlayer());
        assertTrue(game.canPlay());
        result = game.play(p3.getCard(Rank.JACK, Suit.DIAMONDS));
        assertEquals(PlayResult.OK, result);

        assertEquals(4, game.getActivePlayer());
        assertTrue(game.canPlay());
        result = game.play(p4.getCard(Rank.SEVEN, Suit.DIAMONDS));
        assertEquals(PlayResult.OK, result);

        assertEquals(1, game.getActivePlayer());
        assertTrue(game.canPlay());
        result = game.play(p1.getCard(Rank.NINE, Suit.DIAMONDS));
        assertEquals(PlayResult.OK, result);

        assertEquals(2, game.getActivePlayer());
        assertTrue(game.canPlay());
        result = game.play(p2.getCard(Rank.TEN, Suit.DIAMONDS));
        assertEquals(PlayResult.ROUND_WIN, result);
        /* ROUND 1 END */

        /* SCORE */
        assertEquals(21, p1.score);
        assertEquals(0, p2.score);
        assertEquals(3, p3.score);
        assertEquals(39, p4.score);

        // Start the next round with ten of diamonds
        game.roundForceStartNoDeal(Rank.TEN, Suit.DIAMONDS);

        // Populate hands
        // Player 1
        p1.addCard(game.deck.get(Rank.SEVEN, Suit.DIAMONDS));
        p1.addCard(game.deck.get(Rank.FOUR, Suit.SPADES));
        p1.addCard(game.deck.get(Rank.SEVEN, Suit.CLUBS));
        p1.addCard(game.deck.get(Rank.FOUR, Suit.HEARTS));
        p1.addCard(game.deck.get(Rank.FIVE, Suit.DIAMONDS));
        // Player 2
        p2.addCard(game.deck.get(Rank.NINE, Suit.DIAMONDS));
        p2.addCard(game.deck.get(Rank.THREE, Suit.SPADES));
        p2.addCard(game.deck.get(Rank.NINE, Suit.CLUBS));
        p2.addCard(game.deck.get(Rank.THREE, Suit.HEARTS));
        p2.addCard(game.deck.get(Rank.JACK, Suit.CLUBS));
        // Player 3
        p3.addCard(game.deck.get(Rank.THREE, Suit.DIAMONDS));
        p3.addCard(game.deck.get(Rank.NINE, Suit.SPADES));
        p3.addCard(game.deck.get(Rank.THREE, Suit.CLUBS));
        p3.addCard(game.deck.get(Rank.NINE, Suit.HEARTS));
        p3.addCard(game.deck.get(Rank.FIVE, Suit.HEARTS));
        // Player 4
        p4.addCard(game.deck.get(Rank.FOUR, Suit.DIAMONDS));
        p4.addCard(game.deck.get(Rank.SEVEN, Suit.SPADES));
        p4.addCard(game.deck.get(Rank.FOUR, Suit.CLUBS));
        p4.addCard(game.deck.get(Rank.FIVE, Suit.SPADES));
        p4.addCard(game.deck.get(Rank.EIGHT, Suit.DIAMONDS));

        /* ROUND 2 START */
        assertEquals(2, game.getActivePlayer());
        assertTrue(game.canPlay());
        result = game.play(p2.getCard(Rank.NINE, Suit.DIAMONDS));
        assertEquals(PlayResult.OK, result);

        assertEquals(3, game.getActivePlayer());
        assertTrue(game.canPlay());
        result = game.play(p3.getCard(Rank.THREE, Suit.DIAMONDS));
        assertEquals(PlayResult.OK, result);

        assertEquals(4, game.getActivePlayer());
        assertTrue(game.canPlay());
        result = game.play(p4.getCard(Rank.FOUR, Suit.DIAMONDS));
        assertEquals(PlayResult.OK, result);

        assertEquals(1, game.getActivePlayer());
        assertTrue(game.canPlay());
        result = game.play(p1.getCard(Rank.FOUR, Suit.SPADES));
        assertEquals(PlayResult.OK, result);

        assertEquals(2, game.getActivePlayer());
        assertTrue(game.canPlay());
        result = game.play(p2.getCard(Rank.THREE, Suit.SPADES));
        assertEquals(PlayResult.OK, result);

        assertEquals(3, game.getActivePlayer());
        assertTrue(game.canPlay());
        result = game.play(p3.getCard(Rank.NINE, Suit.SPADES));
        assertEquals(PlayResult.OK, result);

        assertEquals(4, game.getActivePlayer());
        assertTrue(game.canPlay());
        result = game.play(p4.getCard(Rank.SEVEN, Suit.SPADES));
        assertEquals(PlayResult.OK, result);

        assertEquals(1, game.getActivePlayer());
        assertTrue(game.canPlay());
        result = game.play(p1.getCard(Rank.SEVEN, Suit.CLUBS));
        assertEquals(PlayResult.OK, result);

        assertEquals(2, game.getActivePlayer());
        assertTrue(game.canPlay());
        result = game.play(p2.getCard(Rank.NINE, Suit.CLUBS));
        assertEquals(PlayResult.OK, result);

        assertEquals(3, game.getActivePlayer());
        assertTrue(game.canPlay());
        result = game.play(p3.getCard(Rank.THREE, Suit.CLUBS));
        assertEquals(PlayResult.OK, result);

        assertEquals(4, game.getActivePlayer());
        assertTrue(game.canPlay());
        result = game.play(p4.getCard(Rank.FOUR, Suit.CLUBS));
        assertEquals(PlayResult.OK, result);

        assertEquals(1, game.getActivePlayer());
        assertTrue(game.canPlay());
        result = game.play(p1.getCard(Rank.FOUR, Suit.HEARTS));
        assertEquals(PlayResult.OK, result);

        assertEquals(2, game.getActivePlayer());
        assertTrue(game.canPlay());
        result = game.play(p2.getCard(Rank.THREE, Suit.HEARTS));
        assertEquals(PlayResult.OK, result);

        assertEquals(3, game.getActivePlayer());
        assertTrue(game.canPlay());
        result = game.play(p3.getCard(Rank.NINE, Suit.HEARTS));
        assertEquals(PlayResult.OK, result);

        assertEquals(4, game.getActivePlayer());
        assertTrue(game.canDraw());
        game.riggedDraw(Rank.KING, Suit.SPADES);
        assertFalse(game.isValidPlay(p4.getCard(Rank.KING, Suit.SPADES)));
        assertTrue(game.canDraw());
        game.riggedDraw(Rank.QUEEN, Suit.SPADES);
        assertFalse(game.isValidPlay(p4.getCard(Rank.QUEEN, Suit.SPADES)));
        assertTrue(game.canDraw());
        game.riggedDraw(Rank.KING, Suit.HEARTS);
        assertTrue(game.isValidPlay(p4.getCard(Rank.KING, Suit.HEARTS)));
        result = game.play(p4.getCard(Rank.KING, Suit.HEARTS));
        assertEquals(PlayResult.OK, result);

        assertEquals(1, game.getActivePlayer());
        assertFalse(game.canPlay());
        assertTrue(game.canDraw());
        game.riggedDraw(Rank.SIX, Suit.DIAMONDS);
        assertFalse(game.isValidPlay(p1.getCard(Rank.SIX, Suit.DIAMONDS)));
        assertTrue(game.canDraw());
        game.riggedDraw(Rank.TEN, Suit.DIAMONDS);
        assertFalse(game.isValidPlay(p1.getCard(Rank.TEN, Suit.DIAMONDS)));
        assertTrue(game.canDraw());
        game.riggedDraw(Rank.JACK, Suit.DIAMONDS);
        assertFalse(game.isValidPlay(p1.getCard(Rank.JACK, Suit.DIAMONDS)));
        assertFalse(game.canDraw());
        game.pass();

        assertEquals(2, game.getActivePlayer());
        assertFalse(game.canPlay());
        assertTrue(game.canDraw());
        game.riggedDraw(Rank.SIX, Suit.SPADES);
        assertFalse(game.isValidPlay(p2.getCard(Rank.SIX, Suit.SPADES)));
        assertTrue(game.canDraw());
        game.riggedDraw(Rank.JACK, Suit.SPADES);
        assertFalse(game.isValidPlay(p2.getCard(Rank.JACK, Suit.SPADES)));
        assertTrue(game.canDraw());
        game.riggedDraw(Rank.TEN, Suit.SPADES);
        assertFalse(game.isValidPlay(p2.getCard(Rank.TEN, Suit.SPADES)));
        assertFalse(game.canDraw());
        game.pass();

        assertEquals(3, game.getActivePlayer());
        assertTrue(game.canPlay());
        result = game.play(p3.getCard(Rank.FIVE, Suit.HEARTS));
        assertEquals(PlayResult.ROUND_WIN, result);

        /* ROUND 1 END */

        /* SCORE */
        assertEquals(59, p1.score);
        assertEquals(36, p2.score);
        assertEquals(3, p3.score);
        assertEquals(114, p4.score);

        /* WINNER */
        assertNotNull(game.champion);
        assertEquals(p3, game.champion);
    }
}
