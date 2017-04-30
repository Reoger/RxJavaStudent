package reoger.hut.com.rxjavastudent;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import reoger.hut.com.rxjavastudent.observer.NextActivity;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 这个工程用于学习RXjava的学习
 * 参考博文：http://gank.io/post/560e15be2dca930e00da1083
 * http://www.jianshu.com/p/5e93c9101dc5
 */
public class MainActivity extends AppCompatActivity {

    private ImageView mImage;

    private int drawableRes = R.mipmap.ic_launcher;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mImage = (ImageView) findViewById(R.id.image);


        Observable.create(new Observable.OnSubscribe<Drawable>() {
            @Override
            public void call(Subscriber<? super Drawable> subscriber) {
                Drawable drawable = getResources().getDrawable(drawableRes);

                try {
                    Thread.sleep(7000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                subscriber.onNext(drawable);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())//指定subscribe发生在IO线程
                .observeOn(AndroidSchedulers.mainThread())//指定Subscriber的回调发生在主线程中
                .subscribe(new Observer<Drawable>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(Drawable drawable) {
                        mImage.setImageDrawable(drawable);
                    }
                });
    }

    public void nextActivity(View view){
        startActivity(new Intent(MainActivity.this, NextActivity.class));

    }


}
