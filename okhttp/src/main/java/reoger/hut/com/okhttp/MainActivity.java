package reoger.hut.com.okhttp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * okhttp的使用
 * github地址：https://github.com/square/okhttp
 * 视屏：http://www.imooc.com/video/13576
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView mText;
    private ImageView mDownImage;
    private Button mGet;
    private Button mPost;
    private Button mPostString;
    private Button mPostFile;
    private Button mUpLoad;
    private Button mDownLoad;
    OkHttpClient okHttpClient ;


    //换成你自己的地址
        private String BASE_URL = "http://172.18.9.31:8080/OkhttpTestService/";

        @Override
        protected void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);


            okHttpClient= new OkHttpClient.Builder()
                    .cookieJar(new CookieJar() {
                        private final HashMap<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();

                        @Override
                        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                            cookieStore.put(url, cookies);
                        }

                        @Override
                        public List<Cookie> loadForRequest(HttpUrl url) {
                            List<Cookie> cookies = cookieStore.get(url);
                            return cookies != null ? cookies : new ArrayList<Cookie>();
                        }
                    })
                    .build();

        initView();
    }

    private void initView() {
        mGet = (Button) findViewById(R.id.but_get);
        mPost = (Button) findViewById(R.id.bu_post);
        mPostString = (Button) findViewById(R.id.bu_post_string);
        mPostFile = (Button) findViewById(R.id.bu_post_file);
        mUpLoad = (Button) findViewById(R.id.bu_upload);
        mDownLoad = (Button) findViewById(R.id.bu_download);

        mText = (TextView) findViewById(R.id.text_data);
        mDownImage = (ImageView) findViewById(R.id.downImage);


        mGet.setOnClickListener(this);
        mPost.setOnClickListener(this);
        mPostString.setOnClickListener(this);
        mPostFile.setOnClickListener(this);
        mUpLoad.setOnClickListener(this);
        mDownLoad.setOnClickListener(this);
    }




    private void doGet(){
        //1.拿到okhttpClient对象
//        OkHttpClient okHttpClient = new OkHttpClient();

        //2. 构造request对象
        Request.Builder builder = new Request.Builder();
        Request request = builder.get().url(BASE_URL+"login?username=roger&passwd=123456").build();

        execute(request);


    }

    private void execute(Request request) {
        //3. 执行Call
        okhttp3.Call call = okHttpClient.newCall(request);
        //4.执行
        //同步执行
        //call.execute();
        //异步执行
        call.enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.e("TAG","Error"+e);
            }
            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {

                final String repo = response.body().string();
                Log.d("TAG",repo+" ");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mText.setText(repo);
                    }
                });
            }
        });
    }


    private void doPost(){

        RequestBody requestBody = new FormBody.Builder().add("username","reoger").add("passwd","123").build();

        Request.Builder builder = new Request.Builder();
        Request request = builder.post(requestBody).url(BASE_URL+"login").build();

        execute(request);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.but_get://get 请求
                doGet();
                break;
            case R.id.bu_post://post 请求
                doPost();
                break;
            case R.id.bu_post_string://post上传string或者json数据
                doPostString();
                break;
            case R.id.bu_post_file:
                doPostFile();
                break;
            case R.id.bu_upload:
                doUpload();
                break;
            case R.id.bu_download:
                doDownLoad();
                break;
        }
    }

    private void doDownLoad() {

        Request.Builder builder = new Request.Builder();
        Request request = builder.get().url(BASE_URL+"files/test2.jpg").build();

        okhttp3.Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.e("TAG","Error"+e);
            }
            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {

                //显示到imageView上
               // showImgInImageView(response);

                //保存到本地，
                downSavetoFile(response);
            }
        });

    }

    /**
     * 保存到本地
     * @param response
     * @throws IOException
     */
    private void downSavetoFile(Response response) throws IOException {

        long total = response.body().contentLength();
        long sum = 0L;

        Log.d("TAG","downFile...");
        InputStream is = response.body().byteStream();
        int len;
        File file = new File(Environment.getExternalStorageDirectory(),"12346.jpg");
        FileOutputStream fos = new FileOutputStream(file);
        byte[] buf = new byte[128];
        while( (len=is.read(buf)) != -1) {
            fos.write(buf, 0, len);
            sum+= len;
            Log.d("TAG","--------进度"+sum+" / "+total);
        }
        fos.flush();
        fos.close();
        is.close();
        Log.d("TAG","down success!");
    }

    //显示图片到imageView上
    private void showImgInImageView(Response response) {
    InputStream is = response.body().byteStream();
        final Bitmap bitmap = BitmapFactory.decodeStream(is);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDownImage.setImageBitmap(bitmap);
            }
        });
    }

    private void doPostFile() {
        File file = new File(Environment.getExternalStorageDirectory(),"test.jpg");
        if(!file.exists()){
            Log.e("TAG","file is not exit!");
            return ;
        }

        // 可以搜索mime type 获得MediaType.parse()里面的值与对象
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"),file);
        Request.Builder builder = new Request.Builder();
        Request request = builder.post(requestBody).url(BASE_URL+"postFile").build();
        execute(request);
    }


    private void doUpload() {
        File file = new File(Environment.getExternalStorageDirectory(),"test.jpg");
        if(!file.exists()){
            Log.e("TAG","file is not exit!");
            return ;
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"),file);

        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("username","123465")
                .addFormDataPart("passwd","pppp")
                .addFormDataPart("mPhoto","test2.jpg",requestBody)
                .build();
        CountingRequestBody countingRequestBody = new CountingRequestBody(multipartBody, new CountingRequestBody.Listener() {
            @Override
            public void onRequestProgress(long byteWrited, long contentLength) {
                    Log.d("上传进度",byteWrited+" / "+contentLength);
            }
        });

        Request.Builder builder = new Request.Builder();
        Request request = builder.post(countingRequestBody).url(BASE_URL+"uploadImg").build();
        execute(request);
    }


    private void doPostString() {
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain;charset=utf-8"),"this is json data or string data");
        Request.Builder builder = new Request.Builder();
        Request request = builder.post(requestBody).url(BASE_URL+"postString").build();
        execute(request);
    }




}
