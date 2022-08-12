package com.misa.fresher.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.misa.fresher.R
import com.misa.fresher.model.Category

class AdapterCate(
    context: Context,
    private val layoutResource: Int,
    private val categories: List<Category>
) : ArrayAdapter<Category>(context, layoutResource, categories) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(position, convertView, parent)
    }

    private fun createViewFromResource(
        position: Int,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val view: TextView = convertView as TextView? ?: LayoutInflater.from(context)
            .inflate(layoutResource, parent, false) as TextView
        view.apply {
            this.text = categories[position].title
            this.setTextColor(resources.getColor( R.color.violet_dark))
        }
        return view
    }
}