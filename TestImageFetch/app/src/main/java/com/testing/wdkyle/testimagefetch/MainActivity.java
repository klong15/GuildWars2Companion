package com.testing.wdkyle.testimagefetch;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    ImageView header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        header = (ImageView) findViewById(R.id.image_header);
        getHeaderNews();
    }

    public void getHeaderNews(){

        String url = "https://www.guildwars2.com/en/";
        StringRequest htmlRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){

            @Override
            public void onResponse(String s) {
                //Log.d("MainActivityzz", s);
                Document doc = Jsoup.parse(s);
                parseHeader(doc);
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });

        htmlRequest.setTag("header_news_list");

        AppSingleton.getInstance(this).addToRequestQueue(htmlRequest);
    }

    public void parseHeader(Document doc){

        Elements headerElements = doc.getElementsByClass("yui3-charrousel-content");
        System.out.println(headerElements);

        Elements listItems = headerElements.get(0).getElementsByTag("li");
        System.out.println("size: " + listItems.size());
        for(int i = 0; i < listItems.size(); i++){

            System.out.println("data: " + listItems.get(i).attr("style"));
        }

        String url = listItems.get(0).attr("style");
        url = url.substring(url.lastIndexOf("//") + 2, url.length()-1);
        System.out.println("Image url: " + url);

        url = "https://" + url;
        ImageRequest imageRequest = new ImageRequest(url, new Response.Listener<Bitmap>(){
            @Override
            public void onResponse(Bitmap bitmap) {
                System.out.println("onResponse()...");
                header.setImageBitmap(bitmap);
            }
        }, 0, 0, null, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("onErrorResponse()... " + volleyError.toString());
            }
        });
        imageRequest.setTag("photo");
        AppSingleton.getInstance(this).addToRequestQueue(imageRequest);

        //Add click to image
        final String clickUrl = listItems.get(0).getElementsByClass("carousel-link-overlay").get(0).child(0).attr("href");
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(clickUrl));
                startActivity(i);
            }
        });
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
