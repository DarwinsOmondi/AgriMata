package com.example.agrimata.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agrimata.model.CooperativeGroup
import com.example.agrimata.network.SuparBaseClient.client
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import java.util.UUID

class CooperativeViewModel : ViewModel() {

    fun createGroup(groupName: String, adminId: String) {
        viewModelScope.launch {
            val newGroup = CooperativeGroup(
                groupId = UUID.randomUUID().toString(),
                groupName = groupName,
                adminId = adminId
            )
            try {
                client.postgrest["cooperative_groups"].insert(newGroup)
                Log.d("Cooperative", "Group created successfully")
            } catch (e: Exception) {
                Log.e("Cooperative", "Error creating group: ${e.message}")
            }
        }
    }

    // Join a cooperative
    fun joinGroup(userId: String, groupId: String) {
        viewModelScope.launch {
            try {
                val group = client.postgrest["cooperative_groups"]
                    .select {
                        filter {
                            eq("groupId", groupId)

                        }
                    }
                    .decodeSingle<CooperativeGroup>()

                val updatedMembers = group.members + userId

                client.postgrest["cooperative_groups"]
                    .update(mapOf("members" to updatedMembers)){
                        filter {
                            eq("groupId", groupId)
                        }
                    }
                Log.d("Cooperative", "User $userId joined group $groupId")
            } catch (e: Exception) {
                Log.e("Cooperative", "Error joining group: ${e.message}")
            }
        }
    }

    // List all products in a cooperative
    fun listGroupProducts(groupId: String, onSuccess: (List<String>) -> Unit) {
        viewModelScope.launch {
            try {
                val group = client.postgrest["cooperative_groups"]
                    .select() {
                        filter {
                            eq("groupId", groupId)

                        }
                    }
                    .decodeSingle<CooperativeGroup>()

                onSuccess(group.groupProducts)
            } catch (e: Exception) {
                Log.e("Cooperative", "Error fetching products: ${e.message}")
            }
        }
    }

    // Sell in bulk to buyers
    fun sellInBulk(groupId: String, buyerId: String, productId: String, quantity: Int) {
        viewModelScope.launch {
            try {
                val group = client.postgrest["cooperative_groups"]
                    .select() {
                        filter {
                            eq("groupId", groupId)
                        }
                    }
                    .decodeSingle<CooperativeGroup>()

                val updatedRevenue = group.totalRevenue + (quantity * 10.0) // Placeholder price logic
                client.postgrest["cooperative_groups"]
                    .update(mapOf("totalRevenue" to updatedRevenue))
                    {
                        filter {
                            eq("groupId", groupId)

                        }
                    }

                Log.d("Cooperative", "Sold $quantity of $productId to buyer $buyerId")
            } catch (e: Exception) {
                Log.e("Cooperative", "Error in bulk sale: ${e.message}")
            }
        }
    }

    // Distribute revenue among members
    fun distributeRevenue(groupId: String) {
        viewModelScope.launch {
            try {
                val group = client.postgrest["cooperative_groups"]
                    .select() {
                        filter {
                            eq("groupId", groupId)
                        }
                    }
                    .decodeSingle<CooperativeGroup>()

                val revenuePerMember = group.totalRevenue / group.members.size

                Log.d("Cooperative", "Distributed $revenuePerMember per member")
            } catch (e: Exception) {
                Log.e("Cooperative", "Error distributing revenue: ${e.message}")
            }
        }
    }
}
