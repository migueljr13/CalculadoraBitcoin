package com.courses.calculadorabitcoin

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONObject
import java.net.URL
import java.text.NumberFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    var cotacaoBitcoin: Double = 0.0
    private lateinit var txt_cotacao: TextView
    private lateinit var txt_qtd_bitcoin: TextView
    private lateinit var txt_valor: TextView
    private lateinit var btn_calcular: Button
    private lateinit var btn_limpar: Button

    companion object {
        private const val BASE_URL = "https://www.mercadobitcoin.net/api/BTC/ticker/"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txt_cotacao = findViewById(R.id.txt_cotacao)
        txt_qtd_bitcoin = findViewById(R.id.txt_qtd_bitcoins)
        txt_valor = findViewById(R.id.txt_valor)
        btn_calcular = findViewById(R.id.btn_calcular)
        btn_limpar = findViewById(R.id.btn_limpar)

        buscaCotacao()

        btn_calcular.setOnClickListener {
            calcular()
        }
        btn_limpar.setOnClickListener {
            txt_valor.text = ""
            txt_qtd_bitcoin.text = formataQuantidadeBitcoin(0.0)
        }
    }

    private fun calcular() {

        if (txt_valor.text.isEmpty()) {
            txt_valor.error = "Preencha um valor"
            return
        }

        val valor_digitado = txt_valor.text.toString().formataDouble()

        val resultado = if (cotacaoBitcoin > 0) valor_digitado / cotacaoBitcoin else 0.0
        txt_qtd_bitcoin.text = formataQuantidadeBitcoin(resultado)
    }

    private fun buscaCotacao() {

        doAsync {
            val resposta = URL(BASE_URL).readText()
            cotacaoBitcoin = JSONObject(resposta).getJSONObject("ticker").getDouble("last")
            val formatacao = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
            val cotacaoFormatada = formatacao.format(cotacaoBitcoin)
            uiThread {
                txt_cotacao.setText(cotacaoFormatada)
            }
        }
    }
}

/**
 * Formata a quantidade de bitcoins com o seu padrão especificado
 *
 * @author Miguel
 * @param valor
 * @return String
 *
 **/
fun formataQuantidadeBitcoin(valor: Double) = "%8f".format(valor)

/**
* Formata um valor monetário do tipo de dado String para o tipo de dado Double
 *
 * @author Miguel
 * @return Double
* */
fun String.formataDouble() = this.replace(",", ".").toDouble()
