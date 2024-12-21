import com.example.mrsu.Api.TurnstileApi
import com.example.mrsu.dataclasses.TurnstileHistory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TurnstileRepository {
    private val api: TurnstileApi

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://papi.mrsu.ru/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(TurnstileApi::class.java)
    }

    suspend fun fetchTurnstileHistory(token: String, date: String): List<TurnstileHistory> {
        return api.getTurnstileHistory("Bearer $token", date)
    }
}