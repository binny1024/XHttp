package com.binny.sdk.core.vlayer;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.binny.sdk.constant.Constant;
import com.binny.sdk.core.vlayer.bean.IncorrectMessage;
import com.binny.sdk.core.vlayer.utils.UtilDataMaker;
import com.binny.sdk.exception.SDKException;
import com.binny.core.logger.JJLogger;
import com.binny.sdk.cache.DiskLruCacheHelper;
import com.binny.sdk.common.CommonMethod;
import com.binny.sdk.common.DateUtil;
import com.binny.sdk.common.DensityUtil;
import com.binny.sdk.core.vlayer.bean.CommonDataBean;
import com.binny.sdk.core.vlayer.bean.KeepAliveReturnBean;
import com.binny.sdk.core.vlayer.bean.LoginReturnBean;
import com.binny.sdk.core.vlayer.bean.NotifyPlayBean;
import com.binny.sdk.core.vlayer.callback.OnJJADLayerListener;
import com.binny.sdk.core.vlayer.utils.UtilDataParse;
import com.binny.core.websocket.WebSocketHelper;
import com.binny.core.xhttp.XHttp;
import com.binny.core.xhttp.callback.OnXHttpCallback;
import com.binny.core.xhttp.response.Response;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;

import static com.binny.sdk.constant.Constant.HANDLER;
import static com.binny.sdk.core.vlayer.constant.Constant.BLANK_PAGE;
import static com.binny.sdk.core.vlayer.constant.Constant.CLOSE_SOCKET;
import static com.binny.sdk.core.vlayer.constant.Constant.INCORRECT_BAG;
import static com.binny.sdk.core.vlayer.constant.Constant.KEEP_ALIVE_RET;
import static com.binny.sdk.core.vlayer.constant.Constant.LOGON_RET;
import static com.binny.sdk.core.vlayer.constant.Constant.NOTIFY_FEEDBACK;
import static com.binny.sdk.core.vlayer.constant.Constant.NOTIFY_PLAY;
import static com.binny.sdk.core.vlayer.constant.Constant.PLAY_FAILURE_CODE;
import static com.binny.sdk.core.vlayer.constant.Constant.PLAY_SUCCESS_CODE;
import static com.binny.sdk.core.vlayer.constant.Constant.PLAY_SUCCESS_MESSAGE;
import static com.binny.sdk.core.vlayer.constant.Constant.RESIZE_RETURN;
import static com.binny.sdk.core.vlayer.constant.Constant.SCREEN_CHANGED;
import static com.binny.sdk.core.vlayer.constant.Constant.WS_HOST;

public class JJWSAdLayerView extends WebView implements IJJAdLayerView {
    private static final String TAG = "JJWSAdLayerView";

    /**
     * 是否跳过
     */
    private boolean bNeedSkip = false;

    /**
     * ws 客户端
     */
    private WebSocketHelper mSocketHelper;

    /**
     * activity
     */
    private Activity mActivity;

    /**
     * 播放时长
     */
    private long mPlayTime;

    /**
     * 播放完成后回调用URL
     */
    private String mCallbackUrl;

    /**
     * 广告监听
     */
    private OnJJADLayerListener mListener;

    /**
     * (连接)重连任务
     */
    private Runnable mConnectRunnable;

    /**
     * 一段时间之后，执行关闭广告
     */
    private Runnable mFeedbackRunnable;

    /**
     * 心跳任务
     */
    private Runnable mRunnableKeepAlive;


    /**
     * 心跳周期
     */
    private final int mDelaySecond = 30;


    /**
     * 房间id
     */
    private String mRoomId;

    /**
     * 关闭连接后的重连时间
     */
    private int mReconnectionTime;

    /**
     * 广告展示，默认：保持连接
     */
    private int mDefaultEffect;

    /**
     * 广告被点击，默认：保持连接
     */
    private int mAdClickedEffect;

    /**
     * 广告关闭按钮被点击，默认：保持连接
     */
    private int mAdCloseButtonClickedEffect;


    /**
     * 新的宽高
     */
    private int mNewWidth;

    /**
     * 新的宽高
     */
    private int mNewHeight;


    private DiskLruCacheHelper mAdLayerCache;
    public String mAdPlayUrlCacheKey;
    private WebSocketHelperListenerImp mListenerImp;

    @Override
    public void show() {
        if (mAdLayerCache == null) {
            return;
        }
        if (this.getVisibility() != VISIBLE) {
            String url = this.mAdLayerCache.getString(this.mAdPlayUrlCacheKey);
            if (TextUtils.isEmpty(url)) {
                JJLogger.logInfo("finish", "ad_show_finish");
            } else {
                this.setVisibility(VISIBLE);
                this.loadUrl(url);
            }
        }
    }

    @Override
    public void hide() {
        if (this.getVisibility() != GONE) {
            this.setVisibility(GONE);
            this.loadBlankPage();
        }
    }

    @Override
    public void setRoomId(final String roomId) throws SDKException {
        release();
        loadBlankPage();
        /*
        * 如果版本小于19,则后面的都不再进行
        * */
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            setVisibility(GONE);
            JJLogger.logInfo("SDK","Android SDK < 19");
            return;
        }
        initWebSocket();
        if (!TextUtils.isEmpty(mAdPlayUrlCacheKey) && roomId != mAdPlayUrlCacheKey) {
            JJLogger.logInfo("room_change", "old=" + this.mAdPlayUrlCacheKey + ", new=" + roomId);
            this.mAdLayerCache.remove(this.mAdPlayUrlCacheKey);
        }

        mRoomId = roomId;
        JJLogger.logInfo("room_change", "old=" + mAdPlayUrlCacheKey + ", new=" + roomId);
        mAdPlayUrlCacheKey = mRoomId;
        if (this.mListener == null) {
            throw new NullPointerException("OnJJADLayerListener.mListener == null,请先调用 setOnJJADLayerListener(...)设置监听器！");
        }
    }

    @Override
    public void setOnJJADLayerListener(final OnJJADLayerListener listener) {
        this.mListener = listener;
    }

    /*
    * 房间号改变
    * */
    private void roomIdChanged() {
        this.mDefaultEffect = 1;
        this.mAdClickedEffect = 1;
        this.mAdCloseButtonClickedEffect = 1;
        int platformId = CommonMethod.getPlatformId();
        if (this.mRoomId == null) {
            this.mListener.onAdConfigError("room_id: 房间号 为空！");
            this.release();
        } else {
            String otherMsg = CommonMethod.getPhoneInfo();
            this.mSocketHelper.send(UtilDataMaker.makeBytes(20001, UtilDataMaker.getLoginBodyString("Android", "h5", platformId, this.mRoomId, DensityUtil.px2dip(this.mActivity, (float) this.mNewHeight), DensityUtil.px2dip(this.mActivity, (float) this.mNewWidth), otherMsg)), "login");
        }
    }

    public JJWSAdLayerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);

    }

    public JJWSAdLayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public JJWSAdLayerView(Context context) {
        super(context);
        init(context);
    }

    private void init(final Context context) {
        mRoomId = null;//初始化的时候，id 置空
        /*
        * 如果版本小于19,则后面的都不再进行
        * */
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            setVisibility(GONE);
            JJLogger.logInfo("SDK","Android SDK < 19");
            return;
        }
        mActivity = (Activity) context;

        initLayerView();
        initLayerViewListener();
        initRunnable();
    }

    private void initRunnable() {
        createReconnectRunnable();
        createFeedbackRunnable();
        createKeepAliveRunnable();
    }

    private void initWebSocket() {

        this.createWebSocketHelper();
        createSocketAndConnect();

        try {
            this.mAdLayerCache = new DiskLruCacheHelper(this.mActivity, "layer_cache");
        } catch (IOException var2) {
            JJLogger.logError("mAdLayerCache", var2.getMessage());
        }

    }

    private void createSocketAndConnect() {
        mSocketHelper.createSocketAndConnect();//创建并连接
    }

    private void createWebSocketHelper() {

        mListenerImp = new WebSocketHelperListenerImp();
        mSocketHelper = (new WebSocketHelper())
                .setSocketURI(URI.create(WS_HOST))
                .setSocketListener(mListenerImp)
                .createParse();
    }

    /**
     * 移除任务，
     * 关闭socket
     */
    private void closeSocket() {
        removeReconnectRunnable();
        removeKeepAliveRunnable();

        /*
        * 关闭链接
        * 重连的时候，重新创建socket
        * 不论关闭是否成功；
        * close()中都将 socket 置空，
        * */
        if (mSocketHelper != null) {
            mSocketHelper.close();
            mSocketHelper = null;
        }
    }


    /**
     * 重连之前，停止所有任务，关闭连接，等待重连任务被执行
     *
     * @param nextConnectTime 重连事件
     */
    private void closeSocketAndConnect(final long nextConnectTime) {
        closeSocket();//移除任务，关闭socket
        JJLogger.logInfo(TAG, "closeSocketAndConnect : " + nextConnectTime + " 秒后重新连接 ; " + getCurrentTime());
        HANDLER.postDelayed(mConnectRunnable, nextConnectTime * 1000);//断开重连
    }


    /**
     * 创建展示结果的反馈任务
     */
    private void createFeedbackRunnable() {
        mFeedbackRunnable = new Runnable() {
            @Override
            public void run() {
                adShowFinish(PLAY_SUCCESS_CODE, PLAY_SUCCESS_MESSAGE, mDefaultEffect);//默认关闭
            }
        };
    }

    /**
     * 创建反馈任务
     *
     * @param code    状态码
     * @param message 描述信息
     */
    private void postFeedback(final int code, final int feedbackCode, final String message) {
        mSocketHelper.send(UtilDataMaker.makeBytes(code, UtilDataMaker.getNotifyPlayFeedbackBodyString(feedbackCode, message)), "play_finish");
    }

    /**
     * 移除展示结果的反馈任务
     */
    private void removeFeedbackRunnable() {
        Constant.HANDLER.removeCallbacks(mFeedbackRunnable);
    }

    /**
     * 创建重连任务
     */
    private void createReconnectRunnable() {
        JJLogger.logInfo(TAG, "新建任务 createReconnectRunnable :");
        mConnectRunnable = new Runnable() {
            @Override
            public void run() {
                JJLogger.logInfo(TAG, "重连任务 创建完成:");
                createSocketAndConnect();
            }
        };

    }

    /**
     * 移除重连任务
     */
    private void removeReconnectRunnable() {
        Constant.HANDLER.removeCallbacks(mConnectRunnable);
        JJLogger.logInfo("release  removeReconnectRunnable;", " roomId = " + mRoomId);
    }


    /**
     * 创建心跳任务
     */
    private void createKeepAliveRunnable() {
        mRunnableKeepAlive = new Runnable() {
            @Override
            public void run() {
                /**
                 * 发送心跳数据
                 */
                JJLogger.logInfo(TAG, "发送心跳包 ,时间 " + DateUtil.ms2Date(System.currentTimeMillis()));
                mSocketHelper.send(UtilDataMaker.makeBytes(com.binny.sdk.core.vlayer.constant.Constant.KEEP_ALIVE_SEND, UtilDataMaker.getKeepAliveBodyString(System.currentTimeMillis())), "keepalive");
                Constant.HANDLER.postDelayed(this, mDelaySecond * 1000);

            }
        };
    }

    /**
     * 发送心跳任务
     */
    private void postKeepAliveRunnable() {
        Constant.HANDLER.postDelayed(mRunnableKeepAlive, mDelaySecond * 1000);
    }

    /**
     * 移除心跳任务
     */
    private void removeKeepAliveRunnable() {
        Constant.HANDLER.removeCallbacks(mRunnableKeepAlive);
        JJLogger.logInfo("release  removeKeepAliveRunnable;", " roomId = " + mRoomId);
    }


    /**
     * 关闭广告，每个广告只需关闭一次
     * <p>
     * 关闭广告的情况有三种：其中两种（都是点击关闭）互斥，默认关闭
     * 1、点击关闭按钮关闭广告；
     * 2、点击广告后，广告关闭
     * 3、以上两种情况都不执行的时候，执行默认关闭；
     * 如果点击之后关闭，则不需要执行默认关闭：
     * <p>
     * 用 bAdClose表示广告是否被关闭，如果已 关闭 ；则不在执行
     */
    private void adShowFinish(final int code, final String message, int effect) {

        JJLogger.logInfo(TAG, mPlayTime + "广告关闭；" + getCurrentTime());

        //反馈给Http服务器
        sendPlayFinishFeedbackToHttp();
        JJLogger.logInfo(TAG, "adShowFinish： 关闭 socket effect 值（0 = “关闭连接”；1=“保持连接”） = " + effect);
        JJLogger.logInfo(TAG, "回传信息 :" + "code = " + code + ";message = " + message);

        loadBlankPage();

        //反馈给ws服务器
        if (mSocketHelper.socketAvailable()) {
            if (effect == CLOSE_SOCKET) {
                JJLogger.logInfo(TAG, "adShowFinish : 回传结束，执行effect：关闭WS服务器");
                closeSocketAndConnect(mReconnectionTime);//广告展示完成，断开重连
            } else {
                JJLogger.logInfo(TAG, "adShowFinish : 执行effect：保持连接");
            }

        } else {
            //停止发送数据
            JJLogger.logInfo(TAG, "adShowFinish : 停止发送数据到WS服务器");
        }

    }

    private String getCurrentTime() {
        return "当前时间 ：" + DateUtil.ms2Date(System.currentTimeMillis());
    }


    /**
     * 加载一个空页面
     */
    private void loadBlankPage() {
        setVisibility(GONE);
        loadUrl(BLANK_PAGE);
    }


    /**
     * 释放内存
     */
    public void release() {
        closeSocket();//释放
        removeFeedbackRunnable();
        if (mListenerImp != null) {
            mListenerImp = null;
        }
        if (!TextUtils.isEmpty(this.mAdPlayUrlCacheKey) && this.mAdLayerCache != null) {
            JJLogger.logInfo("release  before= ", mAdLayerCache + " roomId = " + mRoomId);
            this.mAdLayerCache.remove(this.mAdPlayUrlCacheKey);
            JJLogger.logInfo("release  finish =", mAdLayerCache + " roomId = " + mRoomId);
        }
    }


    private void initLayerViewListener() {
        /*
        * 屏蔽掉长按事件 因为webview长按时将会调用系统的复制控件
        * */
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });

        setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(final WebView view, final String url, final Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                JJLogger.logInfo(TAG, "adShowFinish : 发送反馈数据到WS服务器" + "\n广告展示时间 :" + mPlayTime + "秒；" + getCurrentTime());
                postFeedback(NOTIFY_FEEDBACK, PLAY_SUCCESS_CODE, "展示成功");
                if (mFeedbackRunnable == null) {
                    createFeedbackRunnable();
                }
                Constant.HANDLER.postDelayed(mFeedbackRunnable, mPlayTime * 1000);
            }

            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
                super.shouldOverrideUrlLoading(view, url);
                return true;//页面的跳转连接 回调给 APP
            }

            @Override
            public void onReceivedError(final WebView view, final int errorCode, final String description, final String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                //  这里进行无网络或错误处理，具体可以根据errorCode的值进行判断
                JJLogger.logInfo(TAG, "failingUrl = " + errorCode);
                JJLogger.logInfo(TAG, "failingUrl = " + failingUrl);
                JJLogger.logInfo(TAG, "failingUrl = " + description);
                postFeedback(NOTIFY_FEEDBACK, PLAY_FAILURE_CODE, "展示失败 ： " + description);
                loadUrl(BLANK_PAGE);
            }

            @Override
            public void onReceivedSslError(final WebView view, final SslErrorHandler handler, final SslError error) {

                /*
                * webView默认是不处理https请求的，页面显示空白，
                * */
                JJLogger.logInfo(TAG, "处理https");
                handler.proceed();
                super.onReceivedSslError(view, handler, error);
            }
        });
        setWebChromeClient(new WebChromeClient());
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onTouchEvent(final MotionEvent ev) {

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (this.bNeedSkip) {
                this.bNeedSkip = false;
                return false;
            }

            float x = DensityUtil.px2dip(mActivity, ev.getX());
            float y = DensityUtil.px2dip(mActivity, ev.getY());

            String js = "(function () { var result = document.elementFromPoint(" + x + "," + y + "); if (result) { if (result.onclick) { return true;}else { return false;}}})();";

            evaluateJavascript(js, new ValueCallback<String>() {
                @Override
                public void onReceiveValue(final String value) {
                    if (!"true".equals(value.trim())) {
                        Log.i(TAG, "准备模拟事件: " + ev.toString());
                        imitateTouchEvent(ev);
                    } else {
                        Log.i(TAG, "直接处理事件: " + ev.toString());
                        JJWSAdLayerView.super.onTouchEvent(ev);
                    }
                }
            });
            return true;
        } else {
            JJLogger.logInfo(TAG, "onTouchEvent, 直接处理: " + ev);
            return super.onTouchEvent(ev);
        }
    }

    private void imitateTouchEvent(final MotionEvent motionEvent) {
        this.bNeedSkip = true;
        mActivity.dispatchTouchEvent(motionEvent);
    }


    /**
     * 播放完成，发一个http
     */
    private void sendPlayFinishFeedbackToHttp() {
        //反馈给http服务器
        JJLogger.logInfo(TAG, "发送反馈数据到http服务器");

        XHttp.getInstance().get(mCallbackUrl)
                .setOnXHttpCallback(new OnXHttpCallback() {

                    @Override
                    public void onSuccess(final Response response) {

                    }

                    @Override
                    public void onFailure(final Exception ex, final String errorCode) {
                        JJLogger.logInfo(TAG, "回传http服务器失败 :" + errorCode);
                    }
                });
    }


    /*
    * 当窗口改变的时候，例如横竖屏
    *
    * */
    @Override
    protected void onSizeChanged(final int w, final int h, final int ow, final int oh) {
        JJLogger.logInfo("logInfo-new--w", String.valueOf(w));
        JJLogger.logInfo("logInfo-new--h", String.valueOf(h));

        super.onSizeChanged(w, h, ow, oh);
        if ((w * h != 0) && (w != ow || h != oh)) {
            loadBlankPage();
            if (mSocketHelper != null) {
                mSocketHelper.send(UtilDataMaker.makeBytes(SCREEN_CHANGED, UtilDataMaker.getResizeBodyString(DensityUtil.px2dip(mActivity, h), DensityUtil.px2dip(mActivity, w))), "resize");
            }
            mNewWidth = w;
            mNewHeight = h;
            invalidate();
        }
    }

    /*
    * 初始化 view  done
    * */
    @SuppressLint({"AddJavascriptInterface", "SetJavaScriptEnabled"})
    private void initLayerView() {
        requestFocus();//触摸焦点起作用

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });
        WebSettings settings = getSettings();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        String ua = settings.getUserAgentString();
        settings.setUserAgentString(ua + ";JJSDK");
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(false);
        settings.setSupportZoom(false);
        settings.setDisplayZoomControls(false);
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setDefaultTextEncodingName("utf-8");
        settings.setDomStorageEnabled(true);
        settings.setAppCacheEnabled(false);
        settings.setGeolocationEnabled(true);
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        setHorizontalScrollBarEnabled(false);
        addJavascriptInterface(new JavaScriptInterface(), "android");
        loadUrl(BLANK_PAGE);
        setBackgroundColor(Color.TRANSPARENT);
    }

    /*
    * 内部类
    * */
    /*
    * js调用接口
    * */
    public class JavaScriptInterface {

        @JavascriptInterface
        public void onAdClicked(final String message) {
            removeFeedbackRunnable();
            JJLogger.logInfo(TAG, "onAdClicked : 广告被点击！");
            /*
            * 只要点击了广告，就关闭广告(展示一个空白页面)
            * */
            Constant.HANDLER.post(new Runnable() {
                @Override
                public void run() {
                    String closeJs = "close";
                    if (!closeJs.equals(message)) {
                        //跳转,回调APP函数 ，反馈给ws服务器
                        adShowFinish(PLAY_SUCCESS_CODE, "用户点击跳转后，关闭", mAdClickedEffect);//用户点击跳转关闭
                        mListener.onAdClickedListener(message);

                    } else {
                        adShowFinish(PLAY_SUCCESS_CODE, "用户点击关闭按钮", mAdCloseButtonClickedEffect);//用户点击关闭按钮关闭
                    }
                }
            });
        }
    }

    /*
    * ws监听器
    * */
    private class WebSocketHelperListenerImp implements WebSocketHelper.Listener {
        private WebSocketHelperListenerImp() {
        }

        public void onConnectSuccess() {
            JJLogger.logInfo("logInfo-", "连接成功！");
            JJWSAdLayerView.this.roomIdChanged();
        }

        public void onMessage(byte[] data) {
            CommonDataBean parseData = UtilDataParse.parseData(data);
            int cmd = parseData.getDataCmd();
            String body = new String(parseData.getBodyBytes());
            JJLogger.logInfo("JJWSAdLayerView", "接受到的 cmd = " + cmd);
            JJLogger.logInfo("JJWSAdLayerView", "接受到的 data 的 size = " + parseData.getDataSize());
            JJLogger.logInfo("JJWSAdLayerView", "接受到的 body = " + body);
            boolean bDataError = false;
            switch (cmd) {
                case LOGON_RET:
                    LoginReturnBean loginReturnBean = UtilDataParse.parseLoginReturnData(body);
                    if (loginReturnBean == null) {
                        JJLogger.logInfo("JJWSAdLayerView", "登陆返回信息出错");
                        bDataError = true;
                    } else {
                        JJLogger.logInfo("JJWSAdLayerView", "登陆成功！");
                        int code = loginReturnBean.getCode();
                        JJLogger.logInfo("JJWSAdLayerView", "登陆成功后得到的 code :" + code);
                        int nextConnectTime = loginReturnBean.getBody().getNext_connect_time();
                        JJLogger.logInfo("JJWSAdLayerView", "登陆成功后得到的下次重连时间 :" + nextConnectTime + "秒");
                        if (code == -1) {
                            JJLogger.logInfo("JJWSAdLayerView", "服务端返回 -1 ，准备重连，重联时间 " + nextConnectTime);
                            closeSocketAndConnect((long) nextConnectTime);
                        } else if (code == 0) {
                            postKeepAliveRunnable();
                        }
                    }
                    break;
                case KEEP_ALIVE_RET:
                    KeepAliveReturnBean keepAliveReturnBean = UtilDataParse.parseKeepAliveReturnData(body);
                    if (keepAliveReturnBean == null) {
                        JJLogger.logInfo("JJWSAdLayerView", "返回心跳包信息出错");
                        bDataError = true;
                    } else {
                        JJLogger.logInfo("JJWSAdLayerView", keepAliveReturnBean.toString());
                    }
                    break;
                case NOTIFY_PLAY:
                    NotifyPlayBean notifyPlayBodyBean = UtilDataParse.parseNotifyReturnData(body);
                    if (notifyPlayBodyBean != null && notifyPlayBodyBean.getConfig() != null) {
                        JJWSAdLayerView.this.mPlayTime = (long) notifyPlayBodyBean.getPlay_time();
                        JJWSAdLayerView.this.mReconnectionTime = notifyPlayBodyBean.getConfig().getReconnect_time();
                        JJLogger.logInfo("JJWSAdLayerView", "广告重连时间 :" + JJWSAdLayerView.this.mReconnectionTime + "秒");
                        JJWSAdLayerView.this.mCallbackUrl = notifyPlayBodyBean.getDst_url();
                        JJLogger.logInfo("JJWSAdLayerView", "展示完成回传的URL :" + JJWSAdLayerView.this.mCallbackUrl);
                        String playUrl = notifyPlayBodyBean.getPage_url();
                        if (playUrl.contains("?")) {
                            playUrl = playUrl + "&xpos=" + notifyPlayBodyBean.getXpos() + "&ypos=" + notifyPlayBodyBean.getYpos() + "&height=" + notifyPlayBodyBean.getHeight() + "&width=" + notifyPlayBodyBean.getWidth() + "&img_url=" + URLEncoder.encode(notifyPlayBodyBean.getImg_url()) + "&dst_url=" + URLEncoder.encode(notifyPlayBodyBean.getDst_url()) + "&client_width=" + notifyPlayBodyBean.getClient_width() + "&client_height=" + notifyPlayBodyBean.getClient_height();
                        } else {
                            playUrl = playUrl + "?xpos=" + notifyPlayBodyBean.getXpos() + "&ypos=" + notifyPlayBodyBean.getYpos() + "&height=" + notifyPlayBodyBean.getHeight() + "&width=" + notifyPlayBodyBean.getWidth() + "&img_url=" + URLEncoder.encode(notifyPlayBodyBean.getImg_url()) + "&dst_url=" + URLEncoder.encode(notifyPlayBodyBean.getDst_url()) + "&client_width=" + notifyPlayBodyBean.getClient_width() + "&client_height=" + notifyPlayBodyBean.getClient_height();
                        }

                        JJLogger.logInfo("JJWSAdLayerView", "广告展示页面 : " + playUrl);
                        NotifyPlayBean.ConfigBean configBean = notifyPlayBodyBean.getConfig();
                        JJWSAdLayerView.this.mDefaultEffect = configBean.getIgnore_effect();
                        JJLogger.logInfo("JJWSAdLayerView", "广告的默认行为（0 = “关闭连接”；1=“保持连接”） = " + JJWSAdLayerView.this.mDefaultEffect);
                        JJWSAdLayerView.this.mAdClickedEffect = configBean.getClick_effect();
                        JJLogger.logInfo("JJWSAdLayerView", "广告被点击（0 = “关闭连接”；1=“保持连接”） = " + JJWSAdLayerView.this.mAdClickedEffect);
                        JJWSAdLayerView.this.mAdCloseButtonClickedEffect = configBean.getClose_effect();
                        JJLogger.logInfo("JJWSAdLayerView", "广告关闭按钮被点击（0 = “关闭连接”；1=“保持连接”） = " + JJWSAdLayerView.this.mAdCloseButtonClickedEffect);
                        if (JJWSAdLayerView.this.mPlayTime > 0L && !TextUtils.isEmpty(playUrl)) {
                            JJWSAdLayerView.this.removeFeedbackRunnable();
                            JJWSAdLayerView.this.mAdLayerCache.putString(JJWSAdLayerView.this.mAdPlayUrlCacheKey, playUrl);
                            setVisibility(VISIBLE);
                            JJWSAdLayerView.this.loadUrl(playUrl);
                        }
                    } else {
                        JJLogger.logInfo("JJWSAdLayerView", "广告投放信息出错");
                        bDataError = true;
                    }
                    break;
                case RESIZE_RETURN:
                    JJLogger.logInfo("cmd", "resize 返回包 未解析;code =" + RESIZE_RETURN);
                    break;
                case INCORRECT_BAG:
                    IncorrectMessage errorMessage = UtilDataParse.parseErrorMessage(body);
                    if (errorMessage == null) {
                        bDataError = true;
                    } else {
                        JJLogger.logInfo("JJWSAdLayerView", errorMessage.toString());
                    }
                    break;
                default:
                    JJLogger.logError("cmd", "cmd 不匹配");
            }

            if (bDataError) {
                JJLogger.logInfo("JJWSAdLayerView", "JJWSAdLayerView.onMessage :数据出错，准备重连");
                JJWSAdLayerView.this.closeSocketAndConnect(CommonMethod.getRandomTime());
            }

        }

        public void onMessage(String message) {
        }

        public void onDisconnect(String reason) {
            JJLogger.logInfo("JJWSAdLayerView", "断开连接:  reaseon = " + reason);
            JJWSAdLayerView.this.closeSocketAndConnect(CommonMethod.getRandomTime());
        }
    }

}
