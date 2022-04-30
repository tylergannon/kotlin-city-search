package com.meowbox.citysearch

import com.meowbox.citysearch.app.Context
import com.meowbox.citysearch.app.JvmContext
import com.meowbox.citysearch.util.Reader
import com.meowbox.citysearch.util.tsvReader
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.util.zip.GZIPInputStream
import kotlin.streams.toList

private fun BufferedReader.asReader() = Reader { readLine() }

private fun loadSystemResource() = ClassLoader.getSystemResourceAsStream(RESOURCE_NAME)

private fun toCitySearchResults(stream: InputStream) =
    runCatching { GZIPInputStream(stream) }
        .map { it.bufferedReader() }
        .mapCatching(::deserializeSearchResults)
        .getOrThrow().toList()


//actual suspend fun loadCitiesResource(context: Context) =
//    Result.of<InputStream, Throwable>(::loadSystemResource)
//        .flatMap(::toCitySearchResults)
//        .get()
//        .toList()

suspend fun DefaultSearchProvider() = DefaultSearchProvider(JvmContext.singleton)

actual suspend fun loadCitiesResource(path: String): List<CitySearchResult> =
    runCatching { File(path).inputStream() }
        .map(::toCitySearchResults)
        .getOrThrow()


actual suspend fun loadCitiesResource(context: Context) =
    runCatching(::loadSystemResource)
        .map(::toCitySearchResults)
        .getOrThrow()

private fun deserializeSearchResults(input: BufferedReader) =
    tsvReader({ input.readLine() }, transform = CitySearchResult.Companion::fromCsv)


