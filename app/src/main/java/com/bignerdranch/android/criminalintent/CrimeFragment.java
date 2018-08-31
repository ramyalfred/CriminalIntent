package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.LayoutInflaterCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

/**
 * Created by Ramy on 7/24/2018.
 */

public class CrimeFragment extends Fragment {
    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private Button mDeleteButton;
    private CheckBox mSolvedCheckBox;
    private Button mSendReportButton;
    private Button mChooseSuspect;
    private Button mCallSuspect;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private File mPhotoFile;
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_CONTACT = 2;
    private static final int REQUEST_PHOTO = 3;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID) this.getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);

        //Stashing the location of the photo file
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_crime, container,false);
        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mDateButton = (Button) v.findViewById(R.id.crime_date);
        mTimeButton = (Button) v.findViewById(R.id.crime_time);
        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mDeleteButton = (Button) v.findViewById(R.id.delete_crime);
        mSendReportButton = (Button) v.findViewById(R.id.report_crime);
        mChooseSuspect = (Button) v.findViewById(R.id.pick_crime_suspect);
        mCallSuspect = (Button) v.findViewById(R.id.call_crime_suspect);
        mPhotoButton = (ImageButton) v.findViewById(R.id.crime_camera);
        mPhotoView = (ImageView) v.findViewById(R.id.crime_photo);

        //Creating Intent for launching contacts app
        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);

        //Creating intent for launching camera
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //Boolean for allowing take photo button or not depending on availability of camera and storage
        boolean canTakePhoto = mPhotoFile != null && captureImage.resolveActivity(getActivity().getPackageManager()) != null;
        mPhotoButton.setEnabled(canTakePhoto);

        mTitleField.setText(mCrime.getTitle());
        mSolvedCheckBox.setChecked(mCrime.isSolved());

        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        updateDate(mCrime.getDate());

        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment mDatePickerFragment = DatePickerFragment.newInstance(mCrime.getDate());

                //Set CrimeFragment as the target fragment of the DatePickerFragment before showing it
                mDatePickerFragment.setTargetFragment(CrimeFragment.this,REQUEST_DATE);
                mDatePickerFragment.show(getFragmentManager(),DIALOG_DATE);
            }
        });
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerFragment mTimePickerFragment = TimePickerFragment.newInstance(mCrime.getDate());

                //Set target fragment and show time picker
                mTimePickerFragment.setTargetFragment(CrimeFragment.this,REQUEST_TIME);
                mTimePickerFragment.show(getFragmentManager(),DIALOG_TIME);
            }
        });

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CrimeLab.get(getActivity()).deleteCrime(mCrime);
                getActivity().finish();
            }
        });

        mSendReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareCompat.IntentBuilder intentBuilder = ShareCompat.IntentBuilder.from(getActivity());
                intentBuilder.setType("text/plain");
                intentBuilder.setText(getCrimeReport());
                intentBuilder.setSubject(getString(R.string.crime_report_subject));
                intentBuilder.setChooserTitle(R.string.send_report);
                Intent intent = intentBuilder.getIntent();
                startActivity(intent);
            }
        });

        mChooseSuspect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact,REQUEST_CONTACT);
            }
        });

        mCallSuspect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Query the Phone table for the contact with the display name matching the suspect name
                String[] projection = new String[] {ContactsContract.CommonDataKinds.Phone.NUMBER};
                String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " = ?";
                String[] selectionArgument = new String[] {mCrime.getSuspect()};

                Cursor cursor1 = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,projection,selection,selectionArgument,null);

                try{
                    if (cursor1.getCount() != 0){
                        cursor1.moveToFirst();
                        Uri number = Uri.parse("tel:"+cursor1.getString(0));

                        //Dialing the number of the suspect
                        startActivity(new Intent(Intent.ACTION_DIAL,number));
                    }
                }finally {
                    cursor1.close();
                }
            }
        });

        if(canTakePhoto){
            Uri uri = FileProvider.getUriForFile(getContext(),getContext().getApplicationContext().getPackageName() + ".provider",mPhotoFile);
            //captureImage.setDataAndType(uri,"image/jpeg");
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT,uri);
            captureImage.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{
                    startActivityForResult(captureImage, REQUEST_PHOTO);
                }catch(ActivityNotFoundException e){
                    Toast.makeText(getActivity(),e.getMessage().toString(),Toast.LENGTH_LONG).show();
                }


            }
        });

        //If there is no suspect set, disable call button
        if(mCrime.getSuspect() != null){
            mChooseSuspect.setText(mCrime.getSuspect());
        }else{
            mCallSuspect.setEnabled(false);
        }

        //Update crime photo with te photo taken
        updatePhotoView();

        return v;
    }


    @Override
    public void onActivityResult(int requestCode,int resultCode ,Intent intent){
        if(resultCode != Activity.RESULT_OK){
            return;
        }

        if(requestCode == REQUEST_DATE){
            Date date = (Date) intent.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDate(mCrime.getDate());
        }else if(requestCode == REQUEST_TIME){
            Date date = (Date) intent.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mCrime.setTime(date);
            updateDate(mCrime.getDate());
        }else if(requestCode == REQUEST_CONTACT && intent != null){
            Uri contactUri = intent.getData();
            String[] queryFields = new String[] {ContactsContract.Contacts.DISPLAY_NAME};
            Cursor c = getActivity().getContentResolver().query(contactUri,queryFields,null,null,null);

            try{
                if(c.getCount() == 0){
                    return;
                }
                c.moveToFirst();
                String suspect = c.getString(0);
                mCrime.setSuspect(suspect);
                mChooseSuspect.setText(suspect);
            }finally {
                c.close();
            }
        }else if(requestCode == REQUEST_PHOTO){
            updatePhotoView();
        }
    }

    private void updateDate(Date date) {
        mDateButton.setText(DateFormat.getDateInstance().format(date));

        //Set time on the Time Button
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        mTimeButton.setText(DateUtils.formatDateTime(getActivity(), calendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME));
    }

    public static CrimeFragment newInstance(UUID crimeId){
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID,crimeId);

        CrimeFragment crimeFragment = new CrimeFragment();
        crimeFragment.setArguments(args);
        return crimeFragment;
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    private String getCrimeReport(){
        String solvedString = null;
        if(mCrime.isSolved()){
            solvedString = getString(R.string.crime_report_solved);
        }else{
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = android.text.format.DateFormat.format(dateFormat, mCrime.getDate()).toString();

        String suspect = mCrime.getSuspect();
        if (suspect == null){
            suspect = getString(R.string.crime_report_no_suspect);
        }else{
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        String report = getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspect);
        return report;
    }

    private void updatePhotoView(){
        if(mPhotoFile == null || !mPhotoFile.exists()){
            mPhotoView.setImageDrawable(null);
        }else{
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(),getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }
}
