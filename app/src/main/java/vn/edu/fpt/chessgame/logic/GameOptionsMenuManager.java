package vn.edu.fpt.chessgame.logic;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;

import vn.edu.fpt.chessgame.MainActivity;
import vn.edu.fpt.chessgame.R;
import vn.edu.fpt.chessgame.logic.OnlineChessManager;

public class GameOptionsMenuManager {

    private final Activity activity;
    private final boolean isOnlineMode;
    private final boolean isPlayingWithBot;
    private final String matchId;
    private final OnlineChessManager manager;
    private final RestartCallback restartCallback;

    public interface RestartCallback {
        void onRestartRequested();
    }

    public GameOptionsMenuManager(Activity activity, boolean isOnlineMode, boolean isPlayingWithBot,
                                  String matchId, OnlineChessManager manager, RestartCallback restartCallback) {
        this.activity = activity;
        this.isOnlineMode = isOnlineMode;
        this.isPlayingWithBot = isPlayingWithBot;
        this.matchId = matchId;
        this.manager = manager;
        this.restartCallback = restartCallback;
    }

    public void prepare(Menu menu) {
        if (!isPlayingWithBot) {
            // Ẩn nút "Bắt đầu lại" nếu không chơi với máy
            MenuItem restartItem = menu.findItem(R.id.menu_restart);
            if (restartItem != null) {
                restartItem.setVisible(false);
            }
        }
    }

    public boolean handle(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_surrender) {
            handleSurrender();
            return true;

        } else if (id == R.id.menu_restart) {
            if (isOnlineMode) {
                handleSurrender();
            } else if (isPlayingWithBot && restartCallback != null) {
                restartCallback.onRestartRequested();
                Toast.makeText(activity, "Đã bắt đầu lại ván chơi", Toast.LENGTH_SHORT).show();
            }
            return true;

        } else if (id == R.id.menu_exit) {
            activity.finish();
            return true;
        }

        return false;
    }

    private void handleSurrender() {
        Toast.makeText(activity, "Bạn đã đầu hàng!", Toast.LENGTH_SHORT).show();
        if (manager != null && matchId != null) {
            manager.sendSurrender(matchId);
        }
        activity.finish();
    }

    public void goToMainScreen() {
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }
}
