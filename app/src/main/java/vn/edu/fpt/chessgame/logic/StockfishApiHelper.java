package vn.edu.fpt.chessgame.logic;

import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import java.net.URLEncoder;
public class StockfishApiHelper {
    public static String getBestMove(String fen) {
        try {
            OkHttpClient client = new OkHttpClient();

            String encodedFen = URLEncoder.encode(fen, "UTF-8");
            String url = "https://www.stockfish.online/api/s/v2.php?fen=" + encodedFen + "&depth=15";

            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();
            String json = response.body().string();

            JSONObject obj = new JSONObject(json);
            String bestmoveRaw = obj.getString("bestmove"); // "bestmove d2d4 ponder e7e6"

            // Tách chuỗi để lấy đúng nước đi
            String[] parts = bestmoveRaw.split(" ");
            if (parts.length >= 2) {
                return parts[1]; // "d2d4"
            } else {
                return null;
            }

        } catch (Exception e) {
            Log.e("StockfishAPI", "Lỗi khi gọi API", e);
            return null;
        }
    }

}
