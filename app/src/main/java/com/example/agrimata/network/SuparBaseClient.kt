package com.example.agrimata.network

import com.example.agrimata.constants.keys
import io.github.jan.supabase.auth.Auth

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage


object SuparBaseClient {
    val client = createSupabaseClient(
        supabaseUrl = keys.suparbaseUrl,
        supabaseKey = keys.suparbaseKey
    ){
        install(Auth)
        //install(GoTrue)
        install(Postgrest)
        install(Storage)
        install(Realtime)
    }
}