package com.github.psm.firestore.ext.livedata

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot

class QueryLiveData<T>(private val query: Query, private val clazz: Class<T>, private val first: Boolean = false) : LiveData<List<T>>() {
    private var listenerRegistration: ListenerRegistration? = null

    private val eventListener = EventListener<QuerySnapshot> { snapshot, error ->
        if (error != null) return@EventListener
        if (snapshot != null && !snapshot.isEmpty) {
            val items = mutableListOf<T>()
            for (doc in snapshot.documents) {
                items.add(doc.toObject(clazz)!!)
            }
            value = items
        }
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
        inline fun <reified T> create(query: Query) = QueryLiveData(query, T::class.java)
    }
}