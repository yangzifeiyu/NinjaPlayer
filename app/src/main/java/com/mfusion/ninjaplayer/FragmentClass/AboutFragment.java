package com.mfusion.ninjaplayer.FragmentClass;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mfusion.commons.controllers.AbstractFragment;
import com.mfusion.commons.data.DALSettings;
import com.mfusion.commons.entity.exception.ExpiryLicenseException;
import com.mfusion.commons.entity.exception.IllegalLicenseException;
import com.mfusion.commons.entity.license.LicenseEntity;
import com.mfusion.commons.tools.AlertDialogHelper;
import com.mfusion.commons.tools.CallbackBundle;
import com.mfusion.commons.tools.DateConverter;
import com.mfusion.commons.tools.ImageHelper;
import com.mfusion.commons.tools.InternalKeyWords;
import com.mfusion.commons.tools.LicenseDecoder;
import com.mfusion.commons.tools.LicenseStatus;
import com.mfusion.commons.tools.OperateCallbackBundle;
import com.mfusion.commons.view.ImageTextHorizontalView;
import com.mfusion.ninjaplayer.R;

public class AboutFragment extends AbstractFragment {

	LinearLayout layout_verify,layout_display;
	ImageView img_qr;
	TextView tv_license,tv_deviceId;
	EditText et_license;

	ImageTextHorizontalView btn_license_verify,btn_license_cancel,btn_license_update;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		if(rootView!=null)
			return rootView;

		rootView = inflater.inflate(R.layout.fragment_about, container, false);

		img_qr=(ImageView)rootView.findViewById(R.id.license_qr_img);
		img_qr.setImageBitmap(ImageHelper.getBitmap(InternalKeyWords.DeviceQR_Path,-1));

		tv_license=(TextView)  rootView.findViewById(R.id.license_no);
		et_license=(EditText)  rootView.findViewById(R.id.license_str);

		tv_deviceId=(TextView)  rootView.findViewById(R.id.license_deviceId);
		tv_deviceId.setText(InternalKeyWords.DeviceId);

		layout_verify=(LinearLayout) rootView.findViewById(R.id.license_verify_layout);
		layout_display=(LinearLayout) rootView.findViewById(R.id.license_display_layout);

		btn_license_update=(ImageTextHorizontalView)rootView.findViewById(R.id.license_update);
		btn_license_update.setImage(R.drawable.mf_edit);
		btn_license_update.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				initLicenseStatus(true);
			}
		});

		btn_license_cancel=(ImageTextHorizontalView)rootView.findViewById(R.id.license_cancel);
		btn_license_cancel.setImage(R.drawable.mf_cancel);
		btn_license_cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				initLicenseStatus(false);
			}
		});

		btn_license_verify=(ImageTextHorizontalView)rootView.findViewById(R.id.license_verify);
		btn_license_verify.setImage(R.drawable.mf_apply);
		btn_license_verify.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					Boolean result =DALSettings.getInstance().saveLicenseInfo(et_license.getText().toString());

					if(result) {

						initLicenseStatus(false);

						AlertDialogHelper.showInformationDialog(rootView.getContext(), "Information", "License import successfully .", null);
					}else
						AlertDialogHelper.showWarningDialog(rootView.getContext(), "Information", "License import failed.", null);
				} catch (Exception e) {
					e.printStackTrace();
					AlertDialogHelper.showWarningDialog(rootView.getContext(), "Information", e.getMessage(), null);
				}
			}
		});

		this.initLicenseStatus(!LicenseDecoder.checkLicenseValidity());

		return rootView;//return statement
	}

	private void initLicenseStatus(Boolean editLicense){
		if(!editLicense) {
			tv_license.setText(LicenseDecoder.license==null||LicenseDecoder.license.validity!=LicenseStatus.valid?"N/A":String.format("%s ( %s - %s )",LicenseDecoder.license.license,DateConverter.convertToDisplayStr(LicenseDecoder.license.startDate),DateConverter.convertToDisplayStr(LicenseDecoder.license.validDate,"N/A")));
			layout_verify.setVisibility(View.GONE);
			layout_display.setVisibility(View.VISIBLE);
		}else{
			layout_verify.setVisibility(View.VISIBLE);
			layout_display.setVisibility(View.GONE);
			et_license.setText("");
		}
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
