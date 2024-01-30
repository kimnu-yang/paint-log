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

    fun onLogoGroupClick(view: View) {

        logoArrowButton = findViewById(R.id.logoArrow)

        // 클릭할 때마다 이미지를 변경
        if (isImageChanged) {
            // 이미지를 초기 이미지로 변경
            logoArrowButton.setImageResource(R.drawable.arrow_down)
        } else {
            // 이미지를 변경할 이미지로 변경
            logoArrowButton.setImageResource(R.drawable.arrow_up)

            val popupMenu = PopupMenu(ContextThemeWrapper(this,R.style.PopupMenuCustom), view)
            popupMenu.menuInflater.inflate(R.menu.menu_main, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.menu_home -> {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        true
                    }
                    R.id.menu_week -> {
                        // 메뉴 아이템 2 선택 시 할 일
                        // 예: Toast 메시지 표시
                        showToast("Menu Item 2 Clicked")
                        true
                    }
                    else -> false
                }
            }
            // 팝업이 닫힐 때의 이벤트 설정
            popupMenu.setOnDismissListener {
                // 팝업이 닫힐 때 이미지, 변수 초기화
                logoArrowButton.setImageResource(R.drawable.arrow_down)
                isImageChanged = !isImageChanged
            }
            popupMenu.show()
        }

        // 이미지 변경 여부를 토글
        isImageChanged = !isImageChanged
    }

    private fun showToast(message: String) {
        // 예시로 Toast 메시지 표시
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}