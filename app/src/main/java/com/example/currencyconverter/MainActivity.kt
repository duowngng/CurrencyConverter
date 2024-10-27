package com.example.currencyconverter

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private var isSourceFocused = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sourceCurrencySpinner = findViewById<Spinner>(R.id.sourceCurrency)
        val targetCurrencySpinner = findViewById<Spinner>(R.id.targetCurrency)
        val sourceAmountEditText = findViewById<EditText>(R.id.sourceAmount)
        val targetAmountEditText = findViewById<EditText>(R.id.targetAmount)

        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.currency_codes, android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        sourceCurrencySpinner.adapter = adapter
        targetCurrencySpinner.adapter = adapter

        sourceAmountEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                isSourceFocused = true
                sourceAmountEditText.setText("") // Clear existing text
                sourceAmountEditText.addTextChangedListener(sourceTextWatcher)
                targetAmountEditText.removeTextChangedListener(targetTextWatcher)
            }
        }

        targetAmountEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                isSourceFocused = false
                targetAmountEditText.setText("") // Clear existing text
                targetAmountEditText.addTextChangedListener(targetTextWatcher)
                sourceAmountEditText.removeTextChangedListener(sourceTextWatcher)
            }
        }

        sourceCurrencySpinner.onItemSelectedListener = currencySelectionListener
        targetCurrencySpinner.onItemSelectedListener = currencySelectionListener
    }

    private val sourceTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (isSourceFocused) {
                convertCurrency(isSourceToTarget = true)
            }
        }
        override fun afterTextChanged(s: Editable?) {}
    }

    private val targetTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (!isSourceFocused) {
                convertCurrency(isSourceToTarget = false)
            }
        }
        override fun afterTextChanged(s: Editable?) {}
    }

    private val currencySelectionListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
            convertCurrency(isSourceToTarget = isSourceFocused)
        }

        override fun onNothingSelected(parent: AdapterView<*>) {}
    }

    private fun convertCurrency(isSourceToTarget: Boolean) {
        val sourceCurrency = findViewById<Spinner>(R.id.sourceCurrency).selectedItem.toString()
        val targetCurrency = findViewById<Spinner>(R.id.targetCurrency).selectedItem.toString()

        if (isSourceToTarget) {
            val sourceAmountText = findViewById<EditText>(R.id.sourceAmount).text.toString()
            val sourceAmount = sourceAmountText.toDoubleOrNull() ?: return
            val conversionRate = getConversionRate(sourceCurrency, targetCurrency)
            val targetAmount = sourceAmount * conversionRate
            findViewById<EditText>(R.id.targetAmount).setText(targetAmount.toString())
        } else {
            val targetAmountText = findViewById<EditText>(R.id.targetAmount).text.toString()
            val targetAmount = targetAmountText.toDoubleOrNull() ?: return
            val conversionRate = getConversionRate(targetCurrency, sourceCurrency)
            val sourceAmount = targetAmount * conversionRate
            findViewById<EditText>(R.id.sourceAmount).setText(sourceAmount.toString())
        }
    }

    private fun getConversionRate(sourceCurrency: String, targetCurrency: String): Double {
        return when (sourceCurrency to targetCurrency) {
            "USD" to "VND" -> 25355.0
            "VND" to "USD" -> 1 / 25355.0
            "JPY" to "VND" -> 167.5
            "VND" to "JPY" -> 1 / 167.5
            "USD" to "JPY" -> 152.3
            "JPY" to "USD" -> 1 / 152.3
            else -> 1.0
        }
    }
}
