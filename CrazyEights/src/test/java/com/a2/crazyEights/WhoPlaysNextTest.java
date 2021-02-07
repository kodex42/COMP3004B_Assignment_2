package com.a2.crazyEights;

import junit.framework.TestCase;

public class WhoPlaysNextTest extends TestCase {
    public void testPlayer2AfterPlayer1() {
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

        // Start the round
        game.roundForceStartNoDeal(Rank.ACE, Suit.CLUBS);

        // Populate hands with 1 card each and 1 extra card for player 1 to prevent winning
        p1.addCard(game.deck.get(Rank.THREE, Suit.CLUBS));
        p1.addCard(game.deck.draw());
        p2.addCard(game.deck.draw());
        p3.addCard(game.deck.draw());
        p4.addCard(game.deck.draw());

        // check who goes next after player 1 plays the three of clubs
        assertEquals(1, game.getActivePlayer());
        game.play(p1.getCard(Rank.THREE, Suit.CLUBS));
        assertEquals(2, game.getActivePlayer());
    }

    public void testPlayer4AfterPlayer1Ace() {
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

        // Start the round
        game.roundForceStartNoDeal(Rank.ACE, Suit.CLUBS);

        // Populate hands with 1 card each and 1 extra card for player 1 to prevent winning
        p1.addCard(game.deck.get(Rank.ACE, Suit.HEARTS));
        p1.addCard(game.deck.draw());
        p2.addCard(game.deck.draw());
        p3.addCard(game.deck.draw());
        p4.addCard(game.deck.draw());

        // check who goes next after player 1 plays the ace of hearts
        assertEquals(1, game.getActivePlayer());
        game.play(p1.getCard(Rank.ACE, Suit.HEARTS));
        assertEquals(4, game.getActivePlayer());
    }

    public void testPlayer3AfterPlayer1Queen() {
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

        // Start the round
        game.roundForceStartNoDeal(Rank.ACE, Suit.CLUBS);

        // Populate hands with 1 card each and 1 extra card for player 1 to prevent winning
        p1.addCard(game.deck.get(Rank.QUEEN, Suit.CLUBS));
        p1.addCard(game.deck.draw());
        p2.addCard(game.deck.draw());
        p3.addCard(game.deck.draw());
        p4.addCard(game.deck.draw());

        // check who goes next after player 1 plays the queen of clubs
        assertEquals(1, game.getActivePlayer());
        game.play(p1.getCard(Rank.QUEEN, Suit.CLUBS));
        assertEquals(3, game.getActivePlayer());
    }

    public void testPlayer1AfterPlayer4() {
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

        // Start the round
        game.roundForceStartNoDeal(Rank.ACE, Suit.CLUBS);

        // Populate hands with 1 specific card and 1 random card each
        p1.addCard(game.deck.get(Rank.SIX, Suit.CLUBS));
        p2.addCard(game.deck.get(Rank.FIVE, Suit.CLUBS));
        p3.addCard(game.deck.get(Rank.FOUR, Suit.CLUBS));
        p4.addCard(game.deck.get(Rank.THREE, Suit.CLUBS));
        p1.addCard(game.deck.draw());
        p2.addCard(game.deck.draw());
        p3.addCard(game.deck.draw());
        p4.addCard(game.deck.draw());

        // check who goes next after each player plays normally in sequence
        assertEquals(1, game.getActivePlayer());
        game.play(p1.getCard(Rank.SIX, Suit.CLUBS));
        assertEquals(2, game.getActivePlayer());
        game.play(p2.getCard(Rank.FIVE, Suit.CLUBS));
        assertEquals(3, game.getActivePlayer());
        game.play(p3.getCard(Rank.FOUR, Suit.CLUBS));
        assertEquals(4, game.getActivePlayer());
        game.play(p4.getCard(Rank.THREE, Suit.CLUBS));
        assertEquals(1, game.getActivePlayer());
    }

    public void testPlayer3AfterPlayer4Ace() {
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

        // Start the round
        game.roundForceStartNoDeal(Rank.ACE, Suit.CLUBS);

        // Populate hands with 1 specific card and 1 random card each
        p1.addCard(game.deck.get(Rank.SIX, Suit.CLUBS));
        p2.addCard(game.deck.get(Rank.FIVE, Suit.CLUBS));
        p3.addCard(game.deck.get(Rank.FOUR, Suit.CLUBS));
        p4.addCard(game.deck.get(Rank.ACE, Suit.CLUBS));
        p1.addCard(game.deck.draw());
        p2.addCard(game.deck.draw());
        p3.addCard(game.deck.draw());
        p4.addCard(game.deck.draw());

        // check who goes next after each player plays normally in sequence and p4 plays an ace
        assertEquals(1, game.getActivePlayer());
        game.play(p1.getCard(Rank.SIX, Suit.CLUBS));
        assertEquals(2, game.getActivePlayer());
        game.play(p2.getCard(Rank.FIVE, Suit.CLUBS));
        assertEquals(3, game.getActivePlayer());
        game.play(p3.getCard(Rank.FOUR, Suit.CLUBS));
        assertEquals(4, game.getActivePlayer());
        game.play(p4.getCard(Rank.ACE, Suit.CLUBS));
        assertEquals(3, game.getActivePlayer());
    }

    public void testPlayer2AfterPlayer4Queen() {
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

        // Start the round
        game.roundForceStartNoDeal(Rank.ACE, Suit.CLUBS);

        // Populate hands with 1 specific card and 1 random card each
        p1.addCard(game.deck.get(Rank.SIX, Suit.CLUBS));
        p2.addCard(game.deck.get(Rank.FIVE, Suit.CLUBS));
        p3.addCard(game.deck.get(Rank.FOUR, Suit.CLUBS));
        p4.addCard(game.deck.get(Rank.QUEEN, Suit.CLUBS));
        p1.addCard(game.deck.draw());
        p2.addCard(game.deck.draw());
        p3.addCard(game.deck.draw());
        p4.addCard(game.deck.draw());

        // check who goes next after each player plays normally in sequence and p4 plays a queen
        assertEquals(1, game.getActivePlayer());
        game.play(p1.getCard(Rank.SIX, Suit.CLUBS));
        assertEquals(2, game.getActivePlayer());
        game.play(p2.getCard(Rank.FIVE, Suit.CLUBS));
        assertEquals(3, game.getActivePlayer());
        game.play(p3.getCard(Rank.FOUR, Suit.CLUBS));
        assertEquals(4, game.getActivePlayer());
        game.play(p4.getCard(Rank.QUEEN, Suit.CLUBS));
        assertEquals(2, game.getActivePlayer());
    }
}
