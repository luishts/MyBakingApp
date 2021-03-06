package com.example.android.mybakingapp;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.mybakingapp.ui.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class MyBakingAppTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    private IdlingResource mIdlingResource;
    private MainActivity mActivity;
    private boolean mIsScreenSw600dp;

    // Registers any resource that needs to be synchronized with Espresso before the test is run.
    @Before
    public void registerIdlingResource() {
        mIdlingResource = mActivityTestRule.getActivity().getIdlingResource();
        Espresso.registerIdlingResources(mIdlingResource);
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void idlingResourceTest() {

        // Check if it phone list view is not empty and it's possible to click at first item (recipe)
        onView(withId(R.id.recyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));


        onView(withId(R.id.step_list)).perform(RecyclerViewActions.scrollToPosition(0));

        // Check is there is a list will all recipe's steps and tries to click at first step
        onView(withId(R.id.step_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));


        // Check if it's possible to back button icon and go back to steps activity
        pressBack();

        if (!mActivity.getResources().getBoolean(R.bool.twoPaneMode)) {
            // Check if it's possible to press back button and go back to recipe list activity
            pressBack();
        }

        // Check if we are at main activity and recipe list is being shown
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));

    }

    // Remember to unregister resources when not needed to avoid malfunction.
    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            Espresso.unregisterIdlingResources(mIdlingResource);
        }
    }
}
