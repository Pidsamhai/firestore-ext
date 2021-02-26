package com.github.psm.firestore.ext.livedata

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot

class QueryFirstLiveData<T>(private val query: Query, private val clazz: Class<T>) : LiveData<T>() {
    private var listenerRegistration: ListenerRegistration? = null

    private val eventListener = EventListener<QuerySnapshot> { snapshot, error ->
        if (error != null) throw error
        if (snapshot != null && !snapshot.isEmpty) {
            value = snapshot.documents[0].toObject(clazz)
        }
    }

    init {
        query.limit(1)
    }

    override fun onActive() {
        super.onActive()
        listenerRegistration = query.addSnapshotListener(eventListener)
    }

    override fun onInactive() {
        super.onInactive()
        if (!hasActiveObservers()) {
            listenerRegistration?.remove()
            listenerRegistration = null
        }
    }

    companion object {
        inline fun <reified T> create(query: Query) = QueryFirstLiveData(query, T::class.java)
    }
}