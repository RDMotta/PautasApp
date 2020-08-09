package com.rdm.uds.pautas.app

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage

class FirebaseUtils private constructor() {
    init {

    }
    private object GetReference {
        val instance = FirebaseUtils()
    }

    private object GetFirebaseAuth {
        val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    }

    private object GetFirebaseFirestore {
        val mFirebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    }

    private object GetFirebaseStorage{
        val mFirebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
    }

    companion object Reference{
        val instance: FirebaseUtils by lazy { GetReference.instance }
        val firebaseAuth :FirebaseAuth by lazy { GetFirebaseAuth.mFirebaseAuth }
        val firebaseFirestore: FirebaseFirestore  by lazy { GetFirebaseFirestore.mFirebaseFirestore}
        val firebaseStorage: FirebaseStorage by lazy { GetFirebaseStorage.mFirebaseStorage }
    }

    val firebaseAuth :FirebaseAuth = Reference.firebaseAuth
    val firebaseFirestore: FirebaseFirestore = Reference.firebaseFirestore
    val firebaseStorage: FirebaseStorage = Reference.firebaseStorage
}