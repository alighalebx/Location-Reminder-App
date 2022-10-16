package com.udacity.project4.locationreminders.reminderslist


import android.os.Build
import android.os.Looper
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.getOrAwaitValue
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.*
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
//fix robolectricVersion with newer version,note we can ignore it but viewmodel use context to use in fragment
@Config(sdk = [Build.VERSION_CODES.O_MR1])
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    //TODO: provide testing to the RemindersListViewModel and its live data objects
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    private lateinit var fakeDataSource: FakeDataSource
    private lateinit var reminderListViewModel: RemindersListViewModel

    @Before
    fun initViewModel() {
        fakeDataSource = FakeDataSource()
        reminderListViewModel = RemindersListViewModel(
            getApplicationContext(),
            fakeDataSource
        )
    }
    @Test
    fun returnError_test() = mainCoroutineRule.runTest {

        fakeDataSource.setReturnError(true)
        reminderListViewModel.loadReminders()
        advanceUntilIdle()
        var value = reminderListViewModel.showSnackBar.getOrAwaitValue()
        assertThat(value, `is`("ERROR IN GET DATA"))

    }
    @Test
    fun reminderList_test() = mainCoroutineRule.runBlockingTest {
        val testReminder1 = ReminderDTO("home", "go home", "cairo", 30.325844, 195.03211)
        val testReminder2 = ReminderDTO("work", "go work", "cairo", 31.325844, 196.03211)
        val testReminder3 = ReminderDTO("park", "go park", "cairo", 32.325844, 197.03211)
        val remindersList = mutableListOf(testReminder1, testReminder2, testReminder3)
        fakeDataSource = FakeDataSource(remindersList)
        reminderListViewModel = RemindersListViewModel(getApplicationContext(), fakeDataSource)
        reminderListViewModel.loadReminders()
        fakeDataSource.saveReminder(testReminder1)
//        advanceUntilIdle()
//        //assertThat(reminderListViewModel.showLoading.getOrAwaitValue(), `is`(true))
//        shadowOf(Looper.getMainLooper()).idle()
//
//
//        assertThat(reminderListViewModel.remindersList.getOrAwaitValue(), (not(emptyList())))
//        assertThat(
//            reminderListViewModel.remindersList.getOrAwaitValue().size, `is`(remindersList.size)
//        )
        mainCoroutineRule.pauseDispatcher()
        reminderListViewModel.loadReminders()

        assertThat(reminderListViewModel.showLoading.getOrAwaitValue(), `is`(true))
        mainCoroutineRule.resumeDispatcher()

        assertThat(reminderListViewModel.showLoading.getOrAwaitValue(), `is`(false))
        assertThat(reminderListViewModel.showNoData.getOrAwaitValue(), `is`(false))
    }

    @After
    fun stopDown() {
        stopKoin()
    }

}



