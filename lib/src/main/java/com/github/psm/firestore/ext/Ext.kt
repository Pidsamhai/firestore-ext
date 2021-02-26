package com.github.psm.firestore.ext

import androidx.lifecycle.LiveData
import com.github.psm.firestore.ext.livedata.DocumentLiveData
import com.github.psm.firestore.ext.livedata.QueryFirstLiveData
import com.github.psm.firestore.ext.livedata.QueryLiveData
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

suspend fun Query.getAwait(): QuerySnapshot {
    return this.get().await()
}

suspend inline fun <reified T> Query.getAwaitToObject(): MutableList<T> {
    return this.get().await().toObjects(T::class.java)
}

suspend inline fun <reified T> Query.getAwaitFirstObject(): T? {
    val data = this.limit(1).get().await().toObjects(T::class.java)
    return if (data.size > 0) data[0] else null
}

suspend fun DocumentReference.getAwaitExist(): Boolean {
    return this.get().await().exists()
}

suspend inline fun <reified T> DocumentReference.getAwaitToObject(): T? {
    return this.get().await().toObject(T::class.java)
}

inline fun <reified T> Query.asLiveData(): LiveData<List<T>> {
    return QueryLiveData.create(this)
}

inline fun <reified T> Query.asFirstLiveData(): LiveData<T> {
    this.limit(1)
    return QueryFirstLiveData.create(this)
}

inline fun <reified T> DocumentReference.asLiveData(): LiveData<T> {
    return DocumentLiveData.create(this)
}