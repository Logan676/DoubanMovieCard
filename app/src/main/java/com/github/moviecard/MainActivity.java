package com.github.moviecard;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.github.moviecard.bean.MovieCardBean;
import com.github.moviecard.view.MovieCardView;

import java.util.ArrayList;
import java.util.List;

import static com.github.moviecard.Constants.FAKE_DATA_COUNT;
import static com.github.moviecard.MovieCardLayout.CARD_TAG;

/**
 * Created by logan676 on 2019-09-09.
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MovieCardLayout cardLayout = findViewById(R.id.movie_card);
        BaseMovieCardAdapter cardAdapter = new MovieCardAdapter(this);
        cardLayout.setAdapter(cardAdapter);
        cardLayout.setOnPageChangeListener(new MovieCardLayout.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                Log.d(CARD_TAG, "onPageSelected position=" + position);
            }
        });

    }

    private class MovieCardAdapter extends BaseMovieCardAdapter {

        private List<MovieCardBean> mCardBeans = new ArrayList<>();

        private Context mContext;

        MovieCardAdapter(Context context) {
            mContext = context;
            for (int i = 0; i < FAKE_DATA_COUNT; i++) {
                MovieCardBean cardBean = new MovieCardBean(i);
                mCardBeans.add(cardBean);
            }
        }

        public int getCount() {
            return mCardBeans.size();
        }

        @Override
        public Object getItem(int position) {
            return mCardBeans.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public CardView getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                view = new MovieCardView(mContext);
            } else {
                Log.d(CARD_TAG, "getView reuse " + position);
                view = convertView;
            }


            if (position >= mCardBeans.size()) {
                return null;
            }
            MovieCardBean bean = mCardBeans.get(position);
            ((MovieCardView) view).onBindView(mContext, bean, position);
            return (MovieCardView) view;
        }
    }
}
