package vn.edu.fpt.chessgame.model;

import vn.edu.fpt.chessgame.R;

public class King extends ChessPiece{
    public  King(Color color){
        super(color);
    }
    @Override
    public boolean isValidMove(int startRow, int startCol, int endRow, int endCol, ChessPiece[][] board) {
        // Không cho đứng yên
        if (startRow == endRow && startCol == endCol) return false;

        int dr = Math.abs(startRow - endRow);
        int dc = Math.abs(startCol - endCol);

        if (dr <= 1 && dc <= 1) {
            ChessPiece target = board[endRow][endCol];
            // Không được ăn quân cùng màu
            return target == null || target.getColor() != this.getColor();
        }

        return false;  }

    @Override
    public int getDrawableRes() {
        return (color== Color.WHITE)? R.drawable.wking : R.drawable.bking;
    }
    @Override
    public ChessPiece clone() {
        return new King(this.getColor());
    }

}
