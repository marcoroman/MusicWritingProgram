package edu.orangecoastcollege.hackathon;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private ArrayList<MediaPlayer> mediaPlayers;
    private Button mediaButton;

    Timer timer;
    TimerTask timerTask;
    int currentPosition;

    final Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mediaPlayers = new ArrayList<>();
        mediaButton = (Button) findViewById(R.id.button);
        int fileId[] = {R.raw.a_up_3,R.raw.a3,R.raw.b3,R.raw.c_up_3,R.raw.c3,R.raw.d_up_3, R.raw.d3, R.raw.e3,R.raw.f_up_3,R.raw.f3,R.raw.g_up_3, R.raw.g3};
        for (int i=0; i<fileId.length ; i++)
            mediaPlayers.add(MediaPlayer.create(this, Uri.parse("android.resource://" + getPackageName() +"/"+ fileId[i])));

        Random rng = new Random();
        int currentPosition = rng.nextInt(mediaPlayers.size());

    }

    public void playMedia(View view)
    {
        startTimer();
    }

    public void stopMedia(View view)
    {
        if (timer != null)
        {
            timer.cancel();
            timer = null;
        }
    }

    public void startTimer(){
        timer = new Timer();

        initializeTimerTask();

        timer.schedule(timerTask, 0, 500);

    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {

            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Random rng = new Random();
                        int randomPosition = rng.nextInt(mediaPlayers.size());
                        if (mediaPlayers.get(currentPosition).isPlaying())
                            mediaPlayers.get(currentPosition).pause();

                        mediaPlayers.get(randomPosition).start();
                        currentPosition = randomPosition;
                    }
                });
            }
        };
    }



}
