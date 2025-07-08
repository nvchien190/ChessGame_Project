package vn.edu.fpt.chessgame.logic;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import vn.edu.fpt.chessgame.R;
import vn.edu.fpt.chessgame.model.Bishop;
import vn.edu.fpt.chessgame.model.ChessPiece;
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
        turnTextView.setText(getString(R.string.textTurn));

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
                ImageView cell = findViewById(resId); // Sửa từ TextView → ImageView
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

    private void onCellClicked(int row, int col) {
        ChessPiece piece = board[row][col];
//Chọn quân
        if (selectedRow == -1 && piece != null) {
            if ((isWhiteTurn && piece.getColor() != ChessPiece.Color.WHITE) ||
                    (!isWhiteTurn && piece.getColor() != ChessPiece.Color.BLACK)) {
                Toast.makeText(this, "🚫 Không phải lượt của bạn", Toast.LENGTH_SHORT).show();
                return;
            }
            // Tô nền cho ô đang chọn
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

        } else if (selectedRow != -1) {
            ChessPiece selectedPiece = board[selectedRow][selectedCol];

            if (selectedPiece != null && selectedPiece.isValidMove(selectedRow, selectedCol, row, col, board)) {
                ChessPiece target = board[row][col];
                if (target != null && target.getColor() == selectedPiece.getColor()) {
                    Toast.makeText(this, "🚫 Không thể ăn quân cùng màu", Toast.LENGTH_SHORT).show();
                } else {
                    board[row][col] = selectedPiece;
                    board[selectedRow][selectedCol] = null;
                    renderPiecesToBoard();
                    if (selectedPiece instanceof Pawn && (row == 0 || row == 7)) {
                        showPromotionDialog(row, col, selectedPiece.getColor());
                        return; // Dừng lại để chờ người chơi chọn quân
                    }

                    // Đổi lượt
                    isWhiteTurn = !isWhiteTurn;
                    turnTextView.setText(isWhiteTurn ? "Lượt: Trắng" : "Lượt: Đen");

                }
                }
//            else {
//                Toast.makeText(this, "❌ Nước đi không hợp lệ", Toast.LENGTH_SHORT).show();
//            }

            selectedRow = -1;
            selectedCol = -1;
            clearHighlights();

        }
    }

    private void highlightValidMoves(int row, int col) {
        ChessPiece piece = board[row][col];
        if (piece == null) return;

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (piece.isValidMove(row, col, r, c, board)) {
                    ChessPiece target = board[r][c];

                    // ✅ Chọn màu viền: đỏ nếu có thể ăn, vàng nếu ô trống
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

    private void showPromotionDialog(int row, int col, ChessPiece.Color color) {
        String[] options = {"Hậu", "Xe", "Tượng", "Mã"};
        int[] imageResIds = {
                color == ChessPiece.Color.WHITE ? R.drawable.wqueen : R.drawable.bqueen,
                color == ChessPiece.Color.WHITE ? R.drawable.wrook : R.drawable.brook,
                color == ChessPiece.Color.WHITE ? R.drawable.wbishop : R.drawable.bbishop,
                color == ChessPiece.Color.WHITE ? R.drawable.wknight : R.drawable.bknight
        };

        PromotionAdapter adapter = new PromotionAdapter(this, options, imageResIds, color);

        new AlertDialog.Builder(this)
                .setTitle("Chọn quân để phong")
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
                    turnTextView.setText(isWhiteTurn ? "Lượt: Trắng" : "Lượt: Đen");
                })
                .setCancelable(false)
                .show();
    }









}
