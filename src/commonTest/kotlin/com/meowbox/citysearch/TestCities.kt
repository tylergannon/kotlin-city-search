package com.meowbox.citysearch

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class TestCities {
    lateinit var search: CitySearchProvider


    @BeforeTest
    fun beforeTest() {
//        search = DefaultSearchProvider("src/androidMain/res/raw/citiesgz")
    }

    @Test
    fun testItHasAnything() = runTest {
        search = DefaultSearchProvider("src/androidMain/res/raw/citiesgz")
        assertEquals(42905, loadCitiesResource("src/androidMain/res/raw/citiesgz").size )
    }


    @Test
    fun testTrySearch() = runTest {
        search = DefaultSearchProvider("src/androidMain/res/raw/citiesgz")
        val result = search("Santa Rosa")
        assertEquals(18, result.size)
    }

    @Test
    fun testISO() = runTest {
        search = DefaultSearchProvider("src/androidMain/res/raw/citiesgz")
        val result = search("Santa Rosa, USA")
        assertEquals(1, result.size)
    }

    @Test
    fun testCountry() = runTest {
        search = DefaultSearchProvider("src/androidMain/res/raw/citiesgz")
        val result = search("Santa Rosa, United")
        assertEquals(1, result.size)
    }
    @Test
    fun testState() = runTest {
        search = DefaultSearchProvider("src/androidMain/res/raw/citiesgz")
        val result = search("Santa Rosa, California")
        assertEquals(1, result.size)
    }
}
