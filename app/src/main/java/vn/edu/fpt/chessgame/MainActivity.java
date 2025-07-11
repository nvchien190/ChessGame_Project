package vn.edu.fpt.chessgame;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import vn.edu.fpt.chessgame.logic.OnlineChessManager;
import vn.edu.fpt.chessgame.logic.OnlineOptionActivity;
import vn.edu.fpt.chessgame.logic.StartGameActivity;
import vn.edu.fpt.chessgame.model.Match;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // layout chính của menu

        Button button2Player = findViewById(R.id.buttonStart);
        Button buttonStartBot = findViewById(R.id.buttonStartBot);

        // 2 người
        button2Player.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, StartGameActivity.class);
            intent.putExtra("isOnline", false);         // 👈 chơi offline
            intent.putExtra("playWithBot", false);      // 👈 không bot
            startActivity(intent);
        });

// Chơi với Bot
        buttonStartBot.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, StartGameActivity.class);
            intent.putExtra("isOnline", false);         // 👈 chơi offline
            intent.putExtra("playWithBot", true);       // 👈 có bot
            startActivity(intent);
        });

//        Button buttonOnline = findViewById(R.id.buttonStartOnline);
//        buttonOnline.setOnClickListener(v -> {
//            // 🔧 Khởi tạo Firebase với URL vùng đúng (asia-southeast1)
//            FirebaseDatabase database = FirebaseDatabase.getInstance(
//                    "https://chessgame-e626e-default-rtdb.asia-southeast1.firebasedatabase.app/"
//            );
//
//            // 🔑 Tạo ID cho phòng chơi
//            String matchId = database.getReference("matches").push().getKey();
//
//            // ✅ Kiểm tra ID hợp lệ
//            if (matchId != null) {
//                // 👤 Tạo đối tượng Match
//                Match match = new Match("uid_1", "", "waiting");
//
//                // 📝 Ghi dữ liệu lên Firebase
//                database.getReference("matches").child(matchId).setValue(match)
//                        .addOnCompleteListener(task -> {
//                            if (task.isSuccessful()) {
//                                String msg = "✅ Đã tạo phòng online: " + matchId;
//                                Log.d("FirebaseMatch", msg);
//                                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
//                            } else {
//                                Log.e("FirebaseMatch", "❌ Ghi dữ liệu thất bại", task.getException());
//                                Toast.makeText(MainActivity.this, "Không ghi được dữ liệu!", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//            } else {
//                Log.e("FirebaseMatch", "❌ Không tạo được matchId (null)");
//                Toast.makeText(MainActivity.this, "Không thể tạo phòng chơi!", Toast.LENGTH_SHORT).show();
//            }
//        });

        Button buttonOnline = findViewById(R.id.buttonStartOnline);
        buttonOnline.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, OnlineOptionActivity.class);
            startActivity(intent);
        });

    }


}