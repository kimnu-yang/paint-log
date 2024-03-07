package com.diary.paintlog.utils.listener

import java.time.LocalDateTime

interface BaseDateListener {

    fun onItemClick(baseDate: LocalDateTime)
}