package vn.edu.fpt.chessgame.logic;

import vn.edu.fpt.chessgame.model.Bishop;
import vn.edu.fpt.chessgame.model.ChessPiece;
import vn.edu.fpt.chessgame.model.King;
import vn.edu.fpt.chessgame.model.Knight;
import vn.edu.fpt.chessgame.model.Pawn;
import vn.edu.fpt.chessgame.model.Queen;
import vn.edu.fpt.chessgame.model.Rook;

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
    private  void setupBoard(int n){
//        board = new ChessPiece[8][8];
//        board[1][1] = new Pawn(ChessPiece.Color.WHITE);
//  board[6][1] = new Pawn(ChessPiece.Color.BLACK);

        board[1][6] = new Queen(ChessPiece.Color.WHITE);
        board[1][7] = new Queen(ChessPiece.Color.WHITE);
        board[0][0] = new King(ChessPiece.Color.BLACK);

//        board[6][1] = new Queen(ChessPiece.Color.BLACK);
//        board[6][2] = new Queen(ChessPiece.Color.BLACK);
        board[7][7] = new King(ChessPiece.Color.WHITE);


    }

    public ChessPiece[][] getBoard() {
        return board;
    }

}
