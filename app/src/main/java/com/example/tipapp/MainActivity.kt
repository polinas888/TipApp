package com.example.tipapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tipapp.components.InputField
import com.example.tipapp.ui.theme.PurpleLight
import com.example.tipapp.ui.theme.TipAppTheme
import com.example.tipapp.util.calculateTip
import com.example.tipapp.util.calculateTotalPerPerson
import com.example.tipapp.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TipApp {
                MainContent()
            }
        }
    }
}

@Composable
fun TipApp(content: @Composable () -> Unit) {
    TipAppTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
            Column {
                content()
            }
        }
    }
}

@Composable
fun Header(totalPerPerson: Double = 134.0) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(15.dp)
            .clip(shape = CircleShape.copy(all = CornerSize(12.dp))),
        color = PurpleLight
    )
    {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val total = "%.2f".format(totalPerPerson)
            Text(
                text = "Total Per Person",
                style = MaterialTheme.typography.h5
            )
            Text(
                text = "$$total",
                style = MaterialTheme.typography.h4, fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
fun MainContent() {
    val numberOfPeopleState = remember {
        mutableStateOf(1)
    }

    val tipAmountState = remember {
        mutableStateOf(0.00)
    }

    val totalPerPersonState = remember {
        mutableStateOf(0.00)
    }
    Column(modifier = Modifier.padding(12.dp)) {
        Header(totalPerPersonState.value)
        BillForm(
            numberOfPeopleState = numberOfPeopleState,
            tipAmountState = tipAmountState,
            totalPerPersonState = totalPerPersonState
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BillForm(
    modifier: Modifier = Modifier,
    numberOfPeopleState: MutableState<Int>,
    tipAmountState: MutableState<Double>,
    totalPerPersonState: MutableState<Double>
) {
    val billState = remember {
        mutableStateOf("")
    }

    val validState = remember(billState.value) {
        billState.value.trim().isNotEmpty()
    }

    val sliderProgressState = remember {
        mutableStateOf(0f)
    }

    val tipPercentage = (sliderProgressState.value * 100).toInt()

    val keyboardController = LocalSoftwareKeyboardController.current

    Surface(
        modifier = modifier.fillMaxWidth().padding(10.dp),
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Column(
            modifier = modifier.padding(3.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            InputField(valueState = billState,
                labelId = "Enter bill",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions {
                    if (!validState) return@KeyboardActions
                    billState.value.trim()
                    keyboardController?.hide()
                })
            if (validState) {
                Row(
                    modifier = modifier.padding(3.dp).align(alignment = Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Split")
                    Spacer(modifier = Modifier.width(120.dp))
                    Row(
                        modifier = Modifier.padding(3.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        RoundIconButton(imageVector = Icons.Default.Remove,
                            onClick = {
                                if (numberOfPeopleState.value != 1) {
                                    numberOfPeopleState.value -= 1
                                }
                                totalPerPersonState.value = calculateTotalPerPerson(
                                    billState.value.toDouble(),
                                    tipAmountState.value,
                                    numberOfPeopleState.value
                                )
                            })
                        Text(
                            text = numberOfPeopleState.value.toString(), modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 9.dp, end = 9.dp)
                        )
                        RoundIconButton(imageVector = Icons.Default.Add, onClick = {
                            numberOfPeopleState.value += 1
                            totalPerPersonState.value = calculateTotalPerPerson(
                                billState.value.toDouble(),
                                tipAmountState.value,
                                numberOfPeopleState.value
                            )
                        })
                    }
                }
                Row(
                    modifier = modifier.padding(horizontal = 3.dp).padding(top = 12.dp, bottom = 50.dp)
                ) {
                    Text(text = "Tip")
                    Spacer(modifier = Modifier.width(210.dp))
                    Text(text = "$ ${tipAmountState.value}")
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "$tipPercentage %")
                    Spacer(modifier = Modifier.height(15.dp))
                    Slider(modifier = Modifier.padding(horizontal = 20.dp),
                        value = sliderProgressState.value,
                        onValueChange = {
                            sliderProgressState.value = it
                            if (billState.value >= 1.toString() && billState.toString()
                                    .isNotEmpty()
                            ) {
                                tipAmountState.value =
                                    calculateTip(billState.value.toDouble(), tipPercentage)
                                totalPerPersonState.value = calculateTotalPerPerson(
                                    billState.value.toDouble(),
                                    tipAmountState.value,
                                    numberOfPeopleState.value
                                )
                            }
                            Log.i("Slider", sliderProgressState.value.toString())
                        }, steps = 5
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TipAppTheme {
        TipApp {
            TipApp {
                MainContent()
            }
        }
    }
}