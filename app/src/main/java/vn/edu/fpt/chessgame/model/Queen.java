package vn.edu.fpt.chessgame.model;

import vn.edu.fpt.chessgame.R;

public class Queen extends ChessPiece{
    public  Queen(Color color){
        super(color);
    }
    @Override
    public boolean isValidMove(int sr, int sc, int er, int ec, ChessPiece[][] board) {
        // Không di chuyển
        if (sr == er && sc == ec) return false;

        int rowDiff = Math.abs(sr - er);
        int colDiff = Math.abs(sc - ec);

        int rowStep = Integer.compare(er, sr); // 1, -1 hoặc 0
        int colStep = Integer.compare(ec, sc); // 1, -1 hoặc 0

        // 1. Kiểm tra xem có đi theo đường hợp lệ không (thẳng hoặc chéo)
        boolean isDiagonal = rowDiff == colDiff;
        boolean isStraight = sr == er || sc == ec;

        if (!isDiagonal && !isStraight) return false;

        int r = sr + rowStep;
        int c = sc + colStep;

        // 2. Kiểm tra chặn đường (trừ ô đích)
        while (r != er || c != ec) {
            if (r < 0 || r >= 8 || c < 0 || c >= 8) return false;
            if (board[r][c] != null) return false;
            r += rowStep;
            c += colStep;
        }

        // 3. Kiểm tra ô đích: trống hoặc có quân đối phương
        ChessPiece target = board[er][ec];
        return target == null || target.getColor() != this.getColor();
    }


    @Override
    public int getDrawableRes() {
        return (color==Color.WHITE)? R.drawable.wqueen : R.drawable.bqueen;
    }
}
