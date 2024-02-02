package com.diary.paintlog.utils

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import android.widget.Toast
import com.diary.paintlog.R
import com.diary.paintlog.utils.retrofit.ApiServerClient
import com.diary.paintlog.utils.retrofit.model.ApiLoginResponse
import com.diary.paintlog.utils.retrofit.model.KakaoRegisterRequest
import com.diary.paintlog.view.dialog.LoadingDialog
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object Kakao {
    private val TAG = this.javaClass.simpleName

    fun openKakaoLogin(context: Context, callbackFunc: () -> Unit = {}) {

        if (!checkNetwork(context)) {
            return
        }

        // 로그인 조합 예제

        // 카카오계정으로 로그인 공통 callback 구성
        // 카카오톡으로 로그인 할 수 없어 카카오계정으로 로그인할 경우 사용됨
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e(TAG, "카카오계정으로 로그인 실패", error)
            } else if (token != null) {
                Log.i(TAG, "카카오계정으로 로그인 성공 ${token.accessToken}")
                apiKakaoLogin(
                    context,
                    token.accessToken,
                    callbackFunc
                )
            }
        }

        // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                if (error != null) {
                    Log.e(TAG, "카카오톡으로 로그인 실패", error)

                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }

                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
                } else if (token != null) {
                    Log.i(TAG, "카카오톡으로 로그인 성공 ${token.accessToken}")

                    apiKakaoLogin(
                        context,
                        token.accessToken,
                        callbackFunc
                    )
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
        }
    }

    fun kakaoLogout(context: Context, callback: () -> Any = {}) {
        if (!checkNetwork(context)) {
            return
        }

        // 로그아웃
        UserApiClient.instance.logout { error ->
            if (error != null) {
                Log.e(TAG, "로그아웃 실패. SDK에서 토큰 삭제됨", error)
            } else {
                Log.i(TAG, "로그아웃 성공. SDK에서 토큰 삭제됨")
            }

            callback()
        }
    }

    private fun checkNetwork(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

        if (networkCapabilities == null) {
            // 네트워크에 연결되어 있지 않음
            Toast.makeText(context, "네트워크 연결 후 시도 바랍니다.", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun apiKakaoLogin(
        context: Context,
        token: String,
        callbackFunc: () -> Unit = {}
    ) {
        val loadingDialog = LoadingDialog(context)
        loadingDialog.show()

        ApiServerClient.api.kakaoLogin(KakaoRegisterRequest(token)).enqueue(object :
            Callback<ApiLoginResponse> {
            override fun onResponse(
                call: Call<ApiLoginResponse>,
                response: Response<ApiLoginResponse>
            ) {
                if (response.isSuccessful) {
                    Log.i(TAG, "${response.body()}")
                    callbackFunc()
                } else {
                    Log.i(TAG, response.toString())
                    kakaoLogout(context)
                    Toast.makeText(
                        context,
                        context.getString(R.string.setting_kakao_login_error, "0"),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                loadingDialog.dismiss()
            }

            override fun onFailure(call: Call<ApiLoginResponse>, t: Throwable) {
                Log.i(TAG, t.localizedMessage?.toString() ?: "ERROR")
                Toast.makeText(
                    context,
                    context.getString(R.string.setting_kakao_login_error, "1"),
                    Toast.LENGTH_SHORT
                ).show()

                kakaoLogout(context)
                loadingDialog.dismiss()
            }
        })
    }
}