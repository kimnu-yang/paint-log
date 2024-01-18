package com.diary.paintlog.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.diary.paintlog.GlobalApplication
import com.diary.paintlog.data.entities.User

class UserViewModel: ViewModel() {

    private val userDao = GlobalApplication.database.userDao()

    fun getUserAll(): LiveData<List<User>> {
        return userDao.getAllLive()
    }

    fun saveUser(user: User) {
        userDao.insertAll(user)
    }
}