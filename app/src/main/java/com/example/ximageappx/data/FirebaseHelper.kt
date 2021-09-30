package com.example.ximageappx.data
//
//import com.example.ximageappx.MainActivity
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.DatabaseError
//import com.google.firebase.database.ValueEventListener
//import com.google.firebase.database.ktx.database
//import com.google.firebase.ktx.Firebase
//
//class FirebaseHelper {
//    private val uid = FirebaseAuth.getInstance().currentUser?.uid
//
//    val userRef = Firebase.database.getReference("users")
//    val imageRef = Firebase.database.getReference("likes/$uid")//${args.photo.id}/liked")
//
//
//    fun getPhotoCreator(photo: PhotoPost): User {
//        val user = User()
//        userRef.child(photo.user)
//            .addValueEventListener(object : ValueEventListener {
//                override fun onDataChange(dataSnapshot: DataSnapshot) {
//                    // This method is called once with the initial value and again
//                    // whenever data at this location is updated.
//                    if (dataSnapshot.exists())// && dataSnapshot.value == true
//                    {
//                        user.login = dataSnapshot.child("login").value.toString()
//                        user.profilePhotoUrl =
//                            dataSnapshot.child("profilePhotoUrl").value.toString()
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {}
//
//            })
//        return user
//    }
//
//
//}