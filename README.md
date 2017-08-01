# XHttp
不依赖第三方的网络请求框架

##### QQ：交流群 ：192268854
##### 引用方式
       compile 'com.xhttp:library-http:1.0.3'
 #### 请使用代理进行测试

 #### 1、请求json
    TaskManager.getmInstance().initGet("http://3434343434")
                    .setTag("aaa")
                    .execute(new OnTaskCallback() {
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
#### 2、请求图片
    TaskManager.getmInstance().initGet("http://sdadadadasd")
                .setTag("bbb")
                .setTimeout(5000)
                .execute(new OnTaskCallback() {
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
#### 3、取消请求（TaskManager 取消请求）
###### 3.1、取消单个请求
      TaskManager.getmInstance().cancel("bbb");
###### 3.2、取消所有请求
     TaskManager.getmInstance().cancelAll();
#### 展示
![](https://github.com/xubinbin1024/XHttp/blob/master/gif/3.gif)
