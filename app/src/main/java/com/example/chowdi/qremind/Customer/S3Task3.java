package com.example.chowdi.qremind.Customer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.chowdi.qremind.R;
import com.example.chowdi.qremind.utils.Constants;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

public class S3Task3 extends AppCompatActivity {

    private TextView totalInQueue,InQueueBeforeUser;
    private String shop = "test_one";
    private String queue ="-KDEOe-qJCm76ihW9GMx";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s3_task3);

        Firebase.setAndroidContext(this);


        totalInQueue = (TextView)findViewById(R.id.textViewTotalInQueue);
        InQueueBeforeUser =(TextView) findViewById(R.id.textViewInQueueBeforeUser);
        Firebase ref = new Firebase(Constants.FIREBASE_QUEUES);
        Firebase refQueue = ref.child(shop);
        refQueue.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println("There are " + snapshot.getChildrenCount() + " in queue");
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        Firebase ref2 = new Firebase(Constants.FIREBASE_QUEUES);
        Firebase ref2Queue = ref2.child(shop);
        Query queryRef2 = ref2Queue.endAt(queue);
        queryRef2.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChild) {
                System.out.println("***there are "+snapshot.getChildrenCount()+" Before me");
                System.out.println("***previous child  "+previousChild+" ");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
            // ....
        });
    }
}
