package reoger.hut.com.rxjavastudent.observer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import reoger.hut.com.rxjavastudent.R;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class NextActivity extends AppCompatActivity {

    private ImageView mImageView;

    private int drawRes = R.mipmap.ic_launcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);

        mImageView = (ImageView) findViewById(R.id.imageview);

        Observable observable = Observable.just(drawRes);

        observable.subscribeOn(Schedulers.io());//指定subscribe发生在Io线程
        observable.observeOn(AndroidSchedulers.mainThread());//指定Subscriber的回调发生在主线程
        observable.subscribe(subscriber);




        Observable observable1 = Observable.create(new Observable.OnSubscribe<Integer>() {

            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                subscriber.onNext(123);
                subscriber.onCompleted();
            }
        });
    }

    Subscriber<Integer> subscriber = new Subscriber<Integer>() {
        @Override
        public void onCompleted() {
            //完成之后执行的回调
        }

        @Override
        public void onError(Throwable e) {
            //发生错误时执行的回调
        }

        @Override
        public void onNext(Integer o) {
            //普通事件，发出就需要执行完毕
            mImageView.setImageResource(o);
        }

        @Override
        public void onStart() {
            super.onStart();
            //开始之前的准备工作在这里执行(在Subscriber增加的方法)
        }

    };

    Observer<Integer> observer  = new Observer <Integer>(){

        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(Integer integer) {

        }
    };



    Func0 func0 = new Func0() {
        @Override
        public Object call() {
            return null;
        }
    };

    Func1<String,Integer> func1 = new Func1<String, Integer>() {
        @Override
        public Integer call(String s) {
            return null;
        }
    };

}
