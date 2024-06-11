package com.mygdx.wartowers.sprites;

public class BattleResult {
    private String player1Name;
    private String player2Name;
    private String winner;

    public BattleResult() {
    }

    public BattleResult(String player1Name, String player2Name, String winner) {
        this.player1Name = player1Name;
        this.player2Name = player2Name;
        this.winner = winner;
    }

    public String getPlayer1Name() {
        return player1Name;
    }

    public void setPlayer1Name(String player1Name) {
        this.player1Name = player1Name;
    }

    public String getPlayer2Name() {
        return player2Name;
    }

    public void setPlayer2Name(String player2Name) {
        this.player2Name = player2Name;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }
}
