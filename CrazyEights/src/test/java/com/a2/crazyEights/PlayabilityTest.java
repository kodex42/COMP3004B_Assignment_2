package com.a2.crazyEights;

import junit.framework.TestCase;

public class PlayabilityTest extends TestCase {
    public void testPlayerCanPlayKingHeartsOnKingClubs() {
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

        // Start the round with king of clubs
        game.roundForceStartNoDeal(Rank.KING, Suit.CLUBS);

        // Populate hands with 1 card each and 1 extra card for player 1 to prevent winning
        p1.addCard(game.deck.get(Rank.KING, Suit.HEARTS));
        p1.addCard(game.deck.draw());
        p2.addCard(game.deck.draw());
        p3.addCard(game.deck.draw());
        p4.addCard(game.deck.draw());

        // check who goes next after player 1 plays the three of clubs
        assertEquals(1, game.getActivePlayer());
        PlayResult result = game.play(p1.getCard(Rank.KING, Suit.HEARTS));
        assertEquals(PlayResult.OK, result);
        assertEquals(2, game.getActivePlayer());
    }

    public void testPlayerCanPlaySevenClubsOnKingClubs() {
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

        // Start the round with king of clubs
        game.roundForceStartNoDeal(Rank.KING, Suit.CLUBS);

        // Populate hands with 1 card each and 1 extra card for player 1 to prevent winning
        p1.addCard(game.deck.get(Rank.SEVEN, Suit.CLUBS));
        p1.addCard(game.deck.draw());
        p2.addCard(game.deck.draw());
        p3.addCard(game.deck.draw());
        p4.addCard(game.deck.draw());

        // check who goes next after player 1 plays the three of clubs
        assertEquals(1, game.getActivePlayer());
        PlayResult result = game.play(p1.getCard(Rank.SEVEN, Suit.CLUBS));
        assertEquals(PlayResult.OK, result);
        assertEquals(2, game.getActivePlayer());
    }

    public void testPlayerCanPlayEightHeartsOnKingClubs() {
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

        // Start the round with king of clubs
        game.roundForceStartNoDeal(Rank.KING, Suit.CLUBS);

        // Populate hands with 1 card each and 1 extra card for player 1 to prevent winning
        p1.addCard(game.deck.get(Rank.EIGHT, Suit.HEARTS));
        p1.addCard(game.deck.draw());
        p2.addCard(game.deck.draw());
        p3.addCard(game.deck.draw());
        p4.addCard(game.deck.draw());

        // check who goes next after player 1 plays the three of clubs
        assertEquals(1, game.getActivePlayer());
        PlayResult result = game.play(p1.getCard(Rank.EIGHT, Suit.HEARTS));
        assertEquals(PlayResult.WILD, result);
        assertEquals(2, game.getActivePlayer());
    }

    public void testPlayerCanPlayFiveSpadesOnKingClubs() {
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

        // Start the round with king of clubs
        game.roundForceStartNoDeal(Rank.KING, Suit.CLUBS);

        // Populate hands with 1 card each and 1 extra card for player 1 to prevent winning
        p1.addCard(game.deck.get(Rank.FIVE, Suit.SPADES));
        p1.addCard(game.deck.draw());
        p2.addCard(game.deck.draw());
        p3.addCard(game.deck.draw());
        p4.addCard(game.deck.draw());

        // check who goes next after player 1 plays the three of clubs
        assertEquals(1, game.getActivePlayer());
        PlayResult result = game.play(p1.getCard(Rank.FIVE, Suit.SPADES));
        assertEquals(PlayResult.INVALID_PLAY, result);
        assertEquals(1, game.getActivePlayer());
    }
}
