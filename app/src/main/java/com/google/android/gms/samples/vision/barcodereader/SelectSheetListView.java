package com.google.android.gms.samples.vision.barcodereader;

/**
 * Created by ttyady on 2016/12/27.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * ListView表示画面に関連するクラス
 * SelectSheetListView
 */
public class SelectSheetListView extends Activity {

    private DBAdapter dbAdapter;
    private MyBaseAdapter myBaseAdapter;
    private List<MyListItem> items;
    private ListView mListView03;
    protected MyListItem myListItem;

    // 参照するDBのカラム：ID,バーコード,商品名,廃棄日の全部なのでnullを指定
    private String[] columns = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_sheet_listview);

        // DBAdapterのコンストラクタ呼び出し
        dbAdapter = new DBAdapter(this);

        // itemsのArrayList生成
        items = new ArrayList<>();

        // MyBaseAdapterのコンストラクタ呼び出し(myBaseAdapterのオブジェクト生成)
        myBaseAdapter = new MyBaseAdapter(this, items);

        // ListViewの結び付け
        mListView03 = (ListView) findViewById(R.id.listView);

        loadMyList();   // DBを読み込む＆更新する処理

        // 行を長押しした時の処理
        mListView03.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                // アラートダイアログ表示
                AlertDialog.Builder builder = new AlertDialog.Builder(SelectSheetListView.this);
                builder.setTitle("削除");
                builder.setMessage("削除しますか？");
                // OKの時の処理
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // IDを取得する
                        myListItem = items.get(position);
                        int listId = myListItem.getId();

                        dbAdapter.openDB();     // DBの読み込み(読み書きの方)
                        dbAdapter.selectDelete(String.valueOf(listId));     // DBから取得したIDが入っているデータを削除する
                        Log.d("Long click : ", String.valueOf(listId));
                        dbAdapter.closeDB();    // DBを閉じる
                        loadMyList();
                    }
                });

                builder.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                // ダイアログの表示
                AlertDialog dialog = builder.create();
                dialog.show();

                return false;
            }
        });
    }

    /**
     * DBを読み込む＆更新する処理
     * loadMyList()
     */
    private void loadMyList() {

        //ArrayAdapterに対してListViewのリスト(items)の更新
        items.clear();

        dbAdapter.openDB();     // DBの読み込み(読み書きの方)

        // DBのデータを取得
        Cursor c = dbAdapter.getDB(columns);

        if (c.moveToFirst()) {
            do {
                // MyListItemのコンストラクタ呼び出し(myListItemのオブジェクト生成)
                myListItem = new MyListItem(
                        c.getInt(0),
                        c.getString(1),
                        c.getString(2),
                        c.getString(3));

                Log.d("取得したCursor(ID):", String.valueOf(c.getInt(0)));
                Log.d("取得したCursor(バーコード):", c.getString(1));
                Log.d("取得したCursor(商品名):", c.getString(2));
                Log.d("取得したCursor(廃棄日):", c.getString(3));

                items.add(myListItem);          // 取得した要素をitemsに追加

            } while (c.moveToNext());
        }
        c.close();
        dbAdapter.closeDB();                    // DBを閉じる
        mListView03.setAdapter(myBaseAdapter);  // ListViewにmyBaseAdapterをセット
        myBaseAdapter.notifyDataSetChanged();   // Viewの更新

    }

    /**
     * BaseAdapterを継承したクラス
     * MyBaseAdapter
     */
    public class MyBaseAdapter extends BaseAdapter {

        private Context context;
        private List<MyListItem> items;

        // 毎回findViewByIdをする事なく、高速化が出来るようするholderクラス
        private class ViewHolder {
            TextView textBarcode;
            TextView textProduct;
            TextView textDisposal;
        }

        // コンストラクタの生成
        public MyBaseAdapter(Context context, List<MyListItem> items) {
            this.context = context;
            this.items = items;
        }

        // Listの要素数を返す
        @Override
        public int getCount() {
            return items.size();
        }

        // indexやオブジェクトを返す
        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        // IDを他のindexに返す
        @Override
        public long getItemId(int position) {
            return position;
        }

        // 新しいデータが表示されるタイミングで呼び出される
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = convertView;
            ViewHolder holder;

            // データを取得
            myListItem = items.get(position);


            if (view == null) {
                LayoutInflater inflater =
                        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.row_sheet_listview, parent, false);

                TextView textBarcode = (TextView) view.findViewById(R.id.textBarcode);      // バーコードのTextView
                TextView textProduct = (TextView) view.findViewById(R.id.textProduct);        // 商品名のTextView
                TextView textDisposal = (TextView) view.findViewById(R.id.textDisposal);        // 廃棄日のTextView

                // holderにviewを持たせておく
                holder = new ViewHolder();
                holder.textBarcode = textBarcode;
                holder.textProduct = textProduct;
                holder.textDisposal = textDisposal;
                view.setTag(holder);

            } else {
                // 初めて表示されるときにつけておいたtagを元にviewを取得する
                holder = (ViewHolder) view.getTag();
            }

            // 取得した各データを各TextViewにセット
            holder.textBarcode.setText(myListItem.getBarcode());
            holder.textProduct.setText(myListItem.getProduct());
            holder.textDisposal.setText(myListItem.getDisposal());

            return view;

        }
    }
}