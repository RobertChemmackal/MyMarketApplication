package com.robert.mymarketplace.data.remote

import com.robert.mymarketplace.data.remote.dto.ListingDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface MarketPlaceApi {
    @GET("listings")
    suspend fun getListings(): List<ListingDto>

    @POST("listings")
    suspend fun createListing(@Body listing: ListingDto): ListingDto

    @PUT("listings/{id}")
    suspend fun updateListing(@Path("id") id: String, @Body listing: ListingDto): ListingDto

    @POST("sync")
    suspend fun syncListings(@Body listings: List<ListingDto>): List<ListingDto>

}
