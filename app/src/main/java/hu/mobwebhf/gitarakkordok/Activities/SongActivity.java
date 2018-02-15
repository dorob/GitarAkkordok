package hu.mobwebhf.gitarakkordok.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;

import hu.mobwebhf.gitarakkordok.ChordImage;
import hu.mobwebhf.gitarakkordok.R;

public class SongActivity extends AppCompatActivity {
    private String name;
    private String chords;
    private String lyrics;
    private LinearLayout listOfChords;
    private LayoutInflater inflater;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        chords = intent.getStringExtra("chords");
        lyrics = intent.getStringExtra("lyrics");
        setTitle(name);
        listOfChords = (LinearLayout) findViewById(R.id.list_of_chords);
        inflater = (LayoutInflater) getSystemService(getBaseContext().LAYOUT_INFLATER_SERVICE);

        ArrayList<ArrayList<String>> finalChords = getListFromChords(chords);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        for (ArrayList<String> row : finalChords) {
            for (final String chord : row) {
                View rowItem = inflater.inflate(R.layout.chords, null);
                Button chordTextView = rowItem.findViewById(R.id.ChordButton);
                chordTextView.setText(chord);

                chordTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LayoutInflater inflater = getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.dialog_chord_image, null);
                        ImageView chordImage = (ImageView) dialogView.findViewById(R.id.ChordImage);
                        int chordImageId = getChordImage(chord);
                        if (chordImageId != 0)
                            chordImage.setImageResource(getChordImage(chord));
                        builder.setView(dialogView);
                        builder.setCancelable(false);
                        /*builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                dialog.dismiss();
                            }
                        });*/
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                });
                listOfChords.addView(rowItem);
            }
        }
        TextView lyricsTextView = (TextView) findViewById(R.id.LyricsTextView);
        String nLyrics = lyrics.replace("\\n","\n");
        lyricsTextView.setText(nLyrics);
    }

    public int getChordImage(String chord) {
        return ChordImage.getChordImage(chord);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public ArrayList<ArrayList<String>> getListFromChords (String chords){
        String chordRows[] = chords.split(" ");
        ArrayList<ArrayList<String>> finalChords = new ArrayList<ArrayList<String>>();
        for (String chordRow : chordRows) {
            ArrayList<String> chordsInRow = new ArrayList<String>();
            String chordsInSingleRow[] = chordRow.split("-");
            for (String chordColumn : chordsInSingleRow) {
                chordsInRow.add(chordColumn);
            }
            finalChords.add(chordsInRow);
        }
        return finalChords;
    }
}
