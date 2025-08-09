package ru.practicum.android.diploma.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.IOException
import retrofit2.HttpException
import ru.practicum.android.diploma.data.dto.Response

class RetrofitNetworkClient(
    private val context: Context
) : NetworkClient {

    companion object {
        private const val TAG = "NetworkClient"
        const val NO_INTERNET_CONNECTION = -1
        const val SUCCESS = 200
        const val SERVER_ERROR = 500
    }

    override suspend fun <T> doRequest(requestCall: suspend () -> retrofit2.Response<T>): Response<T> {
        if (!isConnected()) {
            return Response(NO_INTERNET_CONNECTION)
        }

        return try {
            val response = withContext(Dispatchers.IO) { requestCall() }
            if (response.isSuccessful && response.body() != null) {
                Response(SUCCESS, response.body())
            } else {
                Response(response.code())
            }
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error: ${e.message}", e)
            return Response(e.code())
        } catch (e: IOException) {
            Log.e(TAG, "Network IO error: ${e.message}", e)
            Response(SERVER_ERROR)
        }
    }

    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }
}
