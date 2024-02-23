package com.diary.paintlog.view.dialog

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.diary.paintlog.R
import com.diary.paintlog.utils.Common
import com.diary.paintlog.utils.DataListener
import com.diary.paintlog.viewmodel.DiaryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DrawingDialog(private val baseDate: LocalDate): DialogFragment() {
    private lateinit var diaryViewModel: DiaryViewModel
    private var dataListener: DataListener? = null

    fun setDataListener(listener: DataListener) {
        dataListener = listener
    }

    private fun sendDataToFragment(data: Map<String, String>) {
        dataListener?.onDataReceived(data)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_drawing, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val width = resources.displayMetrics.widthPixels
        dialog?.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)

        val brush = view.findViewById<ImageView>(R.id.brush)
        brush.animate().apply {
            duration = 1000
            translationX(550f)
            withEndAction{
                brush.animate().apply {
                    duration = 1000
                    translationX(0f)
                    translationY(800f)
                }
                withEndAction{
                    brush.animate().apply {
                        duration = 1000
                        translationX(550f)
                        withEndAction{
                            diaryViewModel = ViewModelProvider(this@DrawingDialog)[DiaryViewModel::class.java]
                            val colorData = mutableMapOf(
                                "RED" to 0.0,
                                "ORANGE" to 0.0,
                                "YELLOW" to 0.0,
                                "GREEN" to 0.0,
                                "BLUE" to 0.0,
                                "NAVY" to 0.0,
                                "VIOLET" to 0.0
                            )
                            var total = 0
                            CoroutineScope(Dispatchers.Default).launch {
                                val diaryData = diaryViewModel.getDiaryWeek(baseDate)
                                for(diary in diaryData){
                                    for(color in diary.colors){
                                        total += color.ratio
                                        colorData[color.color.toString()] = colorData[color.color.toString()]!! + color.ratio
                                    }
                                }
                                for(color in colorData){
                                    colorData[color.key] = color.value / total
                                }

                                // 비율 맞추기
                                val drawingData = Common.getDrawingByColorData(colorData)
                                val scaledBitmap = drawingData["drawing"]?.let { Common.setScaleByWidth(resources, width, it.toInt()) }

                                activity?.runOnUiThread {
                                    brush.background = null
                                    view.findViewById<ImageView>(R.id.canvas).background = null
                                    view.findViewById<ImageView>(R.id.canvas).setImageBitmap(scaledBitmap)
                                    view.findViewById<TextView>(R.id.title).text = drawingData["title"]
                                    view.findViewById<TextView>(R.id.artist).text = drawingData["artist"]
                                }
                            }
                        }
                    }
                }
            }
        }

        val saveButton = view.findViewById<ImageButton>(R.id.save_button)
        saveButton.setOnClickListener {
            val dateBundle = Bundle()
            dateBundle.putString("date", baseDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
            findNavController().popBackStack()
            findNavController().navigate(R.id.fragment_week_diary, dateBundle)
        }
    }
}