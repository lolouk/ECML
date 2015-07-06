package com.androidim;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.androidim.interfaces.IAppManager;
import com.androidim.services.IMService;
import com.ecml.R;


//package com.androidim;




import java.util.Calendar;
import java.util.GregorianCalendar;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;


public class Profil extends Activity {
	
	
	private static final String FILL_IN_PROFIL_SUCCESSFUL = "1";
	private static final String FILL_IN_PROFIL_FAILED = "0";
	private EditText usname ;
	private EditText firstName ;
	private EditText lastName ;
	private EditText email ;
	private DatePicker birthDate ;
	private RadioGroup gender ;
	private Spinner sp ;
	private  String ins;
	

	java.sql.Date birth = null;
	private Handler handler = new Handler();
	private IAppManager imService = new IMService();
	   /** Called when the activity is first created. */	
		@Override
    public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);  
	        setContentView(R.layout.fill_in_profil_screen);
	        setTitle("Your Profil");
	        Spinner spinner = (Spinner) findViewById(R.id.instruments_spinner);
	        // Create an ArrayAdapter using the string array and a default spinner LAYOUT
	        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
	        R.array.instruments_array, android.R.layout.simple_spinner_item);
	        // Specify the layout to use when THE LIST of choices appears
	        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        // APPLY the adapter to the spinner
	        spinner.setAdapter(adapter);
	    	final String username = this.getIntent().getExtras().getString("username");
	    	final String password = getIntent().getExtras().getString("password");
	        usname = (EditText) findViewById(R.id.userName);
	        firstName = (EditText) findViewById(R.id.firstname);
	        lastName = (EditText) findViewById(R.id.lastName);
	        email = (EditText) findViewById(R.id.email);
	        birthDate = (DatePicker) findViewById(R.id.birthDate);
	        gender =(RadioGroup) findViewById(R.id.gender);
	        sp = (Spinner) findViewById(R.id.instruments_spinner);
	        // final String username = getIntent().getExtras().getString("username");
	        Button finish = (Button) findViewById(R.id.finish);
	        // write the username automatically
	        usname.setText(username);

	        finish.setOnClickListener(new OnClickListener(){
	        	int Gender ;
	        	@Override
	        	public void onClick(View v) {
			
	        		if (usname.length() > 0 &&	firstName.length() > 0 && lastName.length() > 0 && email.length() > 0)
						{
						if(email.getText().toString().contains("@")){
						        // extract the gender 
							int id = gender.getCheckedRadioButtonId();
							if(id==R.id.radio1){ Gender = 1;}   // female
							else if(id==R.id.radio2){Gender = 2 ;}  // male
							// extract the instrument
							 ins =  (String) sp.getSelectedItem();
							// Date of birth 
							int year = birthDate.getYear();
							int month = birthDate.getMonth();
							int day = birthDate.getDayOfMonth();
							
							Calendar cal = GregorianCalendar.getInstance();
							cal.set( year, month, day);
							birth = new java.sql.Date (cal.getTime().getTime());
							
							
							Thread thread = new Thread(){
								String result = new String();
								@Override
								public void run() {
									result = imService.fillInProfil(username ,password, firstName.getText().toString(), lastName.getText().toString(), Gender, birth,ins);

								    handler.post(new Runnable(){

										public void run() {
											System.out.println("resultat est "+ result);
                                            if (FILL_IN_PROFIL_SUCCESSFUL.equals(result)) {
                                            	System.out.println("succes");
                                               Toast.makeText(getApplicationContext(),R.string.ProfilCompleted, Toast.LENGTH_LONG).show();
                                               // showDialog(SIGN_UP_SUCCESSFULL);
                                               Intent i = new Intent(getApplicationContext(), FriendList.class);
                                               startActivity(i);
                                            }
                                            else{System.out.println("erreur");}
							
							
										}
									});
								}
							}; 
						thread.start();
					}
					else{Toast.makeText(getApplicationContext(),R.string.emailNotValid, Toast.LENGTH_LONG).show();}
					}
				else{Toast.makeText(getApplicationContext(),R.string.signup_fill_all_fields, Toast.LENGTH_LONG).show();}
			}
        });
	    
	        }
		
		
		

}
