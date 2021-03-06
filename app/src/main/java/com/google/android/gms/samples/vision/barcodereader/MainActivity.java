package com.google.android.gms.samples.vision.barcodereader;

/**
 * Created by ttyady on 2016/12/27.
 */

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import java.text.DecimalFormat;
import java.util.Calendar;

//public class DataRegistration extends Activity {
public class MainActivity extends AppCompatActivity {

    private EditText mEditTextBarcode;     // バーコード
    private EditText mEditTextProduct;         // 商品名
    private EditText mEditTextDisposal;         // 廃棄日

    private TextView mText01Kome01;             // バーコードの※印
    private TextView mText01Kome02;             // 商品名の※印
    private TextView mText01Kome03;             // 廃棄日の※印

    private Button mButton01Regist;             // 登録ボタン
    private Button mButton01Show;               // 表示ボタン
    private Button mButtonReadBarcode;         // バーコード読み込みボタン


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
        intent = getIntent();
        String barcodedata = intent.getStringExtra("barcodeValue");

        findViews();        // 各部品の結びつけ処理

        init(barcodedata);             //初期値設定

        varDateSetListener = new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view , int year , int monthOfYear , int dayOfMonth){

                DecimalFormat format_m = new DecimalFormat("00");
                DecimalFormat format_d = new DecimalFormat("00");



                mEditTextDisposal.setText(year + "/" + (format_m.format(monthOfYear + 1)) + "/" + format_d.format(dayOfMonth));
            }
        };

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
                intent = new Intent(MainActivity.this, SelectSheetListView.class);
                startActivity(intent);      // 各画面へ遷移
            }
        });

        // バーコード読み込みボタン押下時処理
        mButtonReadBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // launch barcode activity.
                Intent intent = new Intent(MainActivity.this, BarcodeCaptureActivity.class);
                intent.putExtra(BarcodeCaptureActivity.AutoFocus, autoFocus.isChecked());
                intent.putExtra(BarcodeCaptureActivity.UseFlash, useFlash.isChecked());

                startActivityForResult(intent, RC_BARCODE_CAPTURE);
            }
        });

        mEditTextDisposal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog dateDialog = new DatePickerDialog(
                        MainActivity.this,
                        varDateSetListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                );
                dateDialog.show();
            }
        });

        mEditTextBarcode.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v,boolean hasFocus){
                if(hasFocus == false) { //focusが外れたら
                    String strBarcode = mEditTextBarcode.getText().toString();
                    DBAdapter dbAdapter = new DBAdapter(MainActivity.this);
                    dbAdapter.openDB();                                         // DBの読み書き
                    String[] name = {strBarcode};
                    Cursor c = dbAdapter.searchDB2(null,"barcode",name);
                    if (c.moveToFirst()) {
                        do {
                            Toast.makeText(MainActivity.this, "商品発見:"+c.getString(1), Toast.LENGTH_SHORT).show();
                            mEditTextProduct.setText(c.getString(1));
                        } while (c.moveToNext());
                    }else{
                        //バーコードが見つからなかった場合
                        Toast.makeText(MainActivity.this, "商品名が登録されていません", Toast.LENGTH_SHORT).show();
                        mEditTextProduct.setText("");

                    }
                }
            }
        });
    }



    /**
     * 各部品の結びつけ処理
     * findViews()
     */
    private void findViews() {

        mEditTextBarcode = (EditText) findViewById(R.id.editTextBarcode);   // バーコード
/*        Intent intent = getIntent();
        String barcodedata = intent.getStringExtra("barcodeValue");
        mEditTextBarcode.setText(barcodedata);*/
        mEditTextProduct = (EditText) findViewById(R.id.editTextProduct);     // 商品名
        mEditTextDisposal = (EditText) findViewById(R.id.editTextDisposal);     // 廃棄日

        mText01Kome01 = (TextView) findViewById(R.id.text01Kome01);             // バーコードの※印
        mText01Kome02 = (TextView) findViewById(R.id.text01Kome02);             // 商品名※印
        mText01Kome03 = (TextView) findViewById(R.id.text01Kome03);             // 廃棄日の※印


        mButton01Regist = (Button) findViewById(R.id.buttonRegist);           // 登録ボタン
        mButton01Show = (Button) findViewById(R.id.buttonShow);               // 表示ボタン
        mButtonReadBarcode = (Button) findViewById(R.id.read_barcode);        // バーコード読み取りボタン

        autoFocus = (CompoundButton) findViewById(R.id.auto_focus);
        useFlash = (CompoundButton) findViewById(R.id.use_flash);

    }

    /**
     * 初期値設定 (EditTextの入力欄は空白、※印は消す)
     * init()
     */
    private void init(String barcodedata) {
        mEditTextBarcode.setText(barcodedata);
        mEditTextProduct.setText("");
        mEditTextDisposal.setText("");

        mText01Kome01.setText("");
        mText01Kome02.setText("");
        mText01Kome03.setText("");
        mEditTextBarcode.requestFocus();      // フォーカスを品名のEditTextに指定
    }

    /**
     * 初期値設定 (EditTextの入力欄は空白、※印は消す)
     * init()
     */
    private void init() {
        mEditTextBarcode.setText("");
        mEditTextProduct.setText("");
        mEditTextDisposal.setText("");

        mText01Kome01.setText("");
        mText01Kome02.setText("");
        mText01Kome03.setText("");
        mEditTextBarcode.requestFocus();      // フォーカスを品名のEditTextに指定

    }

    private void saveList() {

        // 各EditTextで入力されたテキストを取得
        String strBarcode = mEditTextBarcode.getText().toString();
        String strProduct = mEditTextProduct.getText().toString();
        String strDisposal = mEditTextDisposal.getText().toString();


        // EditTextが空白の場合
        if (strBarcode.equals("") || strProduct.equals("") || strDisposal.equals("")) {

            if (strBarcode.equals("")) {
                mText01Kome01.setText("※");     // 品名が空白の場合、※印を表示
            } else {
                mText01Kome01.setText("");      // 空白でない場合は※印を消す
            }

            if (strProduct.equals("")) {
                mText01Kome02.setText("※");     // 産地が空白の場合、※印を表示
            } else {
                mText01Kome02.setText("");      // 空白でない場合は※印を消す
            }

            if (strDisposal.equals("")) {
                mText01Kome03.setText("※");     // 個数が空白の場合、※印を表示
            } else {
                mText01Kome03.setText("");      // 空白でない場合は※印を消す
            }


            Toast.makeText(MainActivity.this, "※の箇所を入力して下さい。", Toast.LENGTH_SHORT).show();

        } else {        // EditTextが全て入力されている場合
            DBAdapter dbAdapter = new DBAdapter(this);
            dbAdapter.openDB();                                         // DBの読み書き

            // 入力されたバーコードは文字列からLong型へ変換
            Long iBarcode = Long.parseLong(strBarcode);
            //int iNumber = Integer.parseInt(strNumber);

            dbAdapter.saveDB(iBarcode,strDisposal);   // DBに登録
            dbAdapter.saveDB2(iBarcode,strProduct);   // DBに登録


            init();     // 初期値設定
            Toast.makeText(MainActivity.this, "DB登録完了", Toast.LENGTH_SHORT).show();
            dbAdapter.closeDB();

        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);

                    mEditTextBarcode.setText(barcode.displayValue);
                }
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}