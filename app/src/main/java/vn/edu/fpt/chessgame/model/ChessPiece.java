package vn.edu.fpt.chessgame.model;

import androidx.annotation.NonNull;

public abstract class ChessPiece {
    public enum Color {
        WHITE, BLACK
    }

    protected Color color;
    protected boolean hasMoved = false;




    public ChessPiece(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public abstract boolean isValidMove(int startRow, int startCol, int endRow, int endCol, ChessPiece[][] board);

    public abstract int getDrawableRes();

    @NonNull
    public abstract ChessPiece clone();

    public boolean hasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean moved) {
        this.hasMoved = moved;
    }
}
