package com.cst7335.weatherapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextView weatherTextView;

    //    String apiKey = "1712c545a8c8092e02b65c55a76c5e95";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherTextView = findViewById(R.id.textView_weather);
        Button refreshButton = findViewById(R.id.button_refresh);
        Button gotoTestToolbar = findViewById(R.id.button_go_to_toolbar);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // new FetchWeatherTask().execute("http://api.openweathermap.org/data/2.5/forecast?id=524901&appid=bd5e378503939ddaee76f12ad7a97608");
//                 new FetchWeatherTask().execute("https://api.openweathermap.org/data/2.5/weather?q=Ottawa,ca&APPID=080c60be76c8fe36e624dc83b2550bbb");
                new FetchWeatherTask().execute("https://api.openweathermap.org/data/2.5/forecast?id=524901&APPID=080c60be76c8fe36e624dc83b2550bbb");
            }
        });

        // Set an OnClickListener on the button to start the TestToolbar activity
        gotoTestToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the TestToolbar activity when the button is clicked
                Intent intent = new Intent(MainActivity.this, TestToolbar.class);
                startActivity(intent);
            }
        });
    }

    private class FetchWeatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result = new StringBuilder();
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int date = reader.read();
                while (date != -1) {
                    char current = (char) date;
                    result.append(current);
                    date = reader.read();
                }
                return result.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return "Failed to fetch data";
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray listArray = jsonObject.getJSONArray("list");
                StringBuilder formattedResult = new StringBuilder();

                for (int i = 0; i < listArray.length(); i++) {
                    JSONObject listObject = listArray.getJSONObject(i);
                    long dateTimeInMillis = listObject.getLong("dt") * 1000; // Convert to milliseconds
                    Date date = new Date(dateTimeInMillis);
                    SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE, MMMM d, YYYY h:mm a", Locale.getDefault());
                    String dateText = dateFormatter.format(date);

                    JSONObject main = listObject.getJSONObject("main");
                    double temp = main.getDouble("temp") - 273.15; // Convert from Kelvin to Celsius
                    String temperature = String.format(Locale.getDefault(), "%.1f°C", temp);

                    JSONArray weatherArray = listObject.getJSONArray("weather");
                    JSONObject weather = weatherArray.getJSONObject(0);
                    String description = weather.getString("description");

                    formattedResult.append(dateText)
                            .append("\nWeather: ").append(description)
                            .append("\nTemperature: ").append(temperature)
                            .append("\n\n");
                }

                weatherTextView.setText(formattedResult.toString());
            } catch (Exception e) {
                e.printStackTrace();
                weatherTextView.setText("Failed to parse data");
            }
        }


    }

}


