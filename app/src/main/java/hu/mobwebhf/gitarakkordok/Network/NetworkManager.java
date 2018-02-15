package hu.mobwebhf.gitarakkordok.Network;

import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.util.List;
import hu.mobwebhf.gitarakkordok.Model.SongItem;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Benjamin on 2017. 10. 25..
 */

public final class NetworkManager {

    private NetworkManager() {
    }

    public static boolean isConnected() throws InterruptedException, IOException
    {
        String command = "ping -c 1 81.2.249.30";
        return (Runtime.getRuntime().exec (command).waitFor() == 0);
    }

    public static boolean isThereUpdate(List<SongItem> localSongList) {
        String jsonFromServer = NetworkManager.getJSONFromServer();
        List<SongItem> songListFromServer = NetworkManager.JSONToList(jsonFromServer);
        for (int i=0;i<songListFromServer.size();i++) {
            if (!songItemIsInLocalDB(songListFromServer.get(i)))
                return true;
        }
        return false;
    }

    public static int UpdateLocalDB () {
        String jsonFromServer = NetworkManager.getJSONFromServer();
        List<SongItem> songItemsFromServer = NetworkManager.JSONToList(jsonFromServer);
        return NetworkManager.saveSongItemsToLocalDB(songItemsFromServer);
    }

    private static int saveSongItemsToLocalDB(List<SongItem> songItemsFromServer) {
        int countOfNewSong = 0;
        for(int i=0;i<songItemsFromServer.size();i++) {
            SongItem song = songItemsFromServer.get(i);
            if (!songItemIsInLocalDB(song)) {
                countOfNewSong++;
                song.isFavorite = false;
                song.save();
            }
        }
        return countOfNewSong;
    }

    private static boolean songItemIsInLocalDB(SongItem song) {
        List<SongItem> foundSongItems = SongItem.find(SongItem.class, "name = ?", song.name);
        if (foundSongItems.size() == 0)
            return false;
        return true;
    }

    private static List<SongItem> JSONToList(String jsonRawData) {
        Gson gson = new GsonBuilder().create();
        TypeToken<List<SongItem>> token = new TypeToken<List<SongItem>>() {};
        List<SongItem> songItemsFromServer = gson.fromJson(jsonRawData, token.getType());
        return songItemsFromServer;
    }

    private static String getJSONFromServer() {
        String jsonRawData = new String();
        try {
            Request request = new Request.Builder()
                    .url("http://81.2.249.30/dalok2.json")
                    .build();
            Response response = new OkHttpClient().newCall(request).execute();
            jsonRawData = response.body().string().toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonRawData;
    }
}
