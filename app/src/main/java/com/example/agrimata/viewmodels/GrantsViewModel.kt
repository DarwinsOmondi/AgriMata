package com.example.agrimata.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agrimata.model.Grant
import com.example.agrimata.network.SuparBaseClient.client
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch

class GrantsViewModel : ViewModel() {

    // Fetch available grants
    fun fetchAvailableGrants(onSuccess: (List<Grant>) -> Unit) {
        viewModelScope.launch {
            try {
                val grants = client.postgrest["grants_subsidies"]
                    .select() { filter { eq("status", "open") } }
                    .decodeList<Grant>()
                onSuccess(grants)
            } catch (e: Exception) {
                Log.e("Grants", "Error fetching grants: ${e.message}")
            }
        }
    }

    // Apply for a grant
    fun applyForGrant(userId: String, grantId: String) {
        viewModelScope.launch {
            try {
                val grant = client.postgrest["grants_subsidies"]
                    .select() { filter { eq("grantId", grantId) } }
                    .decodeSingle<Grant>()

                val updatedApplicants = grant.applicants + userId

                client.postgrest["grants_subsidies"]
                    .update(mapOf("applicants" to updatedApplicants))
                    { filter { eq("grantId", grantId) } }

                Log.d("Grants", "User $userId applied for grant $grantId")
            } catch (e: Exception) {
                Log.e("Grants", "Error applying for grant: ${e.message}")
            }
        }
    }

//    // Check if user has applied for any grants
//    fun checkApplicationStatus(userId: String, onSuccess: (List<Grant>) -> Unit) {
//        viewModelScope.launch {
//            try {
//                val grants = client.postgrest["grants_subsidies"]
//                    .select().contains("applicants", listOf(userId))
//                    .decodeList<Grant>()
//                onSuccess(grants)
//            } catch (e: Exception) {
//                Log.e("Grants", "Error checking status: ${e.message}")
//            }
//        }
//    }
}
