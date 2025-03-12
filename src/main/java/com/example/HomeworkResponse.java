package com.example;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class HomeworkResponse {
    public List<Homework> homeworks;
    @SerializedName("current_date")
    public long currentDate;
}
