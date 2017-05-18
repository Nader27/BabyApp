package fcih.babyapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

public class Splashscreen extends Activity {
    /**
     * Called when the activity is first created.
     */

    Thread splashTread;

    public void onAttachedToWindow() {

        super.onAttachedToWindow();

        Window window = getWindow();

        window.setFormat(PixelFormat.RGBA_8888);

    }

    @Override

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splashscreen);

        StartAnimations();

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            if (hasFocus) {
                getWindow().getDecorView()
                        .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
        }
    }

    private void StartAnimations() {

        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);

        anim.reset();

        LinearLayout l = (LinearLayout) findViewById(R.id.lin_lay);

        l.clearAnimation();

        l.startAnimation(anim);


        anim = AnimationUtils.loadAnimation(this, R.anim.translate);

        anim.reset();

        LinearLayout iv = (LinearLayout) findViewById(R.id.splash);

        iv.clearAnimation();

        iv.startAnimation(anim);


        splashTread = new Thread() {

            @Override

            public void run() {

                try {

                    int waited = 0;

                    // Splash screen pause time

                    while (waited < 3500) {

                        sleep(100);

                        waited += 100;

                    }

                    Intent intent = new Intent(Splashscreen.this,

                            BaseActivity.class);

                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                    startActivity(intent);

                    Splashscreen.this.finish();

                } catch (InterruptedException e) {

                    // do nothing

                } finally {

                    Splashscreen.this.finish();

                }


            }

        };

        splashTread.start();


    }


}
