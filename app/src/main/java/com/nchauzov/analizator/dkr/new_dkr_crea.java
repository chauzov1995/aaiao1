package com.nchauzov.analizator.dkr;


import android.app.DatePickerDialog;
import android.app.Instrumentation;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nchauzov.analizator.DB_sql;
import com.nchauzov.analizator.R;
import com.nchauzov.analizator.dohod.dohod_class;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class new_dkr_crea extends AppCompatActivity {

    Calendar dateAndTime = Calendar.getInstance();
    int kuda_intent, id, purse_intent;
    String date;
    SimpleDateFormat ft = new SimpleDateFormat("yyyy.MM.dd");
    Toolbar mToolbar;
    EditText komment_edit;
    AppCompatActivity getact;
    Spinner purse_v, doh_v;
    //TextView resultField; // текстовое поле для вывода результата
    TextView numberField;   // поле для ввода числа
    // TextView operationField;    // текстовое поле для вывода знака операции
    Double operand = null;  // операнд операции
    String lastOperation = "="; // последняя операция
    boolean sled_remov = false;
    ImageButton b43;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_dkr_crea);

        getact = this;

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        id = getIntent().getIntExtra("id", 0);

        //  summa_edit = (EditText) findViewById(R.id.summa_edit);
        komment_edit = (EditText) findViewById(R.id.komment_edit);
        purse_v = (Spinner) findViewById(R.id.purse_v);
        doh_v = (Spinner) findViewById(R.id.doh_v);
        // resultField = (TextView) findViewById(R.id.resultField);
        numberField = (TextView) findViewById(R.id.numberField);
        //   operationField = (TextView) findViewById(R.id.operationField);
        b43 = (ImageButton) findViewById(R.id.b43);


        b43.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String text_poluch = numberField.getText().toString();
                String text_first = text_poluch.substring(0, text_poluch.length() - 1);
                numberField.setText(text_first);

/*
                int iStart = numberField.getSelectionStart();
                if(iStart>0) {
                    String text_poluch = numberField.getText().toString();
                    String text_first = text_poluch.substring(0, iStart - 1);
                    String text_last = text_poluch.substring(iStart, text_poluch.length());
                    numberField.setText(text_first + text_last);
                    numberField.setSelection(iStart - 1);

                }
                */
            }
        });

        b43.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {

                numberField.setText("");
                return true;    // <- set to true
            }
        });

        DB_sql dbHelper = new DB_sql(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

//Поиск кошельков
        Cursor c_purse = db.rawQuery("select * from `an_purse` Where visible=0", null);
        String[] Purse_arr = new String[c_purse.getCount()];
        if (c_purse.moveToFirst()) {
            // определяем номера столбцов по имени в выборке
            int komment = c_purse.getColumnIndex("komment");

            int i = 0;
            do {
                Purse_arr[i] = c_purse.getString(komment);
                i++;
            } while (c_purse.moveToNext());
        }
        c_purse.close();


        //Поиск категорий
        Cursor c_dohod = db.rawQuery("select * from `an_dohod` Where visible=0", null);
        String[] Dohod_arr = new String[c_dohod.getCount()];
        if (c_dohod.moveToFirst()) {
            // определяем номера столбцов по имени в выборке
            int komment = c_dohod.getColumnIndex("komment");

            int i = 0;
            do {
                Dohod_arr[i] = c_dohod.getString(komment);
                i++;
            } while (c_dohod.moveToNext());
        }
        c_dohod.close();


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Purse_arr);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        purse_v.setAdapter(adapter);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Dohod_arr);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        doh_v.setAdapter(adapter2);


        //для скрытия мягкой клавы
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            numberField.setShowSoftInputOnFocus(false);
        } else {
            numberField.setTextIsSelectable(true);
            //N.B. Accepting the case when non editable text will be selectable
        }

        if (id != 0) {

            // делаем запрос всех данных из таблицы mytable, получаем Cursor
            Cursor c = db.rawQuery("select * from `an_dkr_hist` Where id=" + id, null);
            // ставим позицию курсора на первую строку выборки
            // если в выборке нет строк, вернется false
            if (c.moveToFirst()) {
                // определяем номера столбцов по имени в выборке
                int summa = c.getColumnIndex("summa");
                int komment = c.getColumnIndex("komment");
                int kuda = c.getColumnIndex("kuda");
                int purse = c.getColumnIndex("purse");

                komment_edit.setText(c.getString(komment));
                numberField.setText(c.getString(summa));
                kuda_intent = c.getInt(kuda);
                purse_intent = c.getInt(purse);
            }
            c.close();

        } else {

            kuda_intent = getIntent().getIntExtra("kuda", 0);
        }


        Button crea_dkr = (Button) findViewById(R.id.crea_dkr);
        //   Button crea_dkr_to = (Button) findViewById(R.id.crea_dkr_to);
        ImageButton crea_dkr_kal = (ImageButton) findViewById(R.id.crea_dkr_kal);


        crea_dkr.setOnClickListener(new View.OnClickListener() {
            public void onClick(View r) {
                setInitialDateTime();
                create_dkr();
            }
        });

        crea_dkr_kal.setOnClickListener(new View.OnClickListener() {
            public void onClick(View r) {
                new DatePickerDialog(new_dkr_crea.this, d,
                        dateAndTime.get(Calendar.YEAR),
                        dateAndTime.get(Calendar.MONTH),
                        dateAndTime.get(Calendar.DAY_OF_MONTH))
                        .show();
                // setInitialDateTime();

            }
        });


    }


    void create_dkr() {

        DB_sql dbHelper = new DB_sql(new_dkr_crea.this);
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = dbHelper.getWritableDatabase();


//внимание потом тут искать чтобы по календарю

// до сюда

        int purse = purse_intent;
        int kuda = kuda_intent;
        int summa = Integer.parseInt(numberField.getText().toString());
        String komment = komment_edit.getText().toString();
        String data_fakt = date;

        if (id != 0) {
            db.execSQL("UPDATE `an_dkr_hist` SET " +
                    "`kuda`=" + kuda + ", " +
                    "`purse`=" + purse + ", " +
                    "`data_fakt`='" + data_fakt + "', " +
                    "`summa`=" + summa + ", " +
                    "`komment`='" + komment + "' " +
                    "WHERE id=" + id);
        } else {
            db.execSQL("INSERT INTO `an_dkr_hist`" +
                    " ( `kuda`, `summa`, `komment`, `data_fakt`, `visible`, `purse`)" +
                    " VALUES" +
                    " ( " + kuda + ", " + summa + ", '" + komment + "', '" + data_fakt + "', 0, 1)");
        }


        finish();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tollbar_ok, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home) {
            onBackPressed();
        } else if (i == R.id.create_menu) {
            setInitialDateTime();//сегодня
            create_dkr();

        } else {
        }
        return true;
    }

    private void setInitialDateTime() {


        date = ft.format(dateAndTime.getTime());
    }


    // обработка нажатия на числовую кнопку
    public void onNumberClick(View view) {


        Button button = (Button) view;


        if (button.getText().equals(",")) {

            String gettext = numberField.getText().toString();

            int indop = gettext.indexOf(lastOperation);
            if (indop == -1) {
                int indzpt = gettext.indexOf(",");
                if (indzpt == -1) {

                    numberField.append(button.getText());
                }

            }else{
                String last = gettext.substring(indop + 1, gettext.length());
              //  Toast.makeText(this, ""+last, Toast.LENGTH_LONG).show();
                int indzpt = last.indexOf(",");
                if (indzpt == -1) {

                    numberField.append(button.getText());
                }
            }



        } else {


            numberField.append(button.getText());
        }



 /*
        if (lastOperation.equals("=") && operand != null) {
            operand = null;
        }
        */
    }

    // обработка нажатия на кнопку операции
    public void onOperationClick(View view) {

        Button button = (Button) view;//текущщяя кнопка
        String op = button.getText().toString();//текст тек кнопки

        String gettext = numberField.getText().toString().replace(',', '.');

        int indop = gettext.indexOf(lastOperation);

        if (indop != -1) {
            if (indop + 1 - gettext.length() != 0) {
                Double first = Double.valueOf(gettext.substring(0, indop));
                Double last = Double.valueOf(gettext.substring(indop + 1, gettext.length()));
                // Toast.makeText(this, indop+1 +" "+gettext.length() +"", Toast.LENGTH_LONG).show();
                switch (lastOperation) {
                    case "=":
                        first = last;
                        //op="";
                        break;
                    case "÷":
                        if (last == 0) {
                            first = 0.0;
                        } else {
                            first /= last;
                        }
                        break;
                    case "×":
                        first *= last;
                        break;
                    case "+":
                        first += last;
                        break;
                    case "-":
                        first -= last;
                        break;


                }
                if (op.equals("="))
                    numberField.setText(Double.toString(first).replace('.', ','));
                else
                    numberField.setText(Double.toString(first).replace('.', ',') + op);
            } else {
                if (op.equals("="))
                    numberField.setText(gettext.substring(0, indop).replace('.', ','));
                else
                    numberField.setText(gettext.substring(0, indop).replace('.', ',') + op);
            }
        } else {
            if (!op.equals("="))
                numberField.append(button.getText());

        }

        lastOperation = op;
        //  Toast.makeText(this, ""+indop, Toast.LENGTH_LONG).show();

        /*
        Button button = (Button) view;//текущщяя кнопка
        String op = button.getText().toString();//текст тек кнопки
        String number = numberField.getText().toString();//получить текст из поля для ввода
        // если введенно что-нибудь
        if (number.length() > 0) {//если цифр больше 0
            number = number.replace(',', '.');//заменим на точку на щзапятую
            try {
                performOperation(Double.valueOf(number), op);
            } catch (NumberFormatException ex) {
                numberField.setText("");
            }
        }
        lastOperation = op;//значение знака
    //    operationField.setText(lastOperation);//установим в текствиф
*/
    }


    private void performOperation(Double number, String operation) {

        // если операнд ранее не был установлен (при вводе самой первой операции)
        if (operand == null) {
            operand = number;
        } else {

            if (lastOperation.equals("=")) {
                lastOperation = operation;
            }

            switch (lastOperation) {
                case "=":
                    operand = number;
                    break;
                case "/":
                    if (number == 0) {
                        operand = 0.0;
                    } else {
                        operand /= number;
                    }
                    break;
                case "*":
                    operand *= number;
                    break;
                case "+":
                    operand += number;
                    break;
                case "-":
                    operand -= number;
                    break;


            }
        }
        //   resultField.setText(operand.toString().replace('.', ','));
        numberField.setText(operand.toString().replace('.', ','));

    }


    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            // setInitialDateTime();

            //  create_dkr();
        }
    };


}