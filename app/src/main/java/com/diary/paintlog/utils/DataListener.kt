package com.diary.paintlog.utils

interface DataListener {
    fun onDataReceived(data: Map<String, String>)
}