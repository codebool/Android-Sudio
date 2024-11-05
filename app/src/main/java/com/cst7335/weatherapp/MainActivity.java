package com.cst7335.weatherapp;

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

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FetchWeatherTask().execute("https://api.openweathermap.org/data/2.5/weather?q=Ottawa,ca&APPID=1712c545a8c8092e02b65c55a76c5e95");
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
            try {
                JSONObject jsonObject = new JSONObject(result);
                StringBuilder formattedResult = new StringBuilder();

                // There is no JSON Array List name: "list" in the JSON response. So, we can directly extract the data from the root level

                // Extract the timestamp at the root level and format it
                long dateTimeInMillis = jsonObject.getLong("dt") * 1000;
                Date date = new Date(dateTimeInMillis);
                SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE, MMMM d, yyyy h:mm a", Locale.getDefault());
                String dateText = dateFormatter.format(date);

                // Extract the main temperature data
                JSONObject main = jsonObject.getJSONObject("main");
                double temp = main.getDouble("temp") - 273.15; // Convert Kelvin to Celsius
                String temperature = String.format(Locale.getDefault(), "%.1f°C", temp);
                int humidity = main.getInt("humidity");

                // Extract weather description
                JSONArray weatherArray = jsonObject.getJSONArray("weather");
                JSONObject weather = weatherArray.getJSONObject(0);
                String description = weather.getString("description");

                // Extract wind data
                JSONObject wind = jsonObject.getJSONObject("wind");
                double windSpeed = wind.getDouble("speed");
                int windDirection = wind.getInt("deg");

                // Append all extracted data to formattedResult
                formattedResult.append("Date: ").append(dateText)
                        .append("\nWeather: ").append(description)
                        .append("\nTemperature: ").append(temperature)
                        .append("\nHumidity: ").append(humidity).append("%")
                        .append("\nWind: ").append(String.format(Locale.getDefault(), "%.1f m/s, %d°", windSpeed, windDirection))
                        .append("\n\n");

                // Display the result in the weatherTextView
                weatherTextView.setText(formattedResult.toString());

            } catch (Exception e) {
                e.printStackTrace();
                weatherTextView.setText("Failed to parse data");
            }

        }

    }

}


