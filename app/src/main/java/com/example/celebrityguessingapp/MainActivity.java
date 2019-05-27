package com.example.celebrityguessingapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends Activity {

    ArrayList<String> flagURLs = new ArrayList<String>();
    ArrayList<String> flagName = new ArrayList<String>();

    ImageView im;
    int chosenFlag;
    Button b0;
    Button b1;
    Button b2;
    Button b3;

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {

            try {

                URL url = new URL(urls[0]);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                InputStream in = urlConnection.getInputStream();

                Bitmap bitmap = BitmapFactory.decodeStream(in);

                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection;

            try {

                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data;
                data = reader.read();

                while(data != -1)
                {
                    char current = (char) data;
                    result+=current;

                    data = reader.read();
                }

                return result;

            } catch (Exception e) {
                e.printStackTrace();
            }

            return "Failed";
        }
    }


    public void generateQuestion() throws ExecutionException, InterruptedException {
        ImageDownloader imageDownloader = new ImageDownloader();
        Bitmap bitmap;

        Random random = new Random();
        chosenFlag = random.nextInt(flagURLs.size());
        String finalString = "https://www.countries-ofthe-world.com/" + flagURLs.get(chosenFlag);

        bitmap = imageDownloader.execute(finalString).get();
        im.setImageBitmap(bitmap);

        int[] arr = new int[4];
        int holder;
        for(int i=0; i<4; i++)
        {
            holder = random.nextInt(flagURLs.size());
            while(holder==chosenFlag)
                holder = random.nextInt(flagURLs.size());
            arr[i] = holder;
        }

        holder = random.nextInt(4);
        arr[holder] = chosenFlag;

        b0.setText(flagName.get(arr[0]));
        b1.setText(flagName.get(arr[1]));
        b2.setText(flagName.get(arr[2]));
        b3.setText(flagName.get(arr[3]));

    }

    public void createNewQuestion(View view) throws ExecutionException, InterruptedException {

        Button b = (Button)view;
        String s = b.getText().toString();

        if(s.equals(flagName.get(chosenFlag))){
            Toast.makeText(this, "Correct Answer", Toast.LENGTH_LONG).show();
        }

        else
        {
            Toast.makeText(this, "Wrong answer!! The correct answer is: " + flagName.get(chosenFlag), Toast.LENGTH_LONG).show();
        }

        generateQuestion();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        im = (ImageView)findViewById(R.id.flagImage);
        DownloadTask task = new DownloadTask();
        String result = null;

        b0 = (Button) findViewById(R.id.button1);
        b1 = (Button) findViewById(R.id.button2);
        b2 = (Button) findViewById(R.id.button3);
        b3 = (Button) findViewById(R.id.button4);


        try {
            result = task.execute("https://www.countries-ofthe-world.com/flags-of-the-world.html").get();
            Pattern p = Pattern.compile("<img src=\"(.*?)\"");
            Matcher m = p.matcher(result);

            while(m.find()){
                flagURLs.add(m.group(1));
            }

            p = Pattern.compile("</td><td>(.*?)</td>");
            m = p.matcher(result);
            while(m.find()){
                flagName.add(m.group(1));
            }

            generateQuestion();

            Log.i("Set", "Image Settttt");

        } catch (ExecutionException e) {

            e.printStackTrace();

        } catch (InterruptedException e) {

            e.printStackTrace();

        }

    }

}
