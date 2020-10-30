package com.yektasarioglu.asrttscodelabs

import android.app.Application

import com.huawei.hms.mlsdk.common.MLApplication

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        MLApplication.getInstance().apiKey = BuildConfig.HMS_API_KEY
    }

}