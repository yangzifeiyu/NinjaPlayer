package com.mfusion.ninjaplayer.FragmentClass;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mfusion.commons.controllers.AbstractFragment;
import com.mfusion.commons.tools.CallbackBundle;
import com.mfusion.commons.tools.OperateCallbackBundle;
import com.mfusion.ninjaplayer.R;

public class AboutFragment extends AbstractFragment {
	TextView Company,Function;
	Button more;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_about, container, false);

		return rootView;//return statement
	}

	@Override
	public void saveModification(OperateCallbackBundle callbackBundle) {

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
