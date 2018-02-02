/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.binny.sdk.cache;

import com.binny.core.logger.JJLogger;
import com.binny.sdk.core.splash.constant.Configuration;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.Charset;

import static com.binny.sdk.InitAdSDK.getAdContext;

/**
 * function
 */
public final class UtilCache {
    static final Charset US_ASCII = Charset.forName("US-ASCII");
    static final Charset UTF_8 = Charset.forName("UTF-8");

    private UtilCache() {
    }

    @SuppressWarnings("ThrowFromFinallyBlock")
    static String readFully(Reader reader) throws IOException {
        try {
            StringWriter writer = new StringWriter();
            char[] buffer = new char[1024];
            int count;
            while ((count = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, count);
            }
            return writer.toString();
        } finally {
            reader.close();
        }
    }

    /**
     * Deletes the contents of {@code dir}. Throws an IOException if any file
     * could not be deleted, or if {@code dir} is not a readable directory.
     */
    static void deleteContents(File dir) throws IOException {
        File[] files = dir.listFiles();
        if (files == null) {
            throw new IOException("not a readable directory: " + dir);
        }
        for (File file : files) {
            if (file.isDirectory()) {
                deleteContents(file);
            }
            if (!file.delete()) {
                throw new IOException("failed to removeAll file: " + file);
            }
        }
    }

    static void closeQuietly(/*Auto*/Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception ignored) {
            }
        }
    }
    private static DiskLruCacheHelper mHelperAdImgCache;
    private static DiskLruCacheHelper mHelperAdListAndAdInfoCache;


    private static DiskLruCacheHelper getHelperAdImgCache() {

        if (mHelperAdImgCache == null) {
            try {
                mHelperAdImgCache = new DiskLruCacheHelper(getAdContext(), Configuration.AD_BITMAP_CACHE);
            } catch (IOException e) {
                JJLogger.logError("init_cache","初始化 spalsh_ad_image 缓存失败" + e.getMessage());
            }
        }

        return mHelperAdImgCache;
    }

    private static DiskLruCacheHelper getHelperAdListAndAdInfoCache() {
        if (mHelperAdListAndAdInfoCache == null) {
            try {
                mHelperAdListAndAdInfoCache = new DiskLruCacheHelper(getAdContext(), Configuration.AD_LIST_CACHE);
            } catch (IOException e) {
                JJLogger.logError("init_cache","初始化 spalsh_ad_list 缓存失败" + e.getMessage());
            }
        }
        return mHelperAdListAndAdInfoCache;
    }

    /**
     * 该对象同来获取缓存广告的图片
     * 如果，在SDK 外部调用，而没有初始化SDK，则会捕获空指针异常
     */
    public static DiskLruCacheHelper getAdBitmapCacheHelper() {
        return getHelperAdImgCache();
    }

    /**
     * 根据不同 key 来获取广告缓存的信息
     * 1、可以用来获取 缓存的广告列表
     * 2、可以用来获取 缓存的默认广告id
     * 3、可以用来获取 缓存的单个广告实体信息 bean
     */
    public static DiskLruCacheHelper getAdInfoCacheHelper() {
        return getHelperAdListAndAdInfoCache();
    }

}
