package com.meowbox.citysearch

import android.content.Context
import androidx.test.runner.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlin.test.Test
import kotlin.test.assertEquals

@Suppress("DEPRECATION")
@RunWith(AndroidJUnit4::class)
@Config(sdk = [29])
class TestAndroidSearch {
    @Test fun loadCities() = runTest {
        val context = getApplicationContext<Context>()
        val search = DefaultSearchProvider(context)
        assertEquals(1, search("Santa Rosa, Ca").size)
    }
}