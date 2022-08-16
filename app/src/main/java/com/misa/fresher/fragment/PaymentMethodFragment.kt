package com.misa.fresher.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.misa.fresher.databinding.FragmentPaymentMethodBinding
import com.misa.fresher.viewmodel.PaymentViewModel

class PaymentMethodFragment : Fragment() {
    val binding: FragmentPaymentMethodBinding by lazy {
        FragmentPaymentMethodBinding.inflate(
            layoutInflater
        )
    }
    val viewModel: PaymentViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        binding.ibBack.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.btnMethod1.setOnClickListener {
            viewModel.add(1)
            activity?.onBackPressed()
        }
        binding.btnMethod2.setOnClickListener {
            viewModel.add(2)
            activity?.onBackPressed()
        }
    }

}