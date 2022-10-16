package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

//    TODO: Add testing implementation to the RemindersLocalRepository.kt


    private lateinit var remindersLocalRepository: RemindersLocalRepository
    private lateinit var database: RemindersDatabase

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    @Before
    fun createRepository() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()

        remindersLocalRepository =
            RemindersLocalRepository(database.reminderDao(), Dispatchers.Unconfined)
    }

    val reminder = ReminderDTO(
        title = "Pop in da house",
        description = "house",
        location = "cairo",
        latitude = 30.0490707583168,
        longitude = 31.36187761235351)

    @Test
    fun saveReminder_returnReminder_test() = runBlocking {

        remindersLocalRepository.saveReminder(reminder)
        val result = remindersLocalRepository.getReminder(reminder.id)

        result as Result.Success
        assertThat(result.data.title, `is`(reminder.title))
        assertThat(result.data.description, `is`(reminder.description))
        assertThat(result.data.location, `is`(reminder.location))
        assertThat(result.data.latitude, `is`(reminder.latitude))
        assertThat(result.data.longitude,`is`(reminder.longitude))

    }

    @Test
    fun reminderNoData_test()= runBlocking{

        remindersLocalRepository.saveReminder(reminder)
        remindersLocalRepository.deleteAllReminders()
        // WHEN  - Task retrieved by ID.
        val result = remindersLocalRepository.getReminder(reminder.id)
        result as Result.Error
        assertThat(result.message, `is`("Reminder not found!"))
    }


}