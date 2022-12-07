package com.ismt.journeyjournal.journal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.content.SharedPreferences;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import android.widget.EditText;
import android.text.TextWatcher;
import android.text.Editable;
import android.widget.LinearLayout;
import androidx.appcompat.app.AlertDialog;
import android.view.MenuItem;
import android.widget.PopupMenu;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.ismt.journeyjournal.JounrneyDatabase;
import com.ismt.journeyjournal.R;
import com.ismt.journeyjournal.SplashScreen;
import com.ismt.journeyjournal.adapters.JournalAdapters;
import com.ismt.journeyjournal.views.Calendar;
import com.ismt.journeyjournal.views.LocationMap;
import com.ismt.journeyjournal.views.Login;
import android.content.DialogInterface;
import java.util.ArrayList;
import java.util.List;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

public class JournalDashboard extends AppCompatActivity implements JourneyListners {

    public static final int REQUEST_CODE_ADD_JOURNAL = 1;
    public static final int REQUEST_CODE_UPDATE_JOURNAL = 2;
    public static final int REQUEST_CODE_SHOW_JOURNAL = 3;
    private static final int PERMISSION_REQUEST_CODE = 101;
    private ImageView DashHome, calender1, mapAddNote, logoutClick;
    private RecyclerView notesRecyleView;
    private List<Journal> journalList;
    private JournalAdapters journalAdapters;
    SharedPreferences sharedPreferences;
    private LinearLayout layout_Journal;
    String file_path = null;
    private int journalClickedPosition = -1;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_dashboard);

        sharedPreferences = getSharedPreferences("journal_preference", Context.MODE_PRIVATE);


        logoutClick = findViewById(R.id.logoutClick);
        DashHome = findViewById(R.id.DashHome);
        calender1 = findViewById(R.id.calender1);
        mapAddNote = findViewById(R.id.mapAddNote);
        ImageView addButton = findViewById(R.id.addButton);
        layout_Journal = findViewById(R.id.layoutJournal);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this,gso);

/*        editor.putString("username", userName);
        editor.putString("password", password);
        editor.putBoolean("isLoggedIn", true);
        //any other detail you want to save
        editor.apply();*/

        /* For DashHome */
        DashHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(JournalDashboard.this, JournalDashboard.class));
            }
        });

        /* For Calendar */
        calender1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(JournalDashboard.this, Calendar.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }

        });

        /* For Logout */
        /* When we click on Logout button it will go on login page*/
        logoutClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                popupMenu.getMenuInflater().inflate(R.menu.logout_menu, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId())
                        {
                            case R.id.logoutClick:
                                AlertDialog.Builder builder=new AlertDialog.Builder(JournalDashboard.this);
                                builder.setMessage("Do you want to exit?");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        signOut();
                                        finish();
                                        Intent intent = new Intent(JournalDashboard.this,Login.class);
                                        startActivity(intent);
                                        sharedPreferences.edit().remove("User_login").commit();
                                        finish();
                                        Toast.makeText(JournalDashboard.this, "logout Success", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                                AlertDialog alert=builder.create();
                                alert.show();

                        }
                        return false;
                    }
                });
            }
        });

        /* For map */
        mapAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(JournalDashboard.this, LocationMap.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        new Intent(getApplicationContext(), CreateJournal.class),
                        REQUEST_CODE_ADD_JOURNAL                );
            }
        });

        notesRecyleView = findViewById(R.id.notesRecyleView);
        notesRecyleView.setLayoutManager(
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        journalList = new ArrayList<>();
        journalAdapters = new JournalAdapters(journalList, this);
        notesRecyleView.setAdapter(journalAdapters);
        getJournal(REQUEST_CODE_SHOW_JOURNAL, false);

        EditText inputSearch = findViewById(R.id.inputSearch);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                journalAdapters.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(journalList.size() != 0){
                    journalAdapters.searchJournal(s.toString());
                }
            }
        });

    }

    void signOut(){
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                finish();
                Intent intent = new Intent(JournalDashboard.this, Login.class);
                startActivity(intent);
            }
        });
    }

    private boolean checkPermission(String[] permission){
        for (int i=0; 1<permission.length;i++ ){
            int result = ContextCompat.checkSelfPermission(JournalDashboard.this,permission[i]);
            if(result == PackageManager.PERMISSION_GRANTED){
                continue;
            }
            else{
                return false;
            }
        }
        return true;
    }

    private void requestPermission(String[] permission){
        ActivityCompat.requestPermissions(JournalDashboard.this,permission,PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onJournalClicked(Journal journal, int position){
        journalClickedPosition = position;
        Intent intent = new Intent(getApplicationContext(), CreateJournal.class);
        intent.putExtra("isVieworUpdate", true);
        intent.putExtra("journal", journal);
        startActivityForResult(intent, REQUEST_CODE_UPDATE_JOURNAL);
    }

    private void getJournal(final int requestCode, final boolean isJournalDeleted){
        class GetJournalsTask extends AsyncTask<Void, Void, List<Journal>> {
            @Override
            protected List<Journal>doInBackground(Void... voids){
                return JounrneyDatabase
                        .getInstance(getApplicationContext())
                        .journalDao().getAllJournal();
            }

            @Override
            protected void onPostExecute(List<Journal> journal) {
                super.onPostExecute(journal);
                 if (requestCode == REQUEST_CODE_SHOW_JOURNAL){
                     journalList.addAll(journal);
                     journalAdapters.notifyDataSetChanged();
                 }else if(requestCode == REQUEST_CODE_ADD_JOURNAL){
                     journalList.add(0, journal.get(0));
                     journalAdapters.notifyItemInserted(0);
                     notesRecyleView.smoothScrollToPosition(0);
                 }else if (requestCode == REQUEST_CODE_UPDATE_JOURNAL){
                     journalList.remove(journalClickedPosition);
                     if(isJournalDeleted){
                         journalAdapters.notifyItemRemoved(journalClickedPosition);
                     }else{
                         journalList.add(journalClickedPosition, journal.get(journalClickedPosition));
                         journalAdapters.notifyItemChanged(journalClickedPosition);

                     }
                 }
            }
        }
        new GetJournalsTask().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_JOURNAL && resultCode == RESULT_OK){
            getJournal(REQUEST_CODE_ADD_JOURNAL, false);
        } else if(requestCode == REQUEST_CODE_UPDATE_JOURNAL && resultCode == RESULT_OK){
            if(data != null){
                getJournal(REQUEST_CODE_UPDATE_JOURNAL, data.getBooleanExtra("isJournalDelete", false));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.logout_menu, menu );
        return super.onCreateOptionsMenu(menu);

    }
}