package com.example.chatty.AboutScreen

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.chatty.DashboardScreen.Dashboardactivity
import com.example.chatty.FileHelper
import com.example.chatty.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.changestatus_dialog.view.*
import java.lang.Exception
import java.util.*


class SettingsActivity : AppCompatActivity() {
    private lateinit var mCurrentUser: FirebaseUser
    private lateinit var mUserDatabase: DatabaseReference
    private lateinit var database: DatabaseReference
    //////////////////////////////////////////////////////////
    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        ////////////////////////////////////////////////
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        //////////////////////////////////////////////////
        mCurrentUser = FirebaseAuth.getInstance().currentUser!!
       val current_uid: String = mCurrentUser.uid
        mUserDatabase = FirebaseDatabase.getInstance().reference.child("users").child(current_uid)
        database = FirebaseDatabase.getInstance().reference
        ///////////////////////////////////////////////
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val name: String = dataSnapshot.child("username").value.toString()
                val status: String = dataSnapshot.child("status").value.toString()
                var image: String = dataSnapshot.child("image").value.toString()
                var thum_img: String = dataSnapshot.child("thum_img").value.toString()
                settings_displayname.text = name
                setting_status.text = status
                Picasso.get()
                    .load(image)
                    .networkPolicy(NetworkPolicy.OFFLINE, NetworkPolicy.NO_CACHE)
                    .placeholder(R.drawable.user)
                    .into(select_imageview_settings, object : com.squareup.picasso.Callback{
                        override fun onSuccess() {

                        }

                        override fun onError(e: Exception?) {
                            Picasso.get()
                                .load(image)
                                .placeholder(R.drawable.user)
                                .into(select_imageview_settings, object : com.squareup.picasso.Callback{
                                    override fun onSuccess() {

                                    }

                                    override fun onError(e: Exception?) {

                                    }

                                } )
                        }

                    } )
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("nothing happened", "loadPost:onCancelled", databaseError.toException())
                // [START_EXCLUDE]
                Toast.makeText(baseContext, "Failed to load post.",
                    Toast.LENGTH_SHORT).show()
                // [END_EXCLUDE]
            }
        }
        settings_statusbtn.setOnClickListener{
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.changestatus_dialog,null)
            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
            //////////stting status equals to current status//////////////////////////
            mDialogView.statuschange_et.setText(setting_status.text.toString())
            ///////////showing the new status dialog////////////////////////////
            val mAlertDialog = mBuilder.show()
            mAlertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            ////////////////////setting on click listeners for dialog save and cancel button/////////////////////////////////////////////////
            mDialogView.savechanges_btn.setOnClickListener {
                val status = mDialogView.statuschange_et.text.toString()
                /////////////// Writing to the status database///////////////////////////////////
                mUserDatabase.child("status").setValue(status)
                mAlertDialog.dismiss()
            }
            mDialogView.cancel_btn.setOnClickListener {
                mAlertDialog.dismiss()
            }
        }
        settings_imagebtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type="image/*"
            startActivityForResult(intent,0)
        }

        mUserDatabase.addValueEventListener(postListener)
    }
var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            selectedPhotoUri = data.data
            val bitmap = Compressor(this).setMaxHeight(512).setMaxWidth(512).setQuality(80).compressToBitmap(
                FileHelper.from(this, selectedPhotoUri)
            )

        select_imageview_settings.setImageBitmap(bitmap)
        userImage.alpha = 0f
        uploadImageTofirebaseStorage()
        }

    }

    private fun uploadImageTofirebaseStorage() {
        if (selectedPhotoUri == null) return
        val filename= UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("Register", "Image Uploaded Successfully")

        ref.downloadUrl.addOnSuccessListener {
            saveimageFirebaseDatabase(it.toString())
        } }
            .addOnFailureListener{
                Toast.makeText(
                    baseContext, "Image Upload failed.",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun saveimageFirebaseDatabase(profileUri: String) {
        mUserDatabase.child("image").setValue(profileUri)
       var editor=getSharedPreferences("shared", Context.MODE_PRIVATE).edit()
        editor.putString("profile_url",profileUri).commit()
    }

    override fun onBackPressed() {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        finish()
        startActivity(Intent(this@SettingsActivity,
            Dashboardactivity::class.java))
    }

}
