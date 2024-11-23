package ma.ensa.tpgraphql

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import ma.ensa.tpgraphql.adapter.CompteAdapter
import ma.ensa.tpgraphql.type.TypeCompte
import ma.ensa.tpgraphql.viewmodel.CompteViewModel

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    private lateinit var viewModel: CompteViewModel
    private lateinit var adapter: CompteAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAdd: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "MainActivity created")

        setupViews()
        setupViewModel()
        setupObservers()

        viewModel.fetchComptes()
    }

    private fun setupViews() {
        recyclerView = findViewById(R.id.recyclerView)
        fabAdd = findViewById(R.id.fabAdd)

        adapter = CompteAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        fabAdd.setOnClickListener {
            showAddCompteDialog()
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[CompteViewModel::class.java]
    }

    private fun setupObservers() {
        viewModel.comptes.observe(this) { comptes ->
            Log.d(TAG, "Received ${comptes.size} comptes")
            adapter.updateData(comptes)
        }

        viewModel.error.observe(this) { error ->
            Log.e(TAG, "Error received: $error")
            Toast.makeText(this, "Erreur: $error", Toast.LENGTH_LONG).show()
        }

        viewModel.totalComptes.observe(this) { total ->
            findViewById<TextView>(R.id.totalComptesTextView).text = "Total Comptes: $total"
        }

        viewModel.totalSolde.observe(this) { totalSolde ->
            findViewById<TextView>(R.id.totalSoldeTextView).text = "Total Solde: ${totalSolde} DH"
        }

    }

    private fun showAddCompteDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_compte, null)
        val soldeInput = dialogView.findViewById<TextInputEditText>(R.id.soldeInput)
        val typeSpinner = dialogView.findViewById<Spinner>(R.id.typeSpinner)

        // Use knownValues() instead of values()
        val validTypes = TypeCompte.knownValues()
            .map { it.rawValue }

        val typeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, validTypes)
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typeSpinner.adapter = typeAdapter

        MaterialAlertDialogBuilder(this)
            .setTitle("Ajouter un compte")
            .setView(dialogView)
            .setPositiveButton("Ajouter") { dialog, _ ->
                val soldeText = soldeInput.text.toString()
                val selectedType = typeSpinner.selectedItem.toString()

                if (soldeText.isNotEmpty() && selectedType.isNotEmpty()) {
                    try {
                        val solde = soldeText.toDouble()
                        val type = TypeCompte.safeValueOf(selectedType)
                        viewModel.addCompte(solde, type)
                    } catch (e: NumberFormatException) {
                        Toast.makeText(this, "Solde invalide", Toast.LENGTH_SHORT).show()
                    } catch (e: IllegalArgumentException) {
                        Toast.makeText(this, "Type invalide", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

}