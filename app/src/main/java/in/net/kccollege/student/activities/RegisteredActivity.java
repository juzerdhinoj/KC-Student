package in.net.kccollege.student.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.net.kccollege.student.R;
import in.net.kccollege.student.db.DatabaseHandler;
import in.net.kccollege.student.model.UserDetails;

public class RegisteredActivity extends AppCompatActivity {

	@BindView(R.id.name)
	TextView name;

	@BindView(R.id.str)
	TextView str;

	@BindView(R.id.std)
	TextView std;

	@BindView(R.id.roll)
	TextView roll;

	@BindView(R.id.email)
	TextView email;

	@BindView(R.id.regat)
	TextView created_at;

	@BindView(R.id.toolbar)
	Toolbar toolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registered);

		ButterKnife.bind(this);

		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);


		DatabaseHandler db = new DatabaseHandler(getApplicationContext());

		UserDetails ud;
		ud = db.getUserDetails();

		/**
		 * Displays the registration details in Text view
		 **/
		String[] temp = ud.getDetails().split("-");
		switch (temp[1]) {
			case "SC":
				str.setText("Science");
				break;
			case "AR":

				str.setText("Arts");
				break;
			case "CO":
				str.setText("Commerce");
				break;
		}

		name.setText(ud.getName());
		std.setText(temp[0].equals("X1") ? "FYJC" : "SYJC");
		roll.setText(temp[2]);
		email.setText(ud.getEmail());
		created_at.setText(ud.getCreated_at());

		Button login = (Button) findViewById(R.id.login);
		login.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				finish();
			}

		});
	}

	@Override
	public boolean onSupportNavigateUp() {
		onBackPressed();
		return true;
	}

}
