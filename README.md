# RxJavaStudent
>OKHttp是一款高效的HTTP客户端，支持连接同一地址的链接共享同一个socket，通过连接池来减小响应延迟，还有透明的GZIP压缩，请求缓存等优势 。今天就记录以下使用Okhttp的心得和体会。

[![Github All Releases](https://img.shields.io/github/downloads/atom/atom/total.svg)](https://github.com/Reoger/RxJavaStudent)

---
首先先列出我的参考资料：
* [慕课大神hyman视屏资料](http://www.imooc.com/video/13576)
* [OkHttp官方github地址](https://github.com/square/okhttp)
* [官方教程2016版](https://github.com/square/okhttp/wiki/Recipes)
* [诸葛小布博客](http://lowett.com/2017/02/09/okhttp-1/)
* [清屏网](http://www.qingpingshan.com/rjbc/az/110232.html)

---
下面正式开始来入门。

# 配置环境

## 客户端配置
下载[最新的jar包](https://search.maven.org/remote_content?g=com.squareup.okhttp3&a=okhttp&v=LATEST)或者通过Maven仓库构建。
```
<dependency>
  <groupId>com.squareup.okhttp3</groupId>
  <artifactId>okhttp</artifactId>
  <version>3.7.0</version>
</dependency>
```
或者，Gradle添加如下依赖
···
compile 'com.squareup.okhttp3:okhttp:3.7.0'
···

最好去官网上查看当前的最新版本。写这篇博客的最新版本为3.7.0。

## 服务端配置
这里我们采用eclipse搭配Tomcat作为我们的测试服务器，使用structs2的框架以节省代码的编写。具体的配置参考这篇[博客](http://www.jianshu.com/p/f6c3ec403072)。

# 开始使用
## get请求
okHttp的是非常非常简单的，如果我们要发送一个get请求，四步就可以解决了。

 1.  构建一个```OkHttpClient```,简单的示例代码如下
```
  OkHttpClient client=new OkHttpClient();
```

 2.   构建一个```Request```,简单的示例代码如下
```
     Request request = new Request.Builder().url("http://baidu.com") .build();
```

 3. 获得```Call```对象，简单的实例代码如下：
```
     okhttp3.Call call = okHttpClient.newCall(request);
```

 4.  执行get请求，两种执行方式，同步和异步执行

```
//同步执行
call.execute();

//异步执行
 call.enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
              //发生错误时执行的回调
            }
            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
              //正确执行，获取返回数据的回调
            }
        });
```
一个简单的异步Get请求，我们完全就可以这么写。
```
         OkHttpClient client=new OkHttpClient();
        Request request = new Request.Builder().url("http://baidu.com").build();
        client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                    }
                    @Override
                    public void onResponse(Response response) throws IOException {
                    }
         });
```
上面我们还是用了用git访问了baidu的首页，现在我们实现用get请求我们搭建的测试服务器。
在eclipse中新建一个```UserAction```类，继承自```ActionSupport```，添加一个方法，```login()```用来模拟登录过程。在UserAction中添加username和Passwd属性。
整个UserAction的代码如下：
```
public class UserAction extends ActionSupport{
	private static final long serialVersionUID = 1L;
	private String username;
	private String passwd;

	public String login() throws IOException{		
		System.out.println(" ---:"+username+" "+passwd);	
		HttpServletResponse response = ServletActionContext.getResponse();
		PrintWriter writer = response.getWriter();
		writer.write("login success!");
		writer.flush();
		return null;
	}
public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPasswd() {
		return passwd;
	}
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
```
然后在structs.xml中配置文件添加
```<action name="login" class="action.UserAction" method="login"></action>```
通过浏览器访问```http://127.0.0.1:8080/OkhttpTestService/login ```,```OkhttpTestService```是我的项目名称，后面的login是我们在structs.xml中配置的名称。如果我们访问后返回login success！即代表我们的服务端写好了。
将Get请求中的地址变成如下的形式：
```
Request request = builder.get().url(BASE_URL+"login?username=roger&passwd=123456").build();
```
添加对应的点击事件，并且在获得结果后在TextView显示返回的对象。主要代码如下：
```
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
```
点击按钮，发现服务端能正常接收到我们的get请求，并且能正常解析url中附带的参数信息：username和passwd。

## Post  请求。
与get请求很类似，我们也可以将post请求看做是四个步骤。
```
//1. 构造OkHttpClient对象
OkHttpClient client=new OkHttpClient();

//2 构建一个Request

//2.1 与get请求不同，post中的参数需要通过RequestBody 来进行传递
  RequestBody requestBody = new FormBody.Builder().add("username","reoger").add("passwd","123").build();
//2.2 构建一个builder
 Request.Builder builder = new Request.Builder();
//2.3 完成Request的构建
 Request request = builder.post(requestBody).url(BASE_URL+"login").build();

//3 获得Call对象
 okhttp3.Call call = okHttpClient.newCall(request);

  //4 异步执行
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
```
服务端的代码无须改变。测试一下，正常访问，且服务端也能正确识别传递过去的userName和passwd

## Post上传String或者json数据
通过OkHttp来上传json或者String数据也是一件非常简单的事情。工欲善其事，必先利其器，我们首先来编写服务端的程序，用于接收来自客户端的json或者String数据，并简单的将收到的数据打印出来。服务端的代码如下（还是直接添加到UserAction类中）：
```
public String postString() throws IOException{
		 System.out.println("*********");
		HttpServletRequest request = ServletActionContext.getRequest();
		 ServletInputStream is = request.getInputStream();
		
		 System.out.println("session Id ="+request.getSession().getId());
		 
		 StringBuilder sb = new StringBuilder();
		 int len = 0;
		 byte[] buf = new byte[1024];
		 while((len=is.read(buf))!=-1){
			 sb.append(new String(buf,0,len));
		 }

		 System.out.println(sb.toString());
		return null;
	}
```
然后在strusts2.xml中添加如下的配置信息：
```
 <action name="postString" class="action.UserAction" method="postString"></action>
```
到此，我们的服务端已经编写完成。接下来就是我们的客户端代码的编写。
```
//1.构建OkHttpClient对象
OkHttpClient client=new OkHttpClient();

//2.构建一个Request对象
  RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain;charset=utf-8"),"this is json data or string data");
        Request.Builder builder = new Request.Builder();
        Request request = builder.post(requestBody).url(BASE_URL+"postString").build();
//3、4 获取Call并执行，这里我们将他们封装成一个方法。   代码前面的代码  
   execute(request);
```
这里值得注意的就是```RequestBody.create(MediaType contentType, String content)```里面的参数，contentType表示请求编码的方式，提交json数据格式时，可以设置为：```MediaType.parse("application/json; charset=utf-8")```更多的数据格式，可以参考[这里](http://www.w3school.com.cn/media/media_mimeref.asp),后面的String就是我们要上传的字符串或者json数据。

代码编写完毕，测试，服务端能正常打印客户端发送的数据，通过post上传json或者String数据成功完成。

## Post上传文件
app中，文件上传的需求也比较常见，如用户设置头像等场景。下面介绍使用okhttp通过post上传文件。
首先还是先编写我们的服务端，（为简单起见，还是直接在UserAction类中添加）
```
public String postFile() throws IOException{
		 System.out.println("*****file****");
			HttpServletRequest request = ServletActionContext.getRequest();
			 ServletInputStream is = request.getInputStream();
			 
			 System.out.println("session Id ="+request.getSession().getId());
			
			 String dir = ServletActionContext.getServletContext().getRealPath("files");
			 File file = new File(dir,"test.jpg");
			 FileOutputStream fos = new FileOutputStream(file);
			 
			 int len = 0;
			 byte[] buf = new byte[1024];
			 while((len=is.read(buf))!=-1){
				fos.write(buf, 0, len);
			 }

			 fos.flush();
			 fos.close();
			 System.out.println("----end");
		
		return null;
	}
```
简要说明一下这里的代码，服务端其实也就做了两件事情。首先创建一个名为test.jpg的文件，创建的位置为**项目发布位置的根目录 / files的文件下**或者，你也可以自己文件的位置。然后将收到的数据写入到test.jpg的文件中。注意，这里只是简单的示例代码，并没做异常处理，正常的服务端这里肯定是需要进行try-catch处理的。

同样，在struts.xml中还是需要添加申明。
```
 <action name="postFile" class="action.UserAction" method="postFile"></action>
```
服务端写好了，接下来就是我们的客服端了。通过okhttp来实现post文件上传也是非常简单的。示例代码如下：
```
OkHttpClient client=new OkHttpClient();
  File file = new File(Environment.getExternalStorageDirectory(),"test.jpg");
        if(!file.exists()){
            Log.e("TAG","file is not exit!");
            return ;
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"),file);
        Request.Builder builder = new Request.Builder();
        Request request = builder.post(requestBody).url(BASE_URL+"postFile").build();
        execute(request);
```
也简要说明一下客户端的程序，首先我们拿到在手机根目录下的一个叫做test.jpg的文件，（在这之前我们需要确保手机根目录下有这么一个文件，我们将测试将这个文件传上服务端）。然后我们构建我们的RequestBody，更多的数据格式请参考[这里](http://www.w3school.com.cn/media/media_mimeref.asp)，在create方法中，有一个重载的方法可以添加文件，我们将之前拿到的文件传入就Ok了。
在运行之前，不要忘记在```AndroidManifest.xml```添加相应的权限。
```
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
```
运行程序之后，发现图片能上传到我们指定的位置。

## 上传文件附带认证信息
首先还是编写我们服务端的代码：(继续添加在UserAction中)
```
    public File mPhoto;
	public String mPhotoFileName;
	public String mPhotoContentType;
	//上传图片
	public String upLoadInfo() throws IOException{
		
		System.out.println(username+" ," +passwd+"  "+mPhotoContentType);
		
		if(mPhoto == null){
			System.out.println("mPhoto == null "+mPhotoFileName);
		}
		
		 String dir = ServletActionContext.getServletContext().getRealPath("files");
		 File file = new File(dir,mPhotoFileName);
		 
		 FileUtils.copyFile(mPhoto, file);
		return null;
	}
	```
简要说明一下。mPhone是服务端和客服端协商好的文件名，客户端也只有使用这个文件名时才能被正确识别。前面的username和passwd也是如此（这里写成public是为了省略set和get方法）。
至于后面的代码就更简单了，首先创建一个文件，然后将客服端传过来的文件直接复制到创建的文件中。不要忘记在strusts2.xml中声明：
```
 <action name="uploadImg" class="action.UserAction" method="upLoadInfo"></action>
```
到此，客户端就已经编写完成了。接下来就来编写客服端的代码：完整代码如下：
```
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
        Request.Builder builder = new Request.Builder();
        Request request = builder.post(multipartBody ).url(BASE_URL+"uploadImg").build();
        execute(request);
```
是不是感觉很熟悉？没错，就是我们在我们基本的Post请求上使用功能更加强大的```MultipartBody ```来代替```RequestBody ```而已。我们在```RequestBody ```添加来了用户信息，密码和文件等等。服务端收到post请求会将post的文件一并收取，并将其储存在指定的目录下。
到此，我们的文件上传功能做的差不多了，接下来我们来试试我们去下载文件。

## 文件下载
利用okHttp下载文件也是SoEasy。接下来我们来看怎么实现吧。
文件的下载就不要去编写服务器了，我们可以用之前上传的图片来做测试。或者直接在网上找一张图片来做测试。
下载文件其实也就四步，整体来说，Okhttp实现请求都是四个步骤：直接上代码，不在赘述。
```
      OkHttpClient client=new OkHttpClient();//1

     Request.Builder builder = new Request.Builder();//2
        Request request = builder.get().url(BASE_URL+"files/test2.jpg").build();//2

        okhttp3.Call call = okHttpClient.newCall(request);//3

        call.enqueue(new Callback() {//4
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
```
可以看到，下载文件的步骤同前面的步骤简直一模一样。下载的文件会通过onResponse回传给我们，我们只需要在这里进行简单的处理就好了。例如，这里我直接将图片显示到ImageView上，代码如下：
```
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
```
将图片保存到手机中，实例代码如下：
```
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
```
 到此，基本的Okhttp用法就了解的差不多了。，对了，这里再补充一点，下载的进度okhttp直接给了我们文件的总长度和当前下载的进度两个值，通过这两个值我们就可以计算出当前的进度。但是上传文件的进度okhttp并没有直接给我们相关的数据，我i门可以通过重写RequestBody来实现监听进度。代码如下：
```
public class CountingRequestBody extends RequestBody{

    private RequestBody delegate;
    private Listener listener;

    private CountingSink countingSink;

    public CountingRequestBody(RequestBody delegate, Listener listener) {
        this.delegate = delegate;
        this.listener = listener;
    }

    @Override
    public MediaType contentType() {
        return delegate.contentType();
    }

    @Override
    public long contentLength()  {
        try {
            return delegate.contentLength();
        } catch (IOException e) {
            return -1;
        }
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        countingSink = new CountingSink(sink);

        BufferedSink bufferedSink = Okio.buffer(countingSink);
        delegate.writeTo(bufferedSink);

        bufferedSink.flush();
    }

    protected  final class CountingSink extends ForwardingSink{

        private long byteWriten;


        public CountingSink(Sink delegate) {
            super(delegate);
        }

        @Override
        public void write(Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);
            byteWriten += byteCount;
            listener.onRequestProgress(byteWriten,contentLength());
        }
    }

    public interface Listener {
        void onRequestProgress(long byteWrited,long contentLength);
    }
}

```
在上传文件添加监听，将前面的代码改造如下：
```
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
```

# 封装篇

可以看出来，使用OkHttp来进行网络请求的时候，基本就是四个步骤，我们可以将这四个方法写到一个类中，进行简单的封装，就能实现一句话实现一个网络请求。但是这不是重点，重点还是得知道怎么使用。在了解了怎么使用之后，我们就可以从自己的使用角度进行更加合适的封装。

# 细节篇
可能你会有疑问，okhttp就这么简单？如果我需要设置一些参数（譬如设置缓存、超时设置等等）该怎么办。哈哈，这并不是这篇文件的主要内容，本篇旨在了解怎么使用okhttp的使用。如果需要了解，可以参考[这篇文章](http://lowett.com/2017/02/09/okhttp-1/)或者[这篇](http://blog.csdn.net/itachi85/article/details/51190687).

---
最后，**[源码下载](http://download.csdn.net/detail/reoger/9829189)**
