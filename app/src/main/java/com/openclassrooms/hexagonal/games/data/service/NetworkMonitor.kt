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

    //connectivityManager permet de surveiller l'état de la connexion réseau
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    //networkCallback permet de recevoir des notifications lorsque l'état réseau change
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

    /**
     * Enregistre un NetworkCallback via connectivityManager.registerNetworkCallback
     * permet de recevoir des notifications lorsque l'état réseau change (par exemple, connexion perdue ou disponible).
     */
    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        Log.d("NETWORK_MONITOR", "check connexion réseau démarrée")
        val networkRequest = NetworkRequest.Builder().build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    /**
     * désenregistre le networkcallback via connectivityManager.unregisternetworkcallback
     * libère les ressources et empeche les fuites de mémoire en arretant la surveillance réseau lorsque l'activité n'est plus active.
     */
    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        Log.d("NETWORK_MONITOR", "check connexion réseau arrêtée")
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}