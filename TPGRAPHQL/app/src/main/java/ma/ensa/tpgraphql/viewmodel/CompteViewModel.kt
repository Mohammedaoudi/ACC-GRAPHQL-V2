package ma.ensa.tpgraphql.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ma.ensa.tpgraphql.GetAllComptesQuery
import ma.ensa.tpgraphql.repository.CompteRepository
import ma.ensa.tpgraphql.type.TypeCompte

class CompteViewModel : ViewModel() {
    private val TAG = "CompteViewModel"
    private val repository = CompteRepository()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private val _totalComptes = MutableLiveData<Int>()
    val totalComptes: LiveData<Int> = _totalComptes

    private val _totalSolde = MutableLiveData<Double>()
    val totalSolde: LiveData<Double> = _totalSolde

    private val _comptes = MutableLiveData<List<GetAllComptesQuery.AllCompte>>()
    val comptes: LiveData<List<GetAllComptesQuery.AllCompte>> = _comptes

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchComptes() {
        coroutineScope.launch(Dispatchers.Main) {
            repository.getAllComptes { result ->
                result.fold(
                    onSuccess = { comptesList ->
                        _comptes.postValue(comptesList)
                        _totalComptes.postValue(comptesList.size)
                        _totalSolde.postValue(comptesList.sumOf { it.solde })
                        Log.d(TAG, "Successfully fetched ${comptesList.size} comptes")
                    },
                    onFailure = { exception ->
                        _error.postValue(exception.message)
                        Log.e(TAG, "Error fetching comptes", exception)
                    }
                )
            }
        }
    }

    fun addCompte(solde: Double, type: TypeCompte) {
        coroutineScope.launch(Dispatchers.Main) {
            repository.saveCompte(solde, type) { result ->
                result.fold(
                    onSuccess = { savedCompte ->
                        Log.d(TAG, "Successfully added compte with id: ${savedCompte.id}")
                        // Refresh the list after successful addition
                        fetchComptes()
                    },
                    onFailure = { exception ->
                        _error.postValue(exception.message)
                        Log.e(TAG, "Error adding compte", exception)
                    }
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Cancel any ongoing coroutines when ViewModel is cleared
        coroutineScope.launch {
            // Clean up any resources if needed
        }
    }
}