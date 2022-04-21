package com.f08.prosaver.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.f08.prosaver.R
import com.f08.prosaver.activity.ui.welcom.WelcomScreen1
import com.f08.prosaver.activity.ui.welcom.WelcomScreen2
import com.f08.prosaver.activity.ui.welcom.WelcomScreen3

class WelcomActivity : AppCompatActivity() {

    var count = 1
    lateinit var dot1:TextView
    lateinit var dot2:TextView
    lateinit var dot3:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcom_activity)
         dot1 = findViewById<TextView>(R.id.dot1)
         dot2 = findViewById<TextView>(R.id.dot2)
         dot3 = findViewById<TextView>(R.id.dot3)
        val buttonNext = findViewById<Button>(R.id.btn_next_dots)

        initFragment()
        buttonNext.setOnClickListener {
            if (savedInstanceState == null) {
                if (count==1){
                    goNextFragment(WelcomScreen2.newInstance())
                }else if (count==2){
                    goNextFragment(WelcomScreen3.newInstance())
                    buttonNext.text = "DONE"
                }else if (count==3){
                    val mShared = getSharedPreferences("settings", Context.MODE_PRIVATE)
                    mShared.edit().putBoolean("welcom_viewed",true).commit()
                    startActivity(Intent(this,MainActivity::class.java))
                }
            }
        }

    }

    fun goNextFragment(fm:Fragment){
            supportFragmentManager.beginTransaction()
                .replace(R.id.container,fm)
                .commitNow()
        count+=1
        if (count==2){
            dot1.setBackgroundResource(R.drawable.rounded_dot_false)
            dot2.setBackgroundResource(R.drawable.rounded_dot_true)
        }else if (count==3){
            dot1.setBackgroundResource(R.drawable.rounded_dot_false)
            dot2.setBackgroundResource(R.drawable.rounded_dot_false)
            dot3.setBackgroundResource(R.drawable.rounded_dot_true)
        }
    }
    fun initFragment(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.container,WelcomScreen1.newInstance())
            .commitNow()
        dot1.setBackgroundResource(R.drawable.rounded_dot_true)
    }
}