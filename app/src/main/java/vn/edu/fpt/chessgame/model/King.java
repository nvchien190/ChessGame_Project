package vn.edu.fpt.chessgame.model;

import vn.edu.fpt.chessgame.R;

public class King extends ChessPiece{
    private boolean hasMoved = false;

    public  King(Color color){
        super(color);
    }
    @Override
    public boolean isValidMove(int startRow, int startCol, int endRow, int endCol, ChessPiece[][] board) {
        // Không cho đứng yên
        if (startRow == endRow && startCol == endCol) return false;

        int dr = Math.abs(startRow - endRow);
        int dc = Math.abs(startCol - endCol);

        // Nhập thành gần (Kingside)
        if (!hasMoved && startRow == endRow && endCol - startCol == 2) {
            ChessPiece rook = board[startRow][7];
            if (rook instanceof Rook && !((Rook) rook).hasMoved()) {
                if (board[startRow][5] == null && board[startRow][6] == null) {
                    // TODO: kiểm tra vua không bị chiếu, không đi qua ô bị chiếu
                    return true;
                }
            }
        }

        // Nhập thành xa (Queenside)
        if (!hasMoved && startRow == endRow && startCol - endCol == 2) {
            ChessPiece rook = board[startRow][0];
            if (rook instanceof Rook && !((Rook) rook).hasMoved()) {
                if (board[startRow][1] == null && board[startRow][2] == null && board[startRow][3] == null) {
                    // TODO: kiểm tra vua không bị chiếu, không đi qua ô bị chiếu
                    return true;
                }
            }
        }

        if (dr <= 1 && dc <= 1) {
            ChessPiece target = board[endRow][endCol];
            // Không được ăn quân cùng màu
            return target == null || target.getColor() != this.getColor();
        }

        return false;
    }

    @Override
    public int getDrawableRes() {
        return (color== Color.WHITE)? R.drawable.wking : R.drawable.bking;
    }
    @Override
    public ChessPiece clone() {
        return new King(this.getColor());
    }

    //Nhap thanh
    public boolean hasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean moved) {
        this.hasMoved = moved;
    }
    @Override
    public Type getType() {
        return Type.KING;
    }

}
