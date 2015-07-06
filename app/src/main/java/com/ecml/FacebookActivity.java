package com.ecml;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.sideActivities.BaseActivity;

public class FacebookActivity extends BaseActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.facebook);
		
		//Go on Gmail button
		ImageView gmail = (ImageView) findViewById(R.id.gmail);
		gmail.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent myWebLink = new Intent(
						android.content.Intent.ACTION_VIEW);
				myWebLink.setData(Uri.parse("http://www.gmail.com"));
				startActivity(myWebLink);

			}
		});
		
		

		// Upload on Facebook button
		ImageView facebook = (ImageView) findViewById(R.id.facebook);
		facebook.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent myWebLink = new Intent(
						android.content.Intent.ACTION_VIEW);
				myWebLink.setData(Uri.parse("http://www.facebook.com"));
				startActivity(myWebLink);

			}
		});

	}
}
