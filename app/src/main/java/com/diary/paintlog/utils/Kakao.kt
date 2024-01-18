package com.diary.paintlog.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.diary.paintlog.GlobalApplication
import com.diary.paintlog.model.ApiToken
import com.diary.paintlog.model.repository.TokenRepository
import com.diary.paintlog.utils.retrofit.ApiServerClient
import com.diary.paintlog.utils.retrofit.model.ApiRegisterResponse
import com.diary.paintlog.utils.retrofit.model.KakaoRegisterRequest
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.model.AccessTokenInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object Kakao {
    val TAG = "KAKAO"

    fun openKakaoLogin(context: Context) {
        // 로그인 조합 예제

        // 카카오계정으로 로그인 공통 callback 구성
        // 카카오톡으로 로그인 할 수 없어 카카오계정으로 로그인할 경우 사용됨
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e(TAG, "카카오계정으로 로그인 실패", error)
            } else if (token != null) {
                Log.i(TAG, "카카오계정으로 로그인 성공 ${token.accessToken}")

                apiKakaoLogin(
                    token.accessToken,
                    (context.applicationContext as GlobalApplication).dataStore
                )

                moveSubActivity(context)
            }
        }

        // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                if (error != null) {
                    Log.e(TAG, "카카오톡으로 로그인 실패: " + error.message, error)

                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }

                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(
                        context,
                        callback = callback
                    )
                } else if (token != null) {
                    Log.i(TAG, "카카오톡으로 로그인 성공 ${token.accessToken}")

                    moveSubActivity(context)
                }
            }
        } else {
            Toast.makeText(context, "카카오톡 설치 안된 경우", Toast.LENGTH_LONG).show()
            UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
        }
    }


    fun apiKakaoLogin(token: String, dataStore: DataStore<Preferences>) {
        val tagApi = TAG + "_API"
        var repo = TokenRepository(dataStore)
        ApiServerClient.api.kakaoLogin(KakaoRegisterRequest(token)).enqueue(object :
            Callback<ApiRegisterResponse> {
            override fun onResponse(
                call: Call<ApiRegisterResponse>,
                response: Response<ApiRegisterResponse>
            ) {
                if (response.isSuccessful) {
                    Log.i(tagApi, "${response.body()}")


                    CoroutineScope(Dispatchers.Main).launch {
                        val data = response.body()?.data
                        repo.save(
                            ApiToken(
                                data?.accessToken ?: "",
                                data?.accessTokenExpiredAt ?: "",
                                data?.refreshToken ?: "",
                                data?.refreshTokenExpiredAt ?: ""
                            )
                        )
                    }
                } else {
                    Log.i(tagApi, response.toString())
                }
            }

            override fun onFailure(call: Call<ApiRegisterResponse>, t: Throwable) {
                Log.i(tagApi, t.localizedMessage?.toString() ?: "ERROR")
            }
        })
    }

    private fun moveSubActivity(context: Context) {
        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            if (tokenInfo != null) {
                Log.i(
                    TAG, "토큰 정보 보기 성공" +
                            "\n회원번호: ${tokenInfo.id}" +
                            "\n만료시간: ${tokenInfo.expiresIn} 초"
                )

                // 로그인 후 화면
//                val intent = Intent(context, SubActivity::class.java)
//                intent.putExtra("id", tokenInfo.id)
//                intent.putExtra("expiresIn", tokenInfo.expiresIn)
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

//                context.startActivity(intent)
            } else {
                Log.i(TAG, "토큰 정보 보기 실패: " + error?.message)
            }
        }
    }

    private fun getTokenInfo(): AccessTokenInfo? {
        var accessTokenInfo: AccessTokenInfo? = null

        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            if (tokenInfo != null) {
                Log.i(
                    TAG, "토큰 정보 보기 성공" +
                            "\n회원번호: ${tokenInfo.id}" +
                            "\n만료시간: ${tokenInfo.expiresIn} 초"
                )

                accessTokenInfo = tokenInfo
            }
        }

        return accessTokenInfo
    }
}