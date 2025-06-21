package com.example.todolist;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import java.util.List;
@Dao
public interface TaskDao {
    @Insert
    void insertTask(Task task);

    @Query("SELECT * FROM Task ORDER BY id DESC")
    List<Task> getAllTasks();

    @Update
    void updateTask(Task task);

    @Delete
    void deleteTask(Task task);
}
