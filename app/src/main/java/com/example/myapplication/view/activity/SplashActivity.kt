// SplashActivity.kt
package com.example.myapplication.view.activity

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R

class SplashActivity : AppCompatActivity() {

    private val SPLASH_DELAY: Long = 1000 // 스플래시 화면 지연 시간 (ms)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // "최애" 글자만 두껍게
        val subtitleText = "내 최애들만 따로 모아보자!"
        val spannable = SpannableString(subtitleText)
        spannable.setSpan(StyleSpan(Typeface.BOLD), 2, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        val textSubtitle = findViewById<TextView>(R.id.text_subtitle)
        textSubtitle.text = spannable

        // 지정된 시간 후에 메인 액티비티로 이동합니다.
        Handler().postDelayed({
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, SPLASH_DELAY)
    }
}
