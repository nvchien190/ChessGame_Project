package vn.edu.fpt.chessgame.model;

import vn.edu.fpt.chessgame.R;

public class Bishop extends  ChessPiece{
    public Bishop(Color color) {
        super(color);
    }

    @Override
    public boolean isValidMove(int sr, int sc, int er, int ec, ChessPiece[][] board) {
        int rowDiff = Math.abs(sr - er);
        int colDiff = Math.abs(sc - ec);

        // 1. Kiểm tra đi chéo
        if (rowDiff != colDiff) return false;

        // 2. Tính hướng
        int rowStep = (er > sr) ? 1 : -1;
        int colStep = (ec > sc) ? 1 : -1;

        int r = sr + rowStep;
        int c = sc + colStep;

        // 3. Kiểm tra chặn đường
        while (r != er && c != ec) {
            if (r < 0 || r >= 8 || c < 0 || c >= 8) return false; // tránh crash
            if (board[r][c] != null) return false;
            r += rowStep;
            c += colStep;
        }

        // 4. Kiểm tra ô đích
        ChessPiece target = board[er][ec];
        return target == null || target.getColor() != this.getColor();
    }


    @Override
    public int getDrawableRes() {
        return (color == Color.WHITE) ? R.drawable.wbishop : R.drawable.bbishop;
    }
}
