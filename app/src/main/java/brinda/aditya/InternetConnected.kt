package brinda.aditya

/*
* Created by Brinda Shah & Aditya Vashisht on DECEMBER 9, 2019
*/

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlin.system.exitProcess


class InternetConnected(private val context: Context) {

    //region methods

    fun checkNetworkConnectivity(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return   postMarshmallowInternetCheck(connectivityManager)
    }

    // Marshmallow and up get from activeNetwork of connectivityManager
    private fun postMarshmallowInternetCheck(connectivityManager: ConnectivityManager): Boolean {
        val network = connectivityManager.activeNetwork
        val connection = connectivityManager.getNetworkCapabilities(network)

        return connection != null && (connection.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                connection.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }

    fun outputErrorMessage(){
        AlertDialog.Builder(context)
            .setTitle("Network Error")
            .setMessage("The App requires internet connection")
            //.setPositiveButton(R.string.retry)  {_,_ -> }
            .setNegativeButton("Quit") { _ , _ ->  exitProcess(-1) }
            .show()
    }

    //endregion
}