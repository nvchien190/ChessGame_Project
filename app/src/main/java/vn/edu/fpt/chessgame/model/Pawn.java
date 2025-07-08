package vn.edu.fpt.chessgame.model;

import vn.edu.fpt.chessgame.R;

public class Pawn extends ChessPiece{
    public Pawn(Color color) {
        super(color);
    }

    @Override
    public boolean isValidMove(int sr, int sc, int er, int ec, ChessPiece[][] board) {
        int dir = (color == Color.WHITE) ? -1 : 1;
        if (sc == ec) {
            if (er - sr == dir && board[er][ec] == null) return true;
            if ((color == Color.WHITE && sr == 6 || color == Color.BLACK && sr == 1) && er - sr == 2 * dir && board[er][ec] == null) return true;
        } else if (Math.abs(sc - ec) == 1 && er - sr == dir && board[er][ec] != null && board[er][ec].getColor() != color) {
            return true; // ăn chéo
        }
        return false;
    }

    @Override
    public int getDrawableRes() {
        return (color == Color.WHITE) ? R.drawable.wpawn : R.drawable.bpawn;
    }
    @Override
    public ChessPiece clone() {
        return new Pawn(this.getColor());
    }

}
