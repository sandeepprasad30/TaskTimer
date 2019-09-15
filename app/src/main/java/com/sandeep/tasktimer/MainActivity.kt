package com.sandeep.tasktimer

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


private const val TAG = "MainActivity"
private const val DIALOG_ID_CANCEL_EDIT = 1
class MainActivity : AppCompatActivity(), AddEditFragment.OnSaveClicked, MainActivityFragment.OnTaskEdit,
    AppDialog.DialogEvents {

    private var mtwoPane = false
    private var aboutDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: starts")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        mtwoPane = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        Log.d(TAG, "onCreate: mtwoPane is $mtwoPane")

        var fragment = findFragmentById(R.id.task_details_container)

        if ( fragment != null ) {
            showEditPane()
        } else {
            task_details_container.visibility = if (mtwoPane) View.INVISIBLE else View.GONE
            mainFragment.view?.visibility = View.VISIBLE
        }

        Log.d(TAG, "onCreate: ends")
    }

    private fun showEditPane() {
        task_details_container.visibility = View.VISIBLE
        mainFragment.view?.visibility = if (mtwoPane) View.VISIBLE else View.GONE
    }

    private fun removeEditPane(fragment: Fragment? = null) {
        Log.d(TAG, "removeEditPane called")
        // var fragment = supportFragmentManager.findFragmentById(R.id.task_details_container)
        if(fragment != null) {
            /*supportFragmentManager.beginTransaction()
                .remove(fragment)
                .commit()*/
            removeFragment(fragment)
        }

        task_details_container.visibility = if (mtwoPane) View.INVISIBLE else View.GONE

        mainFragment.view?.visibility = View.VISIBLE

        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    override fun onSaveClicked() {
        Log.d(TAG, "onSaveClicked called")
        val fragment = findFragmentById(R.id.task_details_container)
        removeEditPane(fragment)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.menumain_addtask -> taskEditRequest(null)
            R.id.menumain_showAbout -> showAboutDialog()
            android.R.id.home -> {
                Log.d(TAG, "onOptionsItemSelected home button pressed called")
                val fragment = findFragmentById(R.id.task_details_container)
                // removeEditPane(fragment)

                if ((fragment is AddEditFragment) && fragment.isDirty()) {
                    showConfirmationDialog(DIALOG_ID_CANCEL_EDIT,
                        getString(R.string.canceledit_dialog_menu),
                        R.string.cancelEditDialog_poistive_caption,
                        R.string.cancelEditDialog_negative_caption)
                } else {
                    removeEditPane(fragment)
                }
            }
            //R.id.menumain_settings -> true
        }

        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("InflateParams")
    private fun showAboutDialog() {
        val messageView = layoutInflater.inflate(R.layout.about, null, false)
        val builder = AlertDialog.Builder(this)

        builder.setTitle(R.string.app_name)
        builder.setIcon(R.mipmap.ic_launcher)

        builder.setPositiveButton(R.string.ok) { _, _ ->
            Log.d(TAG, "dialog onClick")
            if(aboutDialog!= null && aboutDialog?.isShowing == true) {
                aboutDialog?.dismiss()
            }
        }

        aboutDialog = builder.setView(messageView).create()
        aboutDialog?.setCanceledOnTouchOutside(true)

        messageView.setOnClickListener {
            Log.d(TAG, "about onClickListener")
            if(aboutDialog!= null && aboutDialog?.isShowing == true) {
                aboutDialog?.dismiss()
            }
        }

        //builder.setTitle(R.string.app_name)
        //builder.setIcon(R.mipmap.ic_launcher)

        val aboutVersion = messageView.findViewById(R.id.about_version) as TextView
        aboutVersion.text = BuildConfig.VERSION_NAME

        aboutDialog?.show()
    }

    @SuppressLint("InflateParams")
    private fun showAboutDialog_backup() {
        val messageView = layoutInflater.inflate(R.layout.about, null, false)
        val builder = AlertDialog.Builder(this)

        builder.setTitle(R.string.app_name)
        builder.setIcon(R.mipmap.ic_launcher)

        aboutDialog = builder.setView(messageView).create()
        aboutDialog?.setCanceledOnTouchOutside(true)

        messageView.setOnClickListener {
            Log.d(TAG, "about onClickListener")
            if(aboutDialog!= null && aboutDialog?.isShowing == true) {
                aboutDialog?.dismiss()
            }
        }

        //builder.setTitle(R.string.app_name)
        //builder.setIcon(R.mipmap.ic_launcher)

        val aboutVersion = messageView.findViewById(R.id.about_version) as TextView
        aboutVersion.text = BuildConfig.VERSION_NAME

        aboutDialog?.show()
    }

    override fun onTaskEdit(task: Task) {
        taskEditRequest(task)
    }

    private fun taskEditRequest (task: Task?) {
        Log.d(TAG, "taskEditRequest starts")

        /*val newFragment = AddEditFragment.newInstance(task)
        supportFragmentManager.beginTransaction()
            .replace(R.id.task_details_container, newFragment)
            .commit()*/

        showEditPane()
        replaceFragment(AddEditFragment.newInstance(task), R.id.task_details_container)

        Log.d(TAG, "taskEditRequest exists")

    }

    override fun onBackPressed() {
        val fragment = findFragmentById(R.id.task_details_container)

        if (fragment == null || mtwoPane) {
            super.onBackPressed()
        } else {
            // removeEditPane(fragment)
            if ((fragment is AddEditFragment) && fragment.isDirty()) {
                showConfirmationDialog(DIALOG_ID_CANCEL_EDIT,
                    getString(R.string.canceledit_dialog_menu),
                    R.string.cancelEditDialog_poistive_caption,
                    R.string.cancelEditDialog_negative_caption)
            } else {
                removeEditPane(fragment)
            }
        }

    }

    override fun onPositiveDialogResult(dialogId: Int, args: Bundle) {
        Log.d(TAG, "onPositiveDialogResult called with dialogId $dialogId")
        if(dialogId == DIALOG_ID_CANCEL_EDIT) {
            val fragment = findFragmentById(R.id.task_details_container)
            removeEditPane(fragment)
        }
    }

    override fun onStart() {
        Log.d(TAG, "onStart: called")
        super.onStart()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        Log.d(TAG, "onRestoreInstanceState: called")
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onResume() {
        Log.d(TAG, "onResume: called")
        super.onResume()
    }

    override fun onPause() {
        Log.d(TAG, "onPause: called")
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        Log.d(TAG, "onSaveInstanceState: called")
        super.onSaveInstanceState(outState)
    }

    override fun onStop() {
        Log.d(TAG, "onStop: called")
        super.onStop()
        if (aboutDialog?.isShowing == true) {
            aboutDialog?.dismiss()
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: called")
        super.onDestroy()
    }
}
