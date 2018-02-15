package hu.mobwebhf.gitarakkordok.Activities;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import hu.mobwebhf.gitarakkordok.Adapter.SongAdapter;
import hu.mobwebhf.gitarakkordok.Model.SongItem;
import hu.mobwebhf.gitarakkordok.R;

public class FavoriteActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SongAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle(getString(R.string.favorites));
        initRecyclerView();
    }

    private void initRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.FavoriteRecycleView);
        adapter = new SongAdapter(getBaseContext());
        loadItemsInBackground();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }


    public void loadItemsInBackground() {
        new AsyncTask<Void, Void, List<SongItem>>() {
            @Override
            protected List<SongItem> doInBackground(Void... voids) {
                List<SongItem> list = SongItem.listAll(SongItem.class);
                List<SongItem> favoriteList = new ArrayList<SongItem>();
                for (SongItem song : list) {
                    if (song.isFavorite)
                        favoriteList.add(song);
                }
                return favoriteList;
            }
            @Override
            protected void onPostExecute(List<SongItem> songItems) {
                super.onPostExecute(songItems);
                adapter.updateSongItemList(songItems);
                TextView noFavorite = (TextView) findViewById(R.id.NoFavorite);
                if (adapter.getSongList().size() == 0) {
                    noFavorite.setVisibility(View.VISIBLE);
                    noFavorite.setText(R.string.no_favorite);
                }
                else {
                    noFavorite.setVisibility(View.INVISIBLE);
                    noFavorite.setText("");
                }
            }
        }.execute();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
