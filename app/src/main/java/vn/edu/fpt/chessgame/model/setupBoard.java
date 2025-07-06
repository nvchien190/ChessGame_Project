package vn.edu.fpt.chessgame.model;

public class setupBoard {
    private ChessPiece[][] board;

    public setupBoard() {
        board = new ChessPiece[8][8];
        setupBoard();
    }

    private void setupBoard() {
        // Đen
        board[0][0] = new Rook(ChessPiece.Color.BLACK);
        board[0][1] = new Knight(ChessPiece.Color.BLACK);
        board[0][2] = new Bishop(ChessPiece.Color.BLACK);
        board[0][3] = new Queen(ChessPiece.Color.BLACK);
        board[0][4] = new King(ChessPiece.Color.BLACK);
        board[0][5] = new Bishop(ChessPiece.Color.BLACK);
        board[0][6] = new Knight(ChessPiece.Color.BLACK);
        board[0][7] = new Rook(ChessPiece.Color.BLACK);
        for (int i = 0; i < 8; i++) {
            board[1][i] = new Pawn(ChessPiece.Color.BLACK);
        }

        // Trắng
        for (int i = 0; i < 8; i++) {
            board[6][i] = new Pawn(ChessPiece.Color.WHITE);
        }
        board[7][0] = new Rook(ChessPiece.Color.WHITE);
        board[7][1] = new Knight(ChessPiece.Color.WHITE);
        board[7][2] = new Bishop(ChessPiece.Color.WHITE);
        board[7][3] = new Queen(ChessPiece.Color.WHITE);
        board[7][4] = new King(ChessPiece.Color.WHITE);
        board[7][5] = new Bishop(ChessPiece.Color.WHITE);
        board[7][6] = new Knight(ChessPiece.Color.WHITE);
        board[7][7] = new Rook(ChessPiece.Color.WHITE);
    }

    public ChessPiece[][] getBoard() {
        return board;
    }

}
