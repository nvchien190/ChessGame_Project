package vn.edu.fpt.chessgame.logic;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import vn.edu.fpt.chessgame.R;
import vn.edu.fpt.chessgame.model.Bishop;
import vn.edu.fpt.chessgame.model.ChessPiece;
import vn.edu.fpt.chessgame.model.King;
import vn.edu.fpt.chessgame.model.Knight;
import vn.edu.fpt.chessgame.model.Match;
import vn.edu.fpt.chessgame.model.Pawn;
import vn.edu.fpt.chessgame.model.Queen;
import vn.edu.fpt.chessgame.model.Rook;

public class StartGameActivity extends AppCompatActivity {
    private ChessPiece[][] board;
    private boolean isWhiteTurn = true;
    private TextView turnTextView;
    private List<ImageView> highlightedCells = new ArrayList<>();
    private boolean isPlayingWithBot = false;
    private int selectedRow = -1, selectedCol = -1;
    private int lastFromRow = -1, lastFromCol = -1;
    private int lastToRow = -1, lastToCol = -1;
    private List<Point> moveHints = new ArrayList<>();
    private String uid;

    private boolean isWhiteSide = true;           // Người chơi này có quân trắng?
    private boolean isOnlineMode = false;         // Đang chơi online?
    private String matchId = "";                  // ID của phòng đấu
    private OnlineChessManager manager;           // Để tương tác Firebase



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newgame_board);
        // Nhận cờ hiệu từ MainActivity
        isOnlineMode = getIntent().getBooleanExtra("isOnline", false);
        isPlayingWithBot = getIntent().getBooleanExtra("playWithBot", false);

        initVariables();        // Khởi tạo biến từ intent
        setupBoardAndUI();      // Khởi tạo bàn cờ và giao diện
        setupGameMode();        // Xử lý theo chế độ chơi (bot / online)


    }
    private GameOptionsMenuManager menuManager;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.game_menu, menu);

        menuManager = new GameOptionsMenuManager(
                this,
                isOnlineMode,
                isPlayingWithBot,
                matchId,
                manager,
                this::setupBoardAndUI // truyền callback khởi động lại
        );

        menuManager.prepare(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return menuManager != null && menuManager.handle(item) || super.onOptionsItemSelected(item);
    }



    private void initVariables() {

        Intent intent = getIntent();
        isOnlineMode = intent.getBooleanExtra("isOnline", false);
        matchId = intent.getStringExtra("matchId");
        uid = getIntent().getStringExtra("uid"); // ✅ THÊM VÀO ĐÂY


        if (matchId == null) matchId = "";
        isWhiteTurn = true;
        manager = isOnlineMode ? new OnlineChessManager(this) : null;
    }

    public void setupBoardAndUI() {
        setupBoard setup = new setupBoard();
        board = setup.getBoard();
        View boardLayout = findViewById(R.id.gridLayoutBoard); // thay bằng đúng ID layout của bàn
        if (isBoardFlipped()) {
            boardLayout.setRotationX(180f); // xoay bàn nhìn từ dưới lên
        }

        renderPiecesToBoard();
        setupCellClickListeners();

        turnTextView = findViewById(R.id.turnTextView);

    }
    private void setupGameMode() {
        if (isPlayingWithBot) {
            // ⚔️ Chế độ chơi với Bot
            isWhiteSide = true; // người chơi là trắng
            isWhiteTurn = true;
            turnTextView.setText("Lượt bạn đi");
            Toast.makeText(this, "Bạn chơi trắng. Bot sẽ đi sau bạn.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isOnlineMode) {
            // 🌐 Chế độ chơi online
            if (manager != null && matchId != null && !matchId.isEmpty()) {
                startOnlineSync(uid);
            }
            return;
        }

        // 👥 Chế độ 2 người 1 máy
        isWhiteSide = true; // người đầu tiên là trắng
        isWhiteTurn = true;
        turnTextView.setText("Lượt Trắng đi");
        Toast.makeText(this, "Chế độ 2 người 1 máy đang hoạt động.", Toast.LENGTH_SHORT).show();
    }

    private void startOnlineSync(String currentUid) {
        manager.listenMatch(matchId, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Match match = snapshot.getValue(Match.class);
                if (match == null || currentUid == null) return;

                // 🎯 Xác định bên chơi (Trắng / Đen)
                isWhiteSide = currentUid.equals(match.whitePlayer);
                isWhiteTurn = match.turn.equals("white");
                boolean isMyTurn = (isWhiteSide && isWhiteTurn) || (!isWhiteSide && !isWhiteTurn);

              // ⛔ Nếu không phải lượt mình, thì bên kia vừa đi → cập nhật bàn cờ
                if (!isMyTurn && match.board != null && !match.board.isEmpty()) {
                    applyBoardFromFEN(match.board);
                    renderPiecesToBoard();
                }

                // 🎯 Dựng lại bàn cờ nếu có dữ liệu FEN
                if (match.board != null && !match.board.isEmpty()) {
                    applyBoardFromFEN(match.board);     // ✅ từ FEN → board[][]
                    renderPiecesToBoard();              // ✅ vẽ lại giao diện
                }

                updateTurnUI(); // ✅ tách riêng phần hiển thị lượt
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StartGameActivity.this, "Lỗi Firebase: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateTurnUI() {
        if ((isWhiteSide && isWhiteTurn) || (!isWhiteSide && !isWhiteTurn)) {
            turnTextView.setText("Lượt bạn đi");
        } else {
            turnTextView.setText("Chờ đối thủ đi");
        }

        String roleText = isWhiteSide ? "Bạn là quân Trắng." : "Bạn là quân Đen.";
        Toast.makeText(this, roleText, Toast.LENGTH_SHORT).show();
    }







    private void renderPiecesToBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                String id = getCellId(row, col);

                int viewId = getResources().getIdentifier(id, "id", getPackageName());
                ImageView cell = findViewById(viewId);
                if (cell == null) continue;

                // 1. Nền mặc định (đen/trắng)
                cell.setBackgroundColor(getDefaultColor(row, col));

                // 2. (Tuỳ chọn) Highlight ô hợp lệ để đi
                if (moveHints != null && moveHints.contains(new Point(row, col))) {
                    cell.setBackgroundColor(Color.parseColor("#90EE90")); // Xanh nhạt
                }

                // 3. Tạo viền nếu cần
                GradientDrawable border = null;

                // 3a. Ô đang được chọn → viền xanh
                if (row == selectedRow && col == selectedCol) {
                    border = new GradientDrawable();
                    border.setColor(Color.TRANSPARENT);
                    border.setStroke(6, Color.parseColor("#00BFFF")); // Xanh dương
                    border.setCornerRadius(6f);
                }

                // 3b. Nước vừa đi → viền vàng (ưu tiên thấp hơn ô đang chọn)
                else if ((row == lastFromRow && col == lastFromCol) ||
                        (row == lastToRow && col == lastToCol)) {
                    border = new GradientDrawable();
                    border.setColor(Color.TRANSPARENT);
                    border.setStroke(6, ContextCompat.getColor(this, R.color.colorPrimaryDark)); // ✅ đúng cách
                    border.setCornerRadius(6f);
                }


                // 4. Áp dụng viền
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    cell.setForeground(border);
                } else if (border != null) {
                    cell.setBackground(border); // fallback nếu cần
                } else {
                    cell.setForeground(null); // xoá viền nếu không cần
                }

                // 5. Hiển thị quân cờ
                ChessPiece piece = board[row][col];
                if (piece != null) {
                    cell.setImageResource(piece.getDrawableRes());
                    cell.setRotation(piece.getColor() == ChessPiece.Color.BLACK ? 180f : 0f);
                } else {
                    cell.setImageDrawable(null);
                }
            }
        }
    }


    private void setupCellClickListeners() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                String id = getCellId(row, col);

                int resId = getResources().getIdentifier(id, "id", getPackageName());
                ImageView cell = findViewById(resId);
                if (cell == null) continue;

                // Gán tọa độ vào tag để dễ xử lý
                cell.setTag(new int[]{row, col});

                // Sự kiện khi click ô
                cell.setOnClickListener(v -> {
                    int[] displayPos = (int[]) v.getTag();

                    // 🔁 Xoay từ giao diện → sang logic nếu bên Đen online
                    int clickedRow = isBoardFlipped() ? 7 - displayPos[0] : displayPos[0];
                    int clickedCol = isBoardFlipped() ? 7 - displayPos[1] : displayPos[1];

                    Log.d("Click", "Giao diện: (" + displayPos[0] + "," + displayPos[1] + ") → Logic: (" + clickedRow + "," + clickedCol + ")");

                    onCellClicked(clickedRow, clickedCol);
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

        String id = getCellId(row, col);

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
//                Toast.makeText(this, getResources().getString(R.string.invalidMove), Toast.LENGTH_SHORT).show();
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
        if (isOnlineMode && !((isWhiteSide && isWhiteTurn) || (!isWhiteSide && !isWhiteTurn))) {
            Toast.makeText(this, "⛔ Chưa đến lượt bạn!", Toast.LENGTH_SHORT).show();
            return;
        }

        ChessPiece clickedPiece = board[row][col];

        // 1. Chưa chọn quân → chọn quân
        if (selectedRow == -1 && clickedPiece != null) {
            if ((isWhiteTurn && clickedPiece.getColor() == ChessPiece.Color.WHITE) ||
                    (!isWhiteTurn && clickedPiece.getColor() == ChessPiece.Color.BLACK)) {
                selectedRow = row;
                selectedCol = col;

                clearHighlights();
                moveHints.clear();

                highlightValidMoves(row, col);
                renderPiecesToBoard();
            } else {
                Toast.makeText(this, "Không phải lượt của bạn!", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        // 2. Bấm lại quân đang chọn → bỏ chọn
        if (selectedRow == row && selectedCol == col) {
            selectedRow = -1;
            selectedCol = -1;
            clearHighlights();
            moveHints.clear();
            renderPiecesToBoard();
            return;
        }

        ChessPiece selectedPiece = board[selectedRow][selectedCol];
        ChessPiece target = board[row][col];

        // 3. Không được ăn quân cùng màu
        if (target != null && target.getColor() == selectedPiece.getColor()) {
            Toast.makeText(this, getString(R.string.notAllowKill), Toast.LENGTH_SHORT).show();
            return;
        }

        // 4. Kiểm tra chiếu hậu nếu đi quân đó
        ChessPiece[][] tempBoard = cloneBoard(board);
        tempBoard[row][col] = tempBoard[selectedRow][selectedCol];
        tempBoard[selectedRow][selectedCol] = null;

        if (isKingInCheckAfterMove(tempBoard, selectedPiece.getColor())) {
            Toast.makeText(this, getString(R.string.handleMoveCheckMate), Toast.LENGTH_SHORT).show();
            return;
        }

        // 5. Kiểm tra nhập thành trước khi di chuyển quân
        boolean isCastling = false;
        if (selectedPiece instanceof King && Math.abs(col - selectedCol) == 2) {
            // Nhập thành gần
            if (col == 6) {
                board[row][6] = selectedPiece;
                board[row][4] = null;
                board[row][5] = board[row][7];
                board[row][7] = null;

                selectedPiece.setHasMoved(true);
                if (board[row][5] instanceof Rook) {
                    ((Rook) board[row][5]).setHasMoved(true);
                }
                isCastling = true;
            }
            // Nhập thành xa
            else if (col == 2) {
                board[row][2] = selectedPiece;
                board[row][4] = null;
                board[row][3] = board[row][0];
                board[row][0] = null;

                selectedPiece.setHasMoved(true);
                if (board[row][3] instanceof Rook) {
                    ((Rook) board[row][3]).setHasMoved(true);
                }
                isCastling = true;
            }
        }

        // 6. Di chuyển quân nếu không phải nhập thành
        if (!isCastling) {
            board[row][col] = selectedPiece;
            board[selectedRow][selectedCol] = null;
            selectedPiece.setHasMoved(true);
        }

        // 7. Cập nhật nước vừa đi
        lastFromRow = selectedRow;
        lastFromCol = selectedCol;
        lastToRow = row;
        lastToCol = col;

        clearHighlights();
        moveHints.clear();
        selectedRow = -1;
        selectedCol = -1;

        renderPiecesToBoard();

        // 8. Phong cấp tốt
        if (selectedPiece instanceof Pawn && (row == 0 || row == 7)) {
            showPromotionDialog(row, col, selectedPiece.getColor());
        }

        // 9. Đổi lượt
        isWhiteTurn = !isWhiteTurn;
        turnTextView.setText(isWhiteTurn ? R.string.textTurnWhite : R.string.textTurnBlack);

        // 10. Gửi nước đi lên Firebase nếu online
        if (isOnlineMode && manager != null && matchId != null && !matchId.isEmpty()) {
            String nextTurn = isWhiteTurn ? "white" : "black";
            String boardState = generateFEN();
            manager.sendMove(matchId, boardState, nextTurn);
        }

        // 11. Nếu chơi với Bot
        if (isPlayingWithBot && !isWhiteTurn) {
            String fen = generateFEN();
            new Thread(() -> {
                String bestMove = StockfishApiHelper.getBestMove(fen);
                runOnUiThread(() -> {
                    if (bestMove != null) {
                        botMove(bestMove);
                    } else {
                        Toast.makeText(this, "Bot không phản hồi!", Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        }

        // 12. Kiểm tra chiếu / chiếu hết
        checkForCheckOrCheckmate();
    }

    private View getCellAt(int row, int col) {
        String cellId = "cell_" + row + "_" + col;
        int resId = getResources().getIdentifier(cellId, "id", getPackageName());
        return findViewById(resId);
    }
    private void resetCellColors() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                View cell = getCellAt(row, col);
                if (cell != null) {
                    cell.setBackgroundColor(getDefaultColor(row, col));
                }
            }
        }
    }
    private int getDefaultColor(int row, int col) {
        boolean isWhite = (row + col) % 2 == 0;
        int colorRes = isWhite ? R.color.colorLightBoard : R.color.colorDarkBoard;
        return ContextCompat.getColor(this, colorRes);
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
                    String id = getCellId(r, c);

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

    private void botMove(String moveStr) {
        int fromCol = fileToCol(moveStr.charAt(0));
        int fromRow = rankToRow(moveStr.charAt(1));
        int toCol = fileToCol(moveStr.charAt(2));
        int toRow = rankToRow(moveStr.charAt(3));

        selectedRow = fromRow;
        selectedCol = fromCol;

        handleMove(toRow, toCol);
    }
    public String generateFEN() {
        StringBuilder fen = new StringBuilder();

        for (int row = 0; row < 8; row++) {
            int empty = 0;
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = board[row][col];
                if (piece == null) {
                    empty++;
                } else {
                    if (empty > 0) {
                        fen.append(empty);
                        empty = 0;
                    }
                    fen.append(getPieceSymbol(piece));
                }
            }
            if (empty > 0) fen.append(empty);
            if (row < 7) fen.append('/');
        }

        fen.append(isWhiteTurn ? " w " : " b ");
        fen.append("- - 0 1");

        return fen.toString();
    }
    private int fileToCol(char file) {
        return file - 'a';
    }

    private int rankToRow(char rank) {
        return 8 - Character.getNumericValue(rank);
    }

    private char getPieceSymbol(ChessPiece piece) {
        ChessPiece.Type type = piece.getType();
        ChessPiece.Color color = piece.getColor();

        char symbol;
        switch (type) {
            case PAWN:   symbol = 'p'; break;
            case KNIGHT: symbol = 'n'; break;
            case BISHOP: symbol = 'b'; break;
            case ROOK:   symbol = 'r'; break;
            case QUEEN:  symbol = 'q'; break;
            case KING:   symbol = 'k'; break;
            default:     symbol = '?'; break;
        }

        return (color == ChessPiece.Color.WHITE) ? Character.toUpperCase(symbol) : symbol;
    }


    private void applyBoardFromFEN(String fen) {
        // 🧼 Xóa toàn bộ bàn cờ trước khi dựng mới
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                board[r][c] = null;
            }
        }

        String[] parts = fen.split(" ");
        if (parts.length < 1) return;

        String boardPart = parts[0];  // phần bàn cờ: "rnbqkbnr/pppppppp/8/..."

        String[] rows = boardPart.split("/");
        if (rows.length != 8) return;

        for (int row = 0; row < 8; row++) {
            int col = 0;
            for (int i = 0; i < rows[row].length(); i++) {
                char c = rows[row].charAt(i);

                if (Character.isDigit(c)) {
                    col += Character.getNumericValue(c);  // số ô trống
                } else {
                    ChessPiece.Color color = Character.isUpperCase(c) ? ChessPiece.Color.WHITE : ChessPiece.Color.BLACK;
                    ChessPiece piece = createPieceFromChar(c, color);  // 👇 dùng hàm phụ
                    board[row][col] = piece;
                    col++;
                }
            }
        }
    }
    private ChessPiece createPieceFromChar(char c, ChessPiece.Color color) {
        switch (Character.toLowerCase(c)) {
            case 'p': return new Pawn(color);
            case 'r': return new Rook(color);
            case 'n': return new Knight(color);
            case 'b': return new Bishop(color);
            case 'q': return new Queen(color);
            case 'k': return new King(color);
            default: return null;
        }
    }
    private boolean isBoardFlipped() {
        return isOnlineMode && !isWhiteSide;
    }
    private String getCellId(int row, int col) {
        int displayRow = isBoardFlipped() ? 7 - row : row;
        int displayCol = isBoardFlipped() ? 7 - col : col;
        return "R" + displayRow + "" + displayCol;
    }






}
