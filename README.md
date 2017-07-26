# XHttp
不依赖第三方的网络请求框架

 ## 请使用代理进行测试

  1、请求json
       TaskBuilder.getIstance().get("http://3434343434")
                .tag("aaa")
                .setOnHttpTaskCallback(new OnHttpTaskCallback() {
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
 2、请求图片

        TaskBuilder.getIstance().get("http://sdadadadasd")
                   .tag("bbb")
                   .setTimeout(5000)
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
                   })
           .build();

#### 展示
![](https://github.com/xubinbin1024/XHttp/blob/master/gif/3.gif)
