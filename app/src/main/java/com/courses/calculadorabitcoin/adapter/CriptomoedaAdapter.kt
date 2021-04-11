package com.courses.calculadorabitcoin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.courses.calculadorabitcoin.R
import com.courses.calculadorabitcoin.model.pojo.CriptomoedaItem

class CriptomoedaAdapter(context: Context, criptomoedaItem: ArrayList<CriptomoedaItem>) :
    ArrayAdapter<CriptomoedaItem>(context, 0, criptomoedaItem) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return criarView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return criarView(position, convertView, parent)
    }

    private fun criarView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view  = LayoutInflater.from(context)
                .inflate(R.layout.criptomoeda_item, parent, false)

         val iv_logo_cripto = view.findViewById(R.id.iv_logo_cripto) as ImageView
         val tv_desc_cripto = view.findViewById(R.id.tv_cripto) as TextView

        val itemAtual = getItem(position)

        if (itemAtual != null) {
            iv_logo_cripto.setImageResource(itemAtual.criptoImagem)
            tv_desc_cripto.text = itemAtual.criptoName
        }
        return view
    }


}