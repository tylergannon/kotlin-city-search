package com.meowbox.citysearch

import com.meowbox.citysearch.app.Context

const val RESOURCE_NAME = "citiesgz"


const val NO_STATE = ""

fun interface CitySearchProvider {
    suspend operator fun invoke(query: String) : List<CitySearchResult>
    suspend fun search(query: String) = invoke(query)
}
fun DefaultSearchProvider(cities: List<CitySearchResult>): CitySearchProvider {
    return CitySearchProvider { query ->
        query.split(",", limit = 2)
            .map(String::trim)
            .let { result -> Pair(result[0], if (result.size > 1) result[1] else NO_STATE) }
            .let { (city, state) -> cities.search(city, state) }
    }
}
suspend fun DefaultSearchProvider(context: Context) = DefaultSearchProvider(loadCitiesResource(context))
suspend fun DefaultSearchProvider(filePath: String) = DefaultSearchProvider(loadCitiesResource(filePath))

expect suspend fun loadCitiesResource(context: Context) : List<CitySearchResult>
expect suspend fun loadCitiesResource(path: String) : List<CitySearchResult>

private fun List<CitySearchResult>.search(cityName: String, stateOrCountry: String = NO_STATE) =
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

