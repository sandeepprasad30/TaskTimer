package com.sandeep.tasktimer

import android.database.Cursor
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.task_list_item.*
import java.lang.IllegalStateException

class TaskViewHolder (override  val containerView: View):
    RecyclerView.ViewHolder(containerView),
    LayoutContainer {
    // var name: TextView = containerView.findViewById(R.id.tli_name)

    fun bind (task: Task, listener: CursorRecyclerViewAdapter.OnTaskClickListener) {
        tli_name.text = task.name
        tli_description.text = task.description
        tli_edit.visibility = View.VISIBLE
        tli_delete.visibility = View.VISIBLE

        tli_edit.setOnClickListener {
            Log.d(TAG, "edit button tapped. task name is ${task.name}")
            listener.onEditClick(task)
        }

        tli_delete.setOnClickListener {
            Log.d(TAG, "delete button tapped. task name is ${task.name}")
            listener.onDeleteClick(task)
        }

        containerView.setOnLongClickListener {
            Log.d(TAG, "onlongclick .task name is ${task.name}")
            listener.onTaskLongClick(task)
            true
        }
    }
}

private val TAG = "CursorRecyclerViewAdapt"

class CursorRecyclerViewAdapter( private var cursor: Cursor?, private val listener: OnTaskClickListener): RecyclerView.Adapter<TaskViewHolder>() {

    interface OnTaskClickListener {
        fun onEditClick(task: Task)
        fun onDeleteClick(task: Task)
        fun onTaskLongClick(task: Task)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        Log.d(TAG, "onCreateViewHolder: starts")
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_list_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder: starts")
        val cursor = cursor

        if (cursor == null || cursor.count == 0) {
            Log.d(TAG, "onBindViewHolder: providing instructions")
            holder.tli_name.setText(R.string.instructions_heading)
            holder.tli_description.setText(R.string.instructions)
            holder.tli_edit.visibility = View.GONE
            holder.tli_delete.visibility = View.GONE
        } else {
            if (!cursor.moveToPosition(position)) {
                throw IllegalStateException("couldn't move cursor to position $position")
            }

            val task = Task(
                cursor.getString(cursor.getColumnIndex(TasksContract.Columns.TASK_NAME)),
                cursor.getString(cursor.getColumnIndex(TasksContract.Columns.TASK_DESCRIPTION)),
                cursor.getInt(cursor.getColumnIndex(TasksContract.Columns.TASK_SORT_ORDER))
            )
            task.id = cursor.getLong(cursor.getColumnIndex(TasksContract.Columns.ID))
            holder.bind(task, listener)
            /*holder.tli_name.text = task.name
            holder.tli_description.text = task.description
            holder.tli_edit.visibility = View.VISIBLE
            holder.tli_delete.visibility = View.VISIBLE*/

        }
    }

    override fun getItemCount(): Int {
        Log.d(TAG, "getItemCount: starts")
        val cursor = cursor
        val count = if (cursor == null || cursor.count == 0) {
            1
        } else {
            cursor.count
        }
        Log.d(TAG, "getItemCount: return $count")
        return count
    }

    fun swapCursor (newCursor: Cursor?): Cursor? {
        if (newCursor == cursor) {
            return null
        }
        val numItems = itemCount
        val oldCursor = cursor
        cursor = newCursor
        if (newCursor != null) {
            notifyDataSetChanged()
        } else {
            notifyItemRangeChanged(0, numItems)
        }
        return oldCursor
    }

}