package com.arosara.anisaw.ui.main.home

import androidx.navigation.NavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import com.arosara.anisaw.R
import com.arosara.anisaw.launchFragmentInHiltContainer
import com.arosara.anisaw.ui.main.home.di.HomeRepositoryModule
import com.arosara.anisaw.ui.main.home.source.HomeFakeDefaultRepository
import com.arosara.anisaw.ui.main.home.source.HomeRepository
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock


@RunWith(AndroidJUnit4::class)
@MediumTest
@HiltAndroidTest
@UninstallModules(HomeRepositoryModule::class)
class HomeFragmentTest {


    @BindValue
    @HomeRepositoryModule.HomeRepo
    val repository: HomeRepository = HomeFakeDefaultRepository()

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun launchFragment() {

        val navController = mock(NavController::class.java)
        launchFragmentInHiltContainer<HomeFragment>(null, R.style.AppTheme, navController)
        Thread.sleep(5000)

    }

}