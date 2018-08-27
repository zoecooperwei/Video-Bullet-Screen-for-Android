package com.example.KC.danmutest;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFormatException;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.InputStream;
import java.util.Random;
import android.util.Log;

import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.danmaku.loader.ILoader;
import master.flame.danmaku.danmaku.loader.IllegalDataException;
import master.flame.danmaku.danmaku.loader.android.DanmakuLoaderFactory;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.IDataSource;
import master.flame.danmaku.danmaku.parser.android.BiliDanmukuParser;
import master.flame.danmaku.ui.widget.DanmakuView;

import java.util.* ;
import java.io.File;

import android.net.Uri;


public class PlayerView extends AppCompatActivity {

    private static final String TAG = "PlayerView";

    private boolean showDanmaku;

    private DanmakuView danmakuView;

    private DanmakuContext danmakuContext;



    private final int UPDATE_VIDEO_SEEKBAR = 1000;

    private LinearLayout videoPauseBtn;
    private LinearLayout touchStatusView;
    private LinearLayout videoControllerLayout;
    private ImageView touchStatusImg;
    private ImageView videoPlayImg;
    private ImageView videoPauseImg;
    private TextView touchStatusTime;
    private TextView videoCurTimeText;
    private TextView videoTotalTimeText;
    private SeekBar videoSeekBar;
    private RelativeLayout viewBox;
    private BaseDanmakuParser parser;

    private ProgressBar progressBar;

    private int duration;
    private String formatTotalTime;

    private Timer timer = new Timer();

    private float touchLastX;
    //show position seekbar
    private static int position;
    private int touchStep = 1000;//forward time 1s
    private int touchPosition = -1;

    private static boolean videoControllerShow = true;
    private static boolean animation = false;


    public static Uri uri;
    public static String path = "";

    public static String fileName;
    public static int resid;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_view);
        final VideoView videoView = (VideoView) findViewById(R.id.video_view);



        videoPauseBtn = (LinearLayout) findViewById(R.id.videoPauseBtn);

        videoControllerLayout = (LinearLayout) findViewById(R.id.videoControllerLayout);
        touchStatusView = (LinearLayout) findViewById(R.id.touch_view);
        touchStatusImg = (ImageView) findViewById(R.id.touchStatusImg);
        touchStatusTime = (TextView) findViewById(R.id.touch_time);
        videoCurTimeText = (TextView) findViewById(R.id.videoCurTime);
        videoTotalTimeText = (TextView) findViewById(R.id.videoTotalTime);
        videoSeekBar = (SeekBar) findViewById(R.id.videoSeekBar);
        videoPlayImg = (ImageView) findViewById(R.id.videoPlayImg);
        videoPlayImg.setVisibility(View.GONE);
        videoPauseImg = (ImageView) findViewById(R.id.videoPauseImg);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        viewBox = (RelativeLayout) findViewById(R.id.viewBox);


        final Handler videoHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case UPDATE_VIDEO_SEEKBAR:
                        if (videoView.isPlaying()) {
                            videoSeekBar.setProgress(videoView.getCurrentPosition());
                        } else {
                            videoSeekBar.setProgress(videoView.getCurrentPosition());
                        }

                        break;
                }
            }
        };

        final TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                videoHandler.sendEmptyMessage(UPDATE_VIDEO_SEEKBAR);
            }
        };



        if (path.isEmpty()) {
            videoView.setVideoURI(uri);
        } else {
            videoView.setVideoPath(path);
        }
        videoView.seekTo(0);
        videoView.start();
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                videoView.start();
            }
        }, 0);



//        viewBox.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                float curY = videoControllerLayout.getY();
//                if (!animation && videoControllerShow) {
//
//                    animation = true;
//                    ObjectAnimator animator = ObjectAnimator.ofFloat(videoControllerLayout, "y",
//                            curY, curY + videoControllerLayout.getHeight());
//                    animator.setDuration(200);
//                    animator.start();
//                    animator.addListener(new Animator.AnimatorListener() {
//                        @Override
//                        public void onAnimationStart(Animator animator) {
//
//                        }
//
//                        @Override
//                        public void onAnimationEnd(Animator animator) {
//                            PlayerView.animation = false;
//                            PlayerView.videoControllerShow = !PlayerView.videoControllerShow;
//                        }
//
//                        @Override
//                        public void onAnimationCancel(Animator animator) {
//
//                        }
//
//                        @Override
//                        public void onAnimationRepeat(Animator animator) {
//
//                        }
//                    });
//                } else if (!animation) {
//                    animation = true;
//                    ObjectAnimator animator = ObjectAnimator.ofFloat(videoControllerLayout, "y",
//                            curY, curY - videoControllerLayout.getHeight());
//                    animator.setDuration(200);
//                    animator.start();
//                    animator.addListener(new Animator.AnimatorListener() {
//                        @Override
//                        public void onAnimationStart(Animator animator) {
//
//                        }
//
//                        @Override
//                        public void onAnimationEnd(Animator animator) {
//                            PlayerView.animation = false;
//                            PlayerView.videoControllerShow = !PlayerView.videoControllerShow;
//                        }
//
//                        @Override
//                        public void onAnimationCancel(Animator animator) {
//
//                        }
//
//                        @Override
//                        public void onAnimationRepeat(Animator animator) {
//
//                        }
//                    });
//                }
//            }
//        });


        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                duration = videoView.getDuration();
                int[] time = getMinuteAndSecond(duration);
                videoTotalTimeText.setText(String.format("%02d:%02d", time[0], time[1]));
                formatTotalTime = String.format("%02d:%02d", time[0], time[1]);
                videoSeekBar.setMax(duration);
                progressBar.setVisibility(View.GONE);

                mediaPlayer.start();
                videoPauseBtn.setEnabled(true);
                videoSeekBar.setEnabled(true);
                videoPauseImg.setImageResource(R.mipmap.icon_video_pause);
                timer.schedule(timerTask, 0, 1000);
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                videoView.seekTo(0);
                videoSeekBar.setProgress(0);
                danmakuView.stop();
                videoPauseImg.setImageResource(R.mipmap.icon_video_play);
                videoPlayImg.setVisibility(View.INVISIBLE);
            }
        });

        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
                return false;
            }
        });





        danmakuView = (DanmakuView) findViewById(R.id.danmaku_view);
        danmakuView.enableDanmakuDrawingCache(true);
        danmakuView.setCallback(new DrawHandler.Callback() {
            @Override
            public void prepared() {
                showDanmaku = true;
                danmakuView.start();

                //generateSomeDanmaku();
            }

            @Override
            public void updateTimer(DanmakuTimer timer) {

            }

            @Override
            public void danmakuShown(BaseDanmaku danmaku) {

            }

            @Override
            public void drawingFinished() {

            }
        });



        try {
            resid = getResources().getIdentifier(fileName , "raw", getPackageName());
            parser = createParser(this.getResources().openRawResource(resid));
        } catch (Exception e) {
            parser = createParser(this.getResources().openRawResource(R.raw.defaults));
        }


        danmakuContext = DanmakuContext.create();
        danmakuView.prepare(parser, danmakuContext);

        final  LinearLayout operationLayout = (LinearLayout) findViewById(R.id.operation_layout);
        final Button send = (Button) findViewById(R.id.send);
        final EditText editText = (EditText) findViewById(R.id.edit_text);
        ImageButton select = (ImageButton) findViewById(R.id.select);
        final RelativeLayout selection = (RelativeLayout) findViewById(R.id.selection);

        final EditText Ecolor = (EditText) findViewById(R.id.color);
        final EditText Esize = (EditText) findViewById(R.id.size);
        Button normal = (Button) findViewById(R.id.normal);
        Button top = (Button) findViewById(R.id.top);
        Button bottom = (Button) findViewById(R.id.bottom);


        class Type {
            int bType = BaseDanmaku.TYPE_SCROLL_RL;
            public void normal() {
                bType = BaseDanmaku.TYPE_SCROLL_RL;
            }
            public void top() {
                bType = BaseDanmaku.TYPE_FIX_TOP;
            }
            public void bottom() {
                bType = BaseDanmaku.TYPE_FIX_BOTTOM;
            }
        }
        final Type type = new Type();

        normal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type.normal();
            }
        });
        top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type.top();
            }
        });
        bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type.bottom();
            }
        });

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selection.getVisibility() == View.GONE) {
                    selection.setVisibility(View.VISIBLE);
                } else {
                    selection.setVisibility(View.GONE);
                }
            }
        });


        viewBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (operationLayout.getVisibility() == View.GONE) {
                    operationLayout.setVisibility(View.VISIBLE);
                    videoControllerLayout.setVisibility(View.VISIBLE);
                } else {
                    operationLayout.setVisibility(View.GONE);
                    videoControllerLayout.setVisibility(View.GONE);
                    selection.setVisibility(View.GONE);
                }

                float curY = videoControllerLayout.getY();
                if (!animation && videoControllerShow) {

                    animation = true;
                    ObjectAnimator animator = ObjectAnimator.ofFloat(videoControllerLayout, "y",
                            curY, curY + videoControllerLayout.getHeight());
                    animator.setDuration(200);
                    animator.start();
                    animator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            PlayerView.animation = false;
                            PlayerView.videoControllerShow = !PlayerView.videoControllerShow;
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {

                        }
                    });
                } else if (!animation) {
                    animation = true;
                    ObjectAnimator animator = ObjectAnimator.ofFloat(videoControllerLayout, "y",
                            curY, curY - videoControllerLayout.getHeight());
                    animator.setDuration(200);
                    animator.start();
                    animator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            PlayerView.animation = false;
                            PlayerView.videoControllerShow = !PlayerView.videoControllerShow;
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {

                        }
                    });
                }


            }
        });

        viewBox.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        if (!videoView.isPlaying()){
                            return false;
                        }
                        float downX =  event.getRawX();
                        touchLastX = downX;
                        Log.d("FilmDetailActivity", "downX" + downX);
                        PlayerView.position = videoView.getCurrentPosition();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (!videoView.isPlaying()){
                            return false;
                        }
                        float currentX =  event.getRawX();
                        float deltaX = currentX - touchLastX;
                        float deltaXAbs  =  Math.abs(deltaX);
                        if (deltaXAbs>1){
                            if (touchStatusView.getVisibility()!=View.VISIBLE){
                                touchStatusView.setVisibility(View.VISIBLE);
                            }
                            touchLastX = currentX;
                            Log.d("FilmDetailActivity","deltaX"+deltaX);
                            if (deltaX > 1) {
                                position += touchStep;
                                if (position > duration) {
                                    position = duration;
                                }
                                touchPosition = position;
                                touchStatusImg.setImageResource(R.mipmap.ic_fast_forward_white_24dp);
                                int[] time = getMinuteAndSecond(position);
                                touchStatusTime.setText(String.format("%02d:%02d/%s", time[0], time[1],formatTotalTime));
                            } else if (deltaX < -1) {
                                position -= touchStep;
                                if (position < 0) {
                                    position = 0;
                                }
                                touchPosition = position;
                                touchStatusImg.setImageResource(R.mipmap.ic_fast_rewind_white_24dp);
                                int[] time = getMinuteAndSecond(position);
                                touchStatusTime.setText(String.format("%02d:%02d/%s", time[0], time[1],formatTotalTime));
                                //mVideoView.seekTo(position);
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (touchPosition!=-1){
                            videoView.seekTo(touchPosition);
                            danmakuView.seekTo((long) touchPosition);
                            touchStatusView.setVisibility(View.GONE);
                            touchPosition = -1;
                            if (videoControllerShow){
                                return true;
                            }
                        }
                        break;
                }
                return false;
            }
        });

        danmakuView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = editText.getText().toString();         //send danma

                String color = Ecolor.getText().toString();
                String size = Esize.getText().toString();
                int nsize ;

                if (color.equals("")) {
                    color = "WHITE";
                }


                if (size.equals("")) {
                    nsize = 20;
                }  else {
                    try {
                        nsize = Integer.parseInt(size,10);
                    } catch (Exception e) {
                        nsize = 20;
                    }

                }


                if (!TextUtils.isEmpty(content)) {
                    addDanmaku(content, true, nsize, color, type.bType);
                    editText.setText("");
                    type.normal();
                    Ecolor.setText("");
                    Esize.setText("");
                }
                selection.setVisibility(View.GONE);
            }
        });
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener (new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility == View.SYSTEM_UI_FLAG_VISIBLE) {
                    onWindowFocusChanged(true);
                }
            }
        });

        videoPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (videoView.isPlaying()) {
                    videoView.pause();
                    danmakuView.pause();
                    videoPauseImg.setImageResource(R.mipmap.icon_video_play);
                    videoPlayImg.setVisibility(View.INVISIBLE);
                } else {
                    videoView.start();
                    danmakuView.resume();
                    videoPauseImg.setImageResource(R.mipmap.icon_video_pause);
                    videoPlayImg.setVisibility(View.INVISIBLE);
                }
            }
        });
        videoSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int[] time = getMinuteAndSecond(progress);
                videoCurTimeText.setText(String.format("%02d:%02d", time[0], time[1]));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                videoView.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                videoView.seekTo(videoSeekBar.getProgress());
                danmakuView.seekTo((long)videoSeekBar.getProgress());
                videoView.start();
                videoPlayImg.setVisibility(View.INVISIBLE);
                videoPauseImg.setImageResource(R.mipmap.icon_video_pause);
            }
        });





    }

    private int[] getMinuteAndSecond(int mils) {
        mils /= 1000;
        int[] time = new int[2];
        time[0] = mils / 60;
        time[1] = mils % 60;
        return time;
    }
    /**
     * 向弹幕View中添加一条弹幕
     * @param content
     *          弹幕的具体内容
     * @param  withBorder
     *          弹幕是否有边框
     */

    private void addDanmaku(String content, boolean withBorder, int size, String color, int type) {
        BaseDanmaku danmaku = danmakuContext.mDanmakuFactory.createDanmaku(type);
        danmaku.text = content;
        danmaku.padding = 5;
        danmaku.textSize = sp2px(size);
        if (isDigit(color)) {
            danmaku.textColor = Color.alpha(Integer.parseInt(color,10)) ;
        } else {
            try {
                danmaku.textColor = Color.parseColor(color);
            } catch (Exception e) {
                danmaku.textColor = Color.WHITE;
            }

        }

        danmaku.setTime(danmakuView.getCurrentTime());
        if (withBorder) {
            danmaku.borderColor = Color.GREEN;
        }
        danmakuView.addDanmaku(danmaku);
    }

    public boolean isDigit(String strNum) {
        return strNum.matches("[0-9]{1,}");
    }
    /**
     * 随机生成一些弹幕内容以供测试
     */
    /*
    private void generateSomeDanmaku() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(showDanmaku) {
                    int time = new Random().nextInt(300);
                    String content = "" + time + time;
                    addDanmaku(content, false);
                    try {
                        Thread.sleep(time);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    */
    /**
     * sp转px的方法。
     */
    public int sp2px(float spValue) {
        final float fontScale = getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }




    private BaseDanmakuParser createParser(InputStream stream) {

        if (stream == null) {
            return new BaseDanmakuParser() {

                @Override
                protected Danmakus parse() {
                    return new Danmakus();
                }
            };
        }

        // DanmakuLoaderFactory.create(DanmakuLoaderFactory.TAG_BILI) //xml解析
        // DanmakuLoaderFactory.create(DanmakuLoaderFactory.TAG_ACFUN) //json文件格式解析
        ILoader loader = DanmakuLoaderFactory.create(DanmakuLoaderFactory.TAG_BILI);

        try {
            loader.load(stream);
        } catch (IllegalDataException e) {
            e.printStackTrace();
        }
        BaseDanmakuParser parser = new BiliDanmukuParser();
        IDataSource<?> dataSource = loader.getDataSource();
        parser.load(dataSource);
        return parser;

    }



    @Override
    protected void onPause() {
        super.onPause();
        if (danmakuView != null && danmakuView.isPrepared()) {
            danmakuView.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (danmakuView != null && danmakuView.isPrepared() && danmakuView.isPaused()) {
            danmakuView.resume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        showDanmaku = false;
        if (danmakuView != null) {
            danmakuView.release();
            danmakuView = null;
            parser.release();
            fileName = "";
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }


}
