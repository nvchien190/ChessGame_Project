package vn.edu.fpt.chessgame.logic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import vn.edu.fpt.chessgame.R;
import vn.edu.fpt.chessgame.model.ChessPiece;

/*Hiển thị quân*/
public class PromotionAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] names;
    private final int[] imageResIds;
    private final ChessPiece.Color color;
       public PromotionAdapter(Context context, String[] names, int[] imageResIds, ChessPiece.Color color) {
        super(context, R.layout.promotion_choice, names);
        this.context = context;
        this.names = names;
        this.imageResIds = imageResIds;
        this.color = color;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View row = LayoutInflater.from(context).inflate(R.layout.promotion_choice, parent, false);
        TextView name = row.findViewById(R.id.pieceName);
        ImageView image = row.findViewById(R.id.pieceImage);

        name.setText(names[position]);
        image.setImageResource(imageResIds[position]);
        image.setRotation(color == ChessPiece.Color.BLACK ? 180f : 0f);

        return row;
    }

}
