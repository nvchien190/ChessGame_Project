package vn.edu.fpt.chessgame.model;


public class Match {
    public String whitePlayer;
    public String blackPlayer;
    public String status;
    public String board;
    public String turn;
    public String winner;

    public Match() {}

    public Match(String whitePlayer) {
        this.whitePlayer = whitePlayer;
        this.blackPlayer = "";
        this.status = "waiting";
        this.board = "";
        this.turn = "white"; // Bắt đầu luôn là lượt trắng
        this.winner = "";
    }
}

