package com.mfusion.ninjaplayer.FragmentClass;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mfusion.commons.controllers.AbstractFragment;
import com.mfusion.ninjaplayer.R;

public class AboutFragment extends AbstractFragment {
	TextView Company,Function;
	Button more;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_about, container, false);

		Company = (TextView)rootView.findViewById(R.id.Company);//company textview
		Function= (TextView)rootView.findViewById(R.id.Function);//app function
		more= (Button)rootView.findViewById(R.id.btnMore);//click for more information



		more.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Company.setText("Founded in 2001, M-Fusion develops proprietary digital signage software.\n" +
						"\n" +
						"Through the years, M-Fusion, went on to provide multimedia solutions on various digital medium. These solutions included kiosks, digital displaysand in-house tvs. Projects after projects we became domain experts in this field and streamlined some of our key solutions into digital signage products.");//set company information

				Function.setText("Our app can enable users to choose template, create Playback, assign a schedule, configure their own profiles...");//set function
			}
		});

		return rootView;//return statement
	}

	@Override
	public Boolean saveModification() {
		return true;
	}

	@Override
	public void cancelSaveModification() {

	}

	@Override
	public void showFragment() {

	}

	@Override
	public void hideFragment() {

	}
}
