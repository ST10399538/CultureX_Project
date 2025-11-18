package com.example.culturex

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.culturex.utils.AppLanguageHelper
import com.example.culturex.utils.SharedPreferencesManager

abstract class BaseActivity : AppCompatActivity() {
    override fun attachBaseContext(newBase: Context) {
        val prefs = SharedPreferencesManager(newBase)
        val lang = prefs.getPreferredLanguage()
        val context = AppLanguageHelper.setLocale(newBase, lang)
        super.attachBaseContext(context)
    }
}




