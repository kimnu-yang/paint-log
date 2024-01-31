package com.diary.paintlog.view.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import com.diary.paintlog.R
import com.diary.paintlog.databinding.ActivityMainBinding
import com.diary.paintlog.utils.Common

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 컨텐츠 설정
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 메뉴 주입
        val popupMenu =
            PopupMenu(ContextThemeWrapper(this, R.style.PopupMenuCustom), binding.header.root)
        popupMenu.menuInflater.inflate(R.menu.menu_main, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.menu_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }

                R.id.menu_week -> {
                    // 메뉴 아이템 2 선택 시 할 일
                    // 예: Toast 메시지 표시
                    Common.showToast(binding.root.context,"Menu Item 2 Clicked")
                    true
                }

                else -> false
            }
        }

        // 로고 우측 화살표
        val logoArrowButton = binding.header.logoArrow

        // 팝업이 닫힐 때의 이벤트 설정
        popupMenu.setOnDismissListener {
            // 팝업이 닫힐 때 이미지 변경
            logoArrowButton.setImageResource(R.drawable.arrow_down)
        }

        binding.header.logoView.setOnClickListener {
            // 로고 클릭 때 이미지 변경
            logoArrowButton.setImageResource(R.drawable.arrow_up)
            popupMenu.show()
        }
    }
}