package in.net.kccollege.student;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class NewTest {

	@Rule
	public ActivityTestRule<Splash> mActivityTestRule = new ActivityTestRule<>(Splash.class);

	@Test
	public void newTest() {
		ViewInteraction textInputEditText = onView(
				withId(R.id.email));
		textInputEditText.perform(scrollTo(), replaceText("sahilnirkhe@outlook.com"), closeSoftKeyboard());

		ViewInteraction textInputEditText2 = onView(
				allOf(withId(R.id.email), withText("sahilnirkhe@outlook.com")));
		textInputEditText2.perform(pressImeActionButton());

		ViewInteraction textInputEditText3 = onView(
				withId(R.id.password));
		textInputEditText3.perform(scrollTo(), replaceText("sss121"), closeSoftKeyboard());

		ViewInteraction textInputEditText4 = onView(
				allOf(withId(R.id.password), withText("sss121")));
		textInputEditText4.perform(pressImeActionButton());

		ViewInteraction appCompatButton = onView(
				allOf(withId(R.id.btnlogin), withText("Login")));
		appCompatButton.perform(scrollTo(), click());


		openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

		ViewInteraction recyclerView = onView(
				allOf(withId(R.id.recyler_view),
						withParent(withId(R.id.swipe_refresh_layout)),
						isDisplayed()));
		recyclerView.perform(actionOnItemAtPosition(0, click()));

		openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

		ViewInteraction appCompatTextView = onView(
				allOf(withId(R.id.title), withText("Logout"), isDisplayed()));
		appCompatTextView.perform(click());


		ViewInteraction appCompatButton2 = onView(
				allOf(withId(R.id.btnguest), withText("Guest Login")));
		appCompatButton2.perform(scrollTo(), click());

	}

}
