package com.google.douban.moviecard;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import static com.google.douban.moviecard.Constants.CARD_TAG;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        CollapseCardLayout cardLayout = findViewById(R.id.collapse_card);
        CollapseCardAdapter cardAdapter = new CollapseCardAdapter(this);
        cardLayout.setAdapter(cardAdapter);
        cardLayout.setOnPageChangeListener(new CollapseCardLayout.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                Log.d(CARD_TAG, "onPageSelected position=" + position);
            }
        });

        ListView listView = findViewById(R.id.list_view);
        CollapseCardAdapter cardAdapter2 = new CollapseCardAdapter(this);
        listView.setAdapter(cardAdapter2);
        cardAdapter2.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
