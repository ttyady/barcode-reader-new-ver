package com.google.android.gms.samples.vision.barcodereader;

/**
 * Created by ttyady on 2016/12/27.
 */

import android.util.Log;

/**
 * SelectSheetListViewに必要なデータを取得するクラス
 * MyListItem
 */
public class MyListItem {
    protected int id;           // ID
    protected String product;   // 品名
    protected String madeIn;    // 産地
    protected String number;    // 個数

    /**
     * MyListItem()
     *
     * @param id      int ID
     * @param product String 品名
     * @param madeIn  String 産地
     * @param number  String 個数
     */
    public MyListItem(int id, String product, String madeIn, String number) {
        this.id = id;
        this.product = product;
        this.madeIn = madeIn;
        this.number = number;
    }

    /**
     * IDを取得
     * getId()
     *
     * @return id int ID
     */
    public int getId() {
        Log.d("取得したID：", String.valueOf(id));
        return id;
    }

    /**
     * 品名を取得
     * getProduct()
     *
     * @return product String 品名
     */
    public String getProduct() {
        return product;
    }

    /**
     * 産地を取得
     * getMadeIn()
     *
     * @return madeIn String 産地
     */
    public String getMadeIn() {
        return madeIn;
    }

    /**
     * 個数を取得
     * getNumber()
     *
     * @return number String 個数
     */
    public String getNumber() {
        return number;
    }

}