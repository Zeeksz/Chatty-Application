package com.example.chatty.DashboardScreen

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.chatty.ModelClasses.ChatMessage
import com.example.chatty.ModelClasses.User
import com.example.chatty.Notifications.*
import com.example.chatty.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import com.xwray.groupie.*
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chatfromrow.view.*
import kotlinx.android.synthetic.main.chattorow.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ChatLogActivity : AppCompatActivity() {
    val adaptor = GroupAdapter<ViewHolder>()
    var toUser: User? = null
    var notify = false
    var apiService: APIService? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        recyclerViewChats.adapter = adaptor
        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        chatUserName.text = toUser?.username
        listenForMessages()
        sendButton.setOnClickListener {
            if (messageEditText.text.trim().toString() == "") {
                Toast.makeText(
                    this@ChatLogActivity,
                    "LOOKS LIKE YOU FORGOT TO ENTER MESSAGE",
                    Toast.LENGTH_SHORT
                ).show()
                messageEditText.text.clear()
            } else {
                notify = true
                performSendMessage()
            }
        }
        val backbutton = findViewById<ImageView>(R.id.chatBackBtn)
        backbutton.setOnClickListener {
            finish()
            val i = Intent(this, Dashboardactivity::class.java)
            startActivity(i)
        }

        apiService = Client.Client.getClient("https://fcm.googleapis.com/")!!.create(
            APIService::class.java
        )
        recyclerViewChats.scrollToPosition(adaptor.itemCount - 1)
    }

    private fun listenForMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)
                if (chatMessage != null) {
                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                        adaptor.add(
                            ChatToItems(
                                chatMessage
                            )
                        )
                    } else {
                        val fromUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
                        adaptor.add(
                            ChatFromItems(
                                chatMessage.text,
                                fromUser
                            )
                        )
                    }
                }
                recyclerViewChats.scrollToPosition(adaptor.itemCount - 1)
            }

            ////////////////////////////////////////////////////////////////////////////////////////////////////
            override fun onChildRemoved(p0: DataSnapshot) {
                overridePendingTransition(0, 0)
                overridePendingTransition(0, 0)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                recreate()
            }

            ////////////////////////////////////////////////////////////////////////////////////////////////////
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}

        })
    }

    override fun onBackPressed() {
        finish()
        val i = Intent(this, Dashboardactivity::class.java)
        startActivity(i)
    }

    private fun performSendMessage() {

        val text = messageEditText.text.toString()
        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user.uid
        val reference =
            FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val toReference =
            FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()
        val chatMessage = ChatMessage(
            reference.key!!,
            text,
            fromId!!,
            toId,
            System.currentTimeMillis()
        )
        reference.setValue(chatMessage).addOnSuccessListener {
            messageEditText.text.clear()
            recyclerViewChats.scrollToPosition(adaptor.itemCount - 1)
        }
        toReference.setValue(chatMessage)

        val latestMessageRef =
            FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessageRef.setValue(chatMessage)

        val latestMessageToRef =
            FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessageToRef.setValue(chatMessage)

        ////////////////////////////////////////////////////////////////////////////////////////////////
        val reference2 = FirebaseDatabase.getInstance().getReference("users").child("$fromId")
        reference2.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                val user1 = p0.getValue(User::class.java)
                if (notify) {
                    sendNotification(toUser!!.uid, user1!!.username, chatMessage.text)
                }
                notify = false
            }
        })
        /////////////////////////////////////////////////////////////////////////////////////////////
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    private fun sendNotification(toId: String?, username: String, text: String) {
            val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
            val query = ref.orderByKey().equalTo(toId)
            query.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    for (dataSnapshot in p0.children) {
                        val token: Token? = dataSnapshot.getValue(Token::class.java)
                        val data = Data(
                            FirebaseAuth.getInstance().uid.toString(),
                            R.mipmap.ic_launcher,
                            "$username: $text",
                            "New Message",
                            toUser!!.uid
                        )
                        val sender = Sender(
                            data,
                            token!!.getToken().toString()
                        )
                        apiService!!.sendNotification(sender)
                            .enqueue(object : Callback<MyResponse> {
                                override fun onFailure(call: Call<MyResponse>, t: Throwable) {}
                                override fun onResponse(
                                    call: Call<MyResponse>,
                                    response: Response<MyResponse>
                                ) {
                                    if (response.code() == 200) {
                                        if (response.body()!!.success !== 1) {
                                            Toast.makeText(
                                                this@ChatLogActivity,
                                                "Failed! Nothing Happened",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                }

                            })
                    }
                }
            })

    }

}

class ChatFromItems(val text: String, val user: User) : Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.chatfromrow
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textViewFromRow.text = text
        val uri = user.image
        val targetImage = viewHolder.itemView.senderImage
        Picasso.get()
            .load(uri)
            .networkPolicy(NetworkPolicy.OFFLINE, NetworkPolicy.NO_CACHE)
            .placeholder(R.drawable.user)
            .into(targetImage, object : com.squareup.picasso.Callback {
                override fun onSuccess() {

                }

                override fun onError(e: Exception?) {
                    Picasso.get()
                        .load(uri)
                        .placeholder(R.drawable.user)
                        .into(targetImage, object : com.squareup.picasso.Callback {
                            override fun onSuccess() {

                            }

                            override fun onError(e: Exception?) {

                            }

                        })
                }

            })
    }
}

class ChatToItems(val chatMessage: ChatMessage) : Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.chattorow
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textViewToRow.text = chatMessage.text
        viewHolder.itemView.textViewToRow.setOnClickListener {
            val options = arrayOf<CharSequence>(
                "Delete Message",
                "Cancel"
            )
            val builder = AlertDialog.Builder(viewHolder.itemView.context)
            builder.setTitle("What do you want?")
            builder.setItems(options, DialogInterface.OnClickListener { dialog, which ->
                if (which == 0) {
                    deleteSendMessage(
                        viewHolder,
                        chatMessage.toId,
                        chatMessage.fromId,
                        chatMessage.id,
                        position
                    )
                }
            })
            builder.show()
        }
    }

    private fun deleteSendMessage(
        viewHolder: ViewHolder,
        toId: String,
        fromId: String,
        id2: String,
        position: Int
    ) {
        val reference = FirebaseDatabase.getInstance().reference
            .child("user-messages").child(fromId)
            .child(toId).child(id2).removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        viewHolder.itemView.context,
                        "Message Deleted Successfully!",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        viewHolder.itemView.context,
                        "Failed to Delete",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        Log.d("Important", "$fromId $toId $id2")
    }
}
