package com.meowbox.citysearch

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

const val RESOURCE_NAME = "citiesgz"


//expect val cities: List<CitySearchResult>
val NO_STATE = ""

fun interface CitySearchProvider {
    suspend operator fun invoke(query: String)
    suspend fun search(query: String) = invoke(query)
}

suspend fun DefaultSearchProvider() = loadCitiesResource().let { cities ->
    CitySearchProvider { query -> query.split(",", limit = 2)
        .map(String::trim)
        .let { result -> Pair(result[0], if (result.size > 1) result[1] else NO_STATE) }
        .let { (city, state) -> cities.search(city, state) } }
}

expect suspend fun loadCitiesResource() : List<CitySearchResult>

fun List<CitySearchResult>.search(cityName: String, stateOrCountry: String = NO_STATE) =
    Pair(cityName.lowercase(), stateOrCountry.lowercase()).let { (city, state) ->
        if (cityName.length < 3 && state.length < 3) {
            return@let emptyList()
        }

        val myCities = if (state == NO_STATE) {
            this
        } else {
            filter {
                it.state.lowercase().startsWith(state)
                        || it.country.lowercase().startsWith(state)
                        || it.iso.lowercase().startsWith(state)
            }
        }
        myCities.filter { city in it.name.lowercase() }
    }

