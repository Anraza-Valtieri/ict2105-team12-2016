package com.example.chowdi.qremind.firebase;

/**
 * Created by L on 3/6/2016.
 */

import com.example.chowdi.qremind.models.Customer;
import com.example.chowdi.qremind.utils.Constants;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by L on 3/6/2016.
 */
public class CustomerFirebase {
    Firebase refBase;
    String hpNo = "98765432";
    //MainActivity ma;
    private static final String TAG = CustomerFirebase.class.getSimpleName();

    public CustomerFirebase(/*MainActivity ma*/) {
        //this.ma = ma;
        refBase = new Firebase(Constants.FIREBASE_CUSTOMER);
        //will it take additional resources during onCreate?
    }

    public void joinQueue(String shopName) {
        Map<String, Object> joinq = new HashMap<String, Object>();
        joinq.put(shopName, true);
        Firebase checkQ = refBase.child(hpNo).child("queues");//current user
        checkQ.updateChildren(joinq);

    }

    public void leaveQueue(String shopName) {
        Map<String, Object> leaveq = new HashMap<String, Object>();
        leaveq.put(shopName, null);
        Firebase checkQ = refBase.child(hpNo).child("queues");//current user
        checkQ.updateChildren(leaveq);
    }

    public void isInQueue(String shopName) {
        Firebase checkQ = refBase.child(hpNo).child("queues").child(shopName);//current user
        checkQ.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String result = snapshot.getValue() == null ? "is not" : "is";
                System.out.println(hpNo + " " + result + " queueing ");
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // ignore
            }
        });

    }

    public void getCurrentCustomer(){

        Firebase customerRef = refBase.child(hpNo);
        customerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Customer result = snapshot.getValue(Customer.class);
                if(result == null){
                    //ma.setCname("NOTHING");
                }else{
                    //ma.setCname(result.getFirstname());
                }

            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // ignore
            }
        });


    }

}