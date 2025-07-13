package vn.edu.fpt.chessgame.logic;


import android.app.Activity;
import android.content.Context;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;

import vn.edu.fpt.chessgame.R;
import vn.edu.fpt.chessgame.logic.LanguageHelper;

public class LanguageSelector {

    public static void attachToSpinner(Activity activity, Spinner spinner) {
        String[] languages = { "🇻🇳 Tiếng Việt", "🇬🇧 English" };
        String[] codes = { "vi", "en" };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                activity,
                android.R.layout.simple_spinner_item,
                languages
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Set ngôn ngữ hiện tại
        String currentLang = LanguageHelper.getSavedLanguage(activity);
        int currentIndex = currentLang.equals("en") ? 1 : 0;
        spinner.setSelection(currentIndex, false); // không trigger lại listener

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                if (!currentLang.equals(codes[position])) {
                    LanguageHelper.setLanguage(activity, codes[position]);
                    activity.recreate(); // áp dụng lại ngôn ngữ
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
}

