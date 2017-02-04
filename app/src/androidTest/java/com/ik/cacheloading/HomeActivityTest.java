package com.ik.cacheloading;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.ik.cacheloading.home.controller.HomeActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by Ismail.Khan2 on 2/4/2017.
 */

@RunWith(AndroidJUnit4.class)
public class HomeActivityTest {

    @Rule
    public ActivityTestRule<HomeActivity> mActivity =
            new ActivityTestRule<HomeActivity>(HomeActivity.class);

    @Test
    public void populatingRecyclerViewWithAPIDataTest(){
        Espresso.registerIdlingResources(mActivity.getActivity().idlingResource);
        onView(withId(R.id.rv_user_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(3, click()));
    }


}
