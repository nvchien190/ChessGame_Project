package vn.edu.fpt.chessgame.logic;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import vn.edu.fpt.chessgame.R;
import vn.edu.fpt.chessgame.model.Bishop;
import vn.edu.fpt.chessgame.model.ChessPiece;
import vn.edu.fpt.chessgame.model.King;
import vn.edu.fpt.chessgame.model.Knight;
import vn.edu.fpt.chessgame.model.Pawn;
import vn.edu.fpt.chessgame.model.Queen;
import vn.edu.fpt.chessgame.model.Rook;

public class StartGameActivity extends AppCompatActivity {
    private ChessPiece[][] board;
    private boolean isWhiteTurn = true;
    private TextView turnTextView;
    private List<ImageView> highlightedCells = new ArrayList<>();

    private int selectedRow = -1, selectedCol = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newgame_board);

        // 1. Setup dữ liệu bàn cờ
        setupBoard setup = new setupBoard();
        board = setup.getBoard();

        // 2. Hiển thị quân cờ lên bàn cờ
        renderPiecesToBoard();

//        //3. Click chọn buoc đi
        setupCellClickListeners();

        //
        turnTextView = findViewById(R.id.turnTextView);
        turnTextView.setText(getString(R.string.textTurnWhite));

    }

    private void renderPiecesToBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                String id = "R" + row + col;
                int viewId = getResources().getIdentifier(id, "id", getPackageName());
                ImageView cell = findViewById(viewId);
                if (cell == null) continue;

                ChessPiece piece = board[row][col];
                if (piece != null) {
                    cell.setImageResource(piece.getDrawableRes());
                    // Xoay cờ đen
                    if (piece.getColor() == ChessPiece.Color.BLACK) {
                        cell.setRotation(180f);
                    } else {
                        cell.setRotation(0f);
                    }
                } else {
                    cell.setImageDrawable(null); // Xóa hình cũ nếu không có quân
                }
            }
        }
    }

    private void setupCellClickListeners() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                String id = "R" + row + col;
                int resId = getResources().getIdentifier(id, "id", getPackageName());
                ImageView cell = findViewById(resId);
                if (cell == null) continue;

                // Gán tọa độ vào tag để dễ xử lý
                cell.setTag(new int[]{row, col});

                cell.setOnClickListener(v -> {
                    int[] pos = (int[]) v.getTag();
                    onCellClicked(pos[0], pos[1]);
                });
            }
        }
    }
    /*Xử lí lượt chọn*/
    private void handlePieceSelection(int row, int col, ChessPiece piece) {
        if ((isWhiteTurn && piece.getColor() != ChessPiece.Color.WHITE) ||
                (!isWhiteTurn && piece.getColor() != ChessPiece.Color.BLACK)) {
            Toast.makeText(this, getResources().getString(R.string.notYetMove), Toast.LENGTH_SHORT).show();
            return;
        }

        String id = "R" + row + col;
        int viewId = getResources().getIdentifier(id, "id", getPackageName());
        ImageView cell = findViewById(viewId);
        if (cell != null) {
            cell.setBackgroundColor(getResources().getColor(R.color.colorSelectedPiece, null));
            highlightedCells.add(cell);
        }

        selectedRow = row;
        selectedCol = col;
        Toast.makeText(this, "Đã chọn: " + piece.getClass().getSimpleName(), Toast.LENGTH_SHORT).show();
        highlightValidMoves(row, col);
    }

    private void onCellClicked(int row, int col) {
        ChessPiece piece = board[row][col];

        if (selectedRow == -1 && piece != null) {
            handlePieceSelection(row, col, piece);
            return;
        }

        if (selectedRow != -1) {
            ChessPiece selectedPiece = board[selectedRow][selectedCol];
            if (selectedPiece != null && selectedPiece.isValidMove(selectedRow, selectedCol, row, col, board)) {
                handleMove(row, col);
            } else {
                Toast.makeText(this, getResources().getString(R.string.invalidMove), Toast.LENGTH_SHORT).show();
            }

            selectedRow = -1;
            selectedCol = -1;
            clearHighlights();
        }
    }

    /*Chiếu và chiếu hết*/
    private void checkForCheckOrCheckmate() {
        ChessPiece.Color justMovedColor = isWhiteTurn ? ChessPiece.Color.WHITE : ChessPiece.Color.BLACK;
        boolean isInCheck = isKingInCheck(justMovedColor);

        if (isInCheck) {
            if (isCheckmate(justMovedColor)) {
                ChessPiece.Color winnerColor = justMovedColor == ChessPiece.Color.WHITE
                        ? ChessPiece.Color.BLACK
                        : ChessPiece.Color.WHITE;
                Toast.makeText(this, getResources().getString(R.string.checkmateEnd) + (winnerColor == ChessPiece.Color.WHITE ? getResources().getString( R.string.white_win) : getResources().getString(R.string.black_win)) , Toast.LENGTH_LONG).show();

                // Hiển thị overlay
                LinearLayout overlay = findViewById(R.id.endGameOverlay);
                TextView endText = findViewById(R.id.endGameText);
                Button restartButton = findViewById(R.id.restartButton);

                String winnerText = (winnerColor == ChessPiece.Color.WHITE ? getResources().getString( R.string.white_win ):getResources().getString( R.string.black_win));
                endText.setText(winnerText);
                overlay.setAlpha(0f);
                overlay.setVisibility(View.VISIBLE);
                overlay.animate().alpha(1f).setDuration(500).start();


                restartButton.setOnClickListener(v -> resetGame());
                  // TODO: Khóa bàn cờ hoặc reset game
            } else {
                Toast.makeText(this, getResources().getString(R.string.checkmate), Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void resetGame() {
        setupBoard setup = new setupBoard();
        board = setup.getBoard();
        renderPiecesToBoard();
        isWhiteTurn = true;
        turnTextView.setText(R.string.textTurnWhite);
        selectedRow = -1;
        selectedCol = -1;
        clearHighlights();

        // Ẩn overlay
        findViewById(R.id.endGameOverlay).setVisibility(View.GONE);
    }

    /*Ktra nước đi hợp lệ khi bị chiếu*/
    private void handleMove(int row, int col) {
        ChessPiece selectedPiece = board[selectedRow][selectedCol];
        ChessPiece target = board[row][col];

        if (target != null && target.getColor() == selectedPiece.getColor()) {
            Toast.makeText(this, getResources().getString(R.string.notAllowKill), Toast.LENGTH_SHORT).show();
            return;
        }

        ChessPiece[][] tempBoard = cloneBoard(board);
        tempBoard[row][col] = tempBoard[selectedRow][selectedCol];
        tempBoard[selectedRow][selectedCol] = null;

        if (isKingInCheckAfterMove(tempBoard, selectedPiece.getColor())) {
            Toast.makeText(this, getResources().getString(R.string.handleMoveCheckMate), Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ Di chuyển quân cờ
        board[row][col] = selectedPiece;
        board[selectedRow][selectedCol] = null;
        selectedPiece.setHasMoved(true); // ✅ Áp dụng cho mọi quân cờ

        // ✅ Xử lý nhập thành
        if (selectedPiece instanceof King) {
            if (selectedCol == 4 && col == 6) { // Nhập thành gần
                board[row][5] = board[row][7];
                board[row][7] = null;
                if (board[row][5] instanceof Rook) {
                    ((Rook) board[row][5]).setHasMoved(true);
                }
            } else if (selectedCol == 4 && col == 2) { // Nhập thành xa
                board[row][3] = board[row][0];
                board[row][0] = null;
                if (board[row][3] instanceof Rook) {
                    ((Rook) board[row][3]).setHasMoved(true);
                }
            }
        }

        renderPiecesToBoard();

        if (selectedPiece instanceof Pawn && (row == 0 || row == 7)) {
            showPromotionDialog(row, col, selectedPiece.getColor());
        }

        isWhiteTurn = !isWhiteTurn;
        turnTextView.setText(isWhiteTurn ? R.string.textTurnWhite : R.string.textTurnBlack);

        checkForCheckOrCheckmate();
    }



    /*Tô nước đi hợp lệ*/
    private void highlightValidMoves(int row, int col) {
        ChessPiece piece = board[row][col];
        if (piece == null) return;

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (piece.isValidMove(row, col, r, c, board)) {
                    ChessPiece target = board[r][c];

                    //  Chọn màu viền: đỏ nếu có thể ăn, vàng nếu ô trống
                    int colorRes;
                    if (target != null && target.getColor() != piece.getColor()) {
                        colorRes = R.color.colorDanger;
                    } else if (target == null) {
                        colorRes = R.color.colorChoosePieces;
                    } else {
                        continue;
                    }
                    String id = "R" + r + c;
                    int viewId = getResources().getIdentifier(id, "id", getPackageName());
                    ImageView cell = findViewById(viewId);
                    if (cell != null) {
                        GradientDrawable border = new GradientDrawable();
                        border.setColor(Color.TRANSPARENT); // Giữ nền gốc
                        border.setStroke(6,  getResources().getColor(colorRes));   // Viền vàng
                        cell.setForeground(border);

                        highlightedCells.add(cell);
                    }
                }
            }
        }
    }

    private void clearHighlights() {
        for (ImageView cell : highlightedCells) {
            String id = getResources().getResourceEntryName(cell.getId()); // ví dụ: "R34"
            int row = Character.getNumericValue(id.charAt(1));
            int col = Character.getNumericValue(id.charAt(2));

            boolean isLight = (row + col) % 2 == 0;
            int colorRes = isLight ? R.color.colorLightBoard : R.color.colorDarkBoard;
            cell.setBackgroundColor(getResources().getColor(colorRes, null));

            // Xóa viền foreground
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cell.setForeground(null);
            }
        }
        highlightedCells.clear();
    }

    /*Chọn cờ phong*/
    private void showPromotionDialog(int row, int col, ChessPiece.Color color) {
        String[] options = {getResources().getString(R.string.textQueen), getResources().getString(R.string.textRook), getResources().getString(R.string.textBishop), getResources().getString(R.string.textKnight)};
        int[] imageResIds = {
                color == ChessPiece.Color.WHITE ? R.drawable.wqueen : R.drawable.bqueen,
                color == ChessPiece.Color.WHITE ? R.drawable.wrook : R.drawable.brook,
                color == ChessPiece.Color.WHITE ? R.drawable.wbishop : R.drawable.bbishop,
                color == ChessPiece.Color.WHITE ? R.drawable.wknight : R.drawable.bknight
        };

        PromotionAdapter adapter = new PromotionAdapter(this, options, imageResIds, color);

        new AlertDialog.Builder(this)
                .setTitle(R.string.selectPieceToPromotion)
                .setAdapter(adapter, (dialog, which) -> {
                    ChessPiece newPiece = null;
                    switch (which) {
                        case 0: newPiece = new Queen(color); break;
                        case 1: newPiece = new Rook(color); break;
                        case 2: newPiece = new Bishop(color); break;
                        case 3: newPiece = new Knight(color); break;
                    }

                    board[row][col] = newPiece;
                    renderPiecesToBoard();

                    isWhiteTurn = !isWhiteTurn;
                    turnTextView.setText(isWhiteTurn ? R.string.textTurnWhite : R.string.textTurnBlack);
                })
                .setCancelable(false)
                .show();
    }

    /*Tìm vua*/
    private int[] findKingPosition(ChessPiece.Color color) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                ChessPiece piece = board[r][c];
                if (piece instanceof King && piece.getColor() == color) {
                    return new int[]{r, c};
                }
            }
        }
        return null; // Vua không còn trên bàn
    }

    /*Ktra vua bị chiếu*/
    private boolean isKingInCheck(ChessPiece.Color kingColor) {
        int[] kingPos = findKingPosition(kingColor);
        if (kingPos == null) {
            Log.d("Checkmate", getResources().getString(R.string.kingNotFound) + kingColor);
            return false;
        }

        int kr = kingPos[0];
        int kc = kingPos[1];

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                ChessPiece piece = board[r][c];
                if (piece != null && piece.getColor() != kingColor) {
                    if (piece.isValidMove(r, c, kr, kc, board)) {
                        Log.d("Checkmate", piece.getClass().getSimpleName() + " tại " + r + "," + c + " đang chiếu vua " + kingColor);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /*Clone bàn cờ tạo nước đi thử nghiệm*/
    private ChessPiece[][] cloneBoard(ChessPiece[][] original) {
        ChessPiece[][] copy = new ChessPiece[8][8];
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (original[r][c] != null) {
                    copy[r][c] = original[r][c].clone(); // Bạn cần implement clone() cho mỗi quân
                }
            }
        }
        return copy;
    }
    /*Ktra nước đi hợp lệ sau khi bị chiếu*/
    private boolean isKingInCheckAfterMove(ChessPiece[][] tempBoard, ChessPiece.Color color) {
        ChessPiece[][] original = board;
        board = tempBoard;
        boolean result = isKingInCheck(color);
        board = original;
        return result;
    }

    /*Ktra chiếu và chiếu hết*/
    private boolean isCheckmate(ChessPiece.Color color) {
//        Log.d("Checkmate", "Đang kiểm tra chiếu hết cho: " + color);

        if (!isKingInCheck(color)) return false;
//        Log.d("Checkmate", "Đang kiểm tra chiếu hết cho: " + color);

        for (int sr = 0; sr < 8; sr++) {
            for (int sc = 0; sc < 8; sc++) {
                ChessPiece piece = board[sr][sc];
                if (piece != null && piece.getColor() == color) {
                    for (int er = 0; er < 8; er++) {
                        for (int ec = 0; ec < 8; ec++) {
                            if (piece.isValidMove(sr, sc, er, ec, board)) {
                                ChessPiece[][] tempBoard = cloneBoard(board);
                                tempBoard[er][ec] = tempBoard[sr][sc];
                                tempBoard[sr][sc] = null;
//                                Log.d("Checkmate", "Thử nước: " + sr + "," + sc + " → " + er + "," + ec);

                                if (!isKingInCheckAfterMove(tempBoard, color)) {
                                    return false; // Có ít nhất 1 nước thoát
                                }
                            }
                        }
                    }
                }
            }
        }
        return true; // Không có nước nào để thoát → chiếu hết
    }









}
