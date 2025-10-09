package mayurmadnani.microfables

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// Data classes for /api/generate
data class OllamaRequest(
    val model: String,
    val prompt: String,
    val stream: Boolean = false
)

data class OllamaResponse(
    val model: String,
    val created_at: String,
    val response: String,
    val done: Boolean
)

// Data classes for /api/tags
data class OllamaTagsResponse(
    val models: List<OllamaModel>
)

data class OllamaModel(
    val name: String,
    val modified_at: String,
    val size: Long
)


// Retrofit interface defining the API endpoints
interface OllamaApiService {
    @POST("/api/generate")
    suspend fun generateResponse(@Body request: OllamaRequest): OllamaResponse

    @GET("/api/tags")
    suspend fun getModels(): OllamaTagsResponse
}

// Singleton object to provide a Retrofit instance
object ApiClient {
    private const val BASE_URL = "http://192.168.1.11:11434/"

    val instance: OllamaApiService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(OllamaApiService::class.java)
    }
}
