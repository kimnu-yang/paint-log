package com.diary.paintlog.view.fragments

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.diary.paintlog.databinding.FragmentDiaryBinding
import com.diary.paintlog.utils.Common
import com.diary.paintlog.utils.retrofit.WeatherServerClient
import com.diary.paintlog.utils.retrofit.model.WeatherResponse
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DiaryFragment : Fragment() {

    private var _binding: FragmentDiaryBinding? = null // 바인딩 객체 선언
    private val binding get() = _binding!! // 바인딩 객체 접근용 getter
    private lateinit var fusedLocationClient: FusedLocationProviderClient

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

        val today = LocalDateTime.now()
        val weekOfDay = today.dayOfWeek.value
        val date = today.format(DateTimeFormatter.ofPattern("yyyy. MM. dd(${Common.getDayOfWeekName(weekOfDay)})"))

        // TODO: 선택한 날짜로 변경 되어야 함
        binding.date.text = date

        binding.weatherButton.setOnClickListener {

            if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        // 위치 정보를 가져온 경우
                        location?.let {
                            val latitude = it.latitude
                            val longitude = it.longitude

                            val grid = Common.convertBtwGridGps(latitude, longitude)
                            var gridX = grid.x.toInt().toString()
                            var gridY = grid.y.toInt().toString()

                            // 이 범위 밖은 한국이 아님
                            // TODO: 한국이 아닌 곳의 날씨 고려
                            if(grid.x < 53 || grid.x > 144 || grid.y < 68 || grid.y > 139){
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
                                        val minTemp = weather?.filter { it.category == "TMN" }?.get(0)?.fcstValue
                                        val maxTemp = weather?.filter { it.category == "TMX" }?.get(0)?.fcstValue

                                        val nowTemp = weather?.filter { it.category == "TMP" && it.fcstTime == LocalDateTime.now().format(
                                            DateTimeFormatter.ofPattern("HH00")) }?.get(0)?.fcstValue

                                        val skyState = weather?.filter { it.category == "SKY" && it.fcstTime == LocalDateTime.now().format(
                                            DateTimeFormatter.ofPattern("HH00")) }?.get(0)?.fcstValue

                                        val rainOrSnow = weather?.filter { it.category == "PTY" && it.fcstTime == LocalDateTime.now().format(
                                            DateTimeFormatter.ofPattern("HH00")) }?.get(0)?.fcstValue

                                        // 날씨 불러오기 버튼과 텍스트 제거
                                        binding.weatherButton.visibility = View.INVISIBLE
                                        binding.weatherButtonText.visibility = View.GONE

                                        val tempNow = "$nowTemp°C"
                                        val tempMinMax = "$minTemp°C / $maxTemp°C"

                                        binding.tempNow.text = tempNow
                                        binding.tempMinMax.text = tempMinMax
                                        binding.weatherImg.setBackgroundResource(Common.getWeatherImage(skyState,rainOrSnow))

                                        binding.tempNow.visibility = View.VISIBLE
                                        binding.tempMinMax.visibility = View.VISIBLE
                                        binding.weatherImg.visibility = View.VISIBLE

                                    } else {
                                        Common.showToast(binding.root.context, "날씨 데이터가 없습니다")
                                        Log.i("Weather Null Error", response.toString())
                                    }
                                }

                                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                                    Common.showToast(binding.root.context, "네트워크 연결 상태를 확인 해 주세요")
                                    t.localizedMessage?.let { Log.i("Weather Network Error", it)
                                    }
                                }
                            })
                        }
                    }
            } else {
                // 권한이 없는 경우 권한 요청
                // TODO: Deprecated 고려
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
                Log.i("TEST", "위치 권한 획득 실패")
            }
        }

        // TODO: 뒤로가기 구현
        binding.cancelButton.setOnClickListener {
        }

        // TODO: 데이터 저장 로직 구현
        binding.saveButton.setOnClickListener {
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }



}