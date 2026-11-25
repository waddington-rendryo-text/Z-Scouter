package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.api.DragonBallApiService
import com.example.domain.repository.DragonBallRepository
import com.example.domain.usecase.CalculateSaiyanRankUseCase
import com.example.domain.usecase.FilterCharactersUseCase
import com.example.domain.usecase.SimulateBattleUseCase
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.GlobalContext
import org.koin.core.context.stopKoin
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Z-Scouter", appName)
    }

    @Test
    fun `koin di graph resolves core dependencies`() {
        val koin = GlobalContext.get()
        assertNotNull(koin.get<DragonBallApiService>())
        assertNotNull(koin.get<DragonBallRepository>())
        assertNotNull(koin.get<FilterCharactersUseCase>())
        assertNotNull(koin.get<CalculateSaiyanRankUseCase>())
        assertNotNull(koin.get<SimulateBattleUseCase>())
    }
}
