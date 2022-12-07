package com.ismt.journeyjournal.adapters;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.ismt.journeyjournal.R;
import com.ismt.journeyjournal.journal.Journal;
import com.ismt.journeyjournal.journal.JourneyListners;
import com.makeramen.roundedimageview.RoundedImageView;
import java.util.Timer;
import java.util.*;
import java.util.TimerTask;

import java.util.List;

public class JournalAdapters extends RecyclerView.Adapter<JournalAdapters.NoteViewHolder> {

    private List<Journal> journal;
    private JourneyListners journeyListners;
    private Timer timer;
    private List<Journal> journalSource;

    public JournalAdapters(List<Journal> journal, JourneyListners journeyListners) {
        this.journal = journal;
        this.journeyListners = journeyListners;
        journalSource = journal;
    }

    public JournalAdapters(List<Journal> journal) {
        this.journal = journal;
    }

    /*Creating separate ViewHolder classes for each layout*/
    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_container_notes,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.setJournal(journal.get(position));
        holder.layoutJournal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                journeyListners.onJournalClicked(journal.get(position), position);
            }
        });
    }

    /*getItemCount() functions returns the total number of items to be displayed in a list.*/
    @Override
    public int getItemCount() {
        return journal.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, txtDesc, textDateTime ;
        LinearLayout layoutJournal;
        RoundedImageView imageJournal;

        NoteViewHolder(@Nullable View itemView) {
            super(itemView);

            textTitle = itemView.findViewById(R.id.textTitle);
            txtDesc = itemView.findViewById(R.id.txtDesc);

            textDateTime = itemView.findViewById(R.id.textDateTime);
            layoutJournal = itemView.findViewById(R.id.layoutJournal);
            imageJournal = itemView.findViewById(R.id.imageJournal);
        }

        void setJournal(@NonNull Journal journal) {
            textTitle.setText(journal.getTitle());
            if (journal.getNoteText().trim().isEmpty()) {
                txtDesc.setVisibility(View.GONE);
            } else {
                txtDesc.setText(journal.getNoteText());
            }
            textDateTime.setText(journal.getDateTime());
            GradientDrawable gradientDrawable = (GradientDrawable) layoutJournal.getBackground();
            if(journal.getColor() != null){
                gradientDrawable.setColor(Color.parseColor(journal.getColor()));
            }
            else {
                gradientDrawable.setColor(Color.parseColor("#333333"));
            }
            if (journal.getImagePath()!= null){
                imageJournal.setImageBitmap(BitmapFactory.decodeFile(journal.getImagePath()));
                imageJournal.setVisibility(View.VISIBLE);
            }
            else {
                imageJournal.setVisibility(View.GONE);
            }
        }
    }

    /* For Search Journal*/
    public void searchJournal(final String searchKeyword){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (searchKeyword.trim().isEmpty()) {
                    journal = journalSource;
                }else {
                    ArrayList<Journal> temp = new ArrayList<>();
                    for(Journal journal : journalSource){
                        if(journal.getTitle().toLowerCase().contains(searchKeyword.toLowerCase())
                                || journal.getNoteText().toLowerCase().contains(searchKeyword.toLowerCase())){
                            temp.add(journal);
                        }
                    }
                    journal = temp;
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        }, 500);
    }

        /*Terminates this timer, discarding any currently scheduled tasks.*/
    public  void  cancelTimer(){
        if(timer !=null){
            timer.cancel();
        }
    }
}
