package com.example.livecoached_oz;

import android.os.Bundle;
import android.os.Vibrator;
import android.support.wearable.activity.WearableActivity;
import android.widget.TextView;

import com.example.livecoached_oz.Communication.Server;
import com.example.livecoached_oz.Interfaces.Decoder;

public class MainActivity extends WearableActivity implements Decoder {

    // UI Components
    private TextView mTextView;

    // communication
    private Server server;

    // feedback
    private Vibrator vibrator;
    private int interactionType;
    private boolean vibroIsForbidden;
    private int patternIndex = 100; // 3: strait, -1: left, 1: right

    private long[] pattern;
    private int[] amplitudes;
    private int indexInPatternToRepeat = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.text);
        server = new Server(this);

        // Enables Always-on
        setAmbientEnabled();
    }

    @Override
    public String decodeMessage(String rep) {
        showOnScreen(rep);
        // vibrate if says so
        return "roger";
    }

    public void showOnScreen(String order){
        final String msg = order;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTextView.setText(msg);
            }
        });
    }
}
