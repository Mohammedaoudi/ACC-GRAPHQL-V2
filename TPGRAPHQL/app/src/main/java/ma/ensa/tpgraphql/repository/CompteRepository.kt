package ma.ensa.tpgraphql.repository

import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import ma.ensa.tpgraphql.GetAllComptesQuery
import ma.ensa.tpgraphql.SaveCompteMutation
import ma.ensa.tpgraphql.type.CompteInput
import ma.ensa.tpgraphql.type.TypeCompte

class CompteRepository {
    private val TAG = "CompteRepository"

    private val apolloClient = ApolloClient.Builder()
        .serverUrl("http://192.168.1.8:8080/graphql")
        .build()

    suspend fun getAllComptes(callback: (Result<List<GetAllComptesQuery.AllCompte>>) -> Unit) {
        try {
            Log.d(TAG, "Fetching all comptes...")
            val response = apolloClient.query(GetAllComptesQuery()).execute()

            if (response.hasErrors()) {
                val error = response.errors?.first()?.message ?: "Unknown error"
                Log.e(TAG, "Error fetching comptes: $error")
                callback(Result.failure(Exception(error)))
            } else {
                val comptes = response.data?.allComptes?.filterNotNull() ?: emptyList()
                Log.d(TAG, "Fetched ${comptes.size} comptes")
                callback(Result.success(comptes))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception while fetching comptes", e)
            callback(Result.failure(e))
        }
    }

    suspend fun saveCompte(solde: Double, type: TypeCompte, callback: (Result<SaveCompteMutation.SaveCompte>) -> Unit) {
        try {
            Log.d(TAG, "Saving compte with solde: $solde and type: $type")
            val input = CompteInput(
                solde = solde,
                type = Optional.Present(type)
            )

            val response = apolloClient.mutation(SaveCompteMutation(input)).execute()

            if (response.hasErrors()) {
                val error = response.errors?.first()?.message ?: "Unknown error"
                Log.e(TAG, "Error saving compte: $error")
                callback(Result.failure(Exception(error)))
            } else {
                response.data?.saveCompte?.let { savedCompte ->
                    Log.d(TAG, "Compte saved successfully with id: ${savedCompte.id}")
                    callback(Result.success(savedCompte))
                } ?: callback(Result.failure(Exception("Save response was null")))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception while saving compte", e)
            callback(Result.failure(e))
        }
    }
}