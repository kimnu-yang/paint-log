package com.diary.paintlog.utils.listener

interface DataListener {
    fun onDataReceived(data: Map<String, String>)
}