package com.zrp.latte.ec.main.personal.p_personal.profile;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.latte.latte_ec.R;
import com.example.latte.latte_ec.R2;
import com.yalantis.ucrop.UCrop;
import com.zrp.latte.app.Latte;
import com.zrp.latte.delegates.bottom.BottomItemDelegate;
import com.zrp.latte.ui.camera.CameraImageBean;
import com.zrp.latte.ui.camera.CameraRequestCodes;
import com.zrp.latte.ui.camera.LatteCamera;
import com.zrp.latte.ui.datepicker.DatePickerDialog;
import com.zrp.latte.ui.datepicker.OnConfirmeListener;
import com.zrp.latte.ui.camera.callback.CallBackManager;
import com.zrp.latte.ui.camera.callback.CallBackType;
import com.zrp.latte.ui.camera.callback.IGlobalCallback;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import pub.devrel.easypermissions.EasyPermissions;


public class ProfileDelegate extends BottomItemDelegate implements RadioButton.OnCheckedChangeListener {

	private static final String ARGS_PROFILE_NAME = "ARGS_PROFILE_NAME";
	private static final String ARGS_PROFILE_PHONE = "ARGS_PROFILE_PHONE";
	private static final String ARGS_PROFILE_SEX = "ARGS_PROFILE_SEX";
	private static final String ARGS_PROFILE_BIRTH = "ARGS_PROFILE_BIRTH";
	private static final String ARGS_PROFILE_AVATAR = "ARGS_PROFILE_AVATAR";

	@BindView(R2.id.iv_profile_photo)
	CircleImageView mIvAvatar;
	@BindView(R2.id.tv_profile_nickname)
	TextView mTvNickname;
	@BindView(R2.id.tv_profile_gender)
	TextView mTvGender;
	@BindView(R2.id.tv_profile_birth)
	TextView mTvBirth;
	@BindView(R2.id.tv_profile_phone)
	TextView mTvPhone;


	private AlertDialog mGenderDialog;
	private String gender;
	private String male;
	private String female;

	@Override
	public Object setLayout() {
		return R.layout.delegate_profile;
	}

	public static ProfileDelegate create(int avatar, String nickName, String sex, String birth, String phone) {
		final Bundle args = new Bundle();
		args.putString(ARGS_PROFILE_NAME, nickName);
		args.putInt(ARGS_PROFILE_AVATAR, avatar);
		args.putString(ARGS_PROFILE_SEX, sex);
		args.putString(ARGS_PROFILE_BIRTH, birth);
		args.putString(ARGS_PROFILE_PHONE, phone);
		final ProfileDelegate delegate = new ProfileDelegate();
		delegate.setArguments(args);
		return delegate;
	}

	@Override
	public void onBindView(@Nullable Bundle savedInstanceState, @NonNull View view) {
		super.onBindView(savedInstanceState, view);
		male = this.getString(R.string.male);
		female = this.getString(R.string.female);
		mGenderDialog = new AlertDialog.Builder(getContext()).create();
		final Bundle profile = getArguments();
		//????????????
		final int avatar = profile.getInt(ARGS_PROFILE_AVATAR);
		mIvAvatar.setImageResource(avatar);
		//??????
		final String name = profile.getString(ARGS_PROFILE_NAME);
		mTvNickname.setText(name);
		//??????
		gender = profile.getString(ARGS_PROFILE_SEX);
		mTvGender.setText(gender);
		//??????
		final String birth = profile.getString(ARGS_PROFILE_BIRTH);
		mTvBirth.setText(birth);
		//????????????
		final String phone = profile.getString(ARGS_PROFILE_PHONE);
		String phoneNumber = phone.substring(0, 3) + "****" + phone.substring(7, phone.length());
		mTvPhone.setText(phoneNumber);
	}


	@OnClick(R2.id.iv_profile_backarrow)
	public void onViewClickedReturn(View view) {
		getSupportDelegate().pop();
	}

	@OnClick(R2.id.tv_profile_gender)
	public void onViewClickedGender(View view) {
		mGenderDialog.show();
		final Window window = mGenderDialog.getWindow();
		if (window != null) {
			window.setContentView(R.layout.dialog_profile_sex);
			window.setGravity(Gravity.BOTTOM);

			if (gender.equals(male)) {
				((RadioButton) window.findViewById(R.id.btn_dialog_profile_male)).setChecked(true);
			} else {
				((RadioButton) window.findViewById(R.id.btn_dialog_profile_female)).setChecked(true);
			}
			//??????????????????
			window.setWindowAnimations(R.style.anim_panel_up_from_bottom);
			window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			//????????????
			final WindowManager.LayoutParams params = window.getAttributes();
			params.width = WindowManager.LayoutParams.MATCH_PARENT;
			//FLAG_DIM_BEHIND: ???????????????????????????  FLAG_BLUR_BEHIND: ?????????????????????????????????
			params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
			window.setAttributes(params);
			((RadioButton) window.findViewById(R.id.btn_dialog_profile_male)).setOnCheckedChangeListener(this);
			((RadioButton) window.findViewById(R.id.btn_dialog_profile_female)).setOnCheckedChangeListener(this);

		}
	}


	@OnClick(R2.id.tv_profile_birth)
	public void onViewClickedBirth() {
		new DatePickerDialog("???????????????",getContext(), 1991, 2019, new OnConfirmeListener() {
			@Override
			public void result(String date) {
				mTvBirth.setText(date);
			}
		}).show();
	}


	@OnClick(R2.id.iv_profile_photo)
	public void onViewClickedChoosePhoto() {
		String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
		//EasyPermission???????????????????????????Manifest?????????
		if (EasyPermissions.hasPermissions(Latte.getApplication(), perms)) {
			CallBackManager.getInstance().addCallback(CallBackType.ON_CROP, new IGlobalCallback() {
				@Override
				public void executeCallBack(Object uri) {
					//??????UI
					Glide.with(getContext())
							.load(uri)
							.into(mIvAvatar);
				}
			});
			LatteCamera.start(ProfileDelegate.this);
		} else {
			EasyPermissions.requestPermissions(this, "?????????????????????", 1, perms);
		}
	}


	@OnClick(R2.id.tv_prpfile_edit_save)
	public void onViewClickedSave() {
		Toast.makeText(Latte.getApplication(), "?????????????????????", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case CameraRequestCodes.TAKE_PHOTO:
					final Uri resultUri = CameraImageBean.getInstance().getPath();
					UCrop.of(resultUri, resultUri)
							.withMaxResultSize(400, 400)
							.start(getContext(), this);
					break;
				case CameraRequestCodes.PICK_PHOTO:
					if (data != null) {
						final Uri pickPath = data.getData();
						//????????????????????????????????????????????????????????????
						final String pickCropPath = LatteCamera.createCropFile().getPath();
						UCrop.of(pickPath, Uri.parse(pickCropPath))
								.withMaxResultSize(400, 400)
								.start(getContext(), this);
					}
					break;
				case CameraRequestCodes.CROP_PHOTO:
					final Uri cropUri = UCrop.getOutput(data);
					//????????????????????????????????????
					@SuppressWarnings("unchecked") final IGlobalCallback<Uri> callback = CallBackManager
							.getInstance()
							.getCallback(CallBackType.ON_CROP);
					if (callback != null) {
						callback.executeCallBack(cropUri);
					}
					break;
				case CameraRequestCodes.CROP_ERROR:
					Toast.makeText(getContext(), "????????????", Toast.LENGTH_SHORT).show();
					break;
				default:
					break;

			}
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

		if (buttonView.getId() == R.id.btn_dialog_profile_male && isChecked) {
			mTvGender.setText(male);
			gender = male;
		} else if(buttonView.getId() == R.id.btn_dialog_profile_male && isChecked){
			mTvGender.setText(female);
			gender = female;
		}
		mGenderDialog.dismiss();
	}
}
