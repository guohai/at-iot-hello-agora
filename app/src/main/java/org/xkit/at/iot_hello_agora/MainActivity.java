package org.xkit.at.iot_hello_agora;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Locale;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;

public class MainActivity extends Activity {

    private static final String LOG_TAG = "Iot_Agora_AT";

    private static final int PERMISSION_REQ_ID_RECORD_AUDIO_VIDEO = 22;

    private RtcEngine mRtcEngine;

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {

        @Override
        public void onUserJoined(final int uid, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserIn(uid);
                }
            });
        }

        @Override
        public void onUserOffline(final int uid, final int reason) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserLeft(uid, reason);
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean done = checkSelfPermissions(new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA}, PERMISSION_REQ_ID_RECORD_AUDIO_VIDEO);
    }

    private void initAgoraEngineAndJoinChannel() {
        initializeAgoraEngine();
        joinChannel();
    }

    public void onJoinAgoraChannel(View view) {
        Button button = (Button) view;

        if (button.getText().toString().startsWith("Join")) {
            initAgoraEngineAndJoinChannel();
            button.setText("Leave Agora Channel");
        } else {
            leaveChannel();
            button.setText("Join Agora Channel");
        }
    }

    private void initializeAgoraEngine() {
        if (mRtcEngine != null) {
            return;
        }

        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.agora_app_id), mRtcEventHandler);
        } catch (Exception e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));

            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }

        mRtcEngine.enableVideo();
    }

    private void joinChannel() {

        SurfaceView localV = RtcEngine.CreateRendererView(this.getApplicationContext());
        FrameLayout localVContainer = (FrameLayout) findViewById(R.id.local_view);
        localVContainer.removeAllViews();
        localVContainer.addView(localV);

        mRtcEngine.setupLocalVideo(new VideoCanvas(localV, VideoCanvas.RENDER_MODE_FIT, 0));

        mRtcEngine.joinChannel(null, "voiceAgoraIoT1", "Extra Optional Data", 0); // if you do not specify the uid, we will generate the uid for you
    }

    private void leaveChannel() {
        mRtcEngine.leaveChannel();
    }

    public boolean checkSelfPermission(String permission, int requestCode) {
        Log.i(LOG_TAG, "checkSelfPermission " + permission + " " + requestCode);

        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{permission},
                    requestCode);
            return false;
        }

        return true;
    }

    public boolean checkSelfPermissions(String[] permissions, int requestCode) {
        Log.i(LOG_TAG, "checkSelfPermission " + Arrays.toString(permissions) + " " + requestCode);

        if (permissions == null) {
            return false;
        }

        boolean needReqPermission = false;
        for (String permission : permissions) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                needReqPermission = true;
                break;
            }
        }

        if (needReqPermission) {
            requestPermissions(permissions,
                    requestCode);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.i(LOG_TAG, "onRequestPermissionsResult " + grantResults[0] + " " + requestCode);

        switch (requestCode) {
            case PERMISSION_REQ_ID_RECORD_AUDIO_VIDEO: {
                boolean noPermission = false;
                int idx;
                for (idx = 0; idx < grantResults.length; idx++) {
                    if (grantResults[idx] != PackageManager.PERMISSION_GRANTED) {
                        noPermission = true;
                        break;
                    }
                }

                if (noPermission) {
                    showLongToast("No permission for " + permissions[idx]);
                    finish();
                }
                break;
            }
        }
    }

    public final void showLongToast(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void onRemoteUserIn(int uid) {
        showLongToast(String.format(Locale.US, "user %d in", (uid & 0xFFFFFFFFL)));
        View tipMsg = findViewById(R.id.textView); // optional UI
        ((TextView) (tipMsg)).setText(String.format(Locale.US, "Hello %d", (uid & 0xFFFFFFFFL)));
        tipMsg.setVisibility(View.VISIBLE);

        SurfaceView remoteV = RtcEngine.CreateRendererView(this.getApplicationContext());
        FrameLayout remoteVContainer = (FrameLayout) findViewById(R.id.remote_view);
        remoteVContainer.removeAllViews();
        remoteVContainer.addView(remoteV);

        mRtcEngine.setupRemoteVideo(new VideoCanvas(remoteV, VideoCanvas.RENDER_MODE_FIT, uid));
    }

    private void onRemoteUserLeft(int uid, int reason) {
        showLongToast(String.format(Locale.US, "user %d left %d", (uid & 0xFFFFFFFFL), reason));
        View tipMsg = findViewById(R.id.textView); // optional UI
        ((TextView) (tipMsg)).setText("Bye");
        tipMsg.setVisibility(View.VISIBLE);
    }
}
