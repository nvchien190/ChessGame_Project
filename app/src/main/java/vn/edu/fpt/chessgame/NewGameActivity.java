package vn.edu.fpt.chessgame;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import vn.edu.fpt.chessgame.model.ChessPiece;
import vn.edu.fpt.chessgame.model.setupBoard;

public class NewGameActivity extends AppCompatActivity {
    private ChessPiece[][] board;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newgame_board);

        // 1. Setup dữ liệu bàn cờ
        setupBoard setup = new setupBoard();
        board = setup.getBoard();

        // 2. Hiển thị quân cờ lên bàn cờ
        renderPiecesToBoard();
    }

    private void renderPiecesToBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                String id = "R" + row + col;
                int viewId = getResources().getIdentifier(id, "id", getPackageName());
                TextView cell = findViewById(viewId);
                if (cell == null) continue;

                ChessPiece piece = board[row][col];

                if (piece != null) {

                    Drawable drawable = getResources().getDrawable(piece.getDrawableRes(), null);
                    int sizeInPx = getResources().getDimensionPixelSize(R.dimen.chess_piece_size);
                    drawable.setBounds(0, 0, sizeInPx, sizeInPx);

                    cell.setCompoundDrawables(null, drawable, null, null);

                    cell.setGravity(Gravity.CENTER);
                } else {
                    cell.setCompoundDrawables(null, null, null, null);
                }
            }
        }
    }

}
