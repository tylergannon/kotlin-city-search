package com.meowbox.citysearch

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test


class TestJvmCitySearch {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test fun loadCities() = runTest {
        val search = DefaultSearchProvider()
        search("Santa Rosa, Ca").size shouldBe 1
    }
}
