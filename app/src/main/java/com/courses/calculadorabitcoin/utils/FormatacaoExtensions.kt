package com.courses.calculadorabitcoin.utils

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

var locale = Locale("pt", "BR")

internal fun formataVolume(valor: Double): String {
    val formatacao = DecimalFormat("##.##")
    return formatacao.format(valor).replace('.', ',')
}

/**
 * Formata a moeda brasileira (R$) de um valor do tipo Double
 *
 * @author Miguel
 * @param valor
 **/
internal fun formataMoeda(valor: Double): String {

    val formatacao = NumberFormat.getCurrencyInstance(locale)
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
internal fun formataQuantidadeBitcoin(valor: Double) = "%8f".format(valor)

/**
 * Formata um valor monetário do tipo de dado String para o tipo de dado Double
 *
 * @author Miguel
 * @return Double
 **/
internal fun String.formataDouble() = this.replace(",", ".").toDouble()