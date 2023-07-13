package com.example.logintest1;

public class RecyclerRankingNote {
    private String LearnersLastname;
    private int LearnersRank;
    private int LearnersScore;

    public RecyclerRankingNote(){
        //empty constructor needed, do not delete
    }

    public RecyclerRankingNote(String rankFullname, int rankPosition, int rankScore){
        this.LearnersLastname = rankFullname;
        this.LearnersRank = rankPosition;
        this.LearnersScore = rankScore;
    }

    public String getLearnersLastname() {
        return LearnersLastname;
    }

    public int getLearnersRank() {
        return LearnersRank;
    }

    public int getLearnersScore() {
        return LearnersScore;
    }
}
