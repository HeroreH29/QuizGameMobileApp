package com.example.logintest1;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class RankAdapter extends FirestoreRecyclerAdapter<RecyclerRankingNote, RankAdapter.NoteHolder>{
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public RankAdapter(@NonNull FirestoreRecyclerOptions<RecyclerRankingNote> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull NoteHolder holder, int position, @NonNull RecyclerRankingNote model) {
        holder.rankFullname.setText(model.getLearnersLastname());
        holder.rankPoints.setText(String.valueOf(model.getLearnersScore()));
        holder.rankPosition.setText(String.valueOf(holder.getBindingAdapterPosition()+1));

        db.collection("Learners")
                .whereEqualTo("LearnersScore", model.getLearnersScore())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String UID = document.getId();
                                DocumentReference rankRef = db.collection("Learners").document(UID);
                                rankRef
                                        .update("LearnersRank", holder.getBindingAdapterPosition()+1);
                            }
                        }
                    }
                });
        if (model.getLearnersRank() == 1){
            holder.rankImage.setImageResource(R.drawable.firstplace);
        }
        else if (model.getLearnersRank() == 2){
            holder.rankImage.setImageResource(R.drawable.secondplace);
        }
        else if (model.getLearnersRank() == 3){
            holder.rankImage.setImageResource(R.drawable.thirdplace);
        }
    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item,
                parent, false);
        return new NoteHolder(v);
    }

    class NoteHolder extends RecyclerView.ViewHolder{

        TextView rankFullname, rankPoints, rankPosition;
        ImageView rankImage;
        /*int pos;*/
        public NoteHolder(@NonNull View itemView) {
            super(itemView);
            rankFullname = itemView.findViewById(R.id.rankingUserName);
            rankPosition = itemView.findViewById(R.id.rankingNumber);
            rankPoints = itemView.findViewById(R.id.rankingPoints);
            rankImage = itemView.findViewById(R.id.rankImg);
            /*
            pos = getAbsoluteAdapterPosition();*/
        }
    }
}
