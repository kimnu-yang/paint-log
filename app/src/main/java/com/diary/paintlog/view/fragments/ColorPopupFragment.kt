package com.diary.paintlog.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.diary.paintlog.R

class ColorPopupFragment(colorNum: Int): DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.set_color_popup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var colorSelect = ""
        var colorPercent = 0

        // 다이얼로그의 너비를 설정
        dialog?.window?.setLayout(1050, ViewGroup.LayoutParams.WRAP_CONTENT) // 너비 설정 (픽셀 단위)

        // 색 선택 버튼에 전부 리스너 처리
        val colorIds = arrayOf(R.id.color_red,R.id.color_orange,R.id.color_yellow,R.id.color_green,R.id.color_blue,R.id.color_navy,R.id.color_violet)
        for (colorId in colorIds) {
            val color = view.findViewById<ImageButton>(colorId)
            color.setOnClickListener {
                for (colorId in colorIds) {
                    val color = view.findViewById<ImageButton>(colorId)
                    color.isSelected = false
                }
                colorSelect = color.tooltipText.toString()
                color.isSelected = true
            }
        }

        // 퍼센트 버튼에 전부 리스너 처리
        val percentIds = arrayOf(R.id.percent_10,R.id.percent_20,R.id.percent_30,R.id.percent_40,R.id.percent_50,R.id.percent_60,R.id.percent_70,R.id.percent_80,R.id.percent_90,R.id.percent_100)
        for (percentId in percentIds) {
            val percent = view.findViewById<ImageButton>(percentId)
            percent.setOnClickListener {
                for(percentId in percentIds) {
                    val percent = view.findViewById<ImageButton>(percentId)
                    percent.isSelected = false
                }
                colorPercent = percent.tooltipText.toString().toInt()
                percent.isSelected = true
            }
        }

        val saveButton = view.findViewById<ImageButton>(R.id.save_button)
        saveButton.setOnClickListener {
            // TODO: 선택한 변수들을 DirayFragment로 전달해야 함
            dismiss()
        }

        // 취소 버튼 클릭시
        val cancelButton = view.findViewById<ImageButton>(R.id.cancel_button)
        cancelButton.setOnClickListener {
            dismiss()
        }
    }
}