package hu.mobwebhf.gitarakkordok.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import hu.mobwebhf.gitarakkordok.Model.SongItem;
import hu.mobwebhf.gitarakkordok.R;
import hu.mobwebhf.gitarakkordok.Activities.SongActivity;

/**
 * Created by Benjamin on 2017. 10. 23..
 */

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    private List<SongItem> songItemList;
    private Context context;

    public SongAdapter(Context c) {
        this.songItemList = new ArrayList<>();
        context = c;
    }

    public void changeFavoriteIcon(final SongAdapter.SongViewHolder holder, SongItem item) {
        if (item.isFavorite) {
            holder.isFavoriteImageButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            item.isFavorite = false;
        }
        else {
            holder.isFavoriteImageButton.setImageResource(R.drawable.ic_favorite_black_24dp);
            item.isFavorite = true;
        }
        item.save();
    }

    public List<SongItem> getSongList() {
        return songItemList;
    }

    @Override
    public void onBindViewHolder(final SongAdapter.SongViewHolder holder, int position) {
        final SongItem item = songItemList.get(position);
        holder.nameTextView.setText(item.name);

        if (item.isFavorite) {
            holder.isFavoriteImageButton.setImageResource(R.drawable.ic_favorite_black_24dp);
        }
        else
            holder.isFavoriteImageButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);

        holder.FavoriteBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFavoriteIcon(holder,item);
            }
        });

        holder.isFavoriteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFavoriteIcon(holder,item);
            }
        });
    }


    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song_list, parent, false);
        return new SongViewHolder(itemView);
    }

    public List<SongItem> getFilteredSongs(String newText) {
        List<SongItem> filteredSongs = new ArrayList<SongItem>();
        List<SongItem> originalSongList = SongItem.listAll(SongItem.class);
        for(int i=0;i<originalSongList.size();i++) {
            SongItem song = originalSongList.get(i);
            if (song.name.contains(newText))
                filteredSongs.add(song);
        }
        return filteredSongs;
    }


    public void updateSongItemList(List<SongItem> songItems) {
        this.songItemList.clear();
        this.songItemList.addAll(songItems);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return songItemList.size();
    }

    public class SongViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        LinearLayout FavoriteBox;
        ImageButton isFavoriteImageButton;

        public SongViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.SongItemNameTextView);
            FavoriteBox = (LinearLayout) itemView.findViewById(R.id.SongItemFavoriteBox);
            isFavoriteImageButton = (ImageButton) itemView.findViewById(R.id.SongItemIsFavoriteImageButton);

            nameTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, SongActivity.class);
                    SongItem song = getSongAttributes(nameTextView.getText().toString());
                    if (song != null) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("name", song.name);
                        intent.putExtra("chords",song.chords);
                        intent.putExtra("lyrics",song.lyrics);
                        context.startActivity(intent);
                    }
                }
            });
        }
    }

    private SongItem getSongAttributes(String name) {
        for (int i=0;i<songItemList.size();i++) {
            if (songItemList.get(i).name.equals(name))
                return songItemList.get(i);
        }
        return null;
    }
}
