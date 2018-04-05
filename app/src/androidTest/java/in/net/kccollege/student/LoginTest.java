package in.net.kccollege.student;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import in.net.kccollege.student.activities.LoginActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class LoginTest {

	@Rule
	public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

	@Test
	public void loginTest() {
		ViewInteraction textInputEditText = onView(
				withId(R.id.email));
		textInputEditText.perform(scrollTo(), click());

		ViewInteraction textInputEditText2 = onView(
				withId(R.id.email));
		textInputEditText2.perform(scrollTo(), replaceText("sahilnirkhe@outlook.com"), closeSoftKeyboard());

		ViewInteraction textInputEditText3 = onView(
				allOf(withId(R.id.email), withText("sahilnirkhe@outlook.com")));
		textInputEditText3.perform(pressImeActionButton());

		ViewInteraction textInputEditText4 = onView(
				withId(R.id.password));
		textInputEditText4.perform(scrollTo(), replaceText("sss121"), closeSoftKeyboard());

		ViewInteraction textInputEditText5 = onView(
				allOf(withId(R.id.password), withText("sss121")));
		textInputEditText5.perform(pressImeActionButton());

		ViewInteraction appCompatButton = onView(
				allOf(withId(R.id.btnlogin), withText("Login")));
		appCompatButton.perform(scrollTo(), click());

	}

}
