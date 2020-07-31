package com.example.chatty.DashboardScreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.chatty.ModelClasses.User
import com.example.chatty.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_all_users.*
import kotlinx.android.synthetic.main.users_display.view.*
import java.lang.Exception

class AllUsersActivity : AppCompatActivity() {
    private lateinit var ntoolbar:androidx.appcompat.widget.Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_users)
        ntoolbar=findViewById(R.id.usersToolbar)
        setSupportActionBar(ntoolbar)
        ntoolbar.setNavigationIcon(R.drawable.backarrow)
        ntoolbar.setNavigationOnClickListener {
            finish()
        }
        fetchUsers()
    }

    private fun fetchUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun onDataChange(p0: DataSnapshot) {
                val adaptor = GroupAdapter<ViewHolder>()
                p0.children.forEach{
                    val user = it.getValue(User::class.java)
                    adaptor.add(
                        UserItem(
                            user!!
                        )

                    )
                }
                recyclerViewAllUsers.adapter=adaptor
            }

        })
    }

    class UserItem(val user: User): Item<ViewHolder>(){
        override fun getLayout(): Int {
            return R.layout.users_display
        }
        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.userName.text = user.username
            viewHolder.itemView.userStatus.text=user.status
            Picasso.get()
                .load(user.image)
                .networkPolicy(NetworkPolicy.OFFLINE, NetworkPolicy.NO_CACHE)
                .placeholder(R.drawable.user)
                .into(viewHolder.itemView.userImage, object : com.squareup.picasso.Callback{
                    override fun onSuccess() {

                    }

                    override fun onError(e: Exception?) {
                        Picasso.get()
                            .load(user.image)
                            .placeholder(R.drawable.user)
                            .into(viewHolder.itemView.userImage, object : com.squareup.picasso.Callback{
                                override fun onSuccess() {

                                }

                                override fun onError(e: Exception?) {

                                }

                            } )
                    }

                } )
        }

    }

    override fun onBackPressed() {
        intent = Intent(this, Dashboardactivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }
}
