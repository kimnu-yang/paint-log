package com.diary.paintlog.view.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import com.diary.paintlog.R
import com.diary.paintlog.databinding.ActivityDiaryBinding
import com.diary.paintlog.databinding.ActivityMainBinding

class DiaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDiaryBinding
    private lateinit var logoArrowButton: ImageView
    private val TAG = this.javaClass.simpleName
    private var isImageChanged = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary)
    }


    private fun showToast(message: String) {
        // 예시로 Toast 메시지 표시
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}