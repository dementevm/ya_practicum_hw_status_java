package com.example;

import com.google.gson.annotations.SerializedName;

public class Homework {
    public int id;
    public String status;
    @SerializedName("homework_name")
    public String homeworkName;
    @SerializedName("reviewer_comment")
    public String reviewerComment;
    @SerializedName("date_updated")
    public String dateUpdated;
    @SerializedName("lesson_name")
    public String lessonName;
}