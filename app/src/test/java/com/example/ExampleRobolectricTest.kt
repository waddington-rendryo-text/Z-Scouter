package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.api.DragonBallApiService
import com.example.domain.repository.DragonBallRepository
import com.example.domain.usecase.CalculateSaiyanRankUseCase
import com.example.domain.usecase.FilterCharactersUseCase
import com.example.domain.usecase.GetCharacterDetailUseCase
import com.example.domain.usecase.GetCharactersUseCase
import com.example.domain.usecase.GetPlanetsUseCase
import com.example.domain.usecase.ManageFiguresUseCase
import com.example.domain.usecase.ManageWorkoutsUseCase
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
    fun `koin di graph resolves core dependencies and all domain use cases`() {
        val koin = GlobalContext.get()
        assertNotNull(koin.get<DragonBallApiService>())
        assertNotNull(koin.get<DragonBallRepository>())
        assertNotNull(koin.get<GetCharactersUseCase>())
        assertNotNull(koin.get<GetCharacterDetailUseCase>())
        assertNotNull(koin.get<GetPlanetsUseCase>())
        assertNotNull(koin.get<FilterCharactersUseCase>())
        assertNotNull(koin.get<CalculateSaiyanRankUseCase>())
        assertNotNull(koin.get<ManageFiguresUseCase>())
        assertNotNull(koin.get<ManageWorkoutsUseCase>())
        assertNotNull(koin.get<SimulateBattleUseCase>())
    }
}
