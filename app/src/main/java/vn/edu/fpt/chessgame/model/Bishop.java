package vn.edu.fpt.chessgame.model;

import vn.edu.fpt.chessgame.R;

public class Bishop extends  ChessPiece{
    public Bishop(Color color) {
        super(color);
    }

    @Override
    public boolean isValidMove(int sr, int sc, int er, int ec, ChessPiece[][] board) {
        return Math.abs(sr - er) == Math.abs(sc - ec);
    }

    @Override
    public int getDrawableRes() {
        return (color == Color.WHITE) ? R.drawable.wbishop : R.drawable.bbishop;
    }
}
