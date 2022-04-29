package com.meowbox.citysearch

import com.github.kittinunf.result.Result
import com.github.kittinunf.result.map
import com.github.kittinunf.result.flatMap
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

private fun unGZipStream(stream: InputStream) = Result.of<InputStream, Throwable> { stream }
    .map(::GZIPInputStream)
    .map(GZIPInputStream::bufferedReader)

private fun deserializeSearchResults(input: BufferedReader) =
    tsvReader({ input.readLine() }, transform = CitySearchResult.Companion::fromCsv)



actual suspend fun loadCitiesResource(context: Context) =
    Result.of<InputStream, Throwable> { loadSystemResource(context) }
        .flatMap(::unGZipStream)
        .map(::deserializeSearchResults)
        .get()
        .toList()

fun sorkin(path: String): List<CitySearchResult> {
    val file = File(path).inputStream()

    return file.use { openStream ->
        val reader = GZIPInputStream(openStream).bufferedReader()
        reader.use { openedReader ->
            deserializeSearchResults(openedReader).toList()
        }
    }
}

actual suspend fun loadCitiesResource(path: String) =
    Result.of<InputStream, Throwable> { File(path).inputStream() }
        .flatMap(::unGZipStream)
        .map(::deserializeSearchResults)
        .get()
        .toList()
