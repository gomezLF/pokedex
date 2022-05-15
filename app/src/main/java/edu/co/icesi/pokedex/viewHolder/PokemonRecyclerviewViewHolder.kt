package edu.co.icesi.pokedex.viewHolder

import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import edu.co.icesi.pokedex.PokemonDetailsActivity
import edu.co.icesi.pokedex.R
import edu.co.icesi.pokedex.model.Pokemon
import edu.co.icesi.pokedex.model.User
import java.text.SimpleDateFormat
import java.util.*

class PokemonRecyclerviewViewHolder(view: View): RecyclerView.ViewHolder(view) {

    var pokemon: Pokemon? = null
    var user: User? = null

    private val viewItem = view
    private val pokemonImage: ImageView = viewItem.findViewById(R.id.image_pokemon)
    private val pokemonName: TextView = viewItem.findViewById(R.id.details_pokemon_name)
    private val catchDatePokemon: TextView = viewItem.findViewById(R.id.catch_Date)
    private val pokemonType: TextView = viewItem.findViewById(R.id.type_pokemon)
    private val pokemonCardView: CardView = viewItem.findViewById(R.id.pokemonCardView)

    fun render(pokemonModel: Pokemon){
        Glide.with(pokemonImage.context).load(pokemon?.img).into(pokemonImage)
        pokemonName.text = pokemonModel.name
        catchDatePokemon.text = SimpleDateFormat("MMM dd, yy 'at' HH:mm").format(Date(pokemonModel.dateCatch))
        pokemonType.text = pokemonModel.type

        watchPokemonDetails()
    }

    private fun watchPokemonDetails(){
        pokemonCardView.setOnClickListener {
            val intent = Intent(viewItem.context, PokemonDetailsActivity::class.java)
            intent.putExtra("currentUser", user!!)
            intent.putExtra("selectedPokemon", pokemon!!)
            intent.putExtra("isCaughtPokemon", true)

            viewItem.context.startActivity(intent)
        }
    }
}