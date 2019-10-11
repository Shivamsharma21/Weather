package com.example.weather;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

             String main=null;
              String description =null;
              Button button;
             TextView textView;
              EditText cityname;
             String message =null;

             //function is called by When user Tap On "what's weather Button"//

   public void findWeather(View view){

        Log.i("Name = ", cityname.getText().toString());

        try {
               //remove the keyboard from the screen when user Accomplished the Input data//
            InputMethodManager imr =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

            imr.hideSoftInputFromWindow(cityname.getWindowToken(),0);

            String perfecturl = URLEncoder.encode(cityname.getText().toString(),"UTF-8");

            Task task = new Task();
       //Run a Task for perform Action In the Background of the app
            //also give a url of api for Accessing json Content
            task.execute("https://openweathermap.org/data/2.5/weather?q="+perfecturl +"&appid=b6907d289e10d714a6e88b30761fae22");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }
 /// TAsk class Perform Background Actions
         class Task extends AsyncTask<String,Void,String>{

       @Override
       protected String doInBackground(String... urls) {

           String RESULT ="";
           try {                                   //Established The Connection,InputStream,InputStreamReader
               URL url = new URL(urls[0]);
               HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                httpsURLConnection.connect();
               InputStream inputStream = httpsURLConnection.getInputStream();
               InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

               int data = inputStreamReader.read();  //getting the Data From the Input streamReader//
               while(data !=-1){

                   char current =(char) data;
                   RESULT += current;
                   data = inputStreamReader.read();
               }
                return RESULT;  //return the string Containing Api Data as Json string Format//
           } catch (Exception e) {
               Toast.makeText(MainActivity.this, "Can't find weather", Toast.LENGTH_SHORT).show();

           }
           return null;
       }

       @RequiresApi(api = Build.VERSION_CODES.KITKAT)
       @Override
       protected void onPostExecute(String RESULT) {
           super.onPostExecute(RESULT);
        Log.i("RESULT = ",RESULT);
           try {
               JSONObject jsonObject = new JSONObject(RESULT); //Creating The Json Object
                                                               //from the URL Content

               String mainstr = jsonObject.getString("main");   //get the string "main" by jsonobj inside String s1

                 String windstr = jsonObject.getString("wind"); //same as mainstr

               String weatherinfo = jsonObject.getString("weather");

               JSONObject new2Obj = new JSONObject(windstr);
               JSONArray jsonArray = new JSONArray(weatherinfo);
                JSONObject newOBj = new JSONObject(mainstr);

                  int Pressure = newOBj.getInt("pressure"); // getting Individual value of APi String
                   int Humidity = newOBj.getInt("humidity");
                    int Temprature = newOBj.getInt("temp");
                      int wind = new2Obj.getInt("speed");

                    for (int i =0; i< jsonArray.length(); i++){ //Extract the Each Obj Value At a Run
                   JSONObject part = jsonArray.getJSONObject(i);
                  main = part.getString("main");
                   description = part.getString("description");
                }
          //Checking that We get Correct value from the server which is requested by us
            if(main !=null && description != null && Pressure != -1 && Humidity != -1 && wind != -1){
                      message = "OverAll :"+ main +"\n"+ "Sky:"+description +"\n"
                              +"wind :"+wind+"km/h"+"\n"+"Humidity :"+Humidity+"%"+"\n"+"Temprature:"+Temprature+"°C"+"\n";
                    }
            //check that data all Values are Proper and then Display them
                    if (message!= null){
                        textView.setText(message);
                    }
                    //Message when did'nt get the desired data
                    else{
                        Toast.makeText(MainActivity.this, "Can't find weather message", Toast.LENGTH_SHORT).show();
                    }
           } catch (JSONException e) {
               e.printStackTrace();
               Toast.makeText(MainActivity.this, "Can't find weather", Toast.LENGTH_SHORT).show();
           }
       }
   }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       button = findViewById(R.id.button);
       textView = findViewById(R.id.information_tv);
       cityname = findViewById(R.id.cityname);
              }
}
