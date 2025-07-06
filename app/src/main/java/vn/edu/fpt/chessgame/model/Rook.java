package vn.edu.fpt.chessgame.model;

import vn.edu.fpt.chessgame.R;

public class Rook extends ChessPiece{
    public Rook(Color color) {
        super(color);
    }

    @Override
    public boolean isValidMove(int sr, int sc, int er, int ec, ChessPiece[][] board) {
        return (sr == er || sc == ec);
    }

    @Override
    public int getDrawableRes() {
        return (color == Color.WHITE) ? R.drawable.wrook : R.drawable.brook;
    }
}
