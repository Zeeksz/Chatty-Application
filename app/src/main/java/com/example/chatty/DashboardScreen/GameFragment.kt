package com.example.chatty.DashboardScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.chatty.R
import com.tapadoo.alerter.Alerter
import kotlinx.android.synthetic.main.fragment_requests.*
import java.util.*
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 */
class GameFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_requests, container, false)
        var cellID = 0
        var buSelected:Button = view.findViewById<Button>(R.id.bu1)
        val bu1 = view.findViewById<Button>(R.id.bu1)
        val bu2 = view.findViewById<Button>(R.id.bu2)
        val bu3 = view.findViewById<Button>(R.id.bu3)
        val bu4 = view.findViewById<Button>(R.id.bu4)
        val bu5 = view.findViewById<Button>(R.id.bu5)
        val bu6 = view.findViewById<Button>(R.id.bu6)
        val bu7 = view.findViewById<Button>(R.id.bu7)
        val bu8 = view.findViewById<Button>(R.id.bu8)
        val bu9 = view.findViewById<Button>(R.id.bu9)
        bu1.setOnClickListener {
            cellID=1
            buSelected = view.findViewById<Button>(R.id.bu1)
        //    Toast.makeText(context,"Hi Im 1",Toast.LENGTH_SHORT).show()
            PlayGame(cellID,buSelected)
        }
        bu2.setOnClickListener {
            cellID=2
            buSelected=view.findViewById<Button>(R.id.bu2)
        //    Toast.makeText(context,"Hi Im 2",Toast.LENGTH_SHORT).show()
            PlayGame(cellID,buSelected)
        }
        bu3.setOnClickListener {
            cellID=3
            buSelected= view.findViewById<Button>(R.id.bu3)
          //  Toast.makeText(context,"Hi Im 3",Toast.LENGTH_SHORT).show()
            PlayGame(cellID,buSelected)
        }
        bu4.setOnClickListener {
            cellID=4
            buSelected= view.findViewById<Button>(R.id.bu4)
        //    Toast.makeText(context,"Hi Im 4",Toast.LENGTH_SHORT).show()
            PlayGame(cellID,buSelected)
        }
        bu5.setOnClickListener {
            cellID=5
            buSelected= view.findViewById<Button>(R.id.bu5)
       //     Toast.makeText(context,"Hi Im 5",Toast.LENGTH_SHORT).show()
            PlayGame(cellID,buSelected)
        }
        bu6.setOnClickListener {
            cellID=6
            buSelected= view.findViewById<Button>(R.id.bu6)
        //    Toast.makeText(context,"Hi Im 6",Toast.LENGTH_SHORT).show()
            PlayGame(cellID,buSelected)}
        bu7.setOnClickListener {
            cellID=7
            buSelected= view.findViewById<Button>(R.id.bu7)
        //    Toast.makeText(context,"Hi Im 7",Toast.LENGTH_SHORT).show()
            PlayGame(cellID,buSelected)
        }
        bu8.setOnClickListener {
            cellID=8
            buSelected= view.findViewById<Button>(R.id.bu8)
        //    Toast.makeText(context,"Hi Im 8",Toast.LENGTH_SHORT).show()
            PlayGame(cellID,buSelected)
        }
        bu9.setOnClickListener {
            cellID=9
            buSelected= view.findViewById<Button>(R.id.bu9)
        //    Toast.makeText(context,"Hi Im 9",Toast.LENGTH_SHORT).show()
            PlayGame(cellID,buSelected)
        }

        val newGame = view.findViewById<Button>(R.id.newGame)
        newGame.setOnClickListener {
            fragmentManager
                ?.beginTransaction()
                ?.detach(this)
                ?.attach(this)
                ?.commit()
        }
        return view
    }
    var player1=ArrayList<Int>()
    var player2=ArrayList<Int>()
    var ActivePlayer=1
    fun PlayGame(cellID:Int,buSelected:Button){

        if(ActivePlayer==1){
            buSelected.text="X"
            buSelected.setBackgroundResource(R.drawable.bluebutton)
            player1.add(cellID)
            ActivePlayer=2
            AutoPlay()
        }else{
            buSelected.text="O"
            buSelected.setBackgroundResource(R.drawable.purplebuttom)
            player2.add(cellID)
            ActivePlayer=1
        }


        buSelected.isEnabled=false
        CheckWiner()
    }
    fun  CheckWiner(){
        var winer=-1

        // row 1
        if(player1.contains(1) && player1.contains(2) && player1.contains(3)){
            winer=1
        }
        if(player2.contains(1) && player2.contains(2) && player2.contains(3)){
            winer=2
        }


        // row 2
        if(player1.contains(4) && player1.contains(5) && player1.contains(6)){
            winer=1
        }
        if(player2.contains(4) && player2.contains(5) && player2.contains(6)){
            winer=2
        }




        // row 3
        if(player1.contains(7) && player1.contains(8) && player1.contains(9)){
            winer=1
        }
        if(player2.contains(7) && player2.contains(8) && player2.contains(9)){
            winer=2
        }



        // col 1
        if(player1.contains(1) && player1.contains(4) && player1.contains(7)){
            winer=1
        }
        if(player2.contains(1) && player2.contains(4) && player2.contains(7)){
            winer=2
        }



        // col 2
        if(player1.contains(2) && player1.contains(5) && player1.contains(8)){
            winer=1
        }
        if(player2.contains(2) && player2.contains(5) && player2.contains(8)){
            winer=2
        }


        // col 3
        if(player1.contains(3) && player1.contains(6) && player1.contains(9)){
            winer=1
        }
        if(player2.contains(3) && player2.contains(6) && player2.contains(9)){
            winer=2
        }

        //diagonal 1
        if(player1.contains(1) && player1.contains(5) && player1.contains(9)){
            winer=1
        }
        if(player2.contains(1) && player2.contains(5) && player2.contains(9)){
            winer=2
        }
        //diagonal 2
        if(player1.contains(3) && player1.contains(5) && player1.contains(7)){
            winer=1
        }
        if(player2.contains(3) && player2.contains(5) && player2.contains(7)){
            winer=2
        }


        if( winer != -1){

            if (winer==1){
                Alerter.Companion.create(activity)
                    .setTitle("C H A T T Y")
                    .setText("Hey CONGRATULATIONS YOU WON THE GAME")
                    .setIcon(R.mipmap.ic_launcher)
                    .setBackgroundColorRes(R.color.colorPrimary)
                    .setDuration(4000)
                    .setOnClickListener(View.OnClickListener {
                    })
                    .show()
                //Toast.makeText(context," Player 1  win the game", Toast.LENGTH_LONG).show()
            }else{
                Alerter.Companion.create(activity)
                    .setTitle("C H A T T Y")
                    .setText("SORRY! BETTER LUCK NEXT TIME PLAYER 2 WON THE GAME")
                    .setIcon(R.mipmap.ic_launcher)
                    .setBackgroundColorRes(R.color.colorAccent)
                    .setDuration(4000)
                    .setOnClickListener(View.OnClickListener {
                    })
                    .show()
                //Toast.makeText(context," Player 2  win the game", Toast.LENGTH_LONG).show()
            }

        }

    }
    fun AutoPlay(){

        var emptyCells=ArrayList<Int>()
        for ( cellID in 1..9){

            if(!( player1.contains(cellID) || player2.contains(cellID))) {
                emptyCells.add(cellID)
            }
        }


        val r= Random()
        val randIndex=r.nextInt(emptyCells.size-0)+0
        val cellID= emptyCells[randIndex]

        var buSelect:Button?
        when(cellID){
            1-> buSelect=bu1
            2-> buSelect=bu2
            3-> buSelect=bu3
            4-> buSelect=bu4
            5-> buSelect=bu5
            6-> buSelect=bu6
            7-> buSelect=bu7
            8-> buSelect=bu8
            9-> buSelect=bu9
            else->{
                buSelect=bu1
            }
        }

        PlayGame(cellID,buSelect)

    }

}

