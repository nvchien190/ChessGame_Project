package vn.edu.fpt.chessgame.model;

import vn.edu.fpt.chessgame.R;

public class Knight extends ChessPiece{
    public Knight(Color color) {
        super(color);
    }

    @Override
    public boolean isValidMove(int sr, int sc, int er, int ec, ChessPiece[][] board) {
        int dx = Math.abs(sr - er);
        int dy = Math.abs(sc - ec);
        return dx * dy == 2;  // L đi
    }

    @Override
    public int getDrawableRes() {
        return (color == Color.WHITE) ? R.drawable.wknight : R.drawable.bknight;
    }
    @Override
    public ChessPiece clone() {
        return new Knight(this.getColor());
    }
    @Override
    public Type getType() {
        return Type.KNIGHT;
    }

}
