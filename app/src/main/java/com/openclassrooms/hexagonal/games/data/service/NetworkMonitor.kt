package com.openclassrooms.hexagonal.games.data.service

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class NetworkMonitor(
    private val context: Context,
    private val onNetworkChange: (Boolean) -> Unit
) : DefaultLifecycleObserver {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        //connexion restaurée
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            Log.d("NETWORK_MONITOR", "Connexion disponible")
            onNetworkChange(true)
        }

        //connexion perdue
        override fun onLost(network: Network) {
            super.onLost(network)
            Log.d("NETWORK_MONITOR", "Connexion perdue")
            onNetworkChange(false)
        }
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        Log.d("NETWORK_MONITOR", "check connexion réseau démarrée")
        val networkRequest = NetworkRequest.Builder().build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        Log.d("NETWORK_MONITOR", "check connexion réseau arrêtée")
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}