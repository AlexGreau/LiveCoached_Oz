package com.example.livecoached_oz;

import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.TextView;

import com.example.livecoached_oz.Communication.Server;
import com.example.livecoached_oz.Interfaces.Decoder;

public class MainActivity extends WearableActivity implements Decoder {
    private final String goRightOrder = "Right";
    private final String goLeftOrder = "Left";
    private final String goStraightOrder = "Straight";
    private final String finishOrder = "Finish";
    private final String startOrder = "Start";
    private final String checkpointReachedOrder = "CP";
    private final int hapticCode = -1;
    private final int visualCode = 1;
    private final int bothCode = 0;
    private final String separator = ";";

    // UI Components
    private TextView orderText;
    private TextView hapticText;

    // communication
    private Server server;

    // feedback
    private Vibrator vibrator;
    private int interactionType;
    private boolean vibroIsForbidden;
    private boolean visualIsForbidden;

    private long[] pattern;
    private int[] amplitudes;
    private int indexInPatternToRepeat = -1;

    // values
    private int currentDirection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        orderText = (TextView) findViewById(R.id.order);
        hapticText = (TextView) findViewById(R.id.hapticExplanation);
        initHapticExplanationText();
        server = new Server(this);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        currentDirection = 0;
        interactionType = 0;
        // Enables Always-on
        setAmbientEnabled();
    }

    @Override
    public String decodeMessage(String rep) {
        String[] parts = rep.split(separator);
        if (parts.length < 2) {
            return "message unclear, send again";
        } else {
            decodeInteraction(parts[0]);
            decodeDirection(parts[1]);
            if (!visualIsForbidden) {
                showOnScreen(rep);
            } else {
                showHapticVocab();
            }

            if (!vibroIsForbidden) {
                vibrate(currentDirection);
            }
        }
        return "roger";
    }

    private void decodeDirection(String i) {
        if (i.equals(goStraightOrder)) {
            currentDirection = 3;
        } else if (i.equals(goLeftOrder)) {
            currentDirection = -1;
        } else if (i.equals(goRightOrder)) {
            currentDirection = 1;
        } else if (i.equals(startOrder)) {
            currentDirection = 100;
        } else if (i.equals(checkpointReachedOrder)) {
            currentDirection = 0;
        } else if (i.equals(finishOrder)) {
            currentDirection = 2;
        }
    }

    public void showOnScreen(String order) {
        final String msg = order;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                orderText.setVisibility(View.VISIBLE);
                hapticText.setVisibility(View.GONE);
                orderText.setText(msg);
            }
        });
    }

    public void showHapticVocab() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                orderText.setVisibility(View.GONE);
                hapticText.setVisibility(View.VISIBLE);
            }
        });
    }

    private void decodeInteraction(String interactionString) {
        int interaction = Integer.parseInt(interactionString);
        if (interaction == hapticCode) {
            vibroIsForbidden = false;
            visualIsForbidden = true;
        } else if (interaction == visualCode) {
            vibroIsForbidden = true;
            visualIsForbidden = false;
        } else if (interaction == bothCode) {
            vibroIsForbidden = false;
            visualIsForbidden = false;
        }
        return;
    }

    public void vibrate(int pat) {
        setVibroValues(pat);
        vibrator.vibrate(VibrationEffect.createWaveform(pattern, amplitudes, indexInPatternToRepeat));

    }

    private void setVibroValues(int style) {
        long shortSig = 200;
        long longSig = 450;
        long delay = 300;
        long pause = 2000;

        int weakAmpli = 70;
        int midAmpli = 150;
        int highAmpli = 250;

        switch (style) {
            case 0:
                // CP
                pattern = new long[]{shortSig, 2 * delay / 3, shortSig, 2 * delay / 3, shortSig, pause};
                amplitudes = new int[]{weakAmpli, 0, midAmpli, 0, highAmpli, 0};
                return;
            case -1:
                // left
                pattern = new long[]{longSig, delay, shortSig, delay, shortSig, pause};
                amplitudes = new int[]{midAmpli, 0, highAmpli, 0, highAmpli, 0};
                return;
            case 1:
                // right
                pattern = new long[]{shortSig, delay, shortSig, delay, longSig, pause};
                amplitudes = new int[]{midAmpli, 0, highAmpli, 0, highAmpli, 0};
                return;
            case 2:
                // end
                pattern = new long[]{longSig, delay, longSig, delay, longSig, pause};
                amplitudes = new int[]{midAmpli, 0, midAmpli, 0, midAmpli, 0};
                return;
            case 3:
                // straight
                pattern = new long[]{longSig, pause};
                amplitudes = new int[]{midAmpli, 0};
                return;
            default:
                //standard
                pattern = new long[]{shortSig, pause};
                amplitudes = new int[]{midAmpli, 0};
                return;
        }
    }

    private void initHapticExplanationText(){
        String explanationText = " - : Straight \n - . . : Left \n . . - : Right \n ... : Checkpoint \n - - - : Finish line";
        hapticText.setText(explanationText);
    }
}
