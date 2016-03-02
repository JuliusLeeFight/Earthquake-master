package com.example.lancelot.earthquake;


import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.SimpleCursorAdapter;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Lancelot on 2016/2/16.
 */
public class EarthquakeListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>{
    //ArrayAdapter<Quake> aa;
    //ArrayList<Quake> earthquakes = new ArrayList<Quake>();
    SimpleCursorAdapter adapter;
    private static final String TAG = "EARTHQUAKE";
    Handler handler = new Handler();
    int count = 0;


    @Override

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        adapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_list_item_1,null,
                new String[]{EarthquakeProvider.KEY_SUMMARY},
                new int[]{android.R.id.text1},0);

        setListAdapter(adapter);
        getLoaderManager().initLoader(0, null, this);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                refreshEarthquake();
            }
        });

        t.start();
        //int layoutId = android.R.layout.simple_list_item_1;
        //aa = new ArrayAdapter<Quake>(getActivity(), layoutId, earthquakes);
        //setListAdapter(aa);

//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                refreshEarthquake();
//            }
//        });
//
//        thread.start();

    }



    public void refreshEarthquake() {

        handler.post(new Runnable() {
            @Override
            public void run() {
                getLoaderManager().restartLoader(0, null, EarthquakeListFragment.this);
            }
        });
        Log.i(".....................service starting........", "starting");
        getActivity().startService(new Intent(getActivity(), EarthquakeUpdateService.class));
        Log.i(".....................service success........", "YES");
        Log.i(".....................which time to update.........", (count++) + "");


    }





    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] protection = new String[]{
                EarthquakeProvider.KEY_ID,
                EarthquakeProvider.KEY_SUMMARY,
        };

        EarthquakeActivity earthquakeActivity = (EarthquakeActivity)getActivity();
        String where = EarthquakeProvider.KEY_MAGNITUDE + ">" + earthquakeActivity.minimumMagnitude;
        CursorLoader loader = new CursorLoader(getActivity(),
                EarthquakeProvider.CONTENT_URI,protection,where, null,null);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        adapter.swapCursor(data);
        Log.i("....................onLoadFinished.adapter count..........", adapter.getCount() + "");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        adapter.swapCursor(null);
        Log.i(".....................onLoaderReset adapter count..........", adapter.getCount()+"");
    }

    @Override
    public void onPause() {

        super.onPause();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }
}
