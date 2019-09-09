package com.github.moviecard.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.github.moviecard.CardView;
import com.github.moviecard.Constants;
import com.github.moviecard.R;
import com.github.moviecard.bean.MovieCardBean;

/**
 * Created by logan676 on 2019-09-09.
 */
public class MovieCardView extends CardView {
    private ImageView mImageView;

    public MovieCardView(Context context) {
        this(context, null);
    }

    public MovieCardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MovieCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    public void init(Context context) {
        ImageView cardView = (ImageView) View.inflate(context, R.layout.movie_card_view, null);
        mImageView = cardView;
        addView(cardView);
    }

    public void onBindView(Context context, MovieCardBean bean, int index) {
        int res = Constants.PICS[index % 14];
        mImageView.setImageResource(res);
    }
}
