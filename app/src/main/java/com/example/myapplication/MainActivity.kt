package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.PopupWindow
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

enum class PokemonType {
    NULL,
    GRASS,
    FIRE,
    WATER,
    NORMAL,
    POISON,
    DRAGON,
    ICE
}

class MainActivity : AppCompatActivity() {

    private val TAG : String = "MAIN"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crud_read)

        val db = Firebase.firestore
        val pokedex = db.collection("pokedex")
        val display = findViewById<TextView>(R.id.data_display)
        pokedex.get().addOnSuccessListener { result ->
            Log.d(TAG, "Search result: $result")
            for(doc in result){
                val data = doc.data
                display.append("${doc.id} | ${data["name"]} | ${data["type_one"]} | ${data["type_two"]}\n")
            }
        }.addOnFailureListener { exception ->
            Log.d(TAG, "Error getting docs: $exception")
        }

        val popup = PopupWindow(this)
        val createLayout = getLayout(R.layout.activity_crud_create, null)
        val createPopupAct = createLayout.findViewById<Button>(R.id.create_popup_act)
        createPopupAct.setOnClickListener {
            val id : String = createLayout.findViewById<EditText>(R.id.create_popup_id).text.toString()
            val name : String = createLayout.findViewById<EditText>(R.id.create_popup_name).text.toString()
            val typeOne : String = createLayout.findViewById<EditText>(R.id.create_popup_type_one).text.toString()
            val typeTwo : String = createLayout.findViewById<EditText>(R.id.create_popup_type_two).text.toString()

            if(id.isNotEmpty() && name.isNotEmpty() && typeOne.isNotEmpty() && typeTwo.isNotEmpty()){
                pokedex.document(id).get().addOnSuccessListener { result ->
                    if(result.data == null){
                        val flagId = !id.contains(Regex("/D"))
                        var flagType = false
                        try {
                            flagType = PokemonType.valueOf(typeOne.toUpperCase()) != null && PokemonType.valueOf(typeTwo.toUpperCase()) != null
                        } catch (e:Exception) {
                            Log.d(TAG, e.toString())
                        }

                        if(flagId && flagType){
                            val entry = hashMapOf(
                                "name" to name.capitalize(),
                                "type_one" to typeOne.toUpperCase(),
                                "type_two" to typeTwo.toUpperCase()
                            )
                            pokedex.document(id).set(entry).addOnSuccessListener {
                                Log.d(TAG, "Added entry: $id")
                                reloadDisplay(display, db)
                                popup.dismiss()
                            }.addOnFailureListener { exception ->
                                Log.d(TAG, "Exception: $exception")
                            }
                        }
                    }
                }
            }
        }

        val updateLayout = getLayout(R.layout.activity_crud_update, null)
        val updatePopupAct = updateLayout.findViewById<Button>(R.id.update_popup_act)
        updatePopupAct.setOnClickListener {
            val id : String = updateLayout.findViewById<EditText>(R.id.update_popup_id).text.toString()
            val name : String = updateLayout.findViewById<EditText>(R.id.update_popup_name).text.toString()
            val typeOne : String = updateLayout.findViewById<EditText>(R.id.update_popup_type_one).text.toString()
            val typeTwo : String = updateLayout.findViewById<EditText>(R.id.update_popup_type_two).text.toString()

            if(id.isNotEmpty() && !id.contains(Regex("/D")) && name.isNotEmpty() && typeOne.isNotEmpty() && typeTwo.isNotEmpty()){
                pokedex.document(id).get().addOnSuccessListener { result ->
                    if(result.data != null){
                        var flagType = false
                        try {
                            flagType = PokemonType.valueOf(typeOne.toUpperCase()) != null && PokemonType.valueOf(typeTwo.toUpperCase()) != null
                        } catch (e:Exception) {
                            Log.d(TAG, e.toString())
                        }

                        if(flagType){
                            val entry = mapOf(
                                "name" to name.capitalize(),
                                "type_one" to typeOne.toUpperCase(),
                                "type_two" to typeTwo.toUpperCase()
                            )
                            pokedex.document(id).update(entry).addOnSuccessListener {
                                Log.d(TAG, "Added entry: $id")
                                reloadDisplay(display, db)
                                popup.dismiss()
                            }.addOnFailureListener { exception ->
                                Log.d(TAG, "Exception: $exception")
                            }
                        }
                    }
                }
            }
        }

        val deleteLayout = getLayout(R.layout.activity_crud_delete, null)
        val deletePopupAct = deleteLayout.findViewById<Button>(R.id.delete_popup_act)
        deleteLayout.findViewById<NumberPicker>(R.id.delete_popup_id).maxValue = 9999
        deletePopupAct.setOnClickListener {
            val id : String = deleteLayout.findViewById<NumberPicker>(R.id.delete_popup_id).value.toString()

            pokedex.document(id).delete().addOnSuccessListener {
                Log.d(TAG, "Entry $id Deleted!")
                reloadDisplay(display, db)
                popup.dismiss()
            }.addOnFailureListener { e ->
                Log.d(TAG, e.toString())
            }
        }

        val createBtn = findViewById<Button>(R.id.create_button)
        addOnClickPopupShow(createBtn, popup, createLayout, display)
        val updateBtn = findViewById<Button>(R.id.update_button)
        addOnClickPopupShow(updateBtn, popup, updateLayout, display)
        val deleteBtn = findViewById<Button>(R.id.delete_button)
        addOnClickPopupShow(deleteBtn, popup, deleteLayout, display)
    }

    private fun getLayout(layout:Int, root: ViewGroup?) : View {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return inflater.inflate(layout, root)
    }

    fun addOnClickPopupShow(btn:Button, popup:PopupWindow, layout:View, root: View) {
        btn.setOnClickListener {
            popup.isFocusable = true
            popup.contentView = layout
            popup.showAtLocation(root, Gravity.CENTER, 0, 0)
        }
    }

    fun reloadDisplay(display:TextView, db:FirebaseFirestore) {
        display.text = ""
        val pokedex = db.collection("pokedex")
        pokedex.get().addOnSuccessListener { result ->
            Log.d(TAG, "Search result: $result")
            for(doc in result){
                val data = doc.data
                display.append("${doc.id} | ${data["name"]} | ${data["type_one"]} | ${data["type_two"]}\n")
            }
        }.addOnFailureListener { exception ->
            Log.d(TAG, "Error getting docs: $exception")
        }
    }

}