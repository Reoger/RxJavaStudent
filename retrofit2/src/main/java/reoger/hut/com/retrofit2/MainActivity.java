package reoger.hut.com.retrofit2;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import reoger.hut.com.retrofit2.bean.IUserService;
import reoger.hut.com.retrofit2.bean.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit学习。
 * 参考博客：http://blog.csdn.net/ljd2038/article/details/51046512
 * 鸿洋：http://blog.csdn.net/lmj623565791/article/details/51304204
 * 简书：http://www.jianshu.com/p/308f3c54abdd
 * 官方教程：http://square.github.io/retrofit/
 * github地址：https://github.com/square/retrofit/
 *
 */
public class MainActivity extends AppCompatActivity {

    private Retrofit retrofit;

    private TextView mImageView;
    private IUserService repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = (TextView) findViewById(R.id.show_res);

        initRetrofit();
    }

    private void initRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl("http://172.18.9.31:8080/OkhttpTestService/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        repo = retrofit.create(IUserService.class);
    }


        public void onTestGet(View view){
            Call<ResponseBody> call =   repo.loginByGet("reoger","123456");
            executeByEn(call);
        }

    public void onTestPost(View view){
        Call<ResponseBody> call =   repo.loginByPost("reoger","12346");
        executeByEn(call);
    }


    public void onTestPostString(View view){
        RequestBody body=RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),"Here is my server to pass the data, can be a string, but of course, can also be JSON data");
        Call<ResponseBody> call = repo.postString(body);
        executeByEn(call);
    }

    public void onTestPostJson(View view){

        Call<ResponseBody> call = repo.postJson(new User("reoger","love"));
        executeByEn(call);
    }

    public void onTestPostSingerFile(View view){
        File file = new File(Environment.getExternalStorageDirectory(),"test.jpg");
        if(!file.exists()){
            Log.e("TAG","file is not exit!");
            return ;
        }
        RequestBody photoRequestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        MultipartBody.Part photo = MultipartBody.Part.createFormData("mPhoto", "test.jpg", photoRequestBody);

        Call<ResponseBody> call = repo.uploadSingerFile(photo, RequestBody.create(null, "abc"), RequestBody.create(null, "123"));
       executeByEn(call);
    }

    public void onTestPostMultiFile(View view){
    //此方法来自 http://blog.csdn.net/lmj623565791/article/details/51304204
//        File file = new File(Environment.getExternalStorageDirectory(), "test.jpg");
//        RequestBody photo = RequestBody.create(MediaType.parse("application/octet-stream"), file);
//        Map<String,RequestBody> photos = new HashMap<>();
//        photos.put("image\"; filename=\"icon.png", photo);
//        photos.put("username",  RequestBody.create(null, "abc"));
//
//        Call<ResponseBody> call = repo.uploadMultiparFile(photos,RequestBody.create(null,"chuchu"));
//
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//            }
//        });

//此方法来自 http://www.tuicool.com/articles/emQV7vi
        File file1 = new File(Environment.getExternalStorageDirectory(),"test.jpg");
        File file2 = new File(Environment.getExternalStorageDirectory(),"test.JPEG");

        RequestBody photoRequestBody1 = RequestBody.create(MediaType.parse("application/octet-stream"), file1);
        RequestBody photoRequestBody2 = RequestBody.create(MediaType.parse("application/octet-stream"), file2);
        MultipartBody.Part photo1 = MultipartBody.Part.createFormData("image", "test22.jpg", photoRequestBody1);
        MultipartBody.Part photo2 = MultipartBody.Part.createFormData("image", "test33.jpg", photoRequestBody2);

        List<MultipartBody.Part> parts = new ArrayList<>();
        parts.add(photo1);
        parts.add(photo2);

        Call<ResponseBody> call = repo.upload(parts);
        executeByEn(call);
    }

    public void onTestdownLoad(View view){
        Call<ResponseBody> call = repo.download();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //save file in here

                Log.d("TAG","downFile...");
                InputStream is = response.body().byteStream();
                int len;
                try {
                    File file = new File(Environment.getExternalStorageDirectory(),"download.jpg");
                    FileOutputStream fos = new FileOutputStream(file);
                    byte[] buf = new byte[128];
                    while( (len=is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    fos.flush();
                    fos.close();
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.d("TAG","down success!");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }


    private void executeByEn(Call<ResponseBody> call) {
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    mImageView.setText(response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mImageView.setText("file to get");
            }
        });
    }

}
