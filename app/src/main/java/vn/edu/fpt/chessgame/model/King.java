package vn.edu.fpt.chessgame.model;

import vn.edu.fpt.chessgame.R;

public class King extends ChessPiece{
    public  King(Color color){
        super(color);
    }
    @Override
    public boolean isValidMove(int startRow, int startCol, int endRow, int endCol, ChessPiece[][] board) {
        return Math.abs(startRow-endRow) <=1 && Math.abs(startCol-endCol) <=1;
    }

    @Override
    public int getDrawableRes() {
        return (color== Color.WHITE)? R.drawable.wking : R.drawable.bking;
    }
}
