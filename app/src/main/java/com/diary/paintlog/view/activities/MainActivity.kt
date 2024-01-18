package com.diary.paintlog.view.activities

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.diary.paintlog.GlobalApplication
import com.diary.paintlog.R
import com.diary.paintlog.data.entities.User
import com.diary.paintlog.databinding.ActivityMainBinding
import com.diary.paintlog.model.repository.TokenRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val dataStore = (application as GlobalApplication).dataStore

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            val repo = TokenRepository(dataStore)
            CoroutineScope(Dispatchers.Main).launch {

                Log.i("MY", repo.getToken())
                Log.i("MY", repo.getTokenExpireAt())
                Log.i("MY", repo.getRefreshToken())
                Log.i("MY", repo.getRefreshTokenExpireAt())

//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
            }
        }

//        Kakao.openKakaoLogin(binding.root.context)

        val userDao = GlobalApplication.database.userDao()

        var user1 = User(
            1L,
            "REGISTERED",
            134513L,
            135315L,
            LocalDateTime.now(),
            LocalDateTime.now(),
            LocalDateTime.now()
        )
        var user2 = User(
            2L,
            "REGISTERED",
            134513L,
            135315L,
            LocalDateTime.now(),
            LocalDateTime.now(),
            LocalDateTime.now()
        )
        var user3 = User(
            3L,
            "REGISTERED",
            134513L,
            135315L,
            LocalDateTime.now(),
            LocalDateTime.now(),
            LocalDateTime.now()
        )

        // 코루틴을 사용한 예시
        CoroutineScope(Dispatchers.IO).launch {
//            userDao.insertAll(user1, user2, user3, user4, user5, user6, user7)

            val users = userDao.getAll()

            for (user in users) {
                Log.i("MY", user.toString())
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}