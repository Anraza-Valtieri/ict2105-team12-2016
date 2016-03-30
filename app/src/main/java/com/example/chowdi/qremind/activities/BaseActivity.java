package com.example.chowdi.qremind.activities;

import android.animation.Animator;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.chowdi.qremind.R;
import com.example.chowdi.qremind.infrastructure.QremindApplication;
import com.example.chowdi.qremind.views.NavDrawer;

/**
 * Created by L on 3/26/2016.
 * Base activity class stub for activites
 */
public class BaseActivity extends AppCompatActivity {
    protected QremindApplication application;
    protected NavDrawer navDrawer;
    protected Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedState){
        super.onCreate(savedState);
        application = (QremindApplication)getApplication();//get application manifested in AndroidManifest.xml
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);

        toolbar = (Toolbar)findViewById(R.id.include_toolbar);

        if(toolbar != null){
            setSupportActionBar(toolbar);
        }
    }

    protected void setNavDrawer(NavDrawer drawer){
        this.navDrawer = drawer;
        this.navDrawer.create();

        overridePendingTransition(0,0);//remove android animation, fly in.

        View rootView = findViewById(android.R.id.content);
        rootView.setAlpha(0);//set 0 so animation can opaque it
        rootView.animate().alpha(1).setDuration(450).start();//animate it to visible
    }

    public Toolbar getToolbar() {
        return toolbar;
    }


    public QremindApplication getQremindApplication() {
        return application;
    }

    public void fadeOut(final FadeOutListener listener){
        View rootView = findViewById(android.R.id.content);//whole activity view
        rootView.animate()
                .alpha(0)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        listener.onFadeOutEnd();//call back
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .setDuration(300)
                .start();
    }

    public interface FadeOutListener{
        void onFadeOutEnd();
    }
}
