package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = Firebase.firestore

        val editNumber = findViewById<EditText>(R.id.editNumber)
        val editName = findViewById<EditText>(R.id.editName)
        val editTypeOne = findViewById<EditText>(R.id.editTypeOne)
        val editTypeTwo = findViewById<EditText>(R.id.editTypeTwo)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        btnRegister.setOnClickListener {
            var pokemonEntry = hashMapOf(
                "nome" to editName.text.toString(),
                "type_one" to editTypeOne.text.toString(),
                "type_two" to editTypeTwo.text.toString()
            )

            db.collection("pokemon")
                .add(pokemonEntry)
                .addOnSuccessListener { documentReference ->
                    Log.d("Main", "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w("Main", "Error adding document", e)
                }
        }
    }
}