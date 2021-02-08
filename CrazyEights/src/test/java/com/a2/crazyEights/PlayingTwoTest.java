package com.a2.crazyEights;

import junit.framework.TestCase;

import java.util.ArrayList;

public class PlayingTwoTest extends TestCase {
    public void testPlayer1Plays2CAndPlayer2Has4HAndDraws6CAnd9DAndPlays() {
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

        // Populate hands with 1 specific card each to prevent null pointers and 1 extra to player 1 to prevent winning
        p1.addCard(game.deck.get(Rank.THREE, Suit.HEARTS));
        p1.addCard(game.deck.get(Rank.TWO, Suit.CLUBS));
        p2.addCard(game.deck.get(Rank.FOUR, Suit.HEARTS));
        p3.addCard(game.deck.get(Rank.QUEEN, Suit.HEARTS));
        p4.addCard(game.deck.get(Rank.JACK, Suit.HEARTS));

        // player 1 plays the two of clubs
        PlayResult result = game.play(p1.getCard(Rank.TWO, Suit.CLUBS));
        // no chain should be possible since player 2 does not have a 2
        assertEquals(PlayResult.TWO, result);
        // player 2 is now active and must draw two cards
        assertEquals(2, game.getActivePlayer());
        game.riggedDrawFromTwo(Rank.SIX, Suit.CLUBS, Rank.NINE, Suit.DIAMONDS);
        assertEquals(3, p2.getHandSize());
        // player 2 is now able to play
        assertTrue(game.canPlay());
        // player 2 plays the six of clubs
        result = game.play(p2.getCard(Rank.SIX, Suit.CLUBS));
        assertEquals(PlayResult.OK, result);
        assertEquals(3, game.getActivePlayer());
    }

    public void testPlayer1Plays2CAndPlayer2Has4HAndDraws6SAnd9DAndMustDraw9HAnd6CAndPlays() {
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

        // Populate hands with 1 specific card each to prevent null pointers and 1 extra to player 1 to prevent winning
        p1.addCard(game.deck.get(Rank.THREE, Suit.HEARTS));
        p1.addCard(game.deck.get(Rank.TWO, Suit.CLUBS));
        p2.addCard(game.deck.get(Rank.FOUR, Suit.HEARTS));
        p3.addCard(game.deck.get(Rank.QUEEN, Suit.HEARTS));
        p4.addCard(game.deck.get(Rank.JACK, Suit.HEARTS));

        // player 1 plays the two of clubs
        PlayResult result = game.play(p1.getCard(Rank.TWO, Suit.CLUBS));
        // no chain should be possible since player 2 does not have a 2
        assertEquals(PlayResult.TWO, result);
        // player 2 is now active and must draw two cards
        assertEquals(2, game.getActivePlayer());
        game.riggedDrawFromTwo(Rank.SIX, Suit.SPADES, Rank.NINE, Suit.DIAMONDS);
        assertEquals(3, p2.getHandSize());
        // player 2 is still unable to play
        assertFalse(game.canPlay());
        // player 2 draws the nine of hearts and the six of clubs
        assertTrue(game.canDraw());
        game.riggedDraw(Rank.NINE, Suit.HEARTS);
        assertTrue(game.canDraw());
        game.riggedDraw(Rank.SIX, Suit.CLUBS);
        assertEquals(5, p2.getHandSize());
        // player 2 is now able to play
        assertTrue(game.canPlay());
        // player 2 plays the six of clubs
        result = game.play(p2.getCard(Rank.SIX, Suit.CLUBS));
        assertEquals(PlayResult.OK, result);
        assertEquals(3, game.getActivePlayer());
    }

    public void testPlayer1Plays2CAndPlayer2Has4HAndDraws6SAnd9DAndMustDraw9HAnd6DAnd5HAndMustPass() {
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

        // Populate hands with 1 specific card each to prevent null pointers and 1 extra to player 1 to prevent winning
        p1.addCard(game.deck.get(Rank.THREE, Suit.HEARTS));
        p1.addCard(game.deck.get(Rank.TWO, Suit.CLUBS));
        p2.addCard(game.deck.get(Rank.FOUR, Suit.HEARTS));
        p3.addCard(game.deck.get(Rank.QUEEN, Suit.HEARTS));
        p4.addCard(game.deck.get(Rank.JACK, Suit.HEARTS));

        // player 1 plays the two of clubs
        PlayResult result = game.play(p1.getCard(Rank.TWO, Suit.CLUBS));
        // no chain should be possible since player 2 does not have a 2
        assertEquals(PlayResult.TWO, result);
        // player 2 is now active and must draw two cards
        assertEquals(2, game.getActivePlayer());
        game.riggedDrawFromTwo(Rank.SIX, Suit.SPADES, Rank.NINE, Suit.DIAMONDS);
        assertEquals(3, p2.getHandSize());
        // player 2 is still unable to play
        assertFalse(game.canPlay());
        // player 2 draws the nine of hearts and the six of diamonds and the five of hearts
        assertTrue(game.canDraw());
        game.riggedDraw(Rank.NINE, Suit.HEARTS);
        assertTrue(game.canDraw());
        game.riggedDraw(Rank.SIX, Suit.DIAMONDS);
        assertTrue(game.canDraw());
        game.riggedDraw(Rank.FIVE, Suit.HEARTS);
        assertEquals(6, p2.getHandSize());
        // player 2 is now unable to play or draw
        assertFalse(game.canDraw());
        assertFalse(game.canPlay());
        // player 2 passes
        game.pass();
        assertEquals(3, game.getActivePlayer());
    }

    public void testPlayer1Plays2CAndPlayer2Has4HAndDraws2HAnd9DAndPlays2HAndPlayer3Has7DAndDraws5SAnd6DAnd6HAnd7CAndPlays() {
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

        // Populate hands with 1 specific card each to prevent null pointers and 1 extra to player 1 to prevent winning
        p1.addCard(game.deck.get(Rank.THREE, Suit.HEARTS));
        p1.addCard(game.deck.get(Rank.TWO, Suit.CLUBS));
        p2.addCard(game.deck.get(Rank.FOUR, Suit.HEARTS));
        p3.addCard(game.deck.get(Rank.QUEEN, Suit.HEARTS));
        p4.addCard(game.deck.get(Rank.JACK, Suit.HEARTS));

        // player 1 plays the two of clubs
        PlayResult result = game.play(p1.getCard(Rank.TWO, Suit.CLUBS));
        // no chain should be possible since player 2 does not have a 2
        assertEquals(PlayResult.TWO, result);
        // player 2 is now active and must draw two cards
        assertEquals(2, game.getActivePlayer());
        game.riggedDrawFromTwo(Rank.TWO, Suit.HEARTS, Rank.NINE, Suit.DIAMONDS);
        assertEquals(3, p2.getHandSize());
        // player 2 is now able to play
        assertTrue(game.canPlay());
        // player 2 plays the six of clubs
        result = game.play(p2.getCard(Rank.TWO, Suit.HEARTS));
        // no chain should be possible since player 3 does not have a 2, so player 3 draws 4 from the stacked twos
        assertEquals(PlayResult.TWO, result);
        assertEquals(3, game.getActivePlayer());
        game.riggedDrawFromTwo(Rank.FIVE, Suit.SPADES, Rank.SIX, Suit.DIAMONDS);
        game.riggedDrawFromTwo(Rank.SIX, Suit.HEARTS, Rank.SEVEN, Suit.CLUBS);
        assertEquals(5, p3.getHandSize());
        // player 3 is now able to play and plays the six of hearts
        assertTrue(game.canPlay());
        result = game.play(p3.getCard(Rank.SIX, Suit.HEARTS));
        assertEquals(PlayResult.OK, result);
        assertEquals(4, game.getActivePlayer());
    }

    public void testPlayer1Plays2CAndPlayer2Has4CAnd6CAnd9DAndDeniesTwo() {
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

        // Populate hands with 1 specific card each to prevent null pointers and 1 extra to player 1 and 2 extra to player 2 to prevent winning
        p1.addCard(game.deck.get(Rank.THREE, Suit.HEARTS));
        p1.addCard(game.deck.get(Rank.TWO, Suit.CLUBS));
        p2.addCard(game.deck.get(Rank.FOUR, Suit.CLUBS));
        p2.addCard(game.deck.get(Rank.SIX, Suit.CLUBS));
        p2.addCard(game.deck.get(Rank.NINE, Suit.DIAMONDS));
        p3.addCard(game.deck.get(Rank.QUEEN, Suit.HEARTS));
        p4.addCard(game.deck.get(Rank.JACK, Suit.HEARTS));

        // player 1 plays the two of clubs
        PlayResult result = game.play(p1.getCard(Rank.TWO, Suit.CLUBS));
        // no chain should be possible since player 2 does not have a 2
        assertEquals(PlayResult.TWO, result);
        // player 2 is now active and checks for a sequence of card plays to deny with
        assertEquals(2, game.getActivePlayer());
        ArrayList<Card> sequence = game.canDenyTwo();
        assertNotNull(sequence);
        // player 2 is now able to deny with the sequence found and does so, ending their turn
        result = game.denyTwo(sequence);
        assertEquals(PlayResult.OK, result);
        assertEquals(3, game.getActivePlayer());
    }

    public void testPlayer1Plays2CAndPlayer2Has4CAnd6CAndDeniesTwo() {
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

        // Populate hands with 1 specific card each to prevent null pointers and 1 extra to player 1 to prevent winning, but 1 extra to player 2 to allow winning
        p1.addCard(game.deck.get(Rank.THREE, Suit.HEARTS));
        p1.addCard(game.deck.get(Rank.TWO, Suit.CLUBS));
        p2.addCard(game.deck.get(Rank.FOUR, Suit.CLUBS));
        p2.addCard(game.deck.get(Rank.SIX, Suit.CLUBS));
        p3.addCard(game.deck.get(Rank.QUEEN, Suit.HEARTS));
        p4.addCard(game.deck.get(Rank.JACK, Suit.HEARTS));

        // player 1 plays the two of clubs
        PlayResult result = game.play(p1.getCard(Rank.TWO, Suit.CLUBS));
        // no chain should be possible since player 2 does not have a 2
        assertEquals(PlayResult.TWO, result);
        // player 2 is now active and checks for a sequence of card plays to deny with
        assertEquals(2, game.getActivePlayer());
        ArrayList<Card> sequence = game.canDenyTwo();
        assertNotNull(sequence);
        // player 2 is now able to deny with the sequence found and does so, winning the round
        result = game.denyTwo(sequence);
        assertEquals(PlayResult.ROUND_WIN, result);
    }
}
