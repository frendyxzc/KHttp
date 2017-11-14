package vip.frendy.khttpdemo;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;

import vip.frendy.khttp.Callback;
import vip.frendy.khttpdemo.entity.News;
import vip.frendy.khttpdemo.net.Request;

/**
 * Created by frendy on 2017/11/14.
 */

public class JavaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_java);

        Request req = new Request(this, new Gson());
        req.getNewsList("0", "0", new Callback<ArrayList<News>>() {
            @Override
            public void onSuccess(ArrayList<News> data) {
                Log.i("", "GET = " + data.get(0).getTitle());
            }
            @Override
            public void onFail(@Nullable String error) {

            }
        });
    }
}
