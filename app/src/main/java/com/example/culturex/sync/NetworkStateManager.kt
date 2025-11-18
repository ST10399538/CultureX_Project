package com.example.culturex.sync

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NetworkStateManager(private val context: Context) {

    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
            as ConnectivityManager

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _connectionType = MutableStateFlow(ConnectionType.NONE)
    val connectionType: StateFlow<ConnectionType> = _connectionType.asStateFlow()

    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    companion object {
        private const val TAG = "NetworkStateManager"

        @Volatile
        private var INSTANCE: NetworkStateManager? = null

        fun getInstance(context: Context): NetworkStateManager {
            return INSTANCE ?: synchronized(this) {
                val instance = NetworkStateManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }

    init {
        updateConnectionState()
    }

    /**
     * Start monitoring network changes
     */
    fun startMonitoring(onConnectionRestored: () -> Unit) {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            .build()

        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                Log.d(TAG, "Network available")
                val wasDisconnected = !_isConnected.value
                updateConnectionState()

                if (wasDisconnected && _isConnected.value) {
                    Log.d(TAG, "Connection restored, triggering sync")
                    onConnectionRestored()
                }
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                Log.d(TAG, "Network lost")
                updateConnectionState()
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                updateConnectionState()
            }
        }

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback!!)
        Log.d(TAG, "Network monitoring started")
    }

    /**
     * Stop monitoring network changes
     */
    fun stopMonitoring() {
        networkCallback?.let {
            connectivityManager.unregisterNetworkCallback(it)
            networkCallback = null
            Log.d(TAG, "Network monitoring stopped")
        }
    }

    /**
     * Update connection state
     */
    private fun updateConnectionState() {
        val network = connectivityManager.activeNetwork
        val capabilities = network?.let {
            connectivityManager.getNetworkCapabilities(it)
        }

        val isConnected = capabilities?.let {
            it.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    it.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        } ?: false

        val connectionType = when {
            capabilities == null -> ConnectionType.NONE
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> ConnectionType.WIFI
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> ConnectionType.CELLULAR
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> ConnectionType.ETHERNET
            else -> ConnectionType.OTHER
        }

        _isConnected.value = isConnected
        _connectionType.value = connectionType

        Log.d(TAG, "Connection state updated: connected=$isConnected, type=$connectionType")
    }

    /**
     * Check if currently connected
     */
    fun isCurrentlyConnected(): Boolean {
        updateConnectionState()
        return _isConnected.value
    }

    /**
     * Check if connected via WiFi
     */
    fun isWifiConnected(): Boolean {
        return _isConnected.value && _connectionType.value == ConnectionType.WIFI
    }
}

enum class ConnectionType {
    NONE,
    WIFI,
    CELLULAR,
    ETHERNET,
    OTHER
}
