package com.diary.paintlog.utils

import android.content.Context
import android.graphics.Color
import com.diary.paintlog.data.entities.enums.Color as ColorEnum
import android.graphics.drawable.Drawable
import android.location.LocationManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.diary.paintlog.R
import com.diary.paintlog.data.entities.DiaryColor
import com.diary.paintlog.data.entities.DiaryTag
import com.diary.paintlog.viewmodel.DiaryColorViewModel
import com.diary.paintlog.viewmodel.DiaryTagViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

object Common {

    fun getColorByString(color: String): ColorEnum {
        return when (color) {
            "RED" -> com.diary.paintlog.data.entities.enums.Color.RED
            "ORANGE" -> com.diary.paintlog.data.entities.enums.Color.ORANGE
            "YELLOW" -> com.diary.paintlog.data.entities.enums.Color.YELLOW
            "GREEN" -> com.diary.paintlog.data.entities.enums.Color.GREEN
            "BLUE" -> com.diary.paintlog.data.entities.enums.Color.BLUE
            "NAVY" -> com.diary.paintlog.data.entities.enums.Color.NAVY
            "VIOLET" -> com.diary.paintlog.data.entities.enums.Color.VIOLET
            else -> com.diary.paintlog.data.entities.enums.Color.NONE
        }
    }

    fun imageSetTintWithAlpha(context: Context, drawable: Drawable, colorString: String, percentString: String) {

        //투명도를 alpha 값으로 변환
        val alpha = 255 * percentString.toInt() / 100

        val color = when (colorString) {
            "RED" -> R.color.red
            "ORANGE" -> R.color.orange
            "YELLOW" -> R.color.yellow
            "GREEN" -> R.color.green
            "BLUE" -> R.color.blue
            "NAVY" -> R.color.navy
            "VIOLET" -> R.color.violet
            else -> R.color.gray
        }

        val tintColor = ContextCompat.getColor(context, color)
        val colorWithAlpha = Color.argb(alpha, Color.red(tintColor), Color.green(tintColor), Color.blue(tintColor))
        DrawableCompat.setTint(drawable, colorWithAlpha)
    }

    // 위치 제공 옵션 활성화 여부
    fun isLocationProviderEnabled(fragmentActivity: FragmentActivity): Boolean {
        val locationManager = fragmentActivity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    val weatherBaseDate: String = getWeatherBaseDateAndTime()[0]
    val weatherBaseTime: String = getWeatherBaseDateAndTime()[1]

    // 날씨 정보를 얻기 위한 기준 일자와 시간을 출력
    private fun getWeatherBaseDateAndTime() =
        when (LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmm")).toInt()) {
            in 0..1720 ->
                listOf(LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd")), "2300")
            in 1721..2020 ->
                listOf(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")), "1700")
            in 2021..2320 ->
                listOf(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")),"2100")
            else ->
                listOf(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")), "2300")
        }

    // 날씨 정보에 맞는 이미지를 출력
    fun getWeatherImage(skyState: String?, rainOrSnow: String?): Int {

        when(rainOrSnow){
            "0" -> {
                when(skyState){
                    "1" -> return R.drawable.weather_sunny
                    "3" -> return R.drawable.weather_cloudy
                    "4" -> return R.drawable.weather_overcast
                }
            }

            "1" -> return R.drawable.weather_rainy
            "2" -> return R.drawable.weather_snow_rain
            "3" -> return R.drawable.weather_snowy
            "4" -> return R.drawable.weather_little_rain
        }

        return R.drawable.weather_sunny
    }

    // 위도, 경도 <-> X,Y 좌표
    fun convertBtwGridGps(latOrX: Double, lngOrY: Double, mode: Int = 0): LatXLngY {
        val RE = 6371.00877 // 지구 반경(km)
        val GRID = 5.0 // 격자 간격(km)
        val SLAT1 = 30.0 // 투영 위도1(degree)
        val SLAT2 = 60.0 // 투영 위도2(degree)
        val OLON = 126.0 // 기준점 경도(degree)
        val OLAT = 38.0 // 기준점 위도(degree)
        val XO = 43.0 // 기준점 X좌표(GRID)
        val YO = 136.0 // 기1준점 Y좌표(GRID)

        //
        // LCC DFS 좌표변환 ( code : "TO_GRID"(위경도->좌표, lat_X:위도,  lng_Y:경도), "TO_GPS"(좌표->위경도,  lat_X:x, lng_Y:y) )
        //
        val DEGRAD = Math.PI / 180.0
        val RADDEG = 180.0 / Math.PI
        val re = RE / GRID
        val slat1 = SLAT1 * DEGRAD
        val slat2 = SLAT2 * DEGRAD
        val olon = OLON * DEGRAD
        val olat = OLAT * DEGRAD
        var sn = tan(Math.PI * 0.25 + slat2 * 0.5) / tan(Math.PI * 0.25 + slat1 * 0.5)
        sn = ln(cos(slat1) / cos(slat2)) / ln(sn)
        var sf = tan(Math.PI * 0.25 + slat1 * 0.5)
        sf = sf.pow(sn) * cos(slat1) / sn
        var ro = tan(Math.PI * 0.25 + olat * 0.5)
        ro = re * sf / ro.pow(sn)
        val rs = LatXLngY()

        // 위도,경도를 좌표로
        if (mode == 0) {
            rs.lat = latOrX
            rs.lng = lngOrY
            var ra = tan(Math.PI * 0.25 + latOrX * DEGRAD * 0.5)
            ra = re * sf / ra.pow(sn)
            var theta = lngOrY * DEGRAD - olon
            if (theta > Math.PI) theta -= 2.0 * Math.PI
            if (theta < -Math.PI) theta += 2.0 * Math.PI
            theta *= sn
            rs.x = floor(ra * sin(theta) + XO + 0.5)
            rs.y = floor(ro - ra * cos(theta) + YO + 0.5)

            // 좌표를 위도,경도로
        } else {
            rs.x = latOrX
            rs.y = lngOrY
            val xn = latOrX - XO
            val yn = ro - lngOrY + YO
            var ra = sqrt(xn * xn + yn * yn)
            if (sn < 0.0) {
                ra = -ra
            }
            var alat = (re * sf / ra).pow(1.0 / sn)
            alat = 2.0 * atan(alat) - Math.PI * 0.5
            var theta = 0.0
            if (abs(xn) <= 0.0) {
                theta = 0.0
            } else {
                if (abs(yn) <= 0.0) {
                    theta = Math.PI * 0.5
                    if (xn < 0.0) {
                        theta = -theta
                    }
                } else theta = atan2(xn, yn)
            }
            val alon = theta / sn + olon
            rs.lat = alat * RADDEG
            rs.lng = alon * RADDEG
        }
        return rs
    }

    // 위도,경도와 X,Y 좌표를 관리하기 위한 클래스
    class LatXLngY {
        var lat = 0.0
        var lng = 0.0
        var x = 0.0
        var y = 0.0
    }

    // dayOfWeek 로 얻은 숫자가 무슨 요일을 뜻하는지 출력
    fun getDayOfWeekName(day: Int): String {
        return when (day) {
            1 -> "월"
            2 -> "화"
            3 -> "수"
            4 -> "목"
            5 -> "금"
            6 -> "토"
            7 -> "일"
            else -> ""
        }
    }

    // 토스트 메세지를 출력
    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    // 일기 ID와 Position 에 매칭되는 태그 데이터를 삭제하고 등록한다.
    fun tagInsertWithDelete(view: ViewModelStoreOwner, diaryId: Long, position: Int, tag: String) {
        val diaryTagViewModel = ViewModelProvider(view)[DiaryTagViewModel::class.java]
        val oldTag = diaryTagViewModel.getDiaryTag(diaryId, position)
        if(oldTag != null) {
            if(oldTag.tag != tag){
                diaryTagViewModel.deleteDiaryTag(diaryId, position)
                diaryTagViewModel.saveDiaryTag(
                    DiaryTag(
                        diaryId = diaryId,
                        position = position,
                        tag = tag)
                )
            }
        } else {
            diaryTagViewModel.saveDiaryTag(
                DiaryTag(
                    diaryId = diaryId,
                    position = position,
                    tag = tag)
            )
        }
    }

    // 일기 ID와 Position 에 매칭되는 색상 데이터를 삭제하고 등록한다.
    fun colorInsertWithDelete(view: ViewModelStoreOwner, diaryId: Long, position: Int, color: String, percent: String) {
        val diaryColorViewModel = ViewModelProvider(view)[DiaryColorViewModel::class.java]
        val oldColor = diaryColorViewModel.getDiaryColor(diaryId, position)
        if(oldColor != null){
            if(oldColor.color != getColorByString(color) || oldColor.ratio != percent.toInt()) {
                diaryColorViewModel.deleteDiaryColor(diaryId, position)
                DiaryColor(
                    diaryId = diaryId,
                    position = position,
                    color = getColorByString(color),
                    ratio = percent.toInt()
                )
            }
        } else {
            diaryColorViewModel.saveDiaryColor(
                DiaryColor(
                    diaryId = diaryId,
                    position = position,
                    color = getColorByString(color),
                    ratio = percent.toInt()
                )
            )
        }
    }
}