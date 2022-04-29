package com.meowbox.citysearch

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class TestCities {
    lateinit var search: CitySearchProvider


    @BeforeTest
    fun beforeTest() = runBlocking {
        search = DefaultSearchProvider("src/androidMain/res/raw/citiesgz")
    }

    @Test
    fun testItHasAnything() = runTest {
        assertEquals(42905, loadCitiesResource("src/androidMain/res/raw/citiesgz").size )
    }


    @Test
    fun testTrySearch() = runTest {
        val result = search("Santa Rosa")
        assertEquals(18, result.size)
    }

    @Test
    fun testISO() = runTest {
        val result = search("Santa Rosa, USA")
        assertEquals(1, result.size)
    }

    @Test
    fun testCountry() = runTest {
        val result = search("Santa Rosa, United")
        assertEquals(1, result.size)
    }
    @Test
    fun testState() = runTest {
        val result = search("Santa Rosa, California")
        assertEquals(1, result.size)
    }
}
