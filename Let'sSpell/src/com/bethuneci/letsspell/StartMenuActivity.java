package com.bethuneci.letsspell;

// Required for all Android apps
import android.os.Bundle;
import android.app.Activity;

// Needed to go to next activity
import android.content.Context;
import android.content.Intent;

// Widgets that need to be used for this activity
import android.widget.Button;
import android.widget.TextView;

// Allows user to click the buttons
import android.view.View;
import android.view.View.OnClickListener;

public class StartMenuActivity extends Activity {

	// Needed to go the next activity
	final Context context = this;
	
	// Reference to UI components that can be updated
	private TextView titleTextView;
	private TextView difficultyTextView;
	private Button threeFourButton;
	private Button fiveSixButton;
	private Button sevenEightButton;
	
	// First method called when our app is launched.
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_menu_activity_main);
		
		// Initialize UI components
		titleTextView = (TextView)findViewById(R.id.titleTextView);
		titleTextView.setText("Let's Spell!");
		difficultyTextView = (TextView)findViewById(R.id.difficultyTextView);
		difficultyTextView.setText("Choose a Difficulty");
		threeFourButton = (Button)findViewById(R.id.threeFourButton);
		threeFourButton.setOnClickListener(new threeFourButtonListener());
		fiveSixButton = (Button)findViewById(R.id.fiveSixButton);
		fiveSixButton.setOnClickListener(new fiveSixButtonListener());
		sevenEightButton = (Button)findViewById(R.id.sevenEightButton);
		sevenEightButton.setOnClickListener(new sevenEightButtonListener());
	}
	
	// Three Four Button Listener handles the difficulty setting for 3 and 4 letter words and
	// continues to the spelling app
	private class threeFourButtonListener implements OnClickListener {
		public void onClick(View v) {
			MainActivity.setDifficulty("3to4");
			Intent intent = new Intent(context, MainActivity.class);
			startActivity(intent);
		}
	};
	
	// Five Six Button Listener handles the difficulty setting for 5 and 6 letter words and
	// continues to the spelling app
	private class fiveSixButtonListener implements OnClickListener {
		public void onClick(View v) {
			MainActivity.setDifficulty("5to6");
			Intent intent = new Intent(context, MainActivity.class);
			startActivity(intent);
		}
	};
	
	// Seven Eight Button Listener handles the difficulty setting for 7 and 8 letter words and
	// continues to the spelling app
	private class sevenEightButtonListener implements OnClickListener {
		public void onClick(View v) {
			MainActivity.setDifficulty("7to8");
			Intent intent = new Intent(context, MainActivity.class);
			startActivity(intent);
		}
	};
	
}
