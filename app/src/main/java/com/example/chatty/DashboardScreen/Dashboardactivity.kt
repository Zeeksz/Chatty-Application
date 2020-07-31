package com.example.chatty.DashboardScreen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.chatty.AboutScreen.SettingsActivity
import com.example.chatty.R
import com.example.chatty.WelcomeScreen.WelcomeActivity
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_dashboardactivity.*
import kotlinx.android.synthetic.main.drawer_header.view.*


class Dashboardactivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    //////////////////////////////////////////////////
    private lateinit var auth: FirebaseAuth
    /////////////////////////////////////////////////////
    private lateinit var mCurrentUser: FirebaseUser
    private lateinit var mUserDatabase: DatabaseReference
    private lateinit var database: DatabaseReference
    ///////////////////////////////////////////////////
    @SuppressLint("CommitTransaction")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboardactivity)
        auth = FirebaseAuth.getInstance()
        //////////////////////////////////////////////////////
        val menub: ChipNavigationBar = findViewById(R.id.nav_bar)
        if(savedInstanceState == null) {
            menub.setItemSelected(R.id.chats, true)
            var mFragment = ChatsFragment()
            val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
            ft.add(R.id.container, mFragment).commit()
        }
        menub.setOnItemSelectedListener { id ->
            var fragment: Fragment? = null
            when (id) {
                R.id.chats -> fragment =
                    ChatsFragment()
                R.id.like -> fragment =
                    GameFragment()
            }
            if (fragment != null) {
                supportFragmentManager.beginTransaction().replace(R.id.container, fragment)
                    .commit()
            } else {
                Log.e("Boom Boom", "Error Creating Fragment")
            }
        }
        /////////////////////////////////////////////////////
        drawerLayout = findViewById(R.id.main_activity_drawer)
        navigationView = findViewById(R.id.nav_view)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        ////////////////////////////////////////////////////////////////////////////////////////////
        //drawer ui setup
        val hView = navigationView.getHeaderView(0)
        hView.btn_view_profile.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
            finish()
        }
        ////////////////////////////////////////////////////////////////////////////////////////////\

        val nav_image = hView.nav_img_user

        ////////////////////////////////////////////////////////////////////////////////////////////

        var shared=getSharedPreferences("shared", MODE_PRIVATE)
        ///////////////////////////////////////////////////////////////////////////////////////////
        Picasso.get()
            .load(shared.getString("profile_url","Loading..."))
            .networkPolicy(NetworkPolicy.OFFLINE, NetworkPolicy.NO_CACHE)
            .placeholder(R.drawable.user)
            .into(nav_image, object : Callback {
                override fun onSuccess() {

                }

                override fun onError(e: Exception?) {
                    Picasso.get()
                        .load(shared.getString("profile_url","www.google.com"))
                        .placeholder(R.drawable.user)
                        .into(nav_image, object : Callback {
                            override fun onSuccess() {
                            }

                            override fun onError(e: Exception?) {
                            }

                        })
                }

            })
        ///////////////////////////////////////////////////////////////////////////////////////////
        Picasso.get()
            .load(shared.getString("profile_url","Loading..."))
            .networkPolicy(NetworkPolicy.OFFLINE, NetworkPolicy.NO_CACHE)
            .placeholder(R.drawable.user)
            .into(small_img_user, object : Callback {
                override fun onSuccess() {

                }

                override fun onError(e: Exception?) {
                    Picasso.get()
                        .load(shared.getString("profile_url","www.google.com"))
                        .placeholder(R.drawable.user)
                        .into(small_img_user, object : Callback {
                            override fun onSuccess() {
                            }

                            override fun onError(e: Exception?) {
                            }

                        })
                }

            })
        ////////////////////////////////////////////////////////////////////////////////////////////
        val navUsername = hView.nav_txt_username
        mCurrentUser = FirebaseAuth.getInstance().currentUser!!
        val current_uid: String = mCurrentUser.uid
        mUserDatabase = FirebaseDatabase.getInstance().reference.child("users").child(current_uid)
        database = FirebaseDatabase.getInstance().reference
        ////////////////////////////////////////////////////////////////////////////////////////////
        mUserDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val name: String = dataSnapshot.child("username").value.toString()
                navUsername.text= name
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("nothing happened", "loadPost:onCancelled", databaseError.toException())
                Toast.makeText(baseContext, "Failed to load post.",
                    Toast.LENGTH_SHORT).show()
            }
        })
        ////////////////////////////////////////////////////////////////////////////////////////////
        val allUsers = navigationView.menu
        val users = allUsers.findItem(R.id.main_all_btn)
        users.setOnMenuItemClickListener {
            startActivity(Intent(this, AllUsersActivity::class.java))
            true
        }
        ////////////////////////////////////////////////////////////////////////////////////////////
        val menu = navigationView.menu
        val settingMenuItem = menu.findItem(R.id.changePassword)
        settingMenuItem.setOnMenuItemClickListener {
            sendMailtoResetPassword()
            true
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        val logout = menu.findItem(R.id.main_logout_btn)
        logout.setOnMenuItemClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(
                Intent(
                    this,
                    WelcomeActivity::class.java
                )
            )
            finish()
            true
        }

        ////////////////////////////////////////////////////////////////////////////////////////////

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        super.onOptionsItemSelected(item)
        if (item!!.itemId == R.id.main_logout_btn) {
            FirebaseAuth.getInstance().signOut()
            startActivity(
                Intent(
                    this,
                    WelcomeActivity::class.java
                )
            )
            finish()
        }
        if (item.itemId == R.id.changePassword) {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        return true
    }
    private fun sendMailtoResetPassword() {
        val currentUser = auth.currentUser
        FirebaseAuth.getInstance().sendPasswordResetEmail(  currentUser?.email.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this,"Email For Password Reset is Sent Successfully",Toast.LENGTH_LONG).show()
                }
            }
    }
    override fun onBackPressed() {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        finish()
    }
}
