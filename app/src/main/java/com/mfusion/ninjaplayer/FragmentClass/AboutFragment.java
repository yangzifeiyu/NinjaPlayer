package com.mfusion.ninjaplayer.FragmentClass;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mfusion.ninjaplayer.R;

public class AboutFragment extends Fragment {
	TextView Company,Function;
	Button more;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_about, container, false);

		Company = (TextView)rootView.findViewById(R.id.Company);
		Function= (TextView)rootView.findViewById(R.id.Function);
		more= (Button)rootView.findViewById(R.id.btnMore);



		more.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Company.setText("Founded in 2001, M-Fusion develops proprietary digital signage software.\n" +
						"\n" +
						"Through the years, M-Fusion, went on to provide multimedia solutions on various digital medium. These solutions included kiosks, digital displaysand in-house tvs. Projects after projects we became domain experts in this field and streamlined some of our key solutions into digital signage products.");

				Function.setText("Our app can enable users to choose template, create Playback, assign a schedule, configure their own profiles...");
			}
		});

		return rootView;
	}

}
