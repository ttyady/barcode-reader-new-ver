package com.google.android.gms.samples.vision.barcodereader;

/**
 * Created by ttyady on 2016/12/27.
 */

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.Calendar;

public class DataRegistration extends Activity implements
        RadioGroup.OnCheckedChangeListener {


    private EditText mEditText01Product;        // 品名
    private EditText mEditText01MadeIn;         // 産地
    private EditText mEditText01Number;         // 個数

    private TextView mText01Kome01;             // 品名の※印
    private TextView mText01Kome02;             // 産地の※印
    private TextView mText01Kome03;             // 個数の※印

    private Button mButton01Regist;             // 登録ボタン
    private Button mButton01Show;               // 表示ボタン
    private Button mButtonReadBarcode;         // バーコード読み込みボタン

    private RadioGroup mRadioGroup01Show;       // 選択用ラジオボタングループ

    private CompoundButton autoFocus;
    private CompoundButton useFlash;

    private static final int RC_BARCODE_CAPTURE = 9001;

    private Intent intent;                      // インテント

    private int year;
    private int month;
    private int day;
    private DatePickerDialog.OnDateSetListener varDateSetListener;

    private static final int DATE_DIALOG_ID = 0;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.data_registration);
        findViews();        // 各部品の結びつけ処理

        init();             //初期値設定

        varDateSetListener = new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view , int year , int monthOfYear , int dayOfMonth){
                mEditText01Number.setText(year + "/" + (monthOfYear + 1) + "/" + dayOfMonth);
            }
        };

        // ラジオボタン選択時
        mRadioGroup01Show.setOnCheckedChangeListener(this);

        // 登録ボタン押下時処理
        mButton01Regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // キーボードを非表示
                InputMethodManager inputMethodManager =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

                // DBに登録
                saveList();


            }

        });

        // 表示ボタン押下時処理
        mButton01Show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intent != null) {
                    startActivity(intent);      // 各画面へ遷移
                } else {
                    Toast.makeText(DataRegistration.this, "ラジオボタンが選択されていません。", Toast.LENGTH_SHORT).show();
                }

            }
        });

        // バーコード読み込みボタン押下時処理
        mButtonReadBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // launch barcode activity.
                Intent intent = new Intent(DataRegistration.this, BarcodeCaptureActivity.class);
                intent.putExtra(BarcodeCaptureActivity.AutoFocus, autoFocus.isChecked());
                intent.putExtra(BarcodeCaptureActivity.UseFlash, useFlash.isChecked());

                startActivityForResult(intent, RC_BARCODE_CAPTURE);
            }
        });

        mEditText01Number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog dateDialog = new DatePickerDialog(
                        DataRegistration.this,
                        varDateSetListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                );
                dateDialog.show();
            }
        });
    }



    /**
     * 各部品の結びつけ処理
     * findViews()
     */
    private void findViews() {

        mEditText01Product = (EditText) findViewById(R.id.editText01Product);   // 品名
        Intent intent = getIntent();
        String barcodedata = intent.getStringExtra("barcodeValue");
        mEditText01Product.setText(barcodedata);
        mEditText01MadeIn = (EditText) findViewById(R.id.editText01MadeIn);     // 産地
        mEditText01Number = (EditText) findViewById(R.id.editText01Number);     // 個数

        mText01Kome01 = (TextView) findViewById(R.id.text01Kome01);             // 品名の※印
        mText01Kome02 = (TextView) findViewById(R.id.text01Kome02);             // 産地※印
        mText01Kome03 = (TextView) findViewById(R.id.text01Kome03);             // 個数の※印


        mButton01Regist = (Button) findViewById(R.id.button01Regist);           // 登録ボタン
        mButton01Show = (Button) findViewById(R.id.button01Show);               // 表示ボタン
        mButtonReadBarcode = (Button) findViewById(R.id.re_read_barcode);        // バーコード読み取りボタン

        mRadioGroup01Show = (RadioGroup) findViewById(R.id.radioGroup01);       // 選択用ラジオボタングループ

        autoFocus = (CompoundButton) findViewById(R.id.re_auto_focus);
        useFlash = (CompoundButton) findViewById(R.id.re_use_flash);


    }

    /**
     * 初期値設定 (EditTextの入力欄は空白、※印は消す)
     * init()
     */
    private void init() {
        mEditText01MadeIn.setText("");
        mEditText01Number.setText("");

        mText01Kome01.setText("");
        mText01Kome02.setText("");
        mText01Kome03.setText("");
        mEditText01Product.requestFocus();      // フォーカスを品名のEditTextに指定
    }

    /**
     * ラジオボタン選択処理
     * onCheckedChanged()
     */
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.radioButton01Product:         // 品名一覧(ListView×ArrayAdapter)を選択した場合
                intent = new Intent(DataRegistration.this, SelectSheetProduct.class);
                break;
            case R.id.radioButton01ListView:        // ListView表示を選択した場合
                intent = new Intent(DataRegistration.this, SelectSheetListView.class);
                break;
            case R.id.radioButton01TableLayout:     // TableLayout表示を選択した場合
                intent = new Intent(DataRegistration.this, SelectSheetTable.class);
                break;
        }
    }

    /**
     * EditTextに入力したテキストをDBに登録
     * saveDB()
     */
    private void saveList() {

        // 各EditTextで入力されたテキストを取得
        String strProduct = mEditText01Product.getText().toString();
        String strMadeIn = mEditText01MadeIn.getText().toString();
        String strNumber = mEditText01Number.getText().toString();

        // EditTextが空白の場合
        if (strProduct.equals("") || strMadeIn.equals("") || strNumber.equals("")) {

            if (strProduct.equals("")) {
                mText01Kome01.setText("※");     // 品名が空白の場合、※印を表示
            } else {
                mText01Kome01.setText("");      // 空白でない場合は※印を消す
            }

            if (strMadeIn.equals("")) {
                mText01Kome02.setText("※");     // 産地が空白の場合、※印を表示
            } else {
                mText01Kome02.setText("");      // 空白でない場合は※印を消す
            }

            if (strNumber.equals("")) {
                mText01Kome03.setText("※");     // 個数が空白の場合、※印を表示
            } else {
                mText01Kome03.setText("");      // 空白でない場合は※印を消す
            }


            Toast.makeText(DataRegistration.this, "※の箇所を入力して下さい。", Toast.LENGTH_SHORT).show();

        } else {        // EditTextが全て入力されている場合

            // 入力されたバーコードは文字列からLong型へ変換
            Long iProduct = Long.parseLong(strProduct);
            //int iNumber = Integer.parseInt(strNumber);

            // DBへの登録処理
            DBAdapter dbAdapter = new DBAdapter(this);
            dbAdapter.openDB();                                         // DBの読み書き

            //dbAdapter.saveDB(strProduct, strMadeIn, iNumber);   // DBに登録
            dbAdapter.saveDB(iProduct, strMadeIn, strNumber);   // DBに登録
            dbAdapter.closeDB();                                        // DBを閉じる

            init();     // 初期値設定
            Toast.makeText(DataRegistration.this,"DB登録完了",Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);

                    mEditText01Product.setText(barcode.displayValue);
                }
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
/*
    // メニュー未使用
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // メニュー未使用
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

}