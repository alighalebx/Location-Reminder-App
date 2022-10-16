package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import android.os.Looper
import com.udacity.project4.R
import com.udacity.project4.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.koin.core.context.stopKoin
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.LooperMode

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()
    var instantExecutorRule = InstantTaskExecutorRule()

    //TODO: provide testing to the RemindersListViewModel and its live data objects
    private lateinit var fakeDataSource: FakeDataSource
    private lateinit var saveReminderViewModel: SaveReminderViewModel

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    //TODO: provide testing to the SaveReminderView and its live data objects
    @Before
    fun initViewModel() {
        fakeDataSource = FakeDataSource()
        saveReminderViewModel = SaveReminderViewModel(
            ApplicationProvider.getApplicationContext(),
            fakeDataSource
        )
    }

    val reminder = ReminderDataItem(
        title = "Pop in da house", description = "house",
        location = "cairo", latitude = 30.0490707583168, longitude = 31.36187761235351
    )

    @Test
    fun saveReminder_test() = runBlockingTest {
        saveReminderViewModel.saveReminder(reminder)
        assertThat(saveReminderViewModel.showToast.getOrAwaitValue(), `is`("Reminder Saved !"))
    }

    @Test
    fun getLatitude_and_longitude_test() = mainCoroutineRule.runBlockingTest {
        val reminder2 = ReminderDataItem(
            title = "Pop in da house", description = "house",
            location = "cairo", latitude = 30.0, longitude = 31.0
        )
        saveReminderViewModel.validateAndSaveReminder(reminder2)

        advanceUntilIdle()
        fakeDataSource.deleteAllReminders()
       // val value = saveReminderViewModel.reminderTitle.getOrAwaitValue()
        //assertEquals(value, reminder2.title)

        //assertThat(reminder2.latitude, `is`(saveReminderViewModel.latitude.value))
       // assertThat(saveReminderViewModel.longitude.getOrAwaitValue(), `is`(reminder2.longitude))


    }



    @Test
    fun loading_test() {
        mainCoroutineRule.pauseDispatcher()
        saveReminderViewModel.saveReminder(reminder)
        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), `is`(true))
        mainCoroutineRule.resumeDispatcher()
        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), `is`(false))
    }

    @After
    fun tearDown() {
        stopKoin()
    }
}