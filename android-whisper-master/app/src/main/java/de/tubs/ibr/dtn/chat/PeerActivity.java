/*
 * MainActivity.java
 * 
 * Copyright (C) 2011 IBR, TU Braunschweig
 *
 * Written-by: Johannes Morgenroth <morgenroth@ibr.cs.tu-bs.de>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package de.tubs.ibr.dtn.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class PeerActivity extends FragmentActivity {

	@SuppressWarnings("unused")
	private final String TAG = "PeerActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		Intent start_intent = new Intent(PeerActivity.this, Latlong.class);
		startService(start_intent);
		Log.d(TAG,"In main");
		Button showLocationButton = (Button)findViewById(R.id.button);
		showLocationButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                Intent showLoc = new Intent(PeerActivity.this,MapsActivity.class);
                startActivity	(showLoc);
			}
		});


	}
}
