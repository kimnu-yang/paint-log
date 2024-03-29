package com.diary.paintlog.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import com.diary.paintlog.data.entities.enums.Color as ColorEnum
import android.graphics.drawable.Drawable
import android.location.LocationManager
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.blue
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.fragment.app.FragmentActivity
import com.diary.paintlog.BuildConfig
import com.diary.paintlog.R
import com.diary.paintlog.data.entities.Art
import com.diary.paintlog.data.entities.DiaryColor
import com.diary.paintlog.data.entities.MyArtWithInfo
import com.diary.paintlog.data.entities.enums.Weather
import okhttp3.internal.toHexString
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Locale
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
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

    fun getColorEnumsByString(color: String): ColorEnum {
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

        val color = getColorByString(colorString)

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

    fun getWeatherImageByWeather(weather: Weather?): Int {

        return when(weather){
            Weather.SUNNY -> R.drawable.weather_sunny
            Weather.CLOUDY -> R.drawable.weather_cloudy
            Weather.OVERCAST -> R.drawable.weather_overcast
            Weather.LITTLE_RAIN -> R.drawable.weather_little_rain
            Weather.RAINY -> R.drawable.weather_rainy
            Weather.SNOWY -> R.drawable.weather_snowy
            Weather.SNOW_RAIN -> R.drawable.weather_snow_rain
            else -> R.drawable.weather_sunny
        }
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

        // LCC DFS 좌표변환 ( code : "TO_GRID"(위경도->좌표, lat_X:위도,  lng_Y:경도), "TO_GPS"(좌표->위경도,  lat_X:x, lng_Y:y) )
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
            var theta: Double
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

    // 숫자로 받은 주차를 문자로 변경
    fun weekNumberToString(weekNumber: Int): String {
        return when(weekNumber){
            1 -> "첫째 주"
            2 -> "둘째 주"
            3 -> "셋째 주"
            4 -> "넷째 주"
            5 -> "다섯째 주"
            else -> ""
        }
    }

    // 기준일이 몇 월의 몇째 주인지 출력
    fun getMonthAndWeek(baseDate: LocalDate): Map<String, Int> {
        val weekField = WeekFields.of(Locale.getDefault())
        val weekMap = mutableMapOf<String,Int>()
        val date = baseDate.with(DayOfWeek.THURSDAY)
        weekMap["month"] = date.monthValue
        weekMap["week"] = date.get(weekField.weekOfMonth())

        val firstDayOfMonth = date.with(TemporalAdjusters.firstDayOfMonth())
        val dayOfWeekFirst = firstDayOfMonth.dayOfWeek.value

        // 기준 월의 첫 요일이 금, 토 일때(일요일인 경우 weekOfMonth 에서 한주로 계산되지 않음)
        if(dayOfWeekFirst in 5..6) weekMap["week"] = weekMap["week"]!! - 1

        return weekMap
    }

    fun blendColors(context: Context, colors: List<DiaryColor>): Int {
        var cnt = 0
        var r = 0F
        var g = 0F
        var b = 0F
        for(color in colors){
            val pickedColor = ContextCompat.getColor(context,getColorByString(color.color.toString()))
            r += Color.red(pickedColor) * (color.ratio.toFloat() / 100)
            g += Color.green(pickedColor) * (color.ratio.toFloat() / 100)
            b += Color.blue(pickedColor) * (color.ratio.toFloat() / 100)
            cnt += 1
        }

        if(cnt > 0){
            r /= cnt
            g /= cnt
            b /= cnt
        } else {
            val color = ContextCompat.getColor(context, R.color.gray)
            r = color.red.toFloat()
            g = color.green.toFloat()
            b = color.blue.toFloat()
        }

        return Color.rgb(r.toInt(),g.toInt(),b.toInt())
    }

    fun getColorByString(colorString: String): Int {
        return when (colorString) {
            "RED" -> R.color.red
            "ORANGE" -> R.color.orange
            "YELLOW" -> R.color.yellow
            "GREEN" -> R.color.green
            "BLUE" -> R.color.blue
            "NAVY" -> R.color.navy
            "VIOLET" -> R.color.violet
            else -> R.color.gray
        }
    }

    fun getDrawingByColorData(color: Int, artData: List<Art>): Map<String, String> {
        val res = mutableMapOf<String, String>()

        val hexString = color.toHexString()
        val r = Integer.parseInt(hexString.substring(2, 4), 16)
        val g = Integer.parseInt(hexString.substring(4, 6), 16)
        val b = Integer.parseInt(hexString.substring(6, 8), 16)

        var oldDiff = 1000
        artData.forEach {
            var diff = 0

            val artR = Integer.parseInt(it.rgb.substring(2, 4), 16)
            val artG = Integer.parseInt(it.rgb.substring(4, 6), 16)
            val artB = Integer.parseInt(it.rgb.substring(6, 8), 16)

            diff += abs(artR - r)
            diff += abs(artG - g)
            diff += abs(artB - b)

            if(oldDiff > diff) {
                oldDiff = diff
                res["id"] = it.id.toString()
                res["title"] = it.title
                res["artist"] = it.artist
                res["drawingId"] = it.resourceId
            }
        }
        return res
    }

    fun setScaleByWidth(resources: Resources, width: Int, image: Int): Bitmap {
        val bitmap = BitmapFactory.decodeResource(resources, image)
        val aspectRatio = bitmap.width.toDouble() / bitmap.height.toDouble()
        val scaledHeight = (width / aspectRatio).toInt()
        return Bitmap.createScaledBitmap(bitmap, width, scaledHeight, false)
    }

    fun hexToRgb(hex: String): IntArray {
        val color = hex.toLong(16).toInt()
        val red = color shr 16 and 0xFF
        val green = color shr 8 and 0xFF
        val blue = color and 0xFF
        return intArrayOf(red, green, blue)
    }

    fun getMyArtWithInfoByRGB(data: MutableList<MyArtWithInfo>, color: Int): MutableList<MyArtWithInfo> {
        val res = mutableListOf<MyArtWithInfo>()

        val hexString = color.toHexString()
        val r = Integer.parseInt(hexString.substring(2, 4), 16)
        val g = Integer.parseInt(hexString.substring(4, 6), 16)
        val b = Integer.parseInt(hexString.substring(6, 8), 16)


        for (myArt in data){
            val artRgb = myArt.art.rgb
            val artR = Integer.parseInt(artRgb.substring(2, 4), 16)
            val artG = Integer.parseInt(artRgb.substring(2, 4), 16)
            val artB = Integer.parseInt(artRgb.substring(2, 4), 16)

            val totalDiff = abs(artR - r) + abs(artG - g) + abs(artB - b)
            if(totalDiff < 150) res.add(myArt)
        }

        return res
    }

    @SuppressLint("GetInstance")
    fun encrypt(text: String): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        val secretKey = SecretKeySpec(BuildConfig.ENCRYPT_KEY.toByteArray(), "AES")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptedBytes = cipher.doFinal(text.toByteArray())
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    @SuppressLint("GetInstance")
    fun decrypt(encryptedText: String): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        val secretKey = SecretKeySpec(BuildConfig.ENCRYPT_KEY.toByteArray(), "AES")
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        val encryptedBytes = Base64.decode(encryptedText, Base64.DEFAULT)
        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return String(decryptedBytes)
    }

}