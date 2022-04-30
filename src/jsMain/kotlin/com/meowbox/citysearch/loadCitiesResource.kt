package com.meowbox.citysearch

import com.meowbox.citysearch.app.Context
import com.meowbox.citysearch.util.axios
import kotlinx.coroutines.await
import kotlinx.js.jso

const val citiesDataUrl = "http://localhost:8000/cities.tsv"

actual suspend fun loadCitiesResource(context: Context): List<CitySearchResult> {
    return loadCitiesFromGitHub()
}

actual suspend fun loadCitiesResource(path: String): List<CitySearchResult> {
    return loadCitiesFromGitHub()
}


suspend fun loadCitiesFromGitHub() : List<CitySearchResult> =
    axiosGetData()
        .data
        .split("\n")
        .map { it.removeSuffix("\n") }
        .map { it.split("\t") }
        .filter { it.size == 8 }
        .map(CitySearchResult.Companion::fromCsv)

private suspend fun axiosGetData() = axios<String>(jso {
    url = citiesDataUrl
    responseType = "text"
}).await()
