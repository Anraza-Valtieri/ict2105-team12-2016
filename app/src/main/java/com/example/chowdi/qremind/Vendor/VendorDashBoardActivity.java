package com.example.chowdi.qremind.Vendor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

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

        if(application.getVendorUser().getShops() == null)
        {
            startActivity(new Intent(this, BusinessProfileActivity.class));
            this.finish();
            return;
        }

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
        Card cardCust,cardServed,cardCurrent,cardNext;
        CardHeader cardCustH,cardServedH,cardCurrentH,cardNextH;


        cardCust = new CardTotalPeople(getApplicationContext(), application);
        cardCustH = new CardHeader(getApplicationContext());
        cardCustH.setTitle("No. of people in Queue");
        cardCust.addCardHeader(cardCustH);
        cardCust.setCardElevation(25);
        cardCust.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                startActivity(new Intent(VendorDashBoardActivity.this, ListOfCustomersInQueueActivity.class));
            }
        });
        cardServed = new CardPeopleServed(getApplicationContext(), application);
        cardServedH = new CardHeader(getApplicationContext());
        cardServedH.setTitle("No. of people served");
        cardServed.addCardHeader(cardServedH);
        cardServed.setCardElevation(0);

        cardNext = new CardNextQueue(getApplicationContext(), application);
        cardNextH = new CardHeader(getApplicationContext());
        cardNextH.setTitle("Next Queue No");
        cardNext.addCardHeader(cardNextH);
        cardNext.setCardElevation(0);

        cardCurrent = new CardCurrentQueue(getApplicationContext(), application);
        cardCurrentH = new CardHeader(getApplicationContext());
        cardCurrentH.setTitle("Current Queue No");
        cardCurrent.addCardHeader(cardCurrentH);
        cardCurrent.setCardElevation(0);

        dashboardCards.add(cardNext);
        dashboardCards.add(cardCurrent);
        dashboardCards.add(cardCust);
        dashboardCards.add(cardServed);
    }
}
