package reoger.hut.com.retrofit2.bean;


import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

/**
 * Created by 24540 on 2017/4/29.
 */

public interface IUserService {
    @GET("rlogin")
    Call<ResponseBody> loginByGet(@Query("user") String user, @Query("passwd") String passwd);


    @POST("rlogin")
    @FormUrlEncoded
    Call<ResponseBody> loginByPost(@Field("user")String user,@Field("passwd") String passwd);

    @POST("rpostString")
    Call<ResponseBody>  postString(@Body RequestBody user);

    @POST("rpostString")
    Call<ResponseBody> postJson(@Body User user);

    @Multipart
    @POST("rpostSingerFile")
    Call<ResponseBody> uploadSingerFile(@Part MultipartBody.Part mPhoto, @Part("user")RequestBody user,@Part("passwd") RequestBody passwd);

    @Multipart
    @POST("rpostMulitFile")
    Call<ResponseBody> uploadMultiparFile(@PartMap Map<String,RequestBody> params,@Part("user") RequestBody user);

    @Multipart
    @POST("rpostMulitFile")
    Call<ResponseBody> upload(@Part()List<MultipartBody.Part> parts);

    @GET("files/test.jpg")
    Call<ResponseBody> download();


}
