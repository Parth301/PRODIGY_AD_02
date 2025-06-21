package com.example.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LinearLayout taskContainer;
    private TaskDatabase taskDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        taskContainer = findViewById(R.id.taskContainer);
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);

        taskDatabase = TaskDatabase.getInstance(this);

        fabAdd.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AddTaskActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayTasks();
    }

    private void displayTasks() {
        taskContainer.removeAllViews();
        List<Task> tasks = taskDatabase.taskDao().getAllTasks();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (Task task : tasks) {
            View taskView = inflater.inflate(R.layout.item_task, taskContainer, false);
            MaterialCardView card = taskView.findViewById(R.id.cardTask);
            card.setTag(task.id);

            com.google.android.material.textview.MaterialTextView tvTitle = taskView.findViewById(R.id.tvTaskTitle);
            com.google.android.material.textview.MaterialTextView tvDesc = taskView.findViewById(R.id.tvTaskDesc);
            com.google.android.material.textview.MaterialTextView tvDue = taskView.findViewById(R.id.tvDueDate);
            com.google.android.material.textview.MaterialTextView tvPrio = taskView.findViewById(R.id.tvPriority);

            tvTitle.setText(task.title);
            tvDesc.setText(task.description);
            tvDue.setText("Due: " + task.dueDate);
            tvPrio.setText("Priority: " + task.priority);

            // Set priority background color dynamically
            setPriorityBackground(tvPrio, task.priority);

            taskView.findViewById(R.id.btnEdit).setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
                intent.putExtra("task_id", task.id);
                intent.putExtra("task_title", task.title);
                intent.putExtra("task_description", task.description);
                intent.putExtra("task_due", task.dueDate);
                intent.putExtra("task_priority", task.priority);

                startActivity(intent);
            });

            taskView.findViewById(R.id.btnDelete).setOnClickListener(v -> {
                taskDatabase.taskDao().deleteTask(task);
                Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show();
                displayTasks();
            });

            taskContainer.addView(taskView);
        }
    }

    /**
     * Sets the background color of the priority TextView based on priority level
     * @param tvPriority The TextView to apply the background to
     * @param priority The priority level (High, Medium, Low)
     */
    private void setPriorityBackground(com.google.android.material.textview.MaterialTextView tvPriority, String priority) {
        if (priority == null) {
            priority = "Low"; // Default to Low if null
        }

        switch (priority.toLowerCase().trim()) {
            case "high":
                tvPriority.setBackground(ContextCompat.getDrawable(this, R.drawable.priority_high_bg));
                break;
            case "medium":
                tvPriority.setBackground(ContextCompat.getDrawable(this, R.drawable.priority_medium_bg));
                break;
            case "low":
            default:
                tvPriority.setBackground(ContextCompat.getDrawable(this, R.drawable.priority_low_bg));
                break;
        }
    }
}