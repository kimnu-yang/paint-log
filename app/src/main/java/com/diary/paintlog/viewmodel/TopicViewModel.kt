package com.diary.paintlog.viewmodel

import androidx.lifecycle.ViewModel
import com.diary.paintlog.GlobalApplication
import com.diary.paintlog.data.entities.Topic

class TopicViewModel: ViewModel() {

    private val topicDao = GlobalApplication.database.topicDao()

    fun getRandomTopic(): Topic? {
        return topicDao.getRandomTopic()
    }
}