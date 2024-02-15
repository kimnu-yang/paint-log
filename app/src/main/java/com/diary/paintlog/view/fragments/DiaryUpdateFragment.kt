package com.diary.paintlog.view.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.diary.paintlog.R
import com.diary.paintlog.data.entities.Diary
import com.diary.paintlog.data.entities.DiaryColor
import com.diary.paintlog.data.entities.DiaryTag
import com.diary.paintlog.data.entities.enums.TempStatus
import com.diary.paintlog.data.entities.enums.Weather
import com.diary.paintlog.databinding.FragmentDiaryBinding
import com.diary.paintlog.utils.Common
import com.diary.paintlog.utils.DataListener
import com.diary.paintlog.utils.retrofit.WeatherServerClient
import com.diary.paintlog.utils.retrofit.model.WeatherResponse
import com.diary.paintlog.view.dialog.ColorSettingDialog
import com.diary.paintlog.viewmodel.DiaryColorViewModel
import com.diary.paintlog.viewmodel.DiaryTagViewModel
import com.diary.paintlog.viewmodel.DiaryViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DiaryUpdateFragment : Fragment(), DataListener {

    private var _binding: FragmentDiaryBinding? = null // 바인딩 객체 선언
    private val binding get() = _binding!! // 바인딩 객체 접근용 getter
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var tempSave: Runnable

    private lateinit var diaryViewModel: DiaryViewModel
    private lateinit var diaryTagViewModel: DiaryTagViewModel
    private lateinit var diaryColorViewModel: DiaryColorViewModel

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private lateinit var baseDate: LocalDateTime
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    private var color1: String = ""
    private var color1Percent: String = ""
    private var color2: String = ""
    private var color2Percent: String = ""
    private var color3: String = ""
    private var color3Percent: String = ""

    private var minTemp: String = ""
    private var maxTemp: String = ""
    private var nowTemp: String = ""
    private var nowWeather: Weather? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiaryBinding.inflate(inflater, container, false) // 바인딩 객체 초기화
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        diaryViewModel = ViewModelProvider(this)[DiaryViewModel::class.java]
        diaryTagViewModel = ViewModelProvider(this)[DiaryTagViewModel::class.java]
        diaryColorViewModel = ViewModelProvider(this)[DiaryColorViewModel::class.java]

        // 위치 정보 관련 패키지
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                p0.lastLocation?.let { location ->
                    // 위치 정보를 성공적으로 가져왔을 때 처리
                    // 위치 정보 사용
                    latitude = location.latitude
                    longitude = location.longitude
                }
            }
        }

        // diaryId 에 매칭된 데이터 처리
        val diaryBundle = arguments
        val diaryId = diaryBundle?.getLong("diaryId")
        CoroutineScope(Dispatchers.Default).launch {
            if(diaryId != null) {
                val diaryData = diaryViewModel.getDiaryById(diaryId)

                activity?.runOnUiThread {
                    if(diaryData?.diary != null) {
                        baseDate = diaryData.diary.registeredAt
                        val weekOfDay = baseDate.dayOfWeek.value
                        val date = baseDate.format(DateTimeFormatter.ofPattern("yyyy. MM. dd(${Common.getDayOfWeekName(weekOfDay)})"))
                        binding.date.text = date

                        binding.title.setText(diaryData.diary.title)
                        binding.content.setText(diaryData.diary.content)
                    }

                    val tag1 = diaryData?.tags?.filter { it.position == 1 }
                    if(tag1?.isNotEmpty() == true) binding.tag1.setText(tag1[0].tag)

                    val tag2 = diaryData?.tags?.filter { it.position == 2 }
                    if(tag2?.isNotEmpty() == true) binding.tag2.setText(tag2[0].tag)

                    val tag3 = diaryData?.tags?.filter { it.position == 3 }
                    if(tag3?.isNotEmpty() == true) binding.tag3.setText(tag3[0].tag)

                    val color1Data = diaryData?.colors?.filter { it.position == 1 }
                    if(color1Data?.isNotEmpty() == true){
                        color1 = color1Data[0].color.toString()
                        color1Percent = color1Data[0].ratio.toString()
                        Common.imageSetTintWithAlpha(requireContext(), binding.color1.background, color1Data[0].color.toString(), color1Data[0].ratio.toString())

                        binding.color1Text.text = "${color1Data[0].ratio}%"
                        binding.color1Text.visibility = View.VISIBLE
                        binding.color1Delete.visibility = View.VISIBLE
                    }

                    val color2Data = diaryData?.colors?.filter { it.position == 2 }
                    if(color2Data?.isNotEmpty() == true){
                        color2 = color2Data[0].color.toString()
                        color2Percent = color2Data[0].ratio.toString()
                        Common.imageSetTintWithAlpha(requireContext(), binding.color2.background, color2Data[0].color.toString(), color2Data[0].ratio.toString())

                        binding.color2Text.text = "${color2Data[0].ratio}%"
                        binding.color2Text.visibility = View.VISIBLE
                        binding.color2Delete.visibility = View.VISIBLE
                    }

                    val color3Data = diaryData?.colors?.filter { it.position == 3 }
                    if(color3Data?.isNotEmpty() == true){
                        color3 = color3Data[0].color.toString()
                        color3Percent = color3Data[0].ratio.toString()
                        Common.imageSetTintWithAlpha(requireContext(), binding.color3.background, color3Data[0].color.toString(), color3Data[0].ratio.toString())

                        binding.color3Text.text = "${color3Data[0].ratio}%"
                        binding.color3Text.visibility = View.VISIBLE
                        binding.color3Delete.visibility = View.VISIBLE
                    }

                    if(diaryData?.diary?.weather != null){
                        binding.weatherButton.visibility = View.INVISIBLE
                        binding.weatherButtonText.visibility = View.INVISIBLE
                        binding.weatherImg.visibility = View.VISIBLE
                        binding.tempMinMax.visibility = View.VISIBLE
                        binding.tempNow.visibility = View.VISIBLE

                        val weatherImage = Common.getWeatherImageByWeather(diaryData.diary.weather)
                        binding.weatherImg.setBackgroundResource(weatherImage)
                        binding.tempMinMax.text = "${diaryData.diary.tempMin}°C / ${diaryData.diary.tempMax}°C"
                        binding.tempNow.text = "${diaryData.diary.tempNow}°C"
                    }
                }
            } else {
                activity?.runOnUiThread {
                    Common.showToast(requireContext(), "일기 ID 값이 전달되지 않았습니다.")
                    findNavController().popBackStack()
                    findNavController().navigate(R.id.fragment_main)
                }
            }
        }

        // 추천 주제 버튼 클릭시 응답
        binding.newTopicBtn.setOnClickListener {

            // TODO: 추천 주제 데이터베이스 연동
            val topic = "오늘 있었던 일은?"
            // 이미 작성된 제목이 있을 때는 확인 후 실행
            if(binding.title.text.toString() != "") {
                showChangeTitleDialog(binding.root.context, topic)
            } else {
                binding.title.setText(topic)
            }
        }

        // 3번 색상버튼 선택
        binding.color1.setOnClickListener {
            val dialog = ColorSettingDialog("1")
            dialog.setDataListener(this)
            dialog.show(childFragmentManager, "ColorPopupFragment")
        }

        // 2번 색상버튼 선택
        binding.color2.setOnClickListener {
            val dialog = ColorSettingDialog("2")
            dialog.setDataListener(this)
            dialog.show(childFragmentManager, "ColorPopupFragment")
        }

        // 3번 색상버튼 선택
        binding.color3.setOnClickListener {
            val dialog = ColorSettingDialog("3")
            dialog.setDataListener(this)
            dialog.show(childFragmentManager, "ColorPopupFragment")
        }

        // 1번 색상 초기화 버튼
        binding.color1Delete.setOnClickListener {
            color1 = ""
            color1Percent = ""

            binding.color1.setBackgroundResource(0)
            binding.color1.setBackgroundResource(R.drawable.add_color_selector)
            binding.color1Delete.visibility = View.GONE
            binding.color1Text.visibility = View.GONE
        }

        // 2번 색상 초기화 버튼
        binding.color2Delete.setOnClickListener {
            color2 = ""
            color2Percent = ""

            binding.color2.setBackgroundResource(0)
            binding.color2.setBackgroundResource(R.drawable.add_color_selector)
            binding.color2Delete.visibility = View.GONE
            binding.color2Text.visibility = View.GONE
        }

        // 3번 색상 초기화 버튼
        binding.color3Delete.setOnClickListener {
            color3 = ""
            color3Percent = ""

            binding.color3.setBackgroundResource(0)
            binding.color3.setBackgroundResource(R.drawable.add_color_selector)
            binding.color3Delete.visibility = View.GONE
            binding.color3Text.visibility = View.GONE
        }

        // 날씨 불러오기 버튼 클릭시 응답
        binding.weatherButton.setOnClickListener {
            val today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            if(today != baseDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))){
                Common.showToast(requireContext(), "오늘의 날씨만 불러올 수 있습니다.")
            } else {
                if(!Common.isLocationProviderEnabled(requireActivity())) {
                    Common.showToast(binding.root.context, "위치 제공 옵션을 켜 주세요")
                } else {

                    if (ActivityCompat.checkSelfPermission(
                            requireContext(),
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(
                            requireContext(),
                            android.Manifest.permission.ACCESS_COARSE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {

                        // TODO: Deprecated 고려
                        val locationRequest = LocationRequest.create().apply {
                            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                            interval = 10000 // 10 seconds
                        }
                        fusedLocationClient.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            null
                        )

                        fusedLocationClient.lastLocation
                            .addOnSuccessListener { location ->

                                if (location == null) {
                                    Common.showToast(
                                        binding.root.context,
                                        "위치 정보를 갱신 합니다.\n잠시후 다시 시도해 주세요."
                                    )
                                } else {
                                    val latitude = latitude
                                    val longitude = longitude

                                    val grid = Common.convertBtwGridGps(latitude, longitude)
                                    var gridX = grid.x.toInt().toString()
                                    var gridY = grid.y.toInt().toString()

                                    // 이 범위 밖은 한국이 아님
                                    // TODO: 한국이 아닌 곳의 날씨 고려
                                    if (grid.x < 53 || grid.x > 144 || grid.y < 68 || grid.y > 139) {
                                        gridX = "58"
                                        gridY = "125"
                                    }

                                    WeatherServerClient.api.getWeather(
                                        Common.weatherBaseDate,
                                        Common.weatherBaseTime,
                                        gridX,
                                        gridY
                                    ).enqueue(object :
                                        Callback<WeatherResponse> {
                                        override fun onResponse(
                                            call: Call<WeatherResponse>,
                                            response: Response<WeatherResponse>
                                        ) {
                                            if (response.body()?.result?.body.toString() != "null") {
                                                val weather = response.body()?.result?.body?.items?.item
                                                minTemp = weather?.filter { it.category == "TMN" }
                                                    ?.get(0)?.fcstValue.toString()
                                                maxTemp = weather?.filter { it.category == "TMX" }
                                                    ?.get(0)?.fcstValue.toString()

                                                nowTemp = weather?.filter {
                                                    it.category == "TMP" && it.fcstTime == LocalDateTime.now()
                                                        .format(
                                                            DateTimeFormatter.ofPattern("HH00")
                                                        )
                                                }?.get(0)?.fcstValue.toString()

                                                val skyState = weather?.filter {
                                                    it.category == "SKY" && it.fcstTime == LocalDateTime.now()
                                                        .format(
                                                            DateTimeFormatter.ofPattern("HH00")
                                                        )
                                                }?.get(0)?.fcstValue

                                                val rainOrSnow = weather?.filter {
                                                    it.category == "PTY" && it.fcstTime == LocalDateTime.now()
                                                        .format(
                                                            DateTimeFormatter.ofPattern("HH00")
                                                        )
                                                }?.get(0)?.fcstValue

                                                // 날씨 불러오기 버튼과 텍스트 제거
                                                binding.weatherButton.visibility = View.INVISIBLE
                                                binding.weatherButtonText.visibility = View.GONE

                                                val tempNow = "$nowTemp°C"
                                                val tempMinMax = "$minTemp°C / $maxTemp°C"

                                                binding.tempNow.text = tempNow
                                                binding.tempMinMax.text = tempMinMax

                                                val weatherImg = Common.getWeatherImage(skyState, rainOrSnow)
                                                nowWeather = when(resources.getResourceEntryName(weatherImg)) {
                                                    "weather_sunny" -> Weather.SUNNY
                                                    "weather_cloudy" -> Weather.CLOUDY
                                                    "weather_overcast" -> Weather.OVERCAST
                                                    "weather_little_rain" -> Weather.LITTLE_RAIN
                                                    "weather_rainy" -> Weather.RAINY
                                                    "weather_snowy" -> Weather.SNOWY
                                                    "weather_snow_rain" -> Weather.SNOW_RAIN
                                                    else -> null
                                                }
                                                binding.weatherImg.setBackgroundResource(weatherImg)

                                                binding.tempNow.visibility = View.VISIBLE
                                                binding.tempMinMax.visibility = View.VISIBLE
                                                binding.weatherImg.visibility = View.VISIBLE

                                            } else {
                                                Common.showToast(binding.root.context, "날씨 데이터가 없습니다")
                                            }
                                        }

                                        override fun onFailure(
                                            call: Call<WeatherResponse>,
                                            t: Throwable
                                        ) {
                                            Common.showToast(
                                                binding.root.context,
                                                "네트워크 연결 상태를 확인 해 주세요"
                                            )
                                            t.localizedMessage?.let {
                                                Log.i("Weather Network Error", it)
                                            }
                                        }
                                    })
                                }
                            }
                    } else {
                        // 권한이 없는 경우 권한 요청
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                            LOCATION_PERMISSION_REQUEST_CODE
                        )
                    }
                }
            }
        }

        binding.cancelButton.setOnClickListener {
            findNavController().popBackStack()
            findNavController().navigate(R.id.fragment_main)
        }

        binding.saveButton.setOnClickListener {

            val title = binding.title.text.toString()
            val content = binding.content.text.toString()

            val tag1 = binding.tag1.editableText.toString()
            val tag2 = binding.tag2.editableText.toString()
            val tag3 = binding.tag3.editableText.toString()

            val color1 = color1
            val color1Percent = color1Percent
            val color2 = color2
            val color2Percent = color2Percent
            val color3 = color3
            val color3Percent = color3Percent

            val weather = nowWeather
            val minTemp = minTemp
            val maxTemp = maxTemp
            val nowTemp = nowTemp

            if(title == "" || content == ""){
                if(title == ""){
                    val targetView = binding.diaryScrollView.findViewById<View>(R.id.title)
                    binding.diaryScrollView.post { binding.diaryScrollView.scrollTo(0, targetView.top) }
                    Common.showToast(requireContext(), "제목을 작성 해 주세요")
                }
                else{
                    val targetView = binding.diaryScrollView.findViewById<View>(R.id.content)
                    binding.diaryScrollView.post { binding.diaryScrollView.scrollTo(0, targetView.top) }
                    Common.showToast(requireContext(), "내용을 작성 해 주세요")
                }
            }else{
                CoroutineScope(Dispatchers.Default).launch {
                    val todayString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    val data = diaryViewModel.getDiary(todayString,"Y")
                    val oldData = diaryViewModel.getDiary(todayString, "N")
                    val diaryId: Long

                    if (data != null){
                        val diary = data.diary
                        diaryId = diary.id
                        diary.isTemp = TempStatus.N
                        diary.title = title
                        diary.content = content
                        diary.updatedAt = LocalDateTime.now()

                        if(weather != null) {
                            diary.weather = weather
                            diary.tempMin = minTemp
                            diary.tempMax = maxTemp
                            diary.tempNow = nowTemp
                        }

                        if(oldData != null) diaryViewModel.deleteDiary(oldData.diary.id)
                        diaryViewModel.updateDiary(diary)
                    }else {
                        val diary = Diary(
                            isTemp = TempStatus.N,
                            title = title,
                            content = content
                        )
                        if (weather != null) {
                            diary.weather = weather
                            diary.tempNow = nowTemp
                            diary.tempMin = minTemp
                            diary.tempMax = maxTemp
                        }
                        diaryId = diaryViewModel.saveDiary(diary)
                    }

                    diaryTagViewModel.deleteDiaryTag(diaryId)
                    if(tag1 != "") diaryTagViewModel.saveDiaryTag(DiaryTag(diaryId = diaryId, position = 1, tag = tag1))
                    if(tag2 != "") diaryTagViewModel.saveDiaryTag(DiaryTag(diaryId = diaryId, position = 2, tag = tag2))
                    if(tag3 != "") diaryTagViewModel.saveDiaryTag(DiaryTag(diaryId = diaryId, position = 3, tag = tag3))

                    diaryColorViewModel.deleteDiaryColor(diaryId)
                    if(color1 != "") diaryColorViewModel.saveDiaryColor(DiaryColor(diaryId = diaryId, position = 1, color = Common.getColorByString(color1), ratio = color1Percent.toInt()))
                    if(color2 != "") diaryColorViewModel.saveDiaryColor(DiaryColor(diaryId = diaryId, position = 2, color = Common.getColorByString(color2), ratio = color2Percent.toInt()))
                    if(color3 != "") diaryColorViewModel.saveDiaryColor(DiaryColor(diaryId = diaryId, position = 3, color = Common.getColorByString(color3), ratio = color3Percent.toInt()))

                    activity?.runOnUiThread {
                        val diaryBundle = Bundle()
                        diaryBundle.putLong("diaryId", diaryId)
                        Common.showToast(requireContext(), "일기가 수정되었습니다.")
                        findNavController().popBackStack()
                        findNavController().navigate(R.id.fragment_diary_view, diaryBundle)
                    }
                }
            }
        }

        // 임시 저장 배치 실행
        tempSave = object: Runnable {
            override fun run() {
                val title = binding.title.text.toString()
                val content = binding.content.text.toString()

                val tag1 = binding.tag1.editableText.toString()
                val tag2 = binding.tag2.editableText.toString()
                val tag3 = binding.tag3.editableText.toString()

                val color1 = color1
                val color1Percent = color1Percent
                val color2 = color2
                val color2Percent = color2Percent
                val color3 = color3
                val color3Percent = color3Percent

                val weather = nowWeather
                val minTemp = minTemp
                val maxTemp = maxTemp
                val nowTemp = nowTemp

                if(title != "" && content != "") {
                    CoroutineScope(Dispatchers.Default).launch {
                        val todayString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        val data = diaryViewModel.getDiary(todayString,"N")
                        var diaryId: Long = 0

                        if (data != null){
                            val diary = data.diary
                            diaryId = diary.id
                            diary.title = title
                            diary.content = content
                            diary.updatedAt = LocalDateTime.now()

                            if(weather != null) {
                                diary.weather = weather
                                diary.tempMin = minTemp
                                diary.tempMax = maxTemp
                                diary.tempNow = nowTemp
                            }
                            diaryViewModel.updateDiary(diary)
                        }

                        diaryTagViewModel.deleteDiaryTag(diaryId)
                        if(tag1 != "") diaryTagViewModel.saveDiaryTag(DiaryTag(diaryId = diaryId, position = 1, tag = tag1))
                        if(tag2 != "") diaryTagViewModel.saveDiaryTag(DiaryTag(diaryId = diaryId, position = 2, tag = tag2))
                        if(tag3 != "") diaryTagViewModel.saveDiaryTag(DiaryTag(diaryId = diaryId, position = 3, tag = tag3))

                        diaryColorViewModel.deleteDiaryColor(diaryId)
                        if(color1 != "") diaryColorViewModel.saveDiaryColor(DiaryColor(diaryId = diaryId, position = 1, color = Common.getColorByString(color1), ratio = color1Percent.toInt()))
                        if(color2 != "") diaryColorViewModel.saveDiaryColor(DiaryColor(diaryId = diaryId, position = 2, color = Common.getColorByString(color2), ratio = color2Percent.toInt()))
                        if(color3 != "") diaryColorViewModel.saveDiaryColor(DiaryColor(diaryId = diaryId, position = 3, color = Common.getColorByString(color3), ratio = color3Percent.toInt()))
                    }
                }
                handler.postDelayed(this, 3000)
            }
        }
        handler.post(tempSave)
    }

    private fun showChangeTitleDialog(context: Context, topic: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("확인")
        builder.setMessage("제목이 추천 주제로 변경됩니다.")

        // "확인" 버튼 클릭 시 동작 설정
        builder.setPositiveButton("확인") { _, _ ->
            binding.title.setText(topic)
        }

        // "취소" 버튼 클릭 시 동작 설정
        builder.setNegativeButton("취소") { _, _ ->
        }

        val dialog = builder.create()
        dialog.show()
    }

    override fun onPause() {
        super.onPause()
        // 위치 업데이트를 중지
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }

    @SuppressLint("SetTextI18n")
    override fun onDataReceived(data: Map<String, String>) {
        // 데이터 받기
        when(data["colorNum"]) {
            "1" -> {
                color1 = data["colorSelect"].toString()
                color1Percent = data["colorPercent"].toString()
                Common.imageSetTintWithAlpha(requireContext(), binding.color1.background, color1, color1Percent)

                binding.color1Text.text = "$color1Percent%"
                binding.color1Text.visibility = View.VISIBLE
                binding.color1Delete.visibility = View.VISIBLE
            }
            "2" -> {
                color2 = data["colorSelect"].toString()
                color2Percent = data["colorPercent"].toString()
                Common.imageSetTintWithAlpha(requireContext(), binding.color2.background, color2, color2Percent)

                binding.color2Text.text = "$color2Percent%"
                binding.color2Text.visibility = View.VISIBLE
                binding.color2Delete.visibility = View.VISIBLE
            }
            "3" -> {
                color3 = data["colorSelect"].toString()
                color3Percent = data["colorPercent"].toString()
                Common.imageSetTintWithAlpha(requireContext(), binding.color3.background, color3, color3Percent)

                binding.color3Text.text = "$color3Percent%"
                binding.color3Text.visibility = View.VISIBLE
                binding.color3Delete.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 프래그먼트 종료시 임시 저장 배치 종료
        handler.removeCallbacks(tempSave)
    }
}