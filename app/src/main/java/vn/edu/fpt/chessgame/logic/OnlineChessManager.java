package vn.edu.fpt.chessgame.logic;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.*;

import java.util.Random;

import vn.edu.fpt.chessgame.model.Match;
import vn.edu.fpt.chessgame.model.MatchCreatedListener;

public class OnlineChessManager {

    private final DatabaseReference matchRefRoot;
    private final Context context;
    private MatchCreatedListener listener;



    public OnlineChessManager(Context ctx) {
        this.context = ctx;
        matchRefRoot = FirebaseDatabase.getInstance(
                "https://chessgame-e626e-default-rtdb.asia-southeast1.firebasedatabase.app/"
        ).getReference("matches");
    }
    public void setMatchCreatedListener(MatchCreatedListener l) {
        this.listener = l;
    }
    // 🟢 Tạo phòng mới
    public void createMatch(String player1Id) {
        String matchId = generateRoomCode(); // Ví dụ tạo mã phòng: "3472"

        Match match = new Match(player1Id);
        matchRefRoot.child(matchId).setValue(match)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        show("✅ Phòng đã tạo: " + matchId);
                        if (listener != null) {
                            listener.onMatchCreated(matchId);
                        }
                    } else {
                        show("❌ Lỗi tạo phòng!");
                    }
                });
    }

    private String generateRoomCode() {
        Random random = new Random();
        int code = 1000 + random.nextInt(9000); // Tạo số từ 1000–9999
        return String.valueOf(code);
    }



    // 🔵 Tham gia phòng bằng ID
    public void joinMatch(@NonNull String matchId, @NonNull String player2Id) {
        DatabaseReference matchRef = matchRefRoot.child(matchId);
        matchRef.child("blackPlayer").setValue(player2Id);
        matchRef.child("status").setValue("ongoing");
        show("✅ Đã tham gia phòng: " + matchId);
    }


    // ♟️ Cập nhật trạng thái bàn cờ và lượt đi
    public void sendMove(@NonNull String matchId, @NonNull String boardState, @NonNull String nextTurn) {
        DatabaseReference matchRef = matchRefRoot.child(matchId);
        matchRef.child("board").setValue(boardState);
        matchRef.child("turn").setValue(nextTurn);
    }

    // 👀 Lắng nghe cập nhật ván cờ
    public void listenMatch(@NonNull String matchId, ValueEventListener listener) {
        matchRefRoot.child(matchId).addValueEventListener(listener);
    }

    // 🏁 Cập nhật trạng thái kết thúc trận
    public void endMatch(@NonNull String matchId, @NonNull String winnerUid) {
        DatabaseReference matchRef = matchRefRoot.child(matchId);
        matchRef.child("status").setValue("finished");
        matchRef.child("winner").setValue(winnerUid);
    }

    private void show(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        Log.d("OnlineChessManager", msg);
    }
    public void sendSurrender(String matchId) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("matches")
                .child(matchId);

        ref.child("status").setValue("surrendered");
        // Có thể thêm: ref.child("endedBy").setValue("localTest");
    }

}
