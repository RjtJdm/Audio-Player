package rajat.com.audioplayer;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static java.util.Arrays.asList;

public class MainActivity extends AppCompatActivity {
    MediaPlayer mp;
    AudioManager audioManager;
    int maxVolume;
    SeekBar volumeControl;
    int currentVolume;
    boolean isStarted=false;
    TextView timing;
    String c;
    int sec,min;
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int key=event.getKeyCode();
        switch (key){
            case KeyEvent.KEYCODE_VOLUME_UP:
                if(currentVolume<maxVolume){
                    currentVolume++;
                    setVolumeScrubber();
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if(currentVolume>0){
                    currentVolume--;
                    setVolumeScrubber();
                }

                return true;
            default:
                return super.dispatchKeyEvent(event);
        }

    }
    public void setVolumeScrubber(){
        Log.d("CurrentVolume",currentVolume+"");
        volumeControl.setProgress(currentVolume);

    }
    public void setVolumeBar(){

        audioManager=(AudioManager) getSystemService(Context.AUDIO_SERVICE);
        maxVolume=audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentVolume=audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        volumeControl=(SeekBar)findViewById(R.id.seekBar);
        volumeControl.setMax(maxVolume);
        setVolumeScrubber();
        volumeControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,i,0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
        });
    }
    public void setPlayer(String s){
        if(mp.isPlaying()){
            mp.stop();
        }
        int id=getResources().getIdentifier(s,"raw",getPackageName());
        mp=MediaPlayer.create(this,id);
        int streamMax=mp.getDuration();
        mp.start();
        final SeekBar statePlayer=(SeekBar)findViewById(R.id.seekBar2);
        statePlayer.setMax(streamMax);
        final Handler h=new Handler();
        statePlayer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            int cur=0;
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mp.seekTo(cur);
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                cur=i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
        });

        Runnable r=new Runnable() {
            @Override
            public void run() {
                h.postDelayed(this,1000);
                int a=mp.getCurrentPosition();
                statePlayer.setProgress(a);
                min=0;
                sec=a/1000;
                if(sec>=60){
                    min=sec/60;
                    sec=sec-min*60;
                }
                c=min+":"+sec;
                timing.setText(c);
            }
        };
        h.post(r);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setVolumeBar();
        mp=MediaPlayer.create(this,R.raw.raabta);
        final TextView songName=(TextView)findViewById(R.id.SongName);
        timing=(TextView)findViewById(R.id.timer);
        ListView myPlaylist=(ListView)findViewById(R.id.songList);
        ArrayList<String> mySongs=new ArrayList<String>(asList("Chittiyaan Kalaiyaan",
                "Dilliwaali Girlfriend",
                "Duaa",
                "Galliyan",
                "Ishq Wala Love",
                "Kabhi Jo Badal Barse",
                "Main Rahoon Ya Na Rahoon",
                "Raabta",
                "Sanam Re",
                "Sun Saathiya",
                "Suno Na Sangemarmar"));
        final ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,mySongs);
        myPlaylist.setAdapter(arrayAdapter);
        myPlaylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                isStarted=true;
                String song="";
                String s=arrayAdapter.getItem(i);
                songName.setText(s);
                String t[]=s.split(" ");
                for(String k:t ){
                    song=song+k;
                }
                song=song.toLowerCase();
                setPlayer(song);
            }
        });

    }
    public void play(View v){
        if (isStarted){
            mp.start();
        }
        else{
            Toast.makeText(this, "Select The Song First!!", Toast.LENGTH_SHORT).show();
        }

    }
    public void pause(View v){
        mp.pause();
    }
}
