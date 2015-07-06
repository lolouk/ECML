package com.sideActivities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ecml.R;

public class YoutubeActivity extends BaseActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.youtube);

		// Go on Youtube button
		TextView youtube = (TextView) findViewById(R.id.youtubesearch);
		youtube.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent myWebLink = new Intent(android.content.Intent.ACTION_VIEW);
				myWebLink.setData(Uri.parse("http://www.youtube.com"));
				startActivity(myWebLink);
			}

		});


		// Upload on Youtube button
		TextView upload = (TextView) findViewById(R.id.youtubeshare);
		upload.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent myWebLink = new Intent(android.content.Intent.ACTION_VIEW);
				myWebLink.setData(Uri.parse("http://www.youtube.com/upload"));
				startActivity(myWebLink);
			}

		});

	}

}
