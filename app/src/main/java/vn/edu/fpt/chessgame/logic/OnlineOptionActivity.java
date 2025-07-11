package vn.edu.fpt.chessgame.logic;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import vn.edu.fpt.chessgame.R;
public class OnlineOptionActivity extends AppCompatActivity {

    private OnlineChessManager manager;
    private String currentUid = "uid_Chiến";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_option);

        manager = new OnlineChessManager(this);
        manager.setMatchCreatedListener(matchId -> {
            Intent intent = new Intent(this, StartGameActivity.class);
            intent.putExtra("matchId", matchId);
            intent.putExtra("uid", currentUid);
            intent.putExtra("isOnline", true);
            intent.putExtra("isHost", true);
            startActivity(intent);
        });

        Button btnCreate = findViewById(R.id.buttonCreateMatch)
                ;
        Button btnJoin = findViewById(R.id.buttonJoinMatch);

        btnCreate.setOnClickListener(v -> manager.createMatch("uid_Chiến"));

        btnJoin.setOnClickListener(v -> showJoinDialog());
    }

    private void showJoinDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nhập mã phòng");

        final EditText input = new EditText(this);
        input.setHint("Mã phòng gồm 4 chữ số");
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("Tham Gia", (dialog, which) -> {
            String matchId = input.getText().toString().trim();

            if (matchId.length() != 4) {
                Toast.makeText(this, "❌ Mã phòng phải gồm 4 chữ số!", Toast.LENGTH_SHORT).show();
                return;
            }

            manager.joinMatch(matchId, "uid_Chiến");

            // 🔁 Mở màn chơi cờ sau khi tham gia phòng
            String currentUid = "uid_Khach";
            Intent intent = new Intent(this, StartGameActivity.class);
            intent.putExtra("matchId", matchId);
            intent.putExtra("uid", currentUid);
            intent.putExtra("isOnline", true);
            intent.putExtra("isHost", false); // Người tham gia là bên Đen
            startActivity(intent);
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

}

