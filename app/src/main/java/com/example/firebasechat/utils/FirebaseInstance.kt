package com.example.firebasechat.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

object FirebaseInstance {
    var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    var firebaseDb: DatabaseReference = Firebase.database.reference
    var firebaseStorage: StorageReference = Firebase.storage.reference
}