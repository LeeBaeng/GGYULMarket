package com.leebaeng.ggyulmarket.di

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.leebaeng.ggyulmarket.common.Constants
import com.leebaeng.ggyulmarket.model.MarketModel
import com.leebaeng.ggyulmarket.model.UserModel
import com.leebaeng.util.log.logEX
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DBAdapter @Inject constructor() {
    val auth = Firebase.auth
    val storage = Firebase.storage
    val firestore = Firebase.firestore

    val userDB = Firebase.firestore.collection(Constants.FS_TABLE_USER)
    val chatDB = Firebase.database.getReference(Constants.RT_TABLE_CHAT)
    val marketDB = Firebase.firestore.collection(Constants.FS_TABLE_MARKET)

    var myUserModel: UserModel? = null

    val marketPhotoRef = storage.reference.child("market/photo")
    val userPhotoRef = storage.reference.child("user/photo")

    suspend fun getMyUserModel(): UserModel? {
        if (myUserModel == null && auth.currentUser != null)
            myUserModel = getUserModel(auth.currentUser!!.uid)
        return myUserModel
    }

    suspend fun getUserModel(uid: String): UserModel? {
        return userDB.document(uid).get().await().toObject(UserModel::class.java)
    }

    suspend fun saveDataInFireStore(childName: String, hashMap: HashMap<String, Any>): Boolean {
        return try {
            val data = firestore
                .collection("users")
                .document(childName)
                .set(hashMap)
                .await()
            return true
        } catch (e: Exception) {
            return false
        }
    }

    // https://stackoverflow.com/questions/64088407/how-to-use-async-await-coroutines-in-oncompletelistener-firebase
    // https://betterprogramming.pub/how-to-use-kotlin-coroutines-with-firebase-6f8577a3e00f
    suspend fun loginWithEmailPassword(email: String, password: String): AuthResult? {
        return try {
            val data = auth
                .signInWithEmailAndPassword(email, password)
                .await()
            data
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getMarketModel(id: String): MarketModel? {
        return marketDB.document(id).get().await().toObject(MarketModel::class.java)
    }

    suspend fun getMarketModels(): List<MarketModel> {
        return marketDB
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(15)
            .get()
            .addOnFailureListener {
                it.logEX("Error getting documents.")
            }
            .await().toObjects(MarketModel::class.java)
    }

}