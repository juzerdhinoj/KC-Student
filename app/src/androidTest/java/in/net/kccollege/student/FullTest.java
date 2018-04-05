package in.net.kccollege.student;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import in.net.kccollege.student.activities.LoginActivity;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class FullTest {

	@Rule
	public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

	@Test
	public void fullTest() {


		ViewInteraction textInputEditText = onView(
				withId(R.id.email));
		textInputEditText.perform(scrollTo(), replaceText("sahilnirkhe@outlook.com"), closeSoftKeyboard());

		ViewInteraction textInputEditText4 = onView(
				withId(R.id.password));
		textInputEditText4.perform(scrollTo(), replaceText("sss121"), closeSoftKeyboard());

		ViewInteraction appCompatButton = onView(
				allOf(withId(R.id.btnlogin), withText("Login")));
		appCompatButton.perform(scrollTo(), click());

		ViewInteraction recyclerView = onView(
				allOf(withId(R.id.recyler_view),
						withParent(withId(R.id.swipe_refresh_layout)),
						isDisplayed()));
		recyclerView.perform(actionOnItemAtPosition(0, click()));

		ViewInteraction recyclerView2 = onView(
				allOf(withId(R.id.recyler_view),
						withParent(withId(R.id.swipe_refresh_layout)),
						isDisplayed()));
		recyclerView2.perform(actionOnItemAtPosition(1, click()));

		ViewInteraction recyclerView3 = onView(
				allOf(withId(R.id.recyler_view),
						withParent(withId(R.id.swipe_refresh_layout)),
						isDisplayed()));
		recyclerView3.perform(actionOnItemAtPosition(2, click()));

		ViewInteraction appCompatImageButton = onView(
				allOf(withContentDescription("Open"),
						withParent(allOf(withId(R.id.toolbar),
								withParent(withId(R.id.barlay)))),
						isDisplayed()));
		appCompatImageButton.perform(click());

		openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

		ViewInteraction appCompatTextView = onView(
				allOf(withId(R.id.title), withText("Logout"), isDisplayed()));
		appCompatTextView.perform(click());

		ViewInteraction appCompatButton2 = onView(
				allOf(withId(R.id.btnguest), withText("Guest Login")));
		appCompatButton2.perform(scrollTo(), click());

	}

}
