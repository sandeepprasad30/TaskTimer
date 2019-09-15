package com.sandeep.tasktimer

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_add_edit.*

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_TASK = "task"

private const val TAG = "AddEditFragment"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [AddEditFragment.OnSaveClicked] interface
 * to handle interaction events.
 * Use the [AddEditFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddEditFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var task: Task? = null
    private var listener: OnSaveClicked? = null
    private val viewModel by lazy { ViewModelProviders.of(activity!!).get(TaskTimerViewModel::class.java)}

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: starts")
        super.onCreate(savedInstanceState)
        task = arguments?.getParcelable(ARG_TASK)
        /*arguments?.let {
            task = it.getString(ARG_TASK)
        }*/
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: starts")
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_edit, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated: called")
        if (savedInstanceState == null) {
            val task = task
            if (task != null) {
                Log.d(TAG, "onViewCreated: task details found ${task.id}")
                addedit_name.setText(task.name)
                addedit_description.setText(task.description)
                addedit_sortorder.setText(task.sortOrder.toString())
            } else {
                // no task so new task
                Log.d(TAG, "onviewcreated: no args, adding new record")
            }
        }
    }

    private fun taskFromUi(): Task {
        val sortOrder = if (addedit_sortorder.text.isNotEmpty()) {
            Integer.parseInt(addedit_sortorder.text.toString())
        } else {
            0
        }

        val newTask = Task(addedit_name.text.toString(), addedit_description.text.toString(), sortOrder)
        newTask.id = task?.id?: 0
        return newTask
    }

    fun isDirty(): Boolean {
        val newTask = taskFromUi()
        return ((newTask != task ) &&
                (newTask.name.isNotBlank() ||
                newTask.description.isNotBlank() ||
                newTask.sortOrder != 0 )
        )
    }

    private fun saveTask() {
        val newTask = taskFromUi()
        if (newTask != task) {
            Log.d(TAG, "save task: saving task ${newTask.id}")
            task = viewModel.saveTask(newTask)
            Log.d(TAG, "save task: saved task ${task?.id}")
        }
    }

    private fun saveTask_backup() {
        val sortOrder = if (addedit_sortorder.text.isNotEmpty()) {
            Integer.parseInt(addedit_sortorder.text.toString())
        } else {
            0
        }

        val values = ContentValues()
        val task = task

        if ( task != null) {
            Log.d(TAG, "save task updating task")
            if(addedit_name.text.toString() != task.name) {
                values.put(TasksContract.Columns.TASK_NAME, addedit_name.text.toString())
            }
            if(addedit_description.text.toString() != task.description) {
                values.put(TasksContract.Columns.TASK_DESCRIPTION, addedit_description.text.toString())
            }
            if(sortOrder != task.sortOrder) {
                values.put(TasksContract.Columns.TASK_SORT_ORDER, sortOrder)
            }
            if (values.size() != 0) {
                Log.d(TAG, "updating task")
                activity?.contentResolver?.update(TasksContract.buildUriFromId(task.id), values, null ,null)
            }
        } else {
            Log.d(TAG, "save task adding new task")
            if(addedit_name.text.isNotEmpty()) {
                values.put(TasksContract.Columns.TASK_NAME, addedit_name.text.toString())
                if(addedit_description.text.isNotEmpty()) {
                    values.put(TasksContract.Columns.TASK_DESCRIPTION, addedit_description.text.toString())
                }
                values.put(TasksContract.Columns.TASK_SORT_ORDER, sortOrder)
            }
            if (values.size() != 0) {
                Log.d(TAG, "adding task")
                activity?.contentResolver?.insert(TasksContract.CONTENT_URI, values)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.d(TAG, "onActivityCreated: starts")
        super.onActivityCreated(savedInstanceState)

        if (listener is AppCompatActivity) {
            val actionBar = (listener as AppCompatActivity?)?.supportActionBar
            actionBar?.setDisplayHomeAsUpEnabled(true)
        }

        addedit_save.setOnClickListener {
            saveTask()
            listener?.onSaveClicked()
        }
    }


    override fun onAttach(context: Context) {
        Log.d(TAG, "onAttach: starts")
        super.onAttach(context)
        if (context is OnSaveClicked) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnSaveClicked")
        }
    }

    override fun onDetach() {
        Log.d(TAG, "onDetach: starts")
        super.onDetach()
        listener = null
    }



    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewStateRestored: called")
        super.onViewStateRestored(savedInstanceState)
    }

    override fun onStart() {
        Log.d(TAG, "onStart: called")
        super.onStart()
    }

    override fun onResume() {
        Log.d(TAG, "onResume: called")
        super.onResume()
    }

    override fun onPause() {
        Log.d(TAG, "onPause: called")
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.d(TAG, "onSaveInstanceState: called")
        super.onSaveInstanceState(outState)
    }

    override fun onStop() {
        Log.d(TAG, "onStop: called")
        super.onStop()
    }

    override fun onDestroyView() {
        Log.d(TAG, "onDestroyView: called")
        super.onDestroyView()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: called")
        super.onDestroy()
    }




    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnSaveClicked {
        fun onSaveClicked()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param task Parameter 1.
         * @return A new instance of fragment AddEditFragment.
         */
        @JvmStatic
        fun newInstance(task: Task?) =
            AddEditFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_TASK, task)
                }
            }
    }
}
