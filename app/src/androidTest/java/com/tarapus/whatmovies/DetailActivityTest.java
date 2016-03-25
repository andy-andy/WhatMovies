package com.tarapus.whatmovies;

import android.support.test.espresso.ViewAssertion;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.tarapus.whatmovies.activities.DetailActivity;
import com.tarapus.whatmovies.activities.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DetailActivityTest {
    @Rule
    public ActivityTestRule<DetailActivity> mActivityRule = new ActivityTestRule<>(
            DetailActivity.class);

    @Test
    public void isRetryButtonClickable() {
        onView(withId(R.id.favorite_action_button)).check(matches(isClickable()));
    }

//    @Test
//    public void testTrailer() {
//        onView(withId(R.drawable.ic_video)).check(matches(isDisplayed()));
//        onView(withId(R.drawable.ic_video)).perform(click());
//    }
}
