package com.google.douban.moviecard;

/**
 * Created by logan676 on 2019-08-02.
 */
public class Constants {
    public static final String CARD_TAG = "cCard";

    public static final int FAKE_DATA_COUNT = 50;

    public static String[] PICS = new String[FAKE_DATA_COUNT];

    static {
        for (int i = 0; i < FAKE_DATA_COUNT; i++) {
            PICS[i] = "https://img3.doubanio.com/view/photo/l/public/p2554569861.webp";
        }
    }

}
