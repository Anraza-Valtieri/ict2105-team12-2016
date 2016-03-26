package com.example.chowdi.qremind.Vendor;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.example.chowdi.qremind.R;
import com.example.chowdi.qremind.activities.BaseActivity;
import com.example.chowdi.qremind.views.VendorMainNavDrawer;
import com.firebase.client.Firebase;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.recyclerview.internal.CardArrayRecyclerViewAdapter;
import it.gmariotti.cardslib.library.recyclerview.view.CardRecyclerView;

public class VendorDashBoardActivity extends BaseActivity {
    ArrayList<Card> dashboardCards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_dash_board);
        setNavDrawer(new VendorMainNavDrawer(this));
        //Firebase stuff
        Firebase.setAndroidContext(this);
        //instantiate dashboardCards AL
        dashboardCards = new ArrayList<Card>();

        createCards();

        CardArrayRecyclerViewAdapter mCardArrayAdapter = new CardArrayRecyclerViewAdapter(this, dashboardCards);

        //Staggered grid view
        CardRecyclerView mRecyclerView = (CardRecyclerView) findViewById(R.id.carddemo_recyclerview);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Set the empty view
        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(mCardArrayAdapter);
        }
    }



    private void createCards(){
        Card cardCust,cardServed,cardCurrent,cardExtension;
        CardHeader cardCustH,cardServedH,cardCurrentH,cardExensionH;


        cardCust = new CardTotalPeople(getApplicationContext());
        cardCustH = new CardHeader(getApplicationContext());
        cardCustH.setTitle("No. of people in Queue");
        cardCust.addCardHeader(cardCustH);

        cardServed = new CardPeopleServed(getApplicationContext());
        cardServedH = new CardHeader(getApplicationContext());
        cardServedH.setTitle("No. of people served");
        cardServed.addCardHeader(cardServedH);

        cardCurrent = new CardCurrentQueue(getApplicationContext());
        cardCurrentH = new CardHeader(getApplicationContext());
        cardCurrentH.setTitle("Current Queue No.");
        cardCurrent.addCardHeader(cardCurrentH);

        cardExtension = new CardTimeExtension(getApplicationContext());
        cardExensionH = new CardHeader(getApplicationContext());
        cardExensionH.setTitle("Extension Requests");
        cardExtension.addCardHeader(cardCurrentH);

        dashboardCards.add(cardExtension);
        dashboardCards.add(cardCurrent);
        dashboardCards.add(cardCust);
        dashboardCards.add(cardServed);


    }



}
