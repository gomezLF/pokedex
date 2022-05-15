package edu.co.icesi.pokedex

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.co.icesi.pokedex.model.User
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
    }

    fun loginClicked(view: View) {
        val username = findViewById<TextView>(R.id.login_username_txt).text.toString()
        var currentUser = User(UUID.randomUUID().toString(), username)

        Firebase.firestore.collection("users").whereEqualTo("username",currentUser.username).get().addOnCompleteListener { task ->
            if (task.result.size() == 0){
                Firebase.firestore.collection("users").document(currentUser.username).set(currentUser)

            }else {
                for (document in task.result){
                    currentUser = document.toObject(User::class.java)
                    break
                }
            }
        }

        changeHomePage(currentUser)
    }

    private fun changeHomePage(currentUser: User){
        val intent = Intent(this@MainActivity, HomeActivity::class.java)
        intent.putExtra("currentUser",currentUser)
        startActivity(intent)
        finish()
    }
}