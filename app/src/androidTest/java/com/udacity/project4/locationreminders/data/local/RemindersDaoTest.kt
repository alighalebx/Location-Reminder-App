package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    private lateinit var database: RemindersDatabase

    @Before
    fun initDb() {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    val reminder = ReminderDTO(
        title = "Pop in da house", description = "house",
        location = "cairo", latitude = 30.0490707583168, longitude = 31.36187761235351)


    @Test
    fun addReminder_returnReminder_test() = runBlocking {


        database.reminderDao().saveReminder(reminder)
        val result = database.reminderDao().getReminderById(reminder.id)
        assertThat(result?.title, `is`(reminder.title))
        assertThat(result?.description, `is`(reminder.description))
        assertThat(result?.location, `is`(reminder.location))
        assertThat(result?.latitude, `is`(reminder.latitude))
        assertThat(result?.longitude,`is`(reminder.longitude))

    }
//    TODO: Add testing implementation to the RemindersDao.kt

}