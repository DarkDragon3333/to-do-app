package com.coding.meet.todo_app

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.registerActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val email = findViewById<TextView>(R.id.email)
        val password = findViewById<TextView>(R.id.password)
        val repeatPassword = findViewById<TextView>(R.id.repeat_password)
        val buttonSingUp: Button = findViewById(R.id.registerButton)
        val saveSingIn: SharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE)

        buttonSingUp.setOnClickListener {
            if (checkEnterData(email, password, repeatPassword, this)){
                workWithBDInRegisterActivity(email, password, this, saveSingIn)
            }
        }
    }
}

fun checkEnterData(
    email: TextView,
    password: TextView,
    repeatPassword: TextView,
    registerActivity: RegisterActivity
): Boolean {
    if (email.text.isEmpty() or password.text.isEmpty() or repeatPassword.text.isEmpty()){
        makeToast(registerActivity, "Please, enter all data")
    } else if (!email.text.contains("@")){
        makeToast(registerActivity, "Please, check email")
    }
    else if (password.text.toString() != repeatPassword.text.toString()){
        makeToast(registerActivity, "Please, check repeat password")
    }
    else {
        return true
    }
    return false
}

fun workWithBDInRegisterActivity(
    email: TextView,
    password: TextView,
    registerActivity: RegisterActivity,
    saveSingIn: SharedPreferences
) {
    val db = Firebase.firestore

    val user = hashMapOf(
        "email" to email.text.toString(),
        "password" to password.text.toString()
    )

    db.collection("users")
        .add(user)
        .addOnSuccessListener {
            saveSingIn.edit().putString("email", email.text.toString()).apply()
            saveSingIn.edit().putString("password", password.text.toString()).apply()

            val intent = Intent(registerActivity, LoginActivity::class.java)
            startActivity(registerActivity, intent, null)
        }
        .addOnFailureListener {
            e ->Log.w(TAG, "Error adding account", e)
            makeToast(registerActivity, e.toString())
        }
}

fun makeToast(context: Context, msg: String){
    Toast.makeText(
        context,
        msg,
        Toast.LENGTH_SHORT
    ).show()
}