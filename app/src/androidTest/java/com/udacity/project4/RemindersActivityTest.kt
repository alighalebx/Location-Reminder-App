package com.udacity.project4

import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.IBinder
import android.view.WindowManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.Root
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.google.android.material.internal.ContextUtils
import com.google.android.material.internal.ContextUtils.getActivity
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.util.monitorActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.get
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@LargeTest
//END TO END test to black box test the app

class RemindersActivityTest :
    AutoCloseKoinTest() {// Extended Koin Test - embed autoclose @after method to close Koin after every test

    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application
    private lateinit var remindersActivity: RemindersActivity
    private lateinit var viewModel: SaveReminderViewModel
    private val dataBindingIdlingResource = DataBindingIdlingResource()


//    @get:Rule
//    val activityRule = ActivityTestRule(RemindersActivity::class.java)


//    var activityTestRule: ActivityTestRule<RemindersActivity> =
//        ActivityTestRule(RemindersActivity::class.java)
    /**
     * As we use Koin as a Service Locator Library to develop our code, we'll also use Koin to test our code.
     * at this step we will initialize Koin related code to be able to use it in out testing.
     */
//    @get:Rule
//    var activityTestRule: ActivityTestRule<RemindersActivity> =
//        ActivityTestRule(RemindersActivity::class.java)


    @Before
    fun init() {
        stopKoin()//stop the original app koin
        appContext = getApplicationContext()
        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single {
                SaveReminderViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single { LocalDB.createRemindersDao(appContext) }
        }
        //declare a new koin module
        startKoin {
            modules(listOf(myModule))
        }
        //remindersActivity = activityTestRule.activity
        //remindersActivity = ApplicationProvider.getApplicationContext<RemindersActivity>()
        //Get our real repository
        repository = get()

        //clear the data to start fresh
        runBlocking {
            repository.deleteAllReminders()
        }
    }


    //    TODO: add End to End testing to the app
    @Test
    fun reminder_Location_Activity_show_toast_message_test() {

        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
        onView(withId(R.id.addReminderFAB)).perform(click())
        onView(withId(R.id.reminderTitle)).perform(replaceText("Test "))
        onView(withId(R.id.reminderDescription)).perform(replaceText("Test"))
        onView(withId(R.id.selectLocation)).perform(click())
        onView(withId(R.id.map)).perform(longClick())
        onView(withId(R.id.confirmloc_btn)).perform(click())
        onView(withId(R.id.saveReminder)).perform(click())
        onView(withText("Reminder Saved !")).inRoot(
            withDecorView(not(`is`(getActivity(activityScenario)!!.window.decorView)))
        ).check(matches(isDisplayed()))



        activityScenario.close()
    }

    private fun getActivity(activityScenario: ActivityScenario<RemindersActivity>): Activity {
        lateinit var activity: Activity
        activityScenario.onActivity {
            activity = it
        }
        return activity
    }

    @Test
    fun ui_Test() = runBlocking {
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        val decorView = ContextUtils.getActivity(appContext)?.window?.decorView

        dataBindingIdlingResource.monitorActivity(activityScenario)

        val title = "Pop in da house"
        val description = "house"

        //click the FAB
        onView(withId(R.id.addReminderFAB)).perform(click())

        //check if a snackbar is displayed if the user tries to save the reminder before typing the title
        onView(withId(R.id.saveReminder)).perform(click())
        onView(withId(com.google.android.material.R.id.snackbar_text)).check(matches(withText(R.string.err_enter_title)))

        //title
        onView(withId(R.id.reminderTitle)).perform(typeText(title))
        Espresso.closeSoftKeyboard()


        //description
        onView(withId(R.id.reminderDescription)).perform(typeText(description))
        Espresso.closeSoftKeyboard()


        //map
        onView(withId(R.id.selectLocation)).perform(click())

        //return to the previous view
        onView(withId(R.id.map)).perform(longClick())
        onView(withId(R.id.confirmloc_btn)).perform(click())

        //verify that a location is selected
        onView(withId(R.id.reminderTitle)).check(matches(withText(title)))
        onView(withId(R.id.reminderDescription)).check(matches(withText(description)))
        onView(withId(R.id.selectLocation)).check(matches(not(withText(""))))
        onView(withId(R.id.saveReminder)).perform(click())

        //main screen
        Espresso.pressBackUnconditionally()
        activityScenario.close()
        Thread.sleep(2000)

    }

    @Test
    fun checkSnackBar_test() {
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        onView(withId(R.id.noDataTextView))
            .check(matches(isDisplayed()))
        onView(withId(R.id.addReminderFAB)).perform(click())
        onView(withId(R.id.saveReminder)).perform(click())
        Thread.sleep(4000)
        onView(withId(R.id.reminderTitle)).perform(replaceText("title"))
        onView(withId(R.id.saveReminder)).perform(click())

        activityScenario.close()
    }

    @After
    fun unregisterIdlingResources() {
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
        IdlingRegistry.getInstance()
            .unregister(RemindersLocalRepository.EspressoIdlingResource.countingIdlingResource)
    }


}
//class ToastMatcher : TypeSafeMatcher<Root>() {
//    override fun describeTo(description: Description) {
//        description.appendText("is toast")
//    }
//
//    public override fun matchesSafely(root: Root): Boolean {
//        val type: Int = root.getWindowLayoutParams().get().type
//        if (type == WindowManager.LayoutParams.TYPE_TOAST) {
//            val windowToken: IBinder = root.getDecorView().getWindowToken()
//            val appToken: IBinder = root.getDecorView().getApplicationWindowToken()
//            if (windowToken === appToken) {
//                // windowToken == appToken means this window isn't contained by any other windows.
//                // if it was a window for an activity, it would have TYPE_BASE_APPLICATION.
//                return true
//            }
//        }
//        return false
//    }

