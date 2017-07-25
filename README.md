# XHttp
不依赖第三方的网络请求框架

 ## 请使用代理进行测试

  1、请求json

    new HttpTask().get("http://sdadadadasd")
            .setOnHttpTaskCallback(new OnHttpTaskCallback() {
                @Override
                public void onSuccess(Response response) {
                     Log.i("xxx", "onSuccess  " +response.toString());
                }

                @Override
                public void onFailure(Exception ex, String errorCode) {
                    Log.i("xxx", "onFailure  " +ex.toString());
                    Log.i("xxx", "onFailure  " +errorCode);
                }
            });
 2、请求图片

    new HttpTask().get("http://sdadadadasd")
            .setOnHttpTaskCallback(new OnHttpTaskCallback() {
                @Override
                public void onSuccess(Response response) {
                     mImageView.setImageBitmap(response.toBitmap());
                }

                @Override
                public void onFailure(Exception ex, String errorCode) {
                    Log.i("xxx", "onFailure  " +ex.toString());
                    Log.i("xxx", "onFailure  " +errorCode);
                }
            });

#### 展示
![](https://github.com/Xander1024/XHttp/blob/master/gif/3.gif)
