package com.login;


import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ecml.R;




public class ForgottenPasswordActivity extends Login {

	private EditText usernameText;
    private EditText answerText;
    private TextView question ;
    private Button OK;
    private Spinner spinner;
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    
        setContentView(R.layout.forgotten_password_screen);
        setTitle("Forgot your password? ");
        usernameText = (EditText) findViewById(R.id.userName);
        answerText = (EditText) findViewById(R.id.answer);
        OK  = (Button)findViewById(R.id.OK);
         spinner = (Spinner) findViewById(R.id.question_spinner);
        // Create an ArrayAdapter using the string array and a default spinner LAYOUT
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
        R.array.questions_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when THE LIST of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // APPLY the adapter to the spinner
        spinner.setAdapter(adapter);
         
        OK.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			  String q = usersdb.getQuestion(usernameText.getText().toString());
			  System.out.println("your question is "+ q);
			  if (spinner.getSelectedItem().toString().equals(q)){  // the right question is chosen
				  if(answerText.getText().toString().equals(usersdb.getAnswer(usernameText.getText().toString()))){
					  // TO DO  give a new password or ...
					  System.out.println("your password is " + usersdb.getPassword(usernameText.getText().toString()));
				  }
				  else{ // the answer is wrong
					  Toast.makeText(getApplicationContext(),R.string.wrong_answer, Toast.LENGTH_LONG).show();
				  } 
			  }
			  else{Toast.makeText(getApplicationContext(),R.string.wrong_answer, Toast.LENGTH_LONG).show();}
			}
		});
      
      
     //   question.setText(q);
	}

}
