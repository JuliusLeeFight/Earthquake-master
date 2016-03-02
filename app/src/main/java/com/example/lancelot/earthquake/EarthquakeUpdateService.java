package com.example.lancelot.earthquake;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.os.IBinder;

import android.preference.PreferenceManager;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Julius on 2016/2/25.
 */
public class EarthquakeUpdateService extends Service {


    private Timer updateTimer;
    public static String TAG = "EARTHQUAKE_UPDATE_SERVICE";
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Context context = getApplicationContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int updateFreq = Integer.parseInt(sharedPreferences.getString(PreferencesActivity.PREF_UPDATE_FREQ, "3"));
        boolean autoUpdateChecked = sharedPreferences.getBoolean(PreferencesActivity.PREF_AUTO_UPDATE,true);
        Log.i(".....................autoUpdateChecked........:  ", autoUpdateChecked + "");
        Log.i(".....................updateFreq........:  ", updateFreq + "");
        updateTimer.cancel();
        if (autoUpdateChecked){
            updateTimer = new Timer("earthquakeUpdates");
            updateTimer.scheduleAtFixedRate(doRefresh,0, updateFreq*1000);
        }else {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    refreshEarthquakes();
                }
            });
            t.start();
        }
        return Service.START_STICKY;
    }

    private TimerTask doRefresh = new TimerTask() {
        @Override
        public void run() {
            //Log.i(".....................which time to update.........",count++ +"");
            refreshEarthquakes();
        }
    };

    @Override
    public void onCreate() {

        updateTimer = new Timer("earthquakeUpdates");
    }

    /**
    * check quake time if first appeared add to SQLite database through ContentProvider
    * */
    private void addNewQuake(Quake quake){

        ContentResolver contentResolver = getContentResolver();
        String where = EarthquakeProvider.KEY_DATE + "=" + quake.getDate().getTime();

        Cursor cursor = contentResolver.query(EarthquakeProvider.CONTENT_URI,null,where,null,null);
        if (cursor.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put(EarthquakeProvider.KEY_DATE, quake.getDate().getTime());
            values.put(EarthquakeProvider.KEY_DETAILS, quake.getDetails());
            values.put(EarthquakeProvider.KEY_SUMMARY, quake.toString());
            //double lat = quake.getLocation().getLatitude();
            //double lng = quake.getLocation().getLongitude();

            values.put(EarthquakeProvider.KEY_LINK, quake.getLink());
            values.put(EarthquakeProvider.KEY_MAGNITUDE, quake.getMagnitude());
            contentResolver.insert(EarthquakeProvider.CONTENT_URI, values);
        }
        cursor.close();
    }

    public void refreshEarthquakes(){
        URL url;
        String quakeFeed = getString(R.string.quake_feed);

        try {
            url = new URL(quakeFeed);
            URLConnection connection = url.openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection)connection;
            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK){

                InputStream in = httpURLConnection.getInputStream();
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document dom = db.parse(in);
                Element docEle = dom.getDocumentElement();
                NodeList nl = docEle.getElementsByTagName("entry");
                if(nl != null&&nl.getLength()!=0){
                    int addCount = 0;
                    for (int i = 0 ; i < nl.getLength() ; i++){
                        Element entry = (Element)nl.item(i);
                        Element title = (Element)entry.getElementsByTagName("title").item(0);
                        Element g = (Element)entry.getElementsByTagName("georss:point").item(0);
                        Element when = (Element)entry.getElementsByTagName("updated").item(0);
                        Element link = (Element)entry.getElementsByTagName("link").item(0);

                        String details = title.getFirstChild().getNodeValue();
                        String hostname = "http://earthquake.usgs.gov";
                        String linkString = hostname + link.getAttribute("href");

                        String point = g.getFirstChild().getNodeValue();
                        String dt = when.getFirstChild().getNodeValue();

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");
                        Date qdate = new GregorianCalendar(0, 0, 0).getTime();
                        try {
                            qdate = sdf.parse(dt);
                        } catch (ParseException e) {
                            Log.d(TAG, "Date parsing exception.", e);
                        }

                        String[] location = point.split(" ");
                        Location l = new Location("dummyGPS");
                        l.setLatitude(Double.parseDouble(location[0]));
                        l.setLongitude(Double.parseDouble(location[1]));

                        String magnitudeString = details.split(" ")[1];
                        int end = magnitudeString.length()-1;
                        double magnitude = Double.parseDouble(magnitudeString.substring(0,end));
                        //details = details.split(",")[1].trim();
                        Quake quake = new Quake(qdate,details,l,magnitude,linkString);
                        Context context = getApplicationContext();
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                        double minimumMagnitude = Double.parseDouble(sharedPreferences.getString(PreferencesActivity.PREF_MIN_MAG, "0"));
                        if (magnitude-minimumMagnitude>0){
                            System.out.println("+++++++++++magnitude++++++++++" + magnitude + "      and minMagni = " + minimumMagnitude);
                            //addNewQuake(quake);
                            System.out.println("+++++++++++add count++++++++++"+ (addCount++));
                        }
                        addNewQuake(quake);

                    }
                }


            }
        } catch (MalformedURLException e) {
            Log.e(TAG,"MalformedURLException",e);
        } catch (IOException e){
            Log.e(TAG,"IOException",e);
        } catch (ParserConfigurationException e){
            Log.e(TAG,"ParserConfigurationException",e);
        } catch (SAXException e){
            Log.e(TAG,"SAXException",e);
        }finally {

        }

    }
}

