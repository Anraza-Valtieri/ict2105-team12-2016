package com.example.chowdi.qremind.Vendor;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.chowdi.qremind.Login_RegisterActivity;
import com.example.chowdi.qremind.R;
import com.example.chowdi.qremind.activities.BaseActivity;
import com.example.chowdi.qremind.utils.Commons;
import com.example.chowdi.qremind.views.VendorMainNavDrawer;
import com.firebase.client.Firebase;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.recyclerview.internal.CardArrayRecyclerViewAdapter;
import it.gmariotti.cardslib.library.recyclerview.view.CardRecyclerView;

public class VendorDashBoardActivity extends BaseActivity {
    private ArrayList<Card> dashboardCards;
    private AsyncTask runFirst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.vendor_dashboard_activity);
        setNavDrawer(new VendorMainNavDrawer(this));

        runFirst = new AsyncTask<Void, Void, Void>(){
            @Override
            protected void onPreExecute()
            {
                if(!Commons.isNetworkAvailable(getApplicationContext()))
                {
                    ((RelativeLayout)findViewById(R.id.layout_no_connection)).setVisibility(View.VISIBLE);
                }
            }
            @Override
            protected Void doInBackground(Void... params)
            {
                // Check network connection
                while(!Commons.isNetworkAvailable(getApplicationContext()))
                {
                    SystemClock.sleep(1000);
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void result)
            {
                ((RelativeLayout)findViewById(R.id.layout_no_connection)).setVisibility(View.GONE);
                init();
            }
        }.execute();
    }

    /**
     * initialise this activity operation
     */
    private void init()
    {
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

    /**
     * Create cards to show information on dash board
     */
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
                // Check network connection
                if (!Commons.isNetworkAvailable(getApplicationContext())) {
                    Commons.showToastMessage("No internet connection", getApplicationContext());
                    return;
                }
                startActivity(new Intent(VendorDashBoardActivity.this, ListOfCustomersInQueueActivity.class));
            }
        });

        cardServed = new CardPeopleServed(getApplicationContext(), application);
        cardServedH = new CardHeader(getApplicationContext());
        cardServedH.setTitle("No. of people served");
        cardServed.addCardHeader(cardServedH);
        cardServed.setBackgroundResourceId(R.color.card_base_cardwithlist_divider_color);
        cardServed.setCardElevation(25);

//        cardNext = new CardNextQueue(getApplicationContext(), application);
//        cardNextH = new CardHeader(getApplicationContext());
//        cardNextH.setTitle("Next Queue No");
//        cardNext.addCardHeader(cardNextH);
//        cardNext.setCardElevation(0);

        cardCurrent = new CardCurrentQueue(getApplicationContext(), application);
        cardCurrentH = new CardHeader(getApplicationContext());
        cardCurrentH.setTitle("Queue No");
        cardCurrent.addCardHeader(cardCurrentH);
        cardCurrent.setBackgroundResourceId(R.color.card_base_cardwithlist_divider_color);
        cardCurrent.setCardElevation(25);

        dashboardCards.add(cardCust);
//        dashboardCards.add(cardNext);
        dashboardCards.add(cardCurrent);
        dashboardCards.add(cardServed);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(runFirst.getStatus() == AsyncTask.Status.RUNNING || runFirst.getStatus() == AsyncTask.Status.PENDING)
        {
            runFirst.cancel(true);
        }
    }
}
