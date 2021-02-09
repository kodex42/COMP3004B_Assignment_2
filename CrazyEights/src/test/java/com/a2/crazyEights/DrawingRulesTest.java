package com.a2.crazyEights;

import junit.framework.TestCase;

public class DrawingRulesTest extends TestCase {
    public void testPlayer1MustDraw1() {
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

        // Start the round
        game.roundForceStartNoDeal(Rank.SEVEN, Suit.CLUBS);

        // Set the player id (normally done automatically by the server)
        p1.setPlayerId(1);
        p2.setPlayerId(2);
        p3.setPlayerId(3);
        p4.setPlayerId(4);

        // Populate hands with 1 specific card each to prevent null pointers
        p1.addCard(game.deck.get(Rank.THREE, Suit.HEARTS));
        p2.addCard(game.deck.get(Rank.KING, Suit.HEARTS));
        p3.addCard(game.deck.get(Rank.QUEEN, Suit.HEARTS));
        p4.addCard(game.deck.get(Rank.JACK, Suit.HEARTS));

        // player 1 should be active and unable to play
        assertEquals(1, game.getActivePlayer());
        assertFalse(game.canPlay());
        // player 1 draws the six of clubs
        assertTrue(game.canDraw());
        game.riggedDraw(Rank.SIX, Suit.CLUBS);
        // player 1 is now able to play or continue drawing
        assertTrue(game.canPlay());
        assertTrue(game.canDraw());
    }

    public void testPlayer1MustDraw2() {
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

        // Start the round
        game.roundForceStartNoDeal(Rank.SEVEN, Suit.CLUBS);

        // Set the player id (normally done automatically by the server)
        p1.setPlayerId(1);
        p2.setPlayerId(2);
        p3.setPlayerId(3);
        p4.setPlayerId(4);

        // Populate hands with 1 specific card each to prevent null pointers
        p1.addCard(game.deck.get(Rank.THREE, Suit.HEARTS));
        p2.addCard(game.deck.get(Rank.KING, Suit.HEARTS));
        p3.addCard(game.deck.get(Rank.QUEEN, Suit.HEARTS));
        p4.addCard(game.deck.get(Rank.JACK, Suit.HEARTS));

        // player 1 should be active and unable to play
        assertEquals(1, game.getActivePlayer());
        assertFalse(game.canPlay());
        // player 1 draws the six of clubs
        assertTrue(game.canDraw());
        game.riggedDraw(Rank.SIX, Suit.DIAMONDS);
        // player 1 is still unable to play
        assertFalse(game.canPlay());
        // player 1 draws the five of clubs
        assertTrue(game.canDraw());
        game.riggedDraw(Rank.FIVE, Suit.CLUBS);
        // player 1 is now able to play or continue drawing
        assertTrue(game.canPlay());
        assertTrue(game.canDraw());
    }

    public void testPlayer1MustDraw3() {
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

        // Start the round
        game.roundForceStartNoDeal(Rank.SEVEN, Suit.CLUBS);

        // Set the player id (normally done automatically by the server)
        p1.setPlayerId(1);
        p2.setPlayerId(2);
        p3.setPlayerId(3);
        p4.setPlayerId(4);

        // Populate hands with 1 specific card each to prevent null pointers
        p1.addCard(game.deck.get(Rank.THREE, Suit.HEARTS));
        p2.addCard(game.deck.get(Rank.KING, Suit.HEARTS));
        p3.addCard(game.deck.get(Rank.QUEEN, Suit.HEARTS));
        p4.addCard(game.deck.get(Rank.JACK, Suit.HEARTS));

        // player 1 should be active and unable to play
        assertEquals(1, game.getActivePlayer());
        assertFalse(game.canPlay());
        // player 1 draws the six of clubs
        assertTrue(game.canDraw());
        game.riggedDraw(Rank.SIX, Suit.DIAMONDS);
        // player 1 is still unable to play
        assertFalse(game.canPlay());
        // player 1 draws the five of spades
        assertTrue(game.canDraw());
        game.riggedDraw(Rank.FIVE, Suit.SPADES);
        // player 1 is still unable to play
        assertFalse(game.canPlay());
        // player 1 draws the seven of hearts
        assertTrue(game.canDraw());
        game.riggedDraw(Rank.SEVEN, Suit.HEARTS);
        // player 1 is now able to play but unable to draw
        assertTrue(game.canPlay());
        assertFalse(game.canDraw());
    }

    public void testPlayer1MustDraw3AndPass() {
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

        // Start the round
        game.roundForceStartNoDeal(Rank.SEVEN, Suit.CLUBS);

        // Set the player id (normally done automatically by the server)
        p1.setPlayerId(1);
        p2.setPlayerId(2);
        p3.setPlayerId(3);
        p4.setPlayerId(4);

        // Populate hands with 1 specific card each to prevent null pointers
        p1.addCard(game.deck.get(Rank.THREE, Suit.HEARTS));
        p2.addCard(game.deck.get(Rank.KING, Suit.HEARTS));
        p3.addCard(game.deck.get(Rank.QUEEN, Suit.HEARTS));
        p4.addCard(game.deck.get(Rank.JACK, Suit.HEARTS));

        // player 1 should be active and unable to play
        assertEquals(1, game.getActivePlayer());
        assertFalse(game.canPlay());
        // player 1 draws the six of clubs
        assertTrue(game.canDraw());
        game.riggedDraw(Rank.SIX, Suit.DIAMONDS);
        // player 1 is still unable to play
        assertFalse(game.canPlay());
        // player 1 draws the five of spades
        assertTrue(game.canDraw());
        game.riggedDraw(Rank.FIVE, Suit.SPADES);
        // player 1 is still unable to play
        assertFalse(game.canPlay());
        // player 1 draws the seven of hearts
        assertTrue(game.canDraw());
        game.riggedDraw(Rank.FOUR, Suit.HEARTS);
        // player 1 is still unable to play, but is now also unable to draw
        assertFalse(game.canPlay());
        assertFalse(game.canDraw());
        // attempt to draw cards anyways (not available to player client, as they will be systematically denied draws if they cannot draw)
        assertEquals(4, p1.getHandSize());
        game.draw();
        game.riggedDraw(Rank.SEVEN, Suit.HEARTS);
        // attempt should have failed
        assertEquals(4, p1.getHandSize());
        assertFalse(game.canPlay());
        assertFalse(game.canDraw());
    }

    public void testPlayer1MustDraw2AndPlayEight() {
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

        // Start the round
        game.roundForceStartNoDeal(Rank.SEVEN, Suit.CLUBS);

        // Set the player id (normally done automatically by the server)
        p1.setPlayerId(1);
        p2.setPlayerId(2);
        p3.setPlayerId(3);
        p4.setPlayerId(4);

        // Populate hands with 1 specific card each to prevent null pointers
        p1.addCard(game.deck.get(Rank.THREE, Suit.HEARTS));
        p2.addCard(game.deck.get(Rank.KING, Suit.HEARTS));
        p3.addCard(game.deck.get(Rank.QUEEN, Suit.HEARTS));
        p4.addCard(game.deck.get(Rank.JACK, Suit.HEARTS));

        // player 1 should be active and unable to play
        assertEquals(1, game.getActivePlayer());
        assertFalse(game.canPlay());
        // player 1 draws the six of clubs
        assertTrue(game.canDraw());
        game.riggedDraw(Rank.SIX, Suit.DIAMONDS);
        // player 1 is still unable to play
        assertFalse(game.canPlay());
        // player 1 draws the eight of hearts
        assertTrue(game.canDraw());
        game.riggedDraw(Rank.EIGHT, Suit.HEARTS);
        // player 1 can now play
        assertTrue(game.canPlay());
        // player 1 plays the eight of hearts and declares diamonds the new suit
        PlayResult result = game.playEight(p1.getCard(Rank.EIGHT, Suit.HEARTS), Suit.DIAMONDS);
        assertEquals(PlayResult.WILD, result);
        assertEquals(Suit.DIAMONDS, game.discard.peek().getSuit());
    }

    public void testPlayer1ChoosesDraw1() {
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

        // Start the round
        game.roundForceStartNoDeal(Rank.SEVEN, Suit.CLUBS);

        // Set the player id (normally done automatically by the server)
        p1.setPlayerId(1);
        p2.setPlayerId(2);
        p3.setPlayerId(3);
        p4.setPlayerId(4);

        // Populate hands with 1 specific card each to prevent null pointers
        p1.addCard(game.deck.get(Rank.THREE, Suit.CLUBS));
        p1.addCard(game.deck.get(Rank.KING, Suit.SPADES));
        p2.addCard(game.deck.get(Rank.KING, Suit.HEARTS));
        p3.addCard(game.deck.get(Rank.QUEEN, Suit.HEARTS));
        p4.addCard(game.deck.get(Rank.JACK, Suit.HEARTS));

        // player 1 should be active, able to play, and able to draw
        assertEquals(1, game.getActivePlayer());
        assertTrue(game.canPlay());
        assertTrue(game.canDraw());
        // player 1 chooses to draw the six of clubs
        game.riggedDraw(Rank.SIX, Suit.CLUBS);
        // player 1 is still able to play or continue drawing
        assertTrue(game.canPlay());
        assertTrue(game.canDraw());
        // player 1 decides to play the six of clubs
        PlayResult result = game.play(p1.getCard(Rank.SIX, Suit.CLUBS));
        assertEquals(PlayResult.OK, result);
        assertEquals(2, game.getActivePlayer());
    }
}
