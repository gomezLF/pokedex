package edu.co.icesi.pokedex

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.co.icesi.pokedex.adapter.PokemonRecyclerviewAdapter
import edu.co.icesi.pokedex.model.Pokemon
import edu.co.icesi.pokedex.model.User
import org.json.JSONObject
import java.io.FileNotFoundException
import java.net.URL
import java.util.*
import javax.net.ssl.HttpsURLConnection
import androidx.lifecycle.lifecycleScope
import edu.co.icesi.pokedex.util.Constant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : AppCompatActivity() {

    private lateinit var currentUser: User
    private lateinit var currentPokemon: Pokemon
    private var pokemonRecyclerviewAdapter = PokemonRecyclerviewAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)

        currentUser = intent.extras?.get("currentUser") as User

        initRecyclerView()
        getCaughtPokemon()
    }



    fun watchDetailsClicked(view: View){
        val pokemonIdentifier = findViewById<EditText>(R.id.searchCatchPokemon_txt)

        if(pokemonIdentifier.text.isBlank()) {
            pokemonSearchError("El pokémon no existe")
        }else {
            GETRequest(true, pokemonIdentifier.text.toString())
        }
    }


    fun catchPokemonClicked(view: View){
        val pokemonIdentifier = findViewById<EditText>(R.id.searchCatchPokemon_txt)

        if(pokemonIdentifier.text.isBlank()) {
            pokemonSearchError("El pokémon no existe")
        }else {
            GETRequest(false, pokemonIdentifier.text.toString())
        }
    }


    fun showAllPokemonClicked(view: View){
        pokemonRecyclerviewAdapter.deleteAllPokemon()
        getCaughtPokemon()
    }


    fun searchPokemonClicked(view: View){
        val pokemonToSearch = findViewById<EditText>(R.id.searchPokemon_txt).text.toString()

        lifecycleScope.launch(Dispatchers.IO){

            val query = Firebase.firestore.collection("users").document(currentUser.username).collection("pokemon").whereEqualTo("name",pokemonToSearch)
            query.get().addOnCompleteListener { task ->

                if (task.result?.size()!=0){

                    lateinit var pokemonSearch : Pokemon
                    pokemonRecyclerviewAdapter.deleteAllPokemon()

                    for (document in task.result!!){
                        pokemonSearch = document.toObject(Pokemon::class.java)
                        pokemonRecyclerviewAdapter.addNewPokemon(pokemonSearch)
                        pokemonRecyclerviewAdapter.notifyDataSetChanged()
                        break
                    }
                }else{
                    pokemonSearchError("Pokémon no atrapado")
                    pokemonRecyclerviewAdapter.deleteAllPokemon()
                    getCaughtPokemon()
                }
            }
        }
    }



    private fun initRecyclerView(){
        val recyclerView = findViewById<RecyclerView>(R.id.pokemonRecyclerview)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = pokemonRecyclerviewAdapter

        pokemonRecyclerviewAdapter.currentUser = currentUser
    }


    private fun getCaughtPokemon(){
        Firebase.firestore.collection("users").document(currentUser.username).collection("pokemon").orderBy("dateCatch", Query.Direction.DESCENDING).get().addOnCompleteListener{ task ->
            for (document in task.result!!){

                val pokemon = document.toObject(Pokemon::class.java)
                pokemonRecyclerviewAdapter.addNewPokemon(pokemon)
                pokemonRecyclerviewAdapter.notifyDataSetChanged()
            }
        }
    }


    private fun GETRequest(showDetails: Boolean, pokemonIdentifier: String) {
        lifecycleScope.launch(Dispatchers.IO) {

            val consult = URL("${Constant.POKEMON_API}/pokemon/${pokemonIdentifier}")
            val client = consult.openConnection() as HttpsURLConnection
            client.requestMethod = "GET"

            try {
                getPokemonFromAPI(showDetails, client)

            } catch (e: FileNotFoundException){
                withContext(Dispatchers.Main){
                    pokemonSearchError("El pokémon no existe")
                }
            }
        }
    }


    private fun getPokemonFromAPI(showDetails: Boolean, client: HttpsURLConnection){

        val json = client.inputStream.bufferedReader().readText()
        val jsonObject = JSONObject(json)
        val name = jsonObject.optJSONObject("species")?.optString("name")
        val type = jsonObject.optJSONArray("types")?.getJSONObject(0)?.optJSONObject("type")?.optString("name")
        val img = jsonObject.optJSONObject("sprites")?.optString("front_default")

        val stat = jsonObject.optJSONArray("stats")
        val life = stat?.getJSONObject(0)?.optInt("base_stat")
        val attack = stat?.getJSONObject(1)?.optInt("base_stat")
        val defense = stat?.getJSONObject(2)?.optInt("base_stat")
        val speed = stat?.getJSONObject(5)?.optInt("base_stat")
        Log.e(">>>>","${name}-${type}-${life}-${speed}-${attack}-${defense}")

        if (name != null && img != null && type != null) {
            passingPokemon(showDetails, name, img, type, life, speed, attack, defense)
        }
    }


    private fun passingPokemon(showDetails: Boolean, name: String, img: String, type: String, life: Int?, speed: Int?, attack: Int?, defense: Int?){

        currentPokemon = Pokemon(
            UUID.randomUUID().toString(),
            currentUser.username,
            name!!,
            img!!,
            type!!,
            Date().time,
            "${life!!}",
            "${attack!!}",
            "${defense!!}",
            "${speed!!}")

        if(showDetails) {
            watchPokemonDetails()
        }else {
            catchPokemon()
        }
    }


    private fun watchPokemonDetails(){
        val intent = Intent(this, PokemonDetailsActivity::class.java)
        intent.putExtra("selectedPokemon", currentPokemon)
        intent.putExtra("currentUser", currentUser)
        intent.putExtra("isCaughtPokemon", false)

        startActivity(intent)
        finish()
    }


    private fun catchPokemon(){
        lifecycleScope.launch(Dispatchers.IO){

            Firebase.firestore.collection("users").document(currentUser.username).collection("pokemon").document(currentPokemon.uid).set(currentPokemon)

            Firebase.firestore.collection("users").document(currentUser.username).collection("pokemon").orderBy("dateCatch", Query.Direction.DESCENDING).get().addOnCompleteListener{ task ->
                pokemonRecyclerviewAdapter.deleteAllPokemon()

                for (document in task.result!!){
                    val pk = document.toObject(Pokemon::class.java)
                    pokemonRecyclerviewAdapter.addNewPokemon(pk)
                    pokemonRecyclerviewAdapter.notifyDataSetChanged()
                }
            }
        }
    }


    private fun pokemonSearchError(message: String){
        Toast.makeText(this@HomeActivity,message, Toast.LENGTH_LONG).show()
    }
}