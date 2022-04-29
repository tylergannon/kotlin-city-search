package com.meowbox.citysearch
import com.meowbox.citysearch.app.Context
data class CitySearchResult(
    val name: String,
    val nameAscii: String,
    val lat: Float,
    val lng: Float,
    val country: String,
    val iso: String,
    val state: String,
    val tz: String
) {
    companion object {
        internal fun fromCsv(data: List<String>) =
            CitySearchResult(data[0], data[1], data[2].toFloat(), data[3].toFloat(), data[4], data[5], data[6], data[7])
    }

    val formatted get() = "$name, $state, $country"
}
