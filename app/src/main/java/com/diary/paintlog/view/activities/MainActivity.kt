package com.diary.paintlog.view.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.diary.paintlog.R
import com.diary.paintlog.databinding.ActivityMainBinding

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
                    findNavController(R.id.nav_host_fragment).navigate(R.id.fragment_main)
                    true
                }

                R.id.menu_week -> {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.fragment_week_diary)
                    true
                }

                R.id.menu_art -> {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_fragment_art_wrok)
                    true
                }

                R.id.menu_search -> {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_fragment_diary_search)
                    true
                }

                R.id.menu_summary -> {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_fragment_stats)
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

        binding.header.settings.setOnClickListener {
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_fragment_settings)
        }

        if (intent.getIntExtra("destination_fragment", -1) != -1) {
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            val navController = navHostFragment.navController

            navController.navigate(intent.getIntExtra("destination_fragment", -1))
        }
    }
}