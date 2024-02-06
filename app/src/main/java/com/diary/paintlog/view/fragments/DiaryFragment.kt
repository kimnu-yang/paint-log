package com.diary.paintlog.view.fragments

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.diary.paintlog.R
import com.diary.paintlog.databinding.FragmentDiaryBinding
import com.diary.paintlog.utils.Common
import com.diary.paintlog.utils.DataListener
import com.diary.paintlog.utils.retrofit.WeatherServerClient
import com.diary.paintlog.utils.retrofit.model.WeatherResponse
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DiaryFragment : Fragment(), DataListener {

    private var _binding: FragmentDiaryBinding? = null // 바인딩 객체 선언
    private val binding get() = _binding!! // 바인딩 객체 접근용 getter
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiaryBinding.inflate(inflater, container, false) // 바인딩 객체 초기화
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 위치 정보 관련 패키지
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                p0?.lastLocation?.let { location ->
                    // 위치 정보를 성공적으로 가져왔을 때 처리
                    // 위치 정보 사용
                    latitude = location.latitude
                    longitude = location.longitude
                }
            }
        }

        val today = LocalDateTime.now()
        val weekOfDay = today.dayOfWeek.value
        val date = today.format(DateTimeFormatter.ofPattern("yyyy. MM. dd(${Common.getDayOfWeekName(weekOfDay)})"))

        // TODO: 선택한 날짜로 변경 되어야 함
        binding.date.text = date

        // 추천 주제 버튼 클릭시 응답
        binding.newTopicBtn.setOnClickListener {

            val topic = "오늘 있었던 일은?"
            // 이미 작성된 제목이 있을 때는 확인 후 실행
            if(binding.title.text.toString() != "") {
                showConfirmationDialog(binding.root.context, topic)
            } else {
                binding.title.setText(topic)
            }
        }

        binding.color1.setOnClickListener {
            val dialog = ColorPopupFragment(1)
            dialog.setDataListener(this)
            dialog.show(childFragmentManager, "ColorPopupFragment")
        }

        binding.color2.setOnClickListener {
            val dialog = ColorPopupFragment(2)
            dialog.setDataListener(this)
            dialog.show(childFragmentManager, "ColorPopupFragment")
        }

        binding.color3.setOnClickListener {
            val dialog = ColorPopupFragment(3)
            dialog.setDataListener(this)
            dialog.show(childFragmentManager, "ColorPopupFragment")
        }

        // 날씨 불러오기 버튼 클릭시 응답
        binding.weatherButton.setOnClickListener {
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
                                    "위치 정보를 갱신 중입니다.\n잠시후 다시 시도해 주세요."
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
                                            val minTemp = weather?.filter { it.category == "TMN" }
                                                ?.get(0)?.fcstValue
                                            val maxTemp = weather?.filter { it.category == "TMX" }
                                                ?.get(0)?.fcstValue

                                            val nowTemp = weather?.filter {
                                                it.category == "TMP" && it.fcstTime == LocalDateTime.now()
                                                    .format(
                                                        DateTimeFormatter.ofPattern("HH00")
                                                    )
                                            }?.get(0)?.fcstValue

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
                                            binding.weatherImg.setBackgroundResource(
                                                Common.getWeatherImage(
                                                    skyState,
                                                    rainOrSnow
                                                )
                                            )

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

        // TODO: 뒤로가기 구현
        binding.cancelButton.setOnClickListener {
        }

        // TODO: 데이터 저장 로직 구현
        binding.saveButton.setOnClickListener {
        }
    }

    private fun showConfirmationDialog(context: Context, topic: String) {
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

    override fun onDataReceived(data: Map<String, String>) {
        // 데이터 받기
        data["colorSelect"]?.let { Log.i("TEST", it) }
    }
}