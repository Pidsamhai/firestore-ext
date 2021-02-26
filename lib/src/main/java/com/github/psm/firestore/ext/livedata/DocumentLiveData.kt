package com.github.psm.firestore.ext.livedata

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration

class DocumentLiveData<T>(private val docRef: DocumentReference, private val clazz: Class<T>) : LiveData<T>() {
    private var listenerRegistration: ListenerRegistration? = null

    private val eventListener = EventListener<DocumentSnapshot> { snapshot, error ->
        if (error != null) return@EventListener
        if (snapshot != null && snapshot.exists()) {
            value = snapshot.toObject(clazz)
        }
    }

    override fun onActive() {
        super.onActive()
        listenerRegistration = docRef.addSnapshotListener(eventListener)
    }

    override fun onInactive() {
        super.onInactive()
        if (!hasActiveObservers()) {
            listenerRegistration?.remove()
            listenerRegistration = null
        }
    }

    companion object {
      inline fun <reified T> create(docRef: DocumentReference) = DocumentLiveData(docRef, T::class.java)
    }
}