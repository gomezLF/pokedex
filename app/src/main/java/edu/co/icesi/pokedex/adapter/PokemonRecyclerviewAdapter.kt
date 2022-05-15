package edu.co.icesi.pokedex.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import edu.co.icesi.pokedex.PokemonDetailsActivity
import edu.co.icesi.pokedex.R
import edu.co.icesi.pokedex.model.Pokemon
import edu.co.icesi.pokedex.model.User
import edu.co.icesi.pokedex.viewHolder.PokemonRecyclerviewViewHolder

class PokemonRecyclerviewAdapter: RecyclerView.Adapter<PokemonRecyclerviewViewHolder>() {

    private var caughtPokemon = ArrayList<Pokemon>()
    lateinit var currentUser: User

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonRecyclerviewViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PokemonRecyclerviewViewHolder(layoutInflater.inflate(R.layout.pokemon_recyclerview_cardview, parent, false))
    }

    override fun onBindViewHolder(holder: PokemonRecyclerviewViewHolder, position: Int) {
        val item = caughtPokemon[position]
        holder.pokemon = item
        holder.user = currentUser
        holder.render(item)
    }

    override fun getItemCount(): Int {
        return caughtPokemon.size
    }

    fun deleteAllPokemon(){
        caughtPokemon.clear()
    }

    fun addNewPokemon(caughtPokemon: Pokemon){
        this.caughtPokemon.add(caughtPokemon)
    }

    fun removePokemon(pokemonToRemove: Pokemon){
        caughtPokemon.remove(pokemonToRemove)
        notifyDataSetChanged()
    }
}