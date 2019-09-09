package com.github.moviecard;

/**
 * Created by logan676 on 2019-08-02.
 */
public abstract class CardBean {

    public int index;

    public String picUrl;

    public String text;

    public CardBean(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "CardBean{" +
                "index=" + index +
                ", picUrl='" + picUrl + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
