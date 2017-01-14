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
    protected String barcode;   // バーコード
    protected String product;    // 商品名
    protected String disposal;    // 廃棄日

    /**
     * MyListItem()
     *
     * @param id      int ID
     * @param barcode String バーコード
     * @param product  String 商品名
     * @param disposal  String 廃棄日
     */
    public MyListItem(int id, String barcode, String product, String disposal) {
        this.id = id;
        this.barcode = barcode;
        this.product = product;
        this.disposal = disposal;
    }
    public MyListItem(String barcode,String product) {
        this.barcode = barcode;
        this.product = product;
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
     * バーコードを取得
     * getBarcode()
     *
     * @return barcode String バーコード
     */
    public String getBarcode() {
        return barcode;
    }

    /**
     * 商品名を取得
     * getProduct()
     *
     * @return product String 商品名
     */
    public String getProduct() {
        return product;
    }

    /**
     * 廃棄日を取得
     * getDisposal()
     *
     * @return disposal String 廃棄日
     */
    public String getDisposal() {
        return disposal;
    }

}