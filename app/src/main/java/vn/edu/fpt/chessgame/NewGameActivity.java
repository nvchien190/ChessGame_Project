package vn.edu.fpt.chessgame;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import vn.edu.fpt.chessgame.model.ChessPiece;
import vn.edu.fpt.chessgame.logic.setupBoard;

public class NewGameActivity extends AppCompatActivity {
    private ChessPiece[][] board;
    private boolean isWhiteTurn = true; // Trắng đi trước
    private TextView turnTextView;

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
            selectedRow = row;
            selectedCol = col;
            Toast.makeText(this, "Đã chọn: " + piece.getClass().getSimpleName(), Toast.LENGTH_SHORT).show();
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

                    // Đổi lượt
                    isWhiteTurn = !isWhiteTurn;
                    turnTextView.setText(isWhiteTurn ? "Lượt: Trắng" : "Lượt: Đen");

                }
                } else {
                Toast.makeText(this, "❌ Nước đi không hợp lệ", Toast.LENGTH_SHORT).show();
            }

            selectedRow = -1;
            selectedCol = -1;
        }
    }





}
