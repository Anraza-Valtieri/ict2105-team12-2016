package com.example.chowdi.qremind.activities;

import android.animation.Animator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.chowdi.qremind.Customer.CurrentServingActivity;
import com.example.chowdi.qremind.R;
import com.example.chowdi.qremind.infrastructure.QremindApplication;
import com.example.chowdi.qremind.infrastructure.QueueInfo;
import com.example.chowdi.qremind.utils.Commons;
import com.example.chowdi.qremind.utils.Constants;
import com.example.chowdi.qremind.views.NavDrawer;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * Contributed by Chin Zhi Qiang on 3/26/2016.
 * Base activity class stub for activites
 */
public class BaseActivity extends AppCompatActivity {
    protected QremindApplication application;
    protected NavDrawer navDrawer;
    protected Toolbar toolbar;
    protected static ValueEventListener queueTurnListener = null;
    protected static Firebase fbRefQueueTurn;
    protected static boolean notificationPoppedOut = false;

    @Override
    protected void onCreate(Bundle savedState){
        super.onCreate(savedState);
        application = (QremindApplication)getApplication();//get application manifested in AndroidManifest.xml
        if(fbRefQueueTurn != null)
        {
            fbRefQueueTurn.removeEventListener(queueTurnListener);
        }
        if(application.getCustomerUser() != null && application.getCustomerUser().getCurrent_queue() != null && this.getClass() != CurrentServingActivity.class) {
            Object[] current_queue = application.getCustomerUser().getCurrent_queue().values().toArray();
            waitForTurn(Commons.keyToNoConverter((String)current_queue[1]) + "");
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        // To cancel and dismiss all current toast
        Commons.cancelToastMessage();
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

    @Override
    public void onBackPressed() {
        // To prevent the actual back button is press, instead like pressing home button
        moveTaskToBack(true);
    }

    /**
     * To set a listener to firebase queues for checking user's turns.
     * Invoke popup notification when user's turn is up.
     * @param queueNo to show the queue in the notification
     */
    protected void waitForTurn(final String queueNo)
    {
        if(fbRefQueueTurn != null)
        {
            fbRefQueueTurn.removeEventListener(queueTurnListener);
        }
        Object[] current_queue = application.getCustomerUser().getCurrent_queue().values().toArray();
        String queueKey = current_queue[1].toString();
        String shopKey = current_queue[0].toString();
        fbRefQueueTurn = new Firebase(Constants.FIREBASE_QUEUES).child(shopKey).child(queueKey);
        queueTurnListener = fbRefQueueTurn.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null)
                {
                    QueueInfo queueInfo = dataSnapshot.getValue(QueueInfo.class);
                    if(queueInfo.getCalling() != null)
                    {
                        if(!notificationPoppedOut)
                            popUpNotification(queueNo);
                        if(!application.notificationSend)
                            application.showNotification(BaseActivity.this);
                    }
                }
                else
                {
                    fbRefQueueTurn.removeEventListener(queueTurnListener);
                    Commons.showToastMessage("You have been removed from queue!", getApplicationContext());
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                handleFirebaseError(firebaseError);
            }
        });
    }

    /**
     * To handle all kind of firebase errors where to show the appropriate
     * and correct error messages on each errors
     * @param firebaseError FirebaseError
     */
    private void handleFirebaseError(FirebaseError firebaseError)
    {
        switch (firebaseError.getCode())
        {
            default:
                Commons.handleCommonFirebaseError(firebaseError, getApplicationContext());
                break;
        }
    }

    /**
     * Initialization of pop up box when queue number is reached
     * @param qNumber
     */
    public void popUpNotification(final String qNumber) {

        final AlertDialog.Builder dlg;
        dlg = new AlertDialog.Builder(this);
        notificationPoppedOut = true;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog alertDialog = dlg.create();
                alertDialog.setTitle("QRemind Notification");
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, (CharSequence) "OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int box) {
                        dialog.dismiss();
                    }
                });
                alertDialog.setMessage(qNumber + " , your turn is approaching.");
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
        });
    }
}
