package ma.ensa.tpgraphql.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ma.ensa.tpgraphql.R
import ma.ensa.tpgraphql.GetAllComptesQuery

class CompteAdapter : RecyclerView.Adapter<CompteAdapter.CompteViewHolder>() {
    private var comptes = mutableListOf<GetAllComptesQuery.AllCompte>()

    inner class CompteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val idTextView: TextView = itemView.findViewById(R.id.idTextView)
        val soldeTextView: TextView = itemView.findViewById(R.id.soldeTextView)
        val typeTextView: TextView = itemView.findViewById(R.id.typeTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_compte, parent, false)
        return CompteViewHolder(view)
    }

    override fun onBindViewHolder(holder: CompteViewHolder, position: Int) {
        val compte = comptes[position]
        holder.apply {
            idTextView.text = "id: ${compte.id}"
            soldeTextView.text = "Solde: ${compte.solde} DH"
            typeTextView.text = "Type: ${compte.type}"
            dateTextView.text = "Date: ${compte.dateCreation ?: "N/A"}"
        }
    }

    override fun getItemCount() = comptes.size

    fun updateData(newComptes: List<GetAllComptesQuery.AllCompte>) {
        comptes.clear()
        comptes.addAll(newComptes)
        notifyDataSetChanged()
    }
}