package com.courses.calculadorabitcoin

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.courses.calculadorabitcoin.adapter.CriptomoedaAdapter
import com.courses.calculadorabitcoin.model.pojo.CriptomoedaItem
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONObject
import java.net.URL
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private var volummeCriptomoeda: Double = 0.0
    private var minimaCriptomoeda: Double = 0.0
    private var maximaCriptomoeda: Double = 0.0
    private var cotacaoCriptomoeda: Double = 0.0

    private lateinit var txt_cotacao: TextView
    private lateinit var txt_ultimo: TextView
    private lateinit var txt_maxima: TextView
    private lateinit var txt_minima: TextView
    private lateinit var txt_volume: TextView

    private lateinit var txt_qtd_bitcoin: TextView
    private lateinit var txt_valor: TextView
    private lateinit var btn_calcular: Button
    private lateinit var btn_limpar: Button
    private lateinit var spnCriptomoeda: Spinner

    private lateinit var adapter: CriptomoedaAdapter

    companion object {
        private const val BASE_URL = "https://www.mercadobitcoin.net/api/"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txt_cotacao = findViewById(R.id.txt_cotacao)
        txt_ultimo = findViewById(R.id.txt_ultimo)
        txt_maxima = findViewById(R.id.txt_maxima)
        txt_minima = findViewById(R.id.txt_minima)
        txt_volume = findViewById(R.id.txt_volume)

        txt_qtd_bitcoin = findViewById(R.id.txt_qtd_bitcoins)
        txt_valor = findViewById(R.id.txt_valor)
        btn_calcular = findViewById(R.id.btn_calcular)
        btn_limpar = findViewById(R.id.btn_limpar)
        spnCriptomoeda = findViewById(R.id.spnCriptomoeda)

        preencheSpinner()
        spnCriptomoeda.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                positon: Int,
                id: Long
            ) {
                buscaCotacao("${BASE_URL}${getCoinSelecionado(positon)}/ticker/")
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
                Toast.makeText(
                    this@MainActivity,
                    "Nenhuma criptomoeda escolhida.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        btn_calcular.setOnClickListener {
            calcular()
        }
        btn_limpar.setOnClickListener {
            txt_valor.text = ""
            txt_qtd_bitcoin.text = formataQuantidadeBitcoin(0.0)
        }
    }

    /**
     * Retorna a criptomoeda escolhida no spinner.
     *
     * @author Miguel
     * @param positon
     */
    private fun getCoinSelecionado(positon: Int) = when (positon) {
        0 -> "BTC"
        1 -> "ETH"
        else -> "Nenhum"
    }

    private fun preencheSpinner() {

        val listaCriptomoeda = arrayListOf(
            CriptomoedaItem("Bitcoin (BTC)", R.mipmap.ic_btc),
            CriptomoedaItem("Ethereum (ETH)", R.mipmap.ic_eth)
        )
        adapter = CriptomoedaAdapter(this, listaCriptomoeda)
        spnCriptomoeda.adapter = adapter
    }

    private fun calcular() {

        if (txt_valor.text.isEmpty()) {
            txt_valor.error = "Preencha um valor"
            return
        }

        val valor_digitado = txt_valor.text.toString().formataDouble()

        val resultado = if (cotacaoCriptomoeda > 0) valor_digitado / cotacaoCriptomoeda else 0.0
        txt_qtd_bitcoin.text = formataQuantidadeBitcoin(resultado)
    }

    private fun buscaCotacao(url: String) {

        doAsync {
            val resposta = URL(url).readText()
            cotacaoCriptomoeda = JSONObject(resposta).getJSONObject("ticker").getDouble("last")
            maximaCriptomoeda = JSONObject(resposta).getJSONObject("ticker").getDouble("high")
            minimaCriptomoeda = JSONObject(resposta).getJSONObject("ticker").getDouble("low")
            volummeCriptomoeda = JSONObject(resposta).getJSONObject("ticker").getDouble("vol")

            uiThread {
                txt_cotacao.setText(formataMoeda(cotacaoCriptomoeda))
                txt_ultimo.setText(formataMoeda(cotacaoCriptomoeda))
                txt_maxima.setText(formataMoeda(maximaCriptomoeda))
                txt_minima.setText(formataMoeda(minimaCriptomoeda))
                txt_volume.setText(formataVolume(volummeCriptomoeda))
            }
        }
    }
}

private fun formataVolume(valor : Double) : String {
    val formatacao = DecimalFormat("##.##")
    return formatacao.format(valor).replace('.', ',')
}

/**
 * Formata a moeda brasileira (R$) de um valor do tipo Double
 *
 * @author Miguel
 * @param valor
 **/
private fun formataMoeda(valor: Double): String {

    val formatacao = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    val cotacaoFormatada = formatacao.format(valor)
    return cotacaoFormatada
}

/**
 * Formata a quantidade de bitcoins com o seu padrão especificado
 *
 * @author Miguel
 * @param valor
 * @return String
 *
 **/
private fun formataQuantidadeBitcoin(valor: Double) = "%8f".format(valor)

/**
 * Formata um valor monetário do tipo de dado String para o tipo de dado Double
 *
 * @author Miguel
 * @return Double
 * */
private fun String.formataDouble() = this.replace(",", ".").toDouble()
