package com.bethuneci.letsspell;


//Required for all Android apps
import android.os.Bundle;
import android.app.Activity;

//Used to build an alert dialog (when the game is over)
import android.app.AlertDialog;
import android.content.DialogInterface;

//Used to "inflate" Button widgets dynamically
import android.view.LayoutInflater;
import android.content.Context;
//.. and handle button click events
import android.view.View;
import android.view.View.OnClickListener;

// Required for text to speech
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.content.Intent;
import java.util.Locale;

// Required for sound
import android.media.AudioManager;
import android.media.SoundPool;

//To implement 1 second delay after a correct answer
import android.os.Handler;

//To store (and shuffle) letters
import java.util.Random;

// widgets needed to make changes to widgets in app
import android.widget.TextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;

public class MainActivity extends Activity implements OnClickListener, OnInitListener {

	// For Text to Speech
	private int MY_DATA_CHECK_CODE = 0;
	private TextToSpeech myTTS;
	
	// Array of letters of the alphabet
	private String[] alphabet = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J","K", "L", "M", 
			"N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
	// Array of three and four letter words
	private static String[] threeAndFourLetterWords = {"ACE", "ARM", "PIN", "COW", "RED", "FOX",
			"HOW", "ICE", "WIN", "PIE", "PET", "TEN", "SKY", "WHO", "YES", "AQUA", "BAND",
			"DAWN", "DECK", "DOGS", "CASE", "DUCK","FARM", "FROG", "GOAT", "KIWI", "LOGO", 
			"VIEW", "RICE", "SHIP" };
	// Array of five and six letter words
	private static String[] fiveAndSixLetterWords = {"WATER", "CHAIR", "BRAVO", "COMBO", "BRAVE",
			"WORDS", "WORLD", "VIDEO", "SPACE", "COVER", "HELLO", "ITCHY", "MONEY", "PEARL",
			"PIZZA", "SUBWAY", "CARPET", "CAMERA", "CHEESE", "COFFEE", "EMBLEM", "FINISH", 
			"GOLDEN", "HUNGRY", "ICICLE", "OYSTER", "RABBIT", "RANDOM", "LETTER", "LAPTOP" };
	// Array of seven and eight letter words
	private static String[] sevenAndEightLetterWords = {"ANDROID", "SPEAKER", "DRAWING", "BUTTONS", 
			"PINBALL", "FRIENDS", "COLOURS", "PRINTER", "MAGICAL", "FANTASY", "BATTERY", 
			"WINDOWS", "MAGNETS", "WELCOME", "CONTENT", "KEYCHAIN", "CONVERSE", "SUNSHINE",
			"INSTANCE", "ELECTRON", "PACKAGES", "EXPLORER", "PRESENTS", "ACTIVITY", "CHICKENS",
			"CHIPMUNK", "EXAMPLES", "BACKYARD", "BARBEQUE", "VELOCITY" };
	
	// Array for buttons (each letter has a specific button)
	private String[] tenLetters = new String[10];
	
	private String jumbledWord; // Chosen word scrambled
	private String userSpelled = ""; // What the user inputs
	private static String difficulty; // Used to set difficulty of app
	private static String chosenWord; // The word being spelled
	private int wordNumber; // Used to keep track of which word you are currently on
	
	private int points; // amount of points you can earn ranging from 0-10
	private int totalPoints = 0;

	private int guessRows = 2; // For setting two rows of buttons
	private static Random randomGenerator = new Random();

	// Used to play sound effects
    private SoundPool soundPool;
    private int right_sound_id;
    private int wrong_sound_id;
	
    // References for UI Components that we will update
	private ImageButton speakButton;
	private ImageButton undoButton;
	private ImageButton submitButton;
	private ImageView correctIncorrectImageView;
	private EditText userWordInputEditText;
	private TableLayout buttonTableLayout;
	private TextView answerTextView;
	private TextView questionNumberTextView;
	private TextView pointsWorthTextView;
	private TextView totalPointsTextView;
	
	// First method called when our app is launched.
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Initialize UI components - Buttons
		speakButton = (ImageButton)findViewById(R.id.speakerImageButton);
		speakButton.setOnClickListener(new speakerButtonListener());
		undoButton = (ImageButton)findViewById(R.id.undoImageButton);
		undoButton.setOnClickListener(new undoButtonListener());
		submitButton = (ImageButton)findViewById(R.id.submitImageButton);
		submitButton.setOnClickListener(new submitButtonListener());
		// ... TextViews
		questionNumberTextView = (TextView)findViewById(R.id.questionNumberTextView);
		answerTextView = (TextView)findViewById(R.id.answerTextView);
		pointsWorthTextView = (TextView)findViewById(R.id.pointsWorthTextView);
		totalPointsTextView = (TextView)findViewById(R.id.totalPointsTextView);
		// ... ImageView
		correctIncorrectImageView = (ImageView) findViewById(R.id.correctIncorrectImageView);
		// ... EditText
		userWordInputEditText = (EditText) findViewById(R.id.userWordInputEditText);
		// ... TableLayout
		buttonTableLayout = (TableLayout) findViewById(R.id.buttonTableLayout);
		
		// Required for text to speech
		Intent checkTTSIntent = new Intent();
		checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);

		resetQuiz();
		
	}
	
	private void resetQuiz() {
		// Allow volume buttons to set the game volume
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
		// Create a SoundPool object, and use it to load the two sound effects
		soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		right_sound_id = soundPool.load(this, R.raw.correct, 1);
		wrong_sound_id = soundPool.load(this, R.raw.wrong, 1);
		
		wordNumber = 1;
		totalPoints = 0;
		nextQuestion();
	}
	
	private void nextQuestion() {
		
		userSpelled = "";
		userWordInputEditText.setText(userSpelled);
		points = 10;
		chosenWord = getChosenWord();
		jumbledWord = chosenWord;
		
		questionNumberTextView.setText("Word " + wordNumber + " of 10");
		answerTextView.setText(getResources().getString(R.string.instructions));
		answerTextView.setTextColor(getResources().getColor(R.color.text_colour));
		pointsWorthTextView.setText(getResources().getString(R.string.points_worth) + " " + points);
		totalPointsTextView.setText(getResources().getString(R.string.total_points) + " " + totalPoints);
		correctIncorrectImageView.setImageResource(R.drawable.straight_face);

		for ( int index = chosenWord.length(); index < 10; index++ ) {
			int randomLetter = randomGenerator.nextInt(26);
			jumbledWord += (alphabet[randomLetter] + "");
		}
		
		for ( int jumbledIndex = 0; jumbledIndex < 10; jumbledIndex++ ) {
			tenLetters[jumbledIndex] = jumbledWord.charAt(jumbledIndex) + "";
		}
		
		for ( int i = 0; i < 10; i++ ) {
			int randomIndex = randomGenerator.nextInt(10);
			String temp = tenLetters[i];
			tenLetters[i] = tenLetters[randomIndex];
			tenLetters[randomIndex] = temp;
		}
		
		  // Remove any answer buttons from the buttonTableLayout
		  for (int row = 0; row < buttonTableLayout.getChildCount(); row++)
		      ((TableRow) buttonTableLayout.getChildAt(row)).removeAllViews();
		   
		  // Get a reference to the LayoutInflater service so we can "inflate" new Buttons
		  LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		  for (int row = 0; row < guessRows; row++) {
		      // Get a reference to the next tableRow
		      TableRow currentTableRow = (TableRow) buttonTableLayout.getChildAt(row);

		      // Place buttons in currentTableRow
		      for (int col = 0; col < 5; col++) {
		          Button newGuessButton = (Button)inflater.inflate(R.layout.letter_buttons, null);
		          if (row == 0) {
		          newGuessButton.setText(tenLetters[col]);
		          }
		          if (row == 1) {
			          newGuessButton.setText(tenLetters[col + 5]);
			          }
		          // Attach listener to the Button and add it to the buttonTableLayout
		          newGuessButton.setOnClickListener(new guessButtonListener());
		          currentTableRow.addView(newGuessButton);
		      }
		  }
	}
	
	// Speaker button listener handles when the speaker button is pressed. When pressed the current word
	// chosen would be spoken
	private class speakerButtonListener implements OnClickListener {
		public void onClick(View v) {
			speakWords(chosenWord);
		}
	};
	
	// Undo button listener handles when the undo button is pressed. When pressed what ever is in the 
	// userWordInputEditText is cleared
	private class undoButtonListener implements OnClickListener {
		public void onClick(View v) {
			undo();
		}
	}
	
	// Submit button listsener handles when the submit button is pressed. This button is used to check whether
	// or not the user's input is correct or incorrect
	private class submitButtonListener implements OnClickListener {
		public void onClick(View v) {
			submit();
		}
	}
	
	// If one of the letter buttons is clicked that letter would be added to the userWordInputEditText
	// and the button pressed cannot be selected again
	private class guessButtonListener implements OnClickListener {
		public void onClick(View v) {
			addLetter( (Button) v);
			v.setEnabled(false);
		}	
	};	
	
	// This method is used to convert the text to speech and then the text is spoken
	private void speakWords(String speech) {
		myTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
	}
	
	// If request code is true then the text to speech is ready to use. If it is not true then anything missing
	// is loaded and installed.
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == MY_DATA_CHECK_CODE) {
	        if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
	            myTTS = new TextToSpeech(this, this);
	        }
	        else {
	            Intent installTTSIntent = new Intent();
	            installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
	            startActivity(installTTSIntent);
	        }
	    }
	}
	
	// If text to speech is working then the language will be set to US (English)
	public void onInit(int initStatus) {
	    if (initStatus == TextToSpeech.SUCCESS) {
	        myTTS.setLanguage(Locale.US);
	    }
	}
	
	// Used to set difficulty of app
	public static void setDifficulty(String choice) {
		difficulty = choice;
	}
	
	// Used to determine which difficulty array to randomize a word from
	public String getChosenWord() {
		if (difficulty.equals("3to4")) {
			return threeAndFourLetterWords[randomGenerator.nextInt(30)];
		}
		if (difficulty.equals("5to6")) {
			return fiveAndSixLetterWords[randomGenerator.nextInt(30)];
		}
		if (difficulty.equals("7to8")) {
			return sevenAndEightLetterWords[randomGenerator.nextInt(30)];
		}
		return "";
	}
	
	// Adds the letter the button represents to userSpelled
	private void addLetter(Button guessButton) {
		userWordInputEditText.setText(userSpelled += guessButton.getText().toString());
	}
	
	// clears userSpelled and userWordInputEditText
	private void undo() {
		userSpelled = "";
		userWordInputEditText.setText(userSpelled);
		
        // Enable all buttons so that they can be selected once again
        for (int row = 0; row < buttonTableLayout.getChildCount(); row++) {
            // Get a reference to the next tableRow
            TableRow currentTableRow = (TableRow) buttonTableLayout.getChildAt(row);

            // Enable buttons in currentTableRow
            for (int col = 0; col < currentTableRow.getChildCount(); col++) {
                currentTableRow.getChildAt(col).setEnabled(true);
            }
        }
	}
	
	// Used to determine whether or not the user's input is correct or incorrect. If the user is correct
	// and the question number is not 10 then the next question would be called. If it is 10 an alert dialog
	// will popup telling the user how well they did and their score. A happy face and correct message would
	// be played/shown when the user is correct and a sad face and incorrect message when the user is wrong.
	private void submit() {
		// Correct!
		if (userSpelled.equals(chosenWord)) {
			
			correctIncorrectImageView.setImageResource(R.drawable.happy_face);
			
			soundPool.play(right_sound_id, 1.0f, 1.0f, 1, 0, 1.0f); // correct sound effect
			wordNumber++;
			totalPoints += points;
			
	        // Display the correct answer in green.
	        answerTextView.setText("Correct!");
	        answerTextView.setTextColor(getResources().getColor(R.color.correct_answer));
	        
		
	        // Disable all buttons so that we don't register any false answers
	        for (int row = 0; row < buttonTableLayout.getChildCount(); row++) {
	        	// Get a reference to the next tableRow
	        	TableRow currentTableRow = (TableRow) buttonTableLayout.getChildAt(row);
	        	
	        	// Disable buttons in currentTableRow
	        	for (int col = 0; col < currentTableRow.getChildCount(); col++) {
	        		currentTableRow.getChildAt(col).setEnabled(false);
	        	}
	        }
        
	        // If they just got the last question correct
	        if (wordNumber > 10) {
	        	// Create an Alert Dialog Builder
	        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        	builder.setTitle(R.string.play_again);
	        	if ( totalPoints >= 80 && totalPoints <= 100 ) {
	        		builder.setMessage("You got " + totalPoints + "/100\nGreat Job! Keep " +
	        				"up the good work!");
	        	}
	        	if ( totalPoints >= 50 && totalPoints < 80 ) {
	        		builder.setMessage("You got " + totalPoints + "/100\nNice Job! You're " +
	        				"working your way up to a good score!");
	        	}
	        	if ( totalPoints < 50 ) {
	        		builder.setMessage("You got " + totalPoints + "/100\nMaybe you should try " +
	        				"harder next time.");
	        	}
	        	builder.setCancelable(false);
	        	
	        	// Add "Reset Quiz" Button
	        	builder.setPositiveButton(R.string.play_again,
	        			new DialogInterface.OnClickListener() {
	        		        public void onClick(DialogInterface dialog, int id) {
	        		            	resetQuiz();
	                		}
	                	}
	        	);
	        	
	        	// Create AlertDialog from the Builder
	        	AlertDialog resetDialog = builder.create();
	        	resetDialog.show();
	        }
	        // Otherwise, user is correct but this was not the last question
	        else {
	        	// Load the next logo after a 1 second delay
	        	Handler handler = new Handler();
	        	handler.postDelayed(new Runnable() {
	        		@Override
	        		public void run() {
	        			nextQuestion();
	        		}
	        	}, 1000);
	        }
		}
	// User Is NOT Correct!
	else {
		
		// Set image to sad face
	    correctIncorrectImageView.setImageResource(R.drawable.sad_face);
	    // Play the sound effect for the wrong answer
	    soundPool.play(wrong_sound_id, 1.0f, 1.0f, 1, 0, 1.0f);
	    
	    // Makes sure that the score awarded does not go below 0
	    if ( points > 0 ) {
        	points--;
        }
	    
	    // Display the "Incorrect Answer!" in red.
		answerTextView.setText("Incorrect!");
        answerTextView.setTextColor(getResources().getColor(R.color.wrong_answer));
        pointsWorthTextView.setText(getResources().getString(R.string.points_worth) + " " + points);
        userSpelled = "";
        userWordInputEditText.setText("");
        
        // Re-enables all buttons so that the word can be spelled again
        for (int row = 0; row < buttonTableLayout.getChildCount(); row++) {
        	// Get a reference to the next tableRow
        	TableRow currentTableRow = (TableRow) buttonTableLayout.getChildAt(row);
        	
        	// Enable buttons in currentTableRow
        	for (int col = 0; col < currentTableRow.getChildCount(); col++) {
        		currentTableRow.getChildAt(col).setEnabled(true);
        	}
        }
        
	} 
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	
}

