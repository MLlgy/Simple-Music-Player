package musicplayer.simplemobiletools.com;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private Bus bus;

    @Bind(R.id.playPauseBtn) ImageView playPauseBtn;
    @Bind(R.id.songs) ListView songsList;
    @Bind(R.id.songTitle) TextView titleTV;
    @Bind(R.id.songArtist) TextView artistTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        bus = BusProvider.getInstance();
        bus.register(this);
        startService(new Intent(Constants.INIT));
    }

    private void songPicked(int pos) {
        final Intent intent = new Intent(Constants.PLAYPOS);
        intent.putExtra(Constants.SONG_POS, pos);
        startService(intent);
    }

    private void updateSongInfo(Song song) {
        if (song != null) {
            titleTV.setText(song.getTitle());
            artistTV.setText(song.getArtist());
        }
    }

    private void fillSongsListView(ArrayList<Song> songs) {
        final SongAdapter adapter = new SongAdapter(this, songs);
        songsList.setAdapter(adapter);
        songsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                songPicked(position);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
    }

    @OnClick(R.id.previousBtn)
    public void previousClicked() {
        startService(new Intent(Constants.PREVIOUS));
    }

    @OnClick(R.id.playPauseBtn)
    public void playPauseClicked() {
        startService(new Intent(Constants.PLAYPAUSE));
    }

    @OnClick(R.id.nextBtn)
    public void nextClicked() {
        startService(new Intent(Constants.NEXT));
    }

    @OnClick(R.id.stopBtn)
    public void stopClicked() {
        startService(new Intent(Constants.STOP));
    }

    @Subscribe
    public void songChangedEvent(Events.SongChanged event) {
        updateSongInfo(event.getSong());
    }

    @Subscribe
    public void songStateChanged(Events.SongStateChanged event) {
        int id = R.mipmap.play;
        if (event.getIsPlaying())
            id = R.mipmap.pause;

        playPauseBtn.setImageDrawable(getResources().getDrawable(id));
    }

    @Subscribe
    public void playlistUpdated(Events.PlaylistUpdated event) {
        fillSongsListView(event.getSongs());
    }
}
