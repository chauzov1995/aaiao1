package com.nchauzov.analizator.purse;


import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nchauzov.analizator.DB_sql;
import com.nchauzov.analizator.R;


/**
 * Created by nikita on 19.11.2017.
 */

public class purse_redak extends AppCompatActivity {

    int id_prihod;
    int name_dohod = 1;
    CheckBox checkBox;
    Activity tecactivity;
    //  Context thiscont;


    TextView purse_komment;

    Button purseredakbtn;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.purse_red);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        id_prihod = getIntent().getIntExtra("id", 0);


        tecactivity = this;
        //  thiscont = this;
        //  name_dohod= getIntent().getIntExtra("name_dohod", 0);


        purse_komment = (TextView) findViewById(R.id.purse_komment);
        purseredakbtn = (Button) findViewById(R.id.purseredakbtn);
        checkBox = (CheckBox) findViewById(R.id.checkBox);

        if (id_prihod != 0) {
            DB_sql dbHelper = new DB_sql(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            // делаем запрос всех данных из таблицы mytable, получаем Cursor
            Cursor c = db.rawQuery("select * from `an_purse` Where id=" + id_prihod, null);
            // ставим позицию курсора на первую строку выборки
            // если в выборке нет строк, вернется false
            if (c.moveToFirst()) {
                // определяем номера столбцов по имени в выборке
                int komment = c.getColumnIndex("komment");
                int deafault = c.getColumnIndex("deafault");

                purse_komment.setText(c.getString(komment));
                boolean cheked=c.getInt(deafault) == 1;
                checkBox.setChecked(cheked);
                checkBox.setEnabled(!cheked);
            }
            purseredakbtn.setText("Сохранить");
        } else {

            purseredakbtn.setText("Создать");
        }


        purseredakbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View r) {
                DB_sql dbHelper = new DB_sql(tecactivity);
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                int defal = 0;
                if (checkBox.isChecked()) {
                    db.execSQL("UPDATE `an_purse` SET " +
                            "   `deafault`=0 " +
                            "   WHERE 1 " );
                    defal = 1;
                }

                if (id_prihod != 0) {
                    db.execSQL("UPDATE `an_purse` SET " +
                            "   `komment`='" + purse_komment.getText() + "', " +
                            "   `deafault`=" + defal + " " +
                            "   WHERE id=" + id_prihod);
                } else {

                    db.execSQL("INSERT INTO `an_purse`" +
                            "( `summa`, `komment`, `visible`, `deafault`) VALUES" +
                            " (0,'" + purse_komment.getText().toString() + "',0," + defal + ")");
                }
                finish();
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {

            finish();
        }
        return super.onOptionsItemSelected(item);
    }


}