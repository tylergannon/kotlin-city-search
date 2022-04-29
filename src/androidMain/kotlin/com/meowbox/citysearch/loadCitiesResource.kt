package com.meowbox.citysearch

import android.content.Context
import com.meowbox.citysearch.util.Reader
import com.meowbox.citysearch.util.tsvReader
import java.io.BufferedReader
import java.io.InputStream
import java.util.zip.GZIPInputStream

const val RESOURCE_TYPE = "raw"

actual suspend fun loadCitiesResource(): List<CitySearchResult> {
    TODO("Not yet implemented")
}

private fun BufferedReader.asReader() = Reader { readLine() }

private fun getCitiesFromStream(it: InputStream) =
    tsvReader(
        GZIPInputStream(it).bufferedReader().asReader(),
        transform = CitySearchResult.Companion::fromCsv
    ).toList()

private fun readCities(context: Context) = context.resources.let { res ->
    res.openRawResource(
        res.getIdentifier(
            RESOURCE_NAME,
            RESOURCE_TYPE,
            context.packageName
        )
    ).use(::getCitiesFromStream)
}
