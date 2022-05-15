package edu.co.icesi.pokedex

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.co.icesi.pokedex.model.Pokemon
import edu.co.icesi.pokedex.model.User

class PokemonDetailsActivity : AppCompatActivity() {

    var currentPokemon: Pokemon? = null
    var currentUser: User? = null
    private var isCaughtPokemon: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pokemon_details_activity)

        currentPokemon = intent.extras?.get("selectedPokemon") as Pokemon
        currentUser = intent.extras?.get("currentUser") as User
        isCaughtPokemon = intent.extras?.get("isCaughtPokemon") as Boolean

        renderPokemonInformation()
        selectButtonAction()
    }

    fun backToHomeClicked(view: View){
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("currentUser", currentUser)
        startActivity(intent)
        finish()
    }

    private fun renderPokemonInformation(){
        val pokemonImage: ImageView = findViewById(R.id.details_pokemon_image)

        Glide.with(pokemonImage.context).load(currentPokemon?.img).into(pokemonImage)
        findViewById<TextView>(R.id.details_pokemon_name).text = currentPokemon?.name
        findViewById<TextView>(R.id.details_pokemon_type).text = currentPokemon?.type
        findViewById<TextView>(R.id.detailsLifeIndicator_label).text = currentPokemon?.life
        findViewById<TextView>(R.id.detailsAttackIndicator_label).text = currentPokemon?.attack
        findViewById<TextView>(R.id.detailsDefenseIndicator_label).text = currentPokemon?.defense
        findViewById<TextView>(R.id.detailsSpeedIndicator_label).text = currentPokemon?.speed

    }

    private fun selectButtonAction(){
        val actionButton = findViewById<Button>(R.id.detailsAction_Button)

        if(isCaughtPokemon){
            actionButton.text = this.getString(R.string.details_drop_button)
            actionButton.setOnClickListener{ dropPokemon() }
        }else {
            actionButton.text = this.getString(R.string.details_catch_button)
            actionButton.setOnClickListener{ catchPokemon() }
        }
    }

    private fun catchPokemon(){
        currentPokemon?.let {
            currentUser?.let { it1 ->
                Firebase.firestore.collection("users").document(it1.username).collection("pokemon").document(
                    currentPokemon!!.uid).set(it)
            }
        }

        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("currentUser", currentUser)
        startActivity(intent)
        finish()
    }

    private fun dropPokemon(){
        currentPokemon?.let {
            currentUser?.let { it1 ->
                Firebase.firestore.collection("users").document(it1.username).collection("pokemon").document(
                    currentPokemon!!.uid).delete()
            }
        }

        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("currentUser", currentUser)
        startActivity(intent)
        finish()
    }
}