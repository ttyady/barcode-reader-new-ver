package com.google.android.gms.samples.vision.barcodereader;

/**
 * Created by ttyady on 2016/12/27.
 */

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * 品名一覧 (ListView×ArrayAdapter)画面に関連するクラス
 * SelectSheetProduct
 */
public class SelectSheetProduct extends AppCompatActivity {

    private DBAdapter dbAdapter;                // DBAdapter
    private ArrayAdapter<String> adapter;       // ArrayAdapter
    private ArrayList<String> items;            // ArrayList

    private ListView mListView02Product;        // ListView
    private Button mButton02AllDelete;          // 全削除ボタン

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_sheet_product);
        setTitle(R.string.name_selectsheet_product);    // タイトルバーのタイトルをセット

        dbAdapter = new DBAdapter(this);
        dbAdapter.openDB();     // DBの読み込み(読み書きの方)

        findViews();            // 各部品の結び付け

        // ArrayListを生成
        items = new ArrayList<>();

        // DBのデータを取得
        String[] columns = {DBAdapter.COL_PRODUCT};     // DBのカラム：品名
        Cursor c = dbAdapter.getDB(columns);

        if (c.moveToFirst()) {
            do {
                items.add(c.getString(0));
                Log.d("取得したCursor:", c.getString(0));
            } while (c.moveToNext());
        }
        c.close();
        dbAdapter.closeDB();    // DBを閉じる

        // ArrayAdapterのコンストラクタ
        // 第1引数：Context
        // 第2引数：リソースとして登録されたTextViewに対するリソースID 今回は元々用意されている定義済みのレイアウトファイルのID
        // 第3引数：一覧させたいデータの配列
        adapter = new ArrayAdapter<>
                (this, android.R.layout.simple_list_item_1, items);

        mListView02Product.setAdapter(adapter);     //ListViewにアダプターをセット(=表示)

        // ArrayAdapterに対してListViewのリスト(items)の更新
        adapter.notifyDataSetChanged();

        // 全削除ボタン押下時処理
        mButton02AllDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!items.isEmpty()) {
                    dbAdapter.openDB();     // DBの読み込み(読み書きの方)
                    dbAdapter.allDelete();  // DBのレコードを全削除
                    dbAdapter.closeDB();    // DBを閉じる

                    //ArrayAdapterに対してListViewのリスト(items)の更新
                    adapter.clear();
                    adapter.addAll(items);
                    adapter.notifyDataSetChanged(); // // Viewの更新

                }else {
                    Toast.makeText(SelectSheetProduct.this, "登録されているデータがありません。", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    /**
     * 各部品の結びつけ処理
     * findViews()
     */
    private void findViews() {
        mListView02Product = (ListView) findViewById(R.id.listView02Product);       // 品名一覧用のListView
        mButton02AllDelete = (Button) findViewById(R.id.button02AllDelete);         // 全削除ボタン
    }

}