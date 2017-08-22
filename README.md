# XHttp  目前支持功能

1、不依赖第三方的网络请求框架;

2、支持get、post、post文件上传;

3、支持重定向;


#### demo展示(代传)
![](https://github.com/Xbean1024/XHttp/blob/master/gif/3.gif)

##### 引用方式

       compile 'com.bean.libs:http:1.0.0'

#### 请使用代理进行测试
### 一、网络请求，默认情况下不启用线程池
#### 1.1、请求json
          XHttp.getInstance().initTask().get("http://3434343434")
                  .setTag("aaa")
                  .setOnTaskCallback(new OnTaskCallback() {
                      @Override
                      public void onSuccess(Response response) {
                          Log.i("xxx", "response  " +response.toString());
                          mTextView.setText(response.toString());
                      }

                      @Override
                      public void onFailure(Exception ex, String errorCode) {
                          Log.i("xxx", "onFailure  " +ex.toString());
                          Log.i("xxx", "onFailure  " +errorCode);
                      }
                  });
#### 1.2、请求图片
         XHttp.getInstance().initTask().get("http://sdadadadasd")
                   .setTag("bbb")
                   .setTimeout(5000)
                   .setOnTaskCallback(new OnTaskCallback() {
                       @Override
                       public void onSuccess(final Response response) {
                           mImageView.setImageBitmap(response.toBitmap());
                           Log.i("xxx", "onSuccess" );
                       }

                       @Override
                       public void onFailure(Exception ex, String errorCode) {
                           Log.i("xxx", "onFailure  " +ex.toString());
                           Log.i("xxx", "onFailure  " +errorCode);
                       }
                   });
#### 1.3、多文件上传
	String basePtah = Environment.getExternalStorageDirectory().getPath();
	String[] path = new String[]{basePtah+ "/setting.cfg",basePtah+"/Screenshot.png"};
	XHttp.getInstance()
				.initTask()
				.post(WebApi.UPLOAD_FILE_URL)//url
  				.setHeads("platform","mobile_phone")//设置请求头
                .uploadFiles(path)//文件路径（完整路径）
                .setOnTaskCallback(new OnTaskCallback() {
                    @Override
                    public void onSuccess(final Response response) {
                        JJLogger.logInfo(TAG,"MainActivity.onSuccess :"+
                                response.toString());
                        Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onFailure(final Exception ex, final String errorCode) {
                        JJLogger.logInfo(TAG,"MainActivity.onFailure :"+ex.getMessage());
                    }
                });
#### 1.4、单文件上传
	String basePtah = Environment.getExternalStorageDirectory().getPath();
	String path = basePtah+ "/setting.cfg";
	XHttp.getInstance()
				.initTask()
				.post(WebApi.UPLOAD_FILE_URL)//url
  				.setHeads("platform","mobile_phone")//设置请求头
                .uploadFile(path)//文件路径（完整路径）
                .setOnTaskCallback(new OnTaskCallback() {
                    @Override
                    public void onSuccess(final Response response) {
                        JJLogger.logInfo(TAG,"MainActivity.onSuccess :"+
                                response.toString());
                        Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onFailure(final Exception ex, final String errorCode) {
                        JJLogger.logInfo(TAG,"MainActivity.onFailure :"+ex.getMessage());
                    }
                });
#### 1.5 、启用并行线程池
	 final StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < 20; i++) {
                final int finalI = i;
                XHttp.getInstance().initTask().post(WebApi.LOGIN_URL)
                        .setParams("account", mName.getText().toString())
                        .setParams("password", mAge.getText().toString())
                        .startConcurrenceThreadPool()
                        .setOnTaskCallback(new OnTaskCallback() {
                            @Override
                            public void onSuccess(final Response response) {
                                Log.i("task","MainActivity.onSuccess 任务"+ finalI +"完成:");
                                stringBuilder.append("并行 任务"+ finalI +"完成:").append("\n");
                                mTextView1.setText(stringBuilder.toString());
                            }

                            @Override
                            public void onFailure(final Exception ex, final String errorCode) {
                                JJLogger.logInfo(TAG, "MainActivity.onFailure :" + ex.getMessage());
                            }
                        });
            }
#### 1.6 、启用串行线程池
	 final StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < 20; i++) {
                final int finalI = i;
                XHttp.getInstance().initTask().post(WebApi.LOGIN_URL)
                        .setParams("account", mName.getText().toString())
                        .setParams("password", mAge.getText().toString())
                        .startSerialThreadPool()
                        .setOnTaskCallback(new OnTaskCallback() {
                            @Override
                            public void onSuccess(final Response response) {
                                Log.i("task","MainActivity.onSuccess 任务"+ finalI +"完成:");
                                stringBuilder.append("串行 任务"+ finalI +"完成:").append("\n");
                                mTextView2.setText(stringBuilder.toString());
                            }

                            @Override
                            public void onFailure(final Exception ex, final String errorCode) {
                                JJLogger.logInfo(TAG, "MainActivity.onFailure :" + ex.getMessage());
                            }
                        });
            }
#### 2、取消请求（TaskManager 取消请求）
###### 2.1、取消单个请求
      TaskManager.getmInstance().cancel("bbb");
###### 2.2、取消所有请求
     TaskManager.getmInstance().cancelAll();
##### QQ：交流群 ：192268854
![](https://github.com/Xbean1024/XHttp/blob/master/gif/QQ.JPG)
