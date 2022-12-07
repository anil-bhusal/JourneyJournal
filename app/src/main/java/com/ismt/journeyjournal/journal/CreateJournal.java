package com.ismt.journeyjournal.journal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.os.StrictMode;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.drawable.BitmapDrawable;
import java.io.File;
import java.io.FileOutputStream;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.ismt.journeyjournal.JounrneyDatabase;
import com.ismt.journeyjournal.R;
import com.ismt.journeyjournal.views.Register;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import android.os.Environment;
import android.graphics.drawable.Drawable;
import android.graphics.Canvas;
import  java.io.OutputStream;

public class CreateJournal extends AppCompatActivity {

    private EditText inputNoteTitle, inputNote;
    private TextView textDateTime;
    private View viewSubtitleIndicator;
    private AppCompatTextView txtlocation;
    private LocationManager locationManager;
    private String colorSelection;
    private AppCompatImageView addLocation;
    private ImageView imageNote;
    private String  selectedImagePath;
    private Location location;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final int SELECT_FILE = 2;
    private Journal alreadyAvailableJournal;
    private AlertDialog dialogDeleteJournal;
    private static final int REQUEST_CAMERA = 3;
    private TextView textlocation;
    String file_path = null;
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_journal);

        addLocation = findViewById(R.id.addLocation);
        textlocation = findViewById(R.id.txtlocation);


        //Initialize fused location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        ImageView imageBack = findViewById(R.id.imageBack);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CreateJournal.this, JournalDashboard.class));
            }
        });

        inputNoteTitle = findViewById(R.id.inputNoteTitle);
        txtlocation = findViewById(R.id.txtlocation);
        inputNote = findViewById(R.id.inputNote);
        textDateTime = findViewById(R.id.textDateTime);
        viewSubtitleIndicator = findViewById(R.id.viewSubtitleIndicator);
        imageNote = findViewById(R.id.imageNote);
        textDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month= calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateJournal.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        textDateTime.setText("Date: "+(month+1) +"-"+ dayOfMonth+ "-" +year );
                    }
                }, year, month, day);
                datePickerDialog.show();

            }
        });

        /* To save image*/
        ImageView imageSave = findViewById(R.id.imageSave);

        imageSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNotes();
                Toast.makeText(CreateJournal.this, "Journal Saved", Toast.LENGTH_SHORT).show();
            }
        });
        colorSelection = "#FFFFFF";
        //electedImagePath = "";
        if(getIntent().getBooleanExtra("isVieworUpdate", false)){
            alreadyAvailableJournal = (Journal) getIntent().getSerializableExtra( "journal");
            setViewOrUpdateJournal();
        }

        findViewById(R.id.imageRemoveImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageNote.setImageBitmap(null);
                imageNote.setVisibility(View.GONE);
                findViewById(R.id.imageRemoveImage).setVisibility(View.GONE);
                selectedImagePath = "";
            }
        });
        drawableSelection();
        setSubtitleIndicatorColor();


        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(CreateJournal.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    ActivityCompat.requestPermissions(CreateJournal.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                }
            }
        });
    }
        /* To get location*/
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.
                PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.
                ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null) {

                    try {
                        Geocoder geocoder = new Geocoder(CreateJournal.this, Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(), 1
                        );

                        textlocation.setText(Html.fromHtml(
                                "<font color= #6200EE></font>" + addresses.get(0).getLocality()
                        ));
                        textlocation.setText(Html.fromHtml(
                                "<font color= #6200EE></font>" + addresses.get(0).getAddressLine(0)
                        ));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    private void saveNotes(){
        if (inputNoteTitle.getText().toString().trim().isEmpty()){
            inputNoteTitle.setError("");
            Toast.makeText(this, "Enter title", Toast.LENGTH_SHORT).show();
            return;
        }

        final Journal journal = new Journal();
        journal.setTitle(inputNoteTitle.getText().toString());
        journal.setNoteText(inputNote.getText().toString());
        journal.setDateTime(textDateTime.getText().toString());
        journal.setColor(colorSelection);
        journal.setImagePath(selectedImagePath);
        journal.setLocation(txtlocation.getText().toString());
        /*journal.setLat(location.getLatitude());
        journal.setLng(location.getLongitude());*/

        if(alreadyAvailableJournal !=null){
            journal.setId(alreadyAvailableJournal.getId());
        }

        @SuppressLint("StaticFieldLeak")
        class saveNotesTask extends AsyncTask<Void, Void, Void>{

            @Override
            protected  Void doInBackground(Void... voids){
                JounrneyDatabase.getInstance(getApplicationContext()).journalDao().insertJournal(journal);
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid){
                super.onPostExecute(aVoid);
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }

        }
        new saveNotesTask().execute();
    }


    private void drawableSelection(){
        final LinearLayout layoutGroup = findViewById(R.id.layoutGroup);
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(layoutGroup);
        layoutGroup.findViewById(R.id.textGroup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
        final ImageView imageColor = findViewById(R.id.imageColor);
        final ImageView imageColor2 = findViewById(R.id.imageColor2);
        final ImageView imageColor3 = findViewById(R.id.imageColor3);

        layoutGroup.findViewById(R.id.viewColor1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                colorSelection = "#FFFFFF";
                imageColor.setImageResource(R.drawable.ic_baseline_done_24);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });

        layoutGroup.findViewById(R.id.viewColor2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                colorSelection = "#8DFFEB3B";
                imageColor.setImageResource(0);
                imageColor2.setImageResource(R.drawable.ic_baseline_done_24);
                imageColor3.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });
        layoutGroup.findViewById(R.id.viewColor3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                colorSelection = "#B7FAEA";
                imageColor.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(R.drawable.ic_baseline_done_24);
                setSubtitleIndicatorColor();
            }
        });
        if(alreadyAvailableJournal != null && alreadyAvailableJournal.getColor().trim().isEmpty()){
            switch (alreadyAvailableJournal.getColor()){
                case "#8DFFEB3B":
                    layoutGroup.findViewById(R.id.viewColor2).performClick();
                    break;
                case "#B7FAEA":
                    layoutGroup.findViewById(R.id.viewColor3).performClick();
                    break;
            }
        }
        layoutGroup.findViewById(R.id.layoutImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            CreateJournal.this,
                            new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_STORAGE_PERMISSION
                    );
                } else {
                    selectImage();
                }
            }
        });

        if(alreadyAvailableJournal !=null){
            layoutGroup.findViewById(R.id.layoutDeleteJournal).setVisibility(View.VISIBLE);
            layoutGroup.findViewById(R.id.layoutDeleteJournal).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    showDeleteJournalDialog();

                }
            });
        }

        if (alreadyAvailableJournal != null){
             layoutGroup.findViewById(R.id.layoutShare).setVisibility(View.VISIBLE);
            layoutGroup.findViewById(R.id.layoutShare).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());
                    Drawable drawable = imageNote.getDrawable();
                    String text = inputNoteTitle.getText().toString();
                    String text2 = inputNote.getText().toString();
                    Bitmap bitmap = drawableToBitmap(drawable);//put here your image id
                    String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/LatestShare.png";
                    OutputStream out = null;
                    File file = new File(path);
                    try {
                        out = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                        out.flush();
                        out.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    path = file.getPath();
                    Uri bmpUri = Uri.parse("file://" + path);
                    Intent shareIntent = new Intent();
                    shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Journey Journal");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, text + "\n" + text2 );
                    shareIntent.setType("image/png");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                    shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(Intent.createChooser(shareIntent, "Share with"));
                }
            });
        }
    }
    public static Bitmap drawableToBitmap (Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    /* Update the Journal */
    private  void setViewOrUpdateJournal(){

        inputNoteTitle.setText(alreadyAvailableJournal.getTitle());
        inputNote.setText(alreadyAvailableJournal.getNoteText());
        textDateTime.setText(alreadyAvailableJournal.getDateTime());
        txtlocation.setText(alreadyAvailableJournal.getLocation());
        if(alreadyAvailableJournal.getImagePath() != null && !alreadyAvailableJournal.getImagePath().trim().isEmpty()){
            imageNote.setImageBitmap(BitmapFactory.decodeFile(alreadyAvailableJournal.getImagePath()));
            imageNote.setVisibility(View.VISIBLE);
            findViewById(R.id.imageRemoveImage).setVisibility(View.VISIBLE);
            selectedImagePath = alreadyAvailableJournal.getImagePath();

        }
    }
        /*ShowDeleteJournalDialog show the dialog box of the delete.*/
    private void  showDeleteJournalDialog(){
        if (dialogDeleteJournal == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateJournal.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_delete_journal,
                    (ViewGroup) findViewById(R.id.layoutDeleteJournalContainer)
            );
            builder.setView(view);
            dialogDeleteJournal = builder.create();
            if(dialogDeleteJournal.getWindow() != null){
                dialogDeleteJournal.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            view.findViewById(R.id.textDeleteJournal).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    @SuppressLint("StaticFieldLeak")
                    class DeleteJournalTask extends AsyncTask<Void, Void, Void>{

                        @Override
                        protected Void doInBackground(Void... voids) {
                            JounrneyDatabase.getInstance(getApplicationContext()).journalDao()
                                   .deleteJournal(alreadyAvailableJournal);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void avoid) {
                            super.onPostExecute(avoid);
                            Intent intent = new Intent();
                            intent.putExtra("isJournalDelete", true);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                    new  DeleteJournalTask().execute();
                }
            });
            view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogDeleteJournal.dismiss();
                }
            });
        }
        dialogDeleteJournal.show();
    }

    /*From Gallery we can select the image*/
    private void selectImage() {

        final CharSequence[] items = {"Camera", "Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateJournal.this);
        builder.setTitle("Add image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals("Camera")){
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent,REQUEST_CAMERA);
                }
                else if (items[i].equals("Gallery")){
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, SELECT_FILE);
                }
                else if (items[i].equals("Cancel")){
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
        } else if (requestCode == 3) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, 3);
        }
        else {
            Intent intent = new Intent(Intent.ACTION_PICK);
            startActivityForResult(intent, 2);
        }
        /*
        Uses Code
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }*/
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageNote.setImageBitmap(photo);
            imageNote.setVisibility(View.VISIBLE);
            findViewById(R.id.imageRemoveImage).setVisibility(View.VISIBLE);
            Uri tempUri = getImageUri(getApplicationContext(), photo);
            selectedImagePath = getPathFromUri(tempUri);
        }else if (requestCode == SELECT_FILE && resultCode == RESULT_OK){
            if (data != null){
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null){
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imageNote.setImageBitmap(bitmap);
                        imageNote.setVisibility(View.VISIBLE);
                        findViewById(R.id.imageRemoveImage).setVisibility(View.VISIBLE);
                        selectedImagePath = getPathFromUri(selectedImageUri);
                    }
                    catch (Exception exception){
                        Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

            }
        }
       /* if (resultCode == Activity.RESULT_OK){
            if (requestCode==REQUEST_CAMERA){
                Bundle bundle = data.getExtras();
                final Bitmap bmp = (Bitmap) bundle.get("data");
                imageNote.setImageBitmap(bmp);
            }
            else if (requestCode==SELECT_FILE){
                Uri selectedImageUri = data.getData();
                imageNote.setImageURI(selectedImageUri);
            }
        }*/
    }
    private String getPathFromUri(Uri contentUri){
        String filePath;
        Cursor cursor = getContentResolver()
                .query(contentUri, null, null, null, null);
        if (cursor == null){
            filePath = contentUri.getPath();
        }
        else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            filePath = cursor.getString(index);
            cursor.close();
        }
        return filePath;
    }

    private void setSubtitleIndicatorColor(){
        GradientDrawable gradientDrawable = (GradientDrawable) viewSubtitleIndicator.getBackground();
        gradientDrawable.setColor(Color.parseColor(colorSelection));
    }

    private void accessLocationService() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, (LocationListener) this);
    }
}