package vn.edu.fpt.chessgame.model;

import vn.edu.fpt.chessgame.R;

public class Queen extends ChessPiece{
    public  Queen(Color color){
        super(color);
    }
    @Override
    public boolean isValidMove(int startRow, int startCol, int endRow, int endCol, ChessPiece[][] board) {
        return (startRow==endRow || startCol==endCol || Math.abs(startRow-endRow)==Math.abs(startCol-endCol));
    }

    @Override
    public int getDrawableRes() {
        return (color==Color.WHITE)? R.drawable.wqueen : R.drawable.bqueen;
    }
}
