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
import com.diary.paintlog.data.entities.DiaryColor
import com.diary.paintlog.data.entities.MyArt
import com.diary.paintlog.utils.Common
import com.diary.paintlog.viewmodel.ArtViewModel
import com.diary.paintlog.viewmodel.DiaryViewModel
import com.diary.paintlog.viewmodel.MyArtViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class DrawingDialog(private val baseDate: LocalDate): DialogFragment() {
    private lateinit var diaryViewModel: DiaryViewModel
    private lateinit var artViewModel: ArtViewModel
    private lateinit var myArtViewModel: MyArtViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_drawing, container, false)
    }

    @SuppressLint("SetTextI18n", "DiscouragedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val width = resources.displayMetrics.widthPixels
        dialog?.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)

        diaryViewModel = ViewModelProvider(this@DrawingDialog)[DiaryViewModel::class.java]
        artViewModel = ViewModelProvider(this@DrawingDialog)[ArtViewModel::class.java]
        myArtViewModel = ViewModelProvider(this@DrawingDialog)[MyArtViewModel::class.java]

        val brush = view.findViewById<ImageView>(R.id.brush)
        brush.animate().apply {

            CoroutineScope(Dispatchers.Default).launch {
                val artData = artViewModel.getAllArt()
                val diaryData = diaryViewModel.getDiaryWeek(baseDate)
                val colorList = mutableListOf<DiaryColor>()
                for (diary in diaryData) {
                    for (color in diary.colors) {
                        colorList.add(color)
                    }
                }

                val blendColor = Common.blendColors(requireContext(), colorList)
                val drawingData = Common.getDrawingByColorData(blendColor, artData)

                myArtViewModel.saveMyArt(
                    MyArt(
                        artId = drawingData["id"]!!.toLong(),
                        baseDate = baseDate.atTime(LocalTime.now())
                    )
                )

                duration = 1000
                translationX(550f)
                withEndAction {
                    brush.animate().apply {
                        duration = 1000
                        translationX(0f)
                        translationY(800f)
                    }
                    withEndAction {
                        brush.animate().apply {
                            duration = 1000
                            translationX(550f)
                            withEndAction {
                                val resourceName = "drawing_${drawingData["drawingId"]?.substring(0, 2)}_${drawingData["drawingId"]?.substring(2, 4)}"
                                val resourceId = resources.getIdentifier(resourceName, "drawable", requireContext().packageName)
                                val scaledBitmap = Common.setScaleByWidth(resources, width, resourceId)

                                activity?.runOnUiThread {
                                    view.findViewById<ImageButton>(R.id.save_button).background.setTint(blendColor)
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