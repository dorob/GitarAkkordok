package hu.mobwebhf.gitarakkordok.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import hu.mobwebhf.gitarakkordok.Adapter.SongAdapter;
import hu.mobwebhf.gitarakkordok.Fragments.UpdateDialogFragment;
import hu.mobwebhf.gitarakkordok.Model.SongItem;
import hu.mobwebhf.gitarakkordok.Network.NetworkManager;
import hu.mobwebhf.gitarakkordok.R;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, DialogInterface.OnDismissListener {
    private RecyclerView recyclerView;
    private SongAdapter adapter;
    boolean isThereUpdate;
    boolean first = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        checkUpdateInBackground();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        initRecyclerView();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals(""))
                    loadItemsInBackground();
                else
                    updateFilteredSongs(newText);
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                loadItemsInBackground();
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sort_name) {
            item.setChecked(true);
            sortByName();
        }
        else if (id == R.id.sort_time){
            item.setChecked(true);
            sortByTime();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_favorite) {
            Intent intent = new Intent(MainActivity.this, FavoriteActivity.class);
            startActivityForResult(intent,777);
        }
        else if (id == R.id.nav_send) {
            try {
                if (NetworkManager.isConnected()) {
                    Intent intent = new Intent(MainActivity.this, SendActivity.class);
                    startActivity(intent);
                }
                else
                    Snackbar.make(findViewById(R.id.ContentID), getString(R.string.no_connection), Snackbar.LENGTH_LONG).show();
            } catch (InterruptedException | IOException e) {
                Snackbar.make(findViewById(R.id.ContentID), getString(R.string.no_connection), Snackbar.LENGTH_LONG).show();
            }
        }
        else if (id == R.id.nav_update) {
            handleNetwork();
            isThereUpdate = false;
        }
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    private void handleNetwork() {
        checkUpdateInBackground();
        if (isThereUpdate && first) {
            isThereUpdate = false;
            UpdateDialogFragment dialogFragment = new UpdateDialogFragment();
            dialogFragment.show(getSupportFragmentManager(), getString(R.string.update_dialog));
        }
        first = false;
    }

    private void checkUpdateInBackground() {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPostExecute(Boolean update) {
                if (update)
                    isThereUpdate = true;
                else
                    isThereUpdate = false;
            }

            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    if (NetworkManager.isConnected()) {
                        if (NetworkManager.isThereUpdate(getSongList())) {
                            Snackbar.make(findViewById(R.id.ContentID), getString(R.string.new_songs_available), Snackbar.LENGTH_LONG).show();
                            return true;
                        } else {
                            Snackbar.make(findViewById(R.id.ContentID), getString(R.string.no_update), Snackbar.LENGTH_LONG).show();
                            return false;
                        }
                    }
                    else {
                        Snackbar.make(findViewById(R.id.ContentID), getString(R.string.no_connection), Snackbar.LENGTH_LONG).show();
                    }
                } catch (InterruptedException | IOException e) {
                    Snackbar.make(findViewById(R.id.ContentID), getString(R.string.no_connection), Snackbar.LENGTH_LONG).show();
                }
                return false;
            }
        }.execute();
    }

    public void sortByName() {
        List<SongItem> originalSongs = SongItem.listAll(SongItem.class);
        for(int i=0;i<originalSongs.size();i++) {
            SongItem bestMatch = originalSongs.get(0);
            for(int j=0; j<originalSongs.size()-i;j++) {
                SongItem current = originalSongs.get(j);
                if (current.name.compareTo(bestMatch.name) < 0)
                    bestMatch = current;
            }
            originalSongs.remove(bestMatch);
            originalSongs.add(bestMatch);
        }
        adapter.updateSongItemList(originalSongs);
    }

    private long subtractDates(Date bestMatch, Date current) {
        return bestMatch.getTime() - current.getTime();
    }

    private void exchangeSongs(List<SongItem> originalSongs, SongItem older, SongItem newer) {
        int olderIndex = originalSongs.indexOf(older);
        int newerIndex = originalSongs.indexOf(newer);
        originalSongs.set(olderIndex,newer);
        originalSongs.set(newerIndex,older);
    }

    private SongItem getRecentDate (SongItem bestMatch, SongItem currentSong) {
        Date bestDate, currentDate;
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-mm-dd");
        try {
            bestDate = parser.parse(bestMatch.datum);
            currentDate = parser.parse(currentSong.datum);
            if (subtractDates(bestDate,currentDate) < 0)
                return currentSong;
            else
                return bestMatch;
        } catch (ParseException e) {
            return bestMatch;
        }
    }

    public void sortByTime() {
        List<SongItem> originalSongs = SongItem.listAll(SongItem.class);
        for(int i=0;i<originalSongs.size()-1;i++) {
            SongItem bestMatch = originalSongs.get(0);
            for(int j=i; j<originalSongs.size();j++) {
                SongItem currentSong = originalSongs.get(j);
                bestMatch = getRecentDate(bestMatch,currentSong);
            }
            if (bestMatch != originalSongs.get(i)) {
                SongItem older = originalSongs.get(i);
                exchangeSongs(originalSongs,older,bestMatch);
            }
        }
        adapter.updateSongItemList(originalSongs);
    }

    private void initRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.SongRecycleView);
        adapter = new SongAdapter(getBaseContext());
        loadItemsInBackground();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }


    public void loadItemsInBackground() {
        new AsyncTask<Void, Void, List<SongItem>>() {
            @Override
            protected List<SongItem> doInBackground(Void... voids) {
                return SongItem.listAll(SongItem.class);
            }
            @Override
            protected void onPostExecute(List<SongItem> songItems) {
                super.onPostExecute(songItems);
                adapter.updateSongItemList(songItems);
            }
        }.execute();
    }

    public List<SongItem> getSongList() {
        return adapter.getSongList();
    }

    public void updateFilteredSongs(String newText) {
        List<SongItem> filteredSongs = adapter.getFilteredSongs(newText);
        adapter.updateSongItemList(filteredSongs);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 777)
            loadItemsInBackground();
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        loadItemsInBackground();
    }
}
