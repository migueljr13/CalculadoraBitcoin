package com.courses.calculadorabitcoin

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.courses.calculadorabitcoin.adapter.CriptomoedaAdapter
import com.courses.calculadorabitcoin.databinding.*
import com.courses.calculadorabitcoin.model.pojo.CriptomoedaItem
import com.courses.calculadorabitcoin.utils.*
import com.courses.calculadorabitcoin.utils.formataDouble
import com.courses.calculadorabitcoin.utils.formataMoeda
import com.courses.calculadorabitcoin.utils.formataVolume
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONObject
import java.net.URL
import java.util.*

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private var volummeCriptomoeda: Double = 0.0
    private var minimaCriptomoeda: Double = 0.0
    private var maximaCriptomoeda: Double = 0.0
    private var cotacaoCriptomoeda: Double = 0.0
    private lateinit var adapter: CriptomoedaAdapter
    private var binding: ActivityMainBinding? = null
    private var bindingCotacaoBinding: BlocoCotacaoBinding? = null
    private var bindingDadosBasicosBinding: BlocoCotacaoDadosBasicosBinding? = null
    private var bindingEntradaBinding: BlocoEntradaBinding? = null
    private var bindingSaidaBinding : BlocoSaidaBinding? = null

    companion object {
        private const val BASE_URL = "https://www.mercadobitcoin.net/api/"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        bindingCotacaoBinding = binding?.blocoCotacao
        bindingDadosBasicosBinding = bindingCotacaoBinding?.dadosBasicos
        bindingEntradaBinding = binding?.blocoEntrada
        bindingSaidaBinding = binding?.blocoSaida
        setContentView(binding?.root)

        preencheSpinner()

        bindingCotacaoBinding?.spnCriptomoeda?.onItemSelectedListener = this

        with(bindingEntradaBinding!!) {
            btnCalcular.setOnClickListener { calcular() }
            btnLimpar.setOnClickListener {
                bindingEntradaBinding?.txtValor?.text?.clear()
                bindingSaidaBinding?.txtQtdBitcoins?.text = ""
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
        bindingCotacaoBinding = null
        bindingDadosBasicosBinding = null
        bindingEntradaBinding = null
        bindingSaidaBinding = null
    }

    override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, positon: Int, id: Long) {
        buscaCotacao("${BASE_URL}${getCoinSelecionado(positon)}/ticker/")
    }

    override fun onNothingSelected(adapterView: AdapterView<*>?) {
        Toast.makeText(
            this@MainActivity,
            "Nenhuma criptomoeda escolhida.",
            Toast.LENGTH_SHORT
        ).show()
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
        bindingCotacaoBinding?.spnCriptomoeda?.adapter = adapter
    }

    private fun calcular() {

        var valor_digitado: Double
        with(bindingEntradaBinding!!) {

            if (txtValor.text.isEmpty()) {
                txtValor.error = "Preencha um valor"
                return
            }
            valor_digitado = txtValor.text.toString().formataDouble()
        }
        val resultado = if (cotacaoCriptomoeda > 0) valor_digitado / cotacaoCriptomoeda else 0.0
        bindingSaidaBinding?.txtQtdBitcoins?.text = formataQuantidadeBitcoin(resultado)
    }

    private fun buscaCotacao(url: String) {

        doAsync {
            val resposta = URL(url).readText()
            cotacaoCriptomoeda = JSONObject(resposta).getJSONObject("ticker").getDouble("last")
            maximaCriptomoeda = JSONObject(resposta).getJSONObject("ticker").getDouble("high")
            minimaCriptomoeda = JSONObject(resposta).getJSONObject("ticker").getDouble("low")
            volummeCriptomoeda = JSONObject(resposta).getJSONObject("ticker").getDouble("vol")

            uiThread {
                with(bindingDadosBasicosBinding!!) {
                    txtCotacao.setText(formataMoeda(cotacaoCriptomoeda))
                    txtUltimo.setText(formataMoeda(cotacaoCriptomoeda))
                    txtMaxima.setText(formataMoeda(maximaCriptomoeda))
                    txtMinima.setText(formataMoeda(minimaCriptomoeda))
                    txtVolume.setText(formataVolume(volummeCriptomoeda))
                }
            }
        }
    }
}