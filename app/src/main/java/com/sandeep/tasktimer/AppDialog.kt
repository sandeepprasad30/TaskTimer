package com.sandeep.tasktimer


import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import java.lang.ClassCastException
import java.lang.IllegalArgumentException

private const val TAG = "AppDialog"
const val DIALOG_ID = "id"
const val DIALOG_MESSAGE = "message"
const val DIALOG_POSITIVE_RID = "positive_rid"
const val DIALOG_NEGATIVE_RID = "negative_rid"

class AppDialog: AppCompatDialogFragment() {

    private var dialogEvents: DialogEvents? = null

    internal interface DialogEvents {
        fun onPositiveDialogResult(dialogId: Int, args: Bundle)
        //fun onNegativeDialogResult(dialogId: Int, args: Bundle)
        //fun onDialogCancelled(dialogId: Int)
    }

    override fun onAttach(context: Context?) {
        Log.d(TAG, "onAttach called context  $context")
        super.onAttach(context)

        dialogEvents = try {
            parentFragment as DialogEvents
        } catch (e: TypeCastException){
            try {
                context as DialogEvents
            } catch (e: ClassCastException) {
                throw ClassCastException("Activity $context must implement AppDialog.DialogEvents interface")
            }
        } catch (e: ClassCastException) {
            throw ClassCastException("Fragment $parentFragment must implement AppDialog.DialogEvents interface")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Log.d(TAG, "onCreateDialog called")

        val builder = AlertDialog.Builder(context!!)

        val arguments= arguments

        val dialogId: Int
        val messageString: String?
        var positiveStringId: Int
        var negativeStringId: Int

        if (arguments!= null) {
            dialogId = arguments.getInt(DIALOG_ID)
            messageString = arguments.getString(DIALOG_MESSAGE)

            if (dialogId == 0 || messageString == null) {
                throw IllegalArgumentException("DIALOG_ID or DIALOG_MESSAGE not present in bundle")
            }

            positiveStringId = arguments.getInt(DIALOG_POSITIVE_RID)
            if (positiveStringId == 0){
                positiveStringId = R.string.ok
            }
            negativeStringId = arguments.getInt(DIALOG_NEGATIVE_RID)
            if (negativeStringId == 0){
                negativeStringId = R.string.cancel
            }
        } else {
            throw IllegalArgumentException("DIALOG_ID or DIALOG_MESSAGE not present in bundle")
        }

        return builder.setMessage(messageString)
            .setPositiveButton(positiveStringId) { dialogInterface, i ->
                dialogEvents?.onPositiveDialogResult(dialogId, arguments)
            }
            .setNegativeButton(negativeStringId) { dialogInterface, i ->
                    //  dialogEvents?.onPositiveDialogResult(dialogId, arguments)
            }
            .create()
    }

    override fun onDetach() {
        Log.d(TAG, "onDetach called")
        super.onDetach()
        dialogEvents = null
    }

    override fun onCancel(dialog: DialogInterface?) {
        Log.d(TAG, "onCancel called")
        val dialogId = arguments!!.getInt(DIALOG_ID)
        // dialogEvents?.onDialogCancelled(dialogId)
    }

    override fun onDismiss(dialog: DialogInterface?) {
        Log.d(TAG, "onDismiss called")
        super.onDismiss(dialog)
    }
}