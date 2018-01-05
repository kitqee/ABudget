package com.budget.abudget;

import android.database.Cursor;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class AnalysisActivity extends AppCompatActivity {

    DBHelper dbHelper;
    EditText from;
    EditText to;
    TextView text;
    Button btmAnalysis;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);

        btmAnalysis = (Button) findViewById(R.id.btmAnalysis);
        text = (TextView) findViewById(R.id.text);
        from = (EditText) findViewById(R.id.from);
        to = (EditText) findViewById(R.id.to);
        dbHelper = new DBHelper(this);

        btmAnalysis.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                text.setText("");
                // Вызов метода для получения резельтата
                cursor = dbHelper.getAnalysis(from.getText().toString(), to.getText().toString());
                // Вывод результата
                if (cursor.moveToFirst()) {
                    int nameColIndex = cursor.getColumnIndex("name");
                    int moneyColIndex = cursor.getColumnIndex("money");
                    text.setText("Результат:\n");
                    do {
                        text.append("\nТовар: " + cursor.getString(nameColIndex) +
                                "\nЗатрачено: " + cursor.getString(moneyColIndex) + "\n -----");
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
        });

    }
}
