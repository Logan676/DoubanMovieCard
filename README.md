# DoubanMovieCard

[![](https://jitpack.io/v/Logan676/DoubanMovieCard.svg)](https://jitpack.io/#Logan676/DoubanMovieCard)

自定义ViewGroup方式实现豆瓣电影推荐卡片层叠效果。内部支持view复用机制，原理同listview；adapter使用同listview。

# 效果

<img src="https://github.com/Logan676/DoubanMovieCard/blob/master/device-2019-09-09-151925.png" width=700 />
          
![gif1](https://github.com/Logan676/DoubanMovieCard/blob/master/movie_card.gif)

# 依赖

```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

	
```
dependencies {
	implementation 'com.github.Logan676:DoubanMovieCard:V1.0.0'
}
```

# 使用示例

```
 <com.github.moviecard.MovieCardLayout
        android:id="@+id/movie_card"
        android:layout_width="match_parent"
        android:layout_height="200dp"
        android:layout_marginTop="20dp"
        app:cardAlpha="0.95"
        app:cardWidth="105dp"
        app:cardHeight="149dp"
        app:cardScale="1.2"
        app:childrenContentMarginLeft="30dp"
        app:itemDividerWidth="1dp"
        app:itemMarginLeft="10dp"
        app:itemMarginRight="10dp" />
        
```
1. cardWidth，cardHeight设置卡片宽高；
2. cardScale设置卡片最大缩放比率；
3. itemDividerWidth设置卡片分割间隔大小；
4. itemMarginLeft左间距；itemMarginRight右间距等

# 外部访问的接口

   ``` 
    public void setChildrenContentMarginLeft(int left)

    public void setItemDividerWidth(int itemDividerWidth) 

    public void setItemMarginLeft(int itemMarginLeft)

    public void setItemMarginTop(int itemMarginTop) 

    public void setItemMarginBottom(int itemMarginBottom)

    public void setItemMarginRight(int itemMarginRight) 

    private void setChildCardViewWidth(int cardChildViewWidth) 

    private void setChildCardViewHeight(int cardChildViewHeight)

    public void setCardViewWidth(int cardViewWidth) 

    public void setCardViewHeight(int cardViewHeight)

    public void setSaleRatio(float scale) 
  
    public void setAlpha(float alpha) 
```
