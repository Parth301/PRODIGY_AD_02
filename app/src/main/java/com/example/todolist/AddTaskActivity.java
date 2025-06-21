package com.example.todolist;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

public class AddTaskActivity extends AppCompatActivity {

    private EditText editTitle, editDescription, editDueDate;
    private Spinner spinnerPriority;
    private TaskDatabase taskDatabase;
    private int taskId = -1;

    private ArrayAdapter<CharSequence> priorityAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        editTitle = findViewById(R.id.editTitle);
        editDescription = findViewById(R.id.editDescription);
        editDueDate = findViewById(R.id.editDueDate);
        spinnerPriority = findViewById(R.id.spinnerPriority);
        Button btnSave = findViewById(R.id.btnSave);

        taskDatabase = TaskDatabase.getInstance(this);

        // Spinner setup
        priorityAdapter = ArrayAdapter.createFromResource(
                this, R.array.priority_levels, android.R.layout.simple_spinner_item);
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(priorityAdapter);

        // Date picker setup
        editDueDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                String date = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year);
                editDueDate.setText(date);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Check if editing existing task
        if (getIntent().hasExtra("task_id")) {
            taskId = getIntent().getIntExtra("task_id", -1);
            String taskTitle = getIntent().getStringExtra("task_title");
            String desc = getIntent().getStringExtra("task_description");
            String due = getIntent().getStringExtra("task_due");
            String prio = getIntent().getStringExtra("task_priority");

            editTitle.setText(taskTitle);
            editDescription.setText(desc);
            editDueDate.setText(due);

            int prioIndex = priorityAdapter.getPosition(prio);
            spinnerPriority.setSelection(prioIndex);

            btnSave.setText("Update Task");
        }

        // Save/Update logic
        btnSave.setOnClickListener(v -> {
            String title = editTitle.getText().toString().trim();
            String description = editDescription.getText().toString().trim();
            String dueDate = editDueDate.getText().toString().trim();
            String priority = spinnerPriority.getSelectedItem().toString();

            if (!title.isEmpty()) {
                if (taskId == -1) {
                    taskDatabase.taskDao().insertTask(new Task(title, description, dueDate, priority));
                } else {
                    Task updatedTask = new Task(title, description, dueDate, priority);
                    updatedTask.id = taskId;
                    taskDatabase.taskDao().updateTask(updatedTask);
                }
                finish();
            } else {
                Toast.makeText(this, "Task title required", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
