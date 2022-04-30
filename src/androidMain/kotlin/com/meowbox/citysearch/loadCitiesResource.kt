package com.meowbox.citysearch

import com.meowbox.citysearch.app.Context
import com.meowbox.citysearch.util.Reader
import com.meowbox.citysearch.util.tsvReader
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.util.zip.GZIPInputStream
import kotlin.streams.toList

const val RESOURCE_TYPE = "raw"


private fun BufferedReader.asReader() = Reader { readLine() }


private fun loadSystemResource(context: Context): InputStream = context.resources.let { res ->
    res.openRawResource(
        res.getIdentifier(
            RESOURCE_NAME,
            RESOURCE_TYPE,
            context.packageName
        )
    )
}


actual suspend fun loadCitiesResource(context: Context) =
    runCatching { loadSystemResource(context) }
        .mapCatching { GZIPInputStream(it) }
        .map { it.bufferedReader() }
        .map(::deserializeSearchResults)
        .getOrThrow()
        .toList()


private fun deserializeSearchResults(input: BufferedReader) =
    tsvReader({ input.readLine() }, transform = CitySearchResult.Companion::fromCsv)


actual suspend fun loadCitiesResource(path: String) =
    runCatching { File(path).inputStream() }
        .mapCatching { GZIPInputStream(it) }
        .map { it.bufferedReader() }
        .map(::deserializeSearchResults)
        .getOrThrow()
        .toList()
