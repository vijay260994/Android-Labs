package com.example.vijay.androidlabs;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.xmlpull.v1.XmlPullParser;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherForecast extends Activity
{
    ProgressBar progressBar;
    TextView currentTemp;
    TextView minTemp;
    TextView maxTemp;
    TextView windSpeed;
    ImageView weatherIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_forecast);

        Log.i("WeatherForecast", "In Oncreate() method");

        progressBar = findViewById(R.id.progress);
        progressBar.setVisibility(View.VISIBLE);

        currentTemp = findViewById(R.id.txtcurrent);
        minTemp = findViewById(R.id.txtmin);
        maxTemp = findViewById(R.id.txtmax);
        weatherIcon = findViewById(R.id.imgcurrent);
        windSpeed = findViewById(R.id.txtwind);

        ForecastQuery forecastQuery = new ForecastQuery();
        forecastQuery.execute("http://api.openweathermap.org/data/2.5/weather?q=ottawa,ca&APPID=d99666875e0e51521f0040a3d97d0f6a&mode=xml&units=metric");
}

    protected static Bitmap getImage(URL url)
    {
        Log.i("WeatherForecast", "In getImage");

        HttpURLConnection iconConn = null;
        try {
        iconConn = (HttpURLConnection) url.openConnection();
        iconConn.connect();
        int response = iconConn.getResponseCode();
        if (response == 200) {
            return BitmapFactory.decodeStream(iconConn.getInputStream());
        } else {
            return null;
        }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        finally {
            if (iconConn != null) {
                iconConn.disconnect();
            }
        }
    }

    public boolean fileExistance(String fileName)
    {
        Log.i("WeatherForecast", "In fileExistance");
        //Log.i("WeatherForecast" , getBaseContext().getFileStreamPath(fileName).toString());
        File file = getBaseContext().getFileStreamPath(fileName);
        return file.exists();
    }

    public class ForecastQuery extends AsyncTask<String,Integer,String>
    {
        String windspeed, mintemp, maxtemp, currenttemp, iconName;
        Bitmap currentweathericon;

        @Override
        protected String doInBackground(String... strings)
        {
            try
            {
                URL url = new URL(strings[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();

                InputStream inputStream = conn.getInputStream();
                XmlPullParser parser = Xml.newPullParser();
                parser.setInput(inputStream, null);

                while (parser.next() != XmlPullParser.END_DOCUMENT)
                {
                    if (parser.getEventType() != XmlPullParser.START_TAG)
                    {
                        continue;
                    }
                   else if (parser.getName().equals("temperature"))
                    {
                        currenttemp = parser.getAttributeValue(null, "value");
                        publishProgress(25);
                        mintemp = parser.getAttributeValue(null, "min");
                        publishProgress(50);
                        maxtemp = parser.getAttributeValue(null, "max");
                        publishProgress(75);
                    }
                   else if (parser.getName().equals("speed"))
                    {
                        windspeed = parser.getAttributeValue(null, "value");
                        //publishProgress(25);
                    }
                    else if (parser.getName().equals("weather"))
                    {
                        iconName = parser.getAttributeValue(null,"icon");
                        String iconFile = iconName+".png";

                        if (fileExistance(iconFile))
                       {
                            FileInputStream fileInputStream = null;
                            try {
                                fileInputStream = openFileInput(iconFile);
                                fileInputStream = new FileInputStream(getBaseContext().getFileStreamPath(iconFile));
                            }
                            catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            currentweathericon = BitmapFactory.decodeStream(fileInputStream);
                            Log.i("WeatherForecast", "Image already exists");
                        }
                        else
                        { URL iconUrl = new URL("http://openweathermap.org/img/w/" + iconName + ".png");
                            currentweathericon = getImage(iconUrl);
                            FileOutputStream outputStream = openFileOutput(iconName+".png", Context.MODE_PRIVATE);
                            currentweathericon.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                            outputStream.flush();
                            outputStream.close();
                            Log.i("WeatherForecast", "Adding new image");
                        }
                        Log.i("WeatherForecast", "file name=" + iconFile);
                        publishProgress(100);
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return "success";
        }
        
        @Override
        protected void onProgressUpdate(Integer... value)
        {
            Log.i("WeatherForecast", "In onProgressUpdate");
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(value[0]);
        }

        @Override
        protected void onPostExecute(String result)
        {
            String degree = Character.toString((char) 0x00B0);
            currentTemp.setText(currentTemp.getText()+currenttemp+degree+"C");
            minTemp.setText(minTemp.getText()+mintemp+degree+"C");
            maxTemp.setText(maxTemp.getText()+maxtemp+degree+"C");
            weatherIcon.setImageBitmap(currentweathericon);
            windSpeed.setText(windSpeed.getText()+windspeed+" m/s");
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

}
