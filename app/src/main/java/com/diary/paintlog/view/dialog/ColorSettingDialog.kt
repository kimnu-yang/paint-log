package com.diary.paintlog.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.DialogFragment
import com.diary.paintlog.R
import com.diary.paintlog.utils.Common
import com.diary.paintlog.utils.DataListener

class ColorSettingDialog(private var colorNum: String): DialogFragment() {

    private var dataListener: DataListener? = null
    private var colorSelect = ""
    private var colorPercent = ""

    fun setDataListener(listener: DataListener) {
        dataListener = listener
    }

    private fun sendDataToFragment(data: Map<String, String>) {
        dataListener?.onDataReceived(data)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_color_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                colorPercent = percent.tooltipText.toString()
                percent.isSelected = true
            }
        }

        val saveButton = view.findViewById<ImageButton>(R.id.save_button)
        saveButton.setOnClickListener {

            if(colorSelect == "" || colorPercent == ""){
                context?.let { it -> Common.showToast(it, "모든 항목을 선택 해 주세요") }
            } else {
                val data = mapOf(
                    "colorNum" to colorNum,
                    "colorSelect" to colorSelect,
                    "colorPercent" to colorPercent
                )
                sendDataToFragment(data)
                dismiss()
            }
        }

        // 취소 버튼 클릭시
        val cancelButton = view.findViewById<ImageButton>(R.id.cancel_button)
        cancelButton.setOnClickListener {
            dismiss()
        }
    }
}