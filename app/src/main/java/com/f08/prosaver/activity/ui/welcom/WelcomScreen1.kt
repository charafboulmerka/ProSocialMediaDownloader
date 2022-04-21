package com.f08.prosaver.activity.ui.welcom

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.f08.prosaver.R

class WelcomScreen1 : Fragment() {

    companion object {
        fun newInstance() = WelcomScreen1()
    }

    //private lateinit var viewModel: WelcomViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.welcom_screen1, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}