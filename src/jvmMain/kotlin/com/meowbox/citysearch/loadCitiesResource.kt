package com.meowbox.citysearch

import com.meowbox.citysearch.util.Reader
import com.meowbox.citysearch.util.tsvReader
import java.io.BufferedReader
import java.util.zip.GZIPInputStream

private fun BufferedReader.asReader() = Reader { readLine() }


val cities by lazy {
    ClassLoader.getSystemResourceAsStream(RESOURCE_NAME).use {
        GZIPInputStream(it).bufferedReader().use { reader ->
            tsvReader(reader.asReader(), transform = CitySearchResult.Companion::fromCsv).toList()
        }
    }
}

actual suspend fun loadCitiesResource() = ClassLoader.getSystemResourceAsStream(RESOURCE_NAME).use {
    GZIPInputStream(it).bufferedReader().use { reader ->
        tsvReader(reader.asReader(), transform = CitySearchResult.Companion::fromCsv).toList()
    }
}
