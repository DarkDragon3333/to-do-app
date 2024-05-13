package com.coding.meet.todo_app

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.loginActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val email = findViewById<TextView>(R.id.email)
        val password = findViewById<TextView>(R.id.password)
        val buttonLogIn: Button = findViewById(R.id.loginButton)
        val saveSingIn: SharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE)

        if ((saveSingIn.getString("email", "no data") != "no data") and
            (saveSingIn.getString("password", "no data") != "no data"))
        {
            email.text = saveSingIn.getString("email", "No data")
            password.text = saveSingIn.getString("password", "No data")

        }

        buttonLogIn.setOnClickListener {
            if (checkEnterData(email, password, this)) {
                workWithBDInLoginActivity(email, password, this, saveSingIn)
            }
        }

        val buttonSingUp: Button = findViewById(R.id.goToRegisterActivity)
        buttonSingUp.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

    }

    private fun checkEnterData(
        email: TextView,
        password: TextView,
        loginActivity: LoginActivity
    ): Boolean {
        if (email.text.isEmpty() or password.text.isEmpty()) {
            makeToast(loginActivity, "Please, enter all data")
        } else if (!email.text.contains("@")) {
            makeToast(loginActivity, "Please, check email")
        } else {
            return true
        }
        return false
    }

    private fun workWithBDInLoginActivity(
        email: TextView,
        password: TextView,
        loginActivity: LoginActivity,
        saveSingIn: SharedPreferences
    ) {
        val db = Firebase.firestore

        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                checkEnterDataWithBD(email, password, loginActivity, result, saveSingIn)
            }
            .addOnFailureListener {
                makeToast(loginActivity, "Error")
            }
    }

    private fun checkEnterDataWithBD(
        email: TextView,
        password: TextView,
        loginActivity: LoginActivity,
        result: QuerySnapshot,
        saveSingIn: SharedPreferences
    ) {
        var flag = 0
        for (document in result) {
            if ((email.text.toString() == document.getString("email")) and
                (password.text.toString() == document.getString("password"))
            ) {
                flag = -1
            } else if ((email.text.toString() != document.getString("email")) or
                (password.text.toString() != document.getString("password"))
            ) {
                flag += 1
            }

            if (flag == result.size() - 1) {
                makeToast(loginActivity, "Error of sing in. Check Enter data")
            } else if (flag == -1) {
                saveSingIn.edit().putString("email", email.text.toString()).apply()
                saveSingIn.edit().putString("password", password.text.toString()).apply()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun makeToast(context: Context, msg: String) {
        Toast.makeText(
            context,
            msg,
            Toast.LENGTH_SHORT
        ).show()
    }
}

