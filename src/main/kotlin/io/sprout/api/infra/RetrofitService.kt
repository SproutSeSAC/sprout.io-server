package io.sprout.api.infra

import org.springframework.stereotype.Service
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

@Service
class RetrofitService {

    companion object {
        const val CLIENT_ID = ""
        const val CLIENT_SECRET = ""
        const val REDIRECT_URI = ""
    }

    //TODO: 환경변수 application.yml에 정의, 변수명 및 함수명 변경, interface 및 data class 분리

    fun inTokenIdOutGoogleUserInfo(tokenId: String): GoogleUserInfoResponse? {

        val retrofit = Retrofit.Builder()
            .baseUrl("https://oauth2.googleapis.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(GoogleUserInfoApi::class.java)
        val data = api.tokenInfo(tokenId).execute()
        return data.body()
    }

    fun inCodeOutTokenId(code: String): GoogleTokenInfoResponse? {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://oauth2.googleapis.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(GoogleTokenInfoApi::class.java)
        val call = api.exchangeCodeForToken(
            code = code,
            clientId = CLIENT_ID,
            clientSecret = CLIENT_SECRET,
            redirectUri = REDIRECT_URI
        )


        var googleTokenInfoResponse: GoogleTokenInfoResponse? = null

        call.enqueue(object : retrofit2.Callback<GoogleTokenInfoResponse> {
            override fun onResponse(
                call: Call<GoogleTokenInfoResponse>,
                response: retrofit2.Response<GoogleTokenInfoResponse>
            ) {
                if (response.isSuccessful) {
                    val tokenResponse = response.body()
                    if (tokenResponse != null) {
                        val accessToken = tokenResponse.access_token
                        val idToken = tokenResponse.id_token
                        googleTokenInfoResponse = GoogleTokenInfoResponse(accessToken, idToken)
                    }
                } else {
                    println("응답실패")
                    println(response)
                }
            }

            override fun onFailure(call: Call<GoogleTokenInfoResponse>, t: Throwable) {
                println("통신 실패")
                println(t)
            }
        })

        return googleTokenInfoResponse
    }

}

interface GoogleUserInfoApi {
    @GET("https://oauth2.googleapis.com/tokeninfo")
    fun tokenInfo(@Query("id_token") id_token: String): Call<GoogleUserInfoResponse>
}

interface GoogleTokenInfoApi {
    @FormUrlEncoded
    @POST("token")
    fun exchangeCodeForToken(
        @Field("code") code: String,
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("redirect_uri") redirectUri: String,
        @Field("grant_type") grantType: String = "authorization_code"
    ): Call<GoogleTokenInfoResponse>
}

data class GoogleUserInfoResponse(
    val name: String,
    val email: String
)

data class GoogleTokenInfoResponse(
    val access_token: String,
    val id_token: String,
    //    val expires_in: Long,
)