package com.example.chatty.DashboardScreen

import com.example.chatty.ModelClasses.ChatMessage
import com.example.chatty.ModelClasses.User
import com.example.chatty.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.latestmessagerow.view.*
import java.lang.Exception
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class LatestMessageRow(val chatMessage: ChatMessage) : Item<ViewHolder>(){
    var chatPartnerUser: User? = null
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.latestMessage.text= chatMessage.text
        //////////////////////////////////////////////////////////////////////////////////////
        val Date = chatMessage.timestamp
        viewHolder.itemView.timeStamp.text=returnDateString(Date)
        //////////////////////////////////////////////////////////////////////////////////////
        val chatPartnerId:String
        if(chatMessage.fromId== FirebaseAuth.getInstance().uid){
            chatPartnerId=chatMessage.toId
        }
        else{
            chatPartnerId=chatMessage.fromId
        }
        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun onDataChange(p0: DataSnapshot) {
                chatPartnerUser = p0.getValue(User::class.java)
                viewHolder.itemView.latestUserName.text=chatPartnerUser?.username
                val targetImageView= viewHolder.itemView.userImage
                Picasso.get()
                    .load(chatPartnerUser?.image)
                    .networkPolicy(NetworkPolicy.OFFLINE, NetworkPolicy.NO_CACHE)
                    .placeholder(R.drawable.user)
                    .into(targetImageView, object : com.squareup.picasso.Callback{
                        override fun onSuccess() {

                        }

                        override fun onError(e: Exception?) {
                            Picasso.get()
                                .load(chatPartnerUser?.image)
                                .placeholder(R.drawable.user)
                                .into(targetImageView, object : com.squareup.picasso.Callback{
                                    override fun onSuccess() {

                                    }

                                    override fun onError(e: Exception?) {

                                    }

                                } )
                        }

                    } )
            }
        })
    }
    override fun getLayout(): Int {
        return R.layout.latestmessagerow
    }
    fun returnDateString(isoString: Long) : String {
        val outDateString: DateFormat = SimpleDateFormat("E, h:mm a", Locale.getDefault())
       //val outDateString = SimpleDateFormat("E, h:mm a", Locale.getDefault())
        val result = Date(isoString)
        return outDateString.format(result)
    }
}