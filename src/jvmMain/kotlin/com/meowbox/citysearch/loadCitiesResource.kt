package com.meowbox.citysearch

import com.meowbox.citysearch.app.Context
import com.meowbox.citysearch.app.JvmContext
import com.meowbox.citysearch.util.Reader
import com.meowbox.citysearch.util.tsvReader
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.flatMap
import com.github.kittinunf.result.map
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.util.zip.GZIPInputStream
import kotlin.streams.toList

private fun BufferedReader.asReader() = Reader { readLine() }

private fun loadSystemResource() = ClassLoader.getSystemResourceAsStream(RESOURCE_NAME)
private fun deserializeSearchResults(input: BufferedReader) =
        tsvReader({ input.readLine() }, transform = CitySearchResult.Companion::fromCsv)

private fun toCitySearchResults(stream: InputStream) =
    Result.of<InputStream, Throwable> { stream }
        .map(::GZIPInputStream)
        .map(GZIPInputStream::bufferedReader)
        .map(::deserializeSearchResults)

actual suspend fun loadCitiesResource(context: Context) =
    Result.of<InputStream, Throwable>(::loadSystemResource)
        .flatMap(::toCitySearchResults)
        .get()
        .toList()

suspend fun DefaultSearchProvider() = DefaultSearchProvider(JvmContext.singleton)

actual suspend fun loadCitiesResource(path: String) =
    Result.of<InputStream, Throwable> { File(path).inputStream() }
        .flatMap(::toCitySearchResults)
        .get()
        .toList()
