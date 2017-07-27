# XHttp
不依赖第三方的网络请求框架
##### 引用方式
       compile 'com.xhttp:library-http:1.0.2'
 #### 请使用代理进行测试

 #### 1、请求json
    new TaskBuilder().get("http://3434343434")
                .tag("aaa")
                .setOnTaskCallback(new OnHttpTaskCallback() {
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
                })
        .build();
#### 2、请求图片

       new TaskBuilder().get("http://sdadadadasd")
                     .tag("bbb")
                     .setTimeout(5000)
                     .setOnTaskCallback(new OnHttpTaskCallback() {
                         @Override
                         public void onSuccess(Response response) {
                             mImageView.setImageBitmap(response.toBitmap());
                         }

                         @Override
                         public void onFailure(Exception ex, String errorCode) {
                             Log.i("xxx", "onFailure  " +ex.toString());
                             Log.i("xxx", "onFailure  " +errorCode);
                         }
                     })
             .build();
#### 3、取消请求（TaskManager 取消请求）
###### 3.1、取消单个请求
     TaskManager.getIstance().cancel("bbb");
###### 3.2、取消所有请求
     TaskManager.getIstance().cancelAll();
#### 展示
![](https://github.com/xubinbin1024/XHttp/blob/master/gif/3.gif)
