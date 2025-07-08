package vn.edu.fpt.chessgame.model;

import vn.edu.fpt.chessgame.R;

public class Rook extends ChessPiece{
    public Rook(Color color) {
        super(color);
    }

    @Override
    public boolean isValidMove(int sr, int sc, int er, int ec, ChessPiece[][] board) {

        if (sr == er && sc == ec) return false;

        int rowStep = Integer.compare(er, sr); // 1, -1 hoặc 0
        int colStep = Integer.compare(ec, sc); // 1, -1 hoặc 0

        boolean isStraight = sr == er || sc == ec;

        if ( !isStraight) return false;

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
        return (color == Color.WHITE) ? R.drawable.wrook : R.drawable.brook;
    }
    @Override
    public ChessPiece clone() {
        return new Rook(this.getColor());
    }

}
