package com.budget.abudget;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class CRUDActivity extends AppCompatActivity {

    private static final String TAG = "CRUDActivity";

    Button btnSave;
    Button btnDel;
    EditText addName;
    EditText addCost;
    EditText addQuantity;

    DBHelper dbHelper;
    SQLiteDatabase db;
    Cursor cursor;
    long rowId=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crud_layout);

        addName = (EditText) findViewById(R.id.addName);
        addCost = (EditText) findViewById(R.id.addCost);
        addQuantity = (EditText) findViewById(R.id.addQuantity);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnDel = (Button) findViewById(R.id.btnDel);

        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData(v);
            }
        });

        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteData(v);
            }
        });

        // id выбранного элемента
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            rowId = extras.getLong("id");
        }
        // Если не 0, то изменение
        if (rowId != 0) {
            // Получаем элемент по id из бд
            cursor = dbHelper.getDataById(String.valueOf(rowId));
            // Заполняем форму существующими значениями
            addName.setText(cursor.getString(1));
            addCost.setText(cursor.getString(2));
            addQuantity.setText(cursor.getString(3));
            cursor.close();
        // Если 0, то добавление -> скрываем кнопку удаления
        } else {
            btnDel.setVisibility(View.GONE);
        }
    }

    public void saveData(View view) {
        // ** Сохранение данных **
        ContentValues contentValues = new ContentValues();
        // Собираем новые данные из формы
        contentValues.put(DBHelper.KEY_NAME, addName.getText().toString());
        contentValues.put(DBHelper.KEY_COST, addCost.getText().toString());
        contentValues.put(DBHelper.KEY_QUANTITY, addQuantity.getText().toString());

        // Проверяем на пустоту
        if (addName.length() == 0 || addCost.length() == 0 || addQuantity.length() == 0) {
            toastMessage("Необходимо заполнить все поля!");
        } else {
            if (rowId != 0) {
                updateData(contentValues, String.valueOf(rowId));
            } else {
                addData(contentValues);
            }
            goHome();
        }
    }

    public void deleteData(View view){
        // ** Удаление данных **
        boolean result = dbHelper.deleteData(String.valueOf(rowId));
        checkResult(result);
        goHome();
    }

    public void addData(ContentValues contentValues) {
        // ** Добавление данных **
        boolean result = dbHelper.addData(contentValues);
        checkResult(result);
    }

    public void updateData(ContentValues contentValues, String id) {
        // ** Изменение данных **
        boolean result = dbHelper.updateData(contentValues, id);
        checkResult(result);
    }

    public void checkResult(Boolean result) {
        if (result) {
            toastMessage("Данные успешно обновлены!");
        } else {
            toastMessage("Что-то пошло не так..");
        }
    }

    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }

    private void goHome(){
        Intent returnIntent = new Intent();
        setResult(RESULT_OK,returnIntent);
        finish();
    }

}




















