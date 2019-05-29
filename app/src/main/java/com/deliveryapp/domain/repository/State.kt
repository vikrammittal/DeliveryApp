package com.deliveryapp.domain.repository

class State {
    companion object {
        const val DONE = "done"
        const val LOADING = "loading"
        const val ERROR = "error"
        const val NETWORK_ERROR = "network_error"
        const val PAGE_LOADING = "page_loading"
        const val LOADED = "loaded"
    }
}