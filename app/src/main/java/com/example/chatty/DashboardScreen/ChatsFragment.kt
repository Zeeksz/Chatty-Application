package com.example.chatty.DashboardScreen

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.chatty.ModelClasses.ChatMessage
import com.example.chatty.ModelClasses.User
import com.example.chatty.Notifications.Token
import com.example.chatty.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder

/**
 * A simple [Fragment] subclass.
 */
class ChatsFragment : Fragment() {

    companion object {
        var currentUser: User? = null

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_chats, container, false)
        val fab: FloatingActionButton = view.findViewById(R.id.message_button)
        fab.setOnClickListener {
            val myIntent = Intent(activity, NewMessageActivity::class.java)
            activity?.finish()
            activity!!.startActivity(myIntent)
        }
        view.findViewById<RecyclerView>(R.id.recyclerviewForLatestMessages).adapter = adapter
        adapter.setOnItemClickListener { item, view ->
            val intent = Intent(activity, ChatLogActivity::class.java)
            val row = item as LatestMessageRow
            intent.putExtra(NewMessageActivity.USER_KEY, row.chatPartnerUser)
            startActivity(intent)
            activity?.finish()
        }
        listenForLatestMessages()
        fetchCurrentUser()
        updateToken(FirebaseInstanceId.getInstance().token)
        return view
    }

    private fun updateToken(token: String?) {
        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
        val token1 = Token(token!!)
        ref.child(FirebaseAuth.getInstance().uid.toString()).setValue(token1)
    }

    val adapter = GroupAdapter<ViewHolder>()

    val latestMessagesList = ArrayList<ChatMessage>()

    private fun listenForLatestMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

                for (i in 0 until latestMessagesList.size) {
                    if (latestMessagesList[i].fromId == p0.key) {
                        latestMessagesList.remove(latestMessagesList[i])
                        val chatmessage = p0.getValue(ChatMessage::class.java) ?: return
                        latestMessagesList.add(chatmessage)
                    }
                }
                refreshRecyclerViewMessages()
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {

                val chatmessage = p0.getValue(ChatMessage::class.java) ?: return
                latestMessagesList.add(chatmessage)
                refreshRecyclerViewMessages()
            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

        })
    }

    private fun refreshRecyclerViewMessages() {
        adapter.clear()
        latestMessagesList.sortWith(Comparator { o1, o2 -> if (o1.timestamp < o2.timestamp) 1 else -1 })
        latestMessagesList.forEach {
            adapter.add(LatestMessageRow(it))
        }
    }

    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(
                    User::class.java
                )
            }
        })
    }

}
