package com.mahjong.onehandscores

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mahjong.onehandscores.ui.theme.MahjongOneHandScoresTheme

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MahjongOneHandScoresTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MahjongScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MahjongScreen(viewModel: MahjongViewModel = viewModel()) {
    // Состояния
    var playerWind by remember { mutableStateOf("east") }
    var roundWind by remember { mutableStateOf("east") }
    var rules by remember { mutableStateOf("классика") }

    // Список комбинаций — вот здесь хранятся все добавленные комбинации
    var combinations by remember { mutableStateOf(listOf<Combination>()) }

    var commonFlags by remember { mutableStateOf(setOf<String>()) }
    var isMahjong by remember { mutableStateOf(false) }
    var mahjongFlags by remember { mutableStateOf(setOf<String>()) }
    var result by remember { mutableStateOf<CalculationResponse?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Функция добавления новой комбинации
    fun addCombination() {
        combinations = combinations + Combination()  // добавляем пустую комбинацию
    }

    // Функция обновления конкретной комбинации по индексу
    fun updateCombination(index: Int, new: Combination) {
        combinations = combinations.toMutableList().apply { set(index, new) }
    }

    // Функция удаления комбинации по индексу
    fun removeCombination(index: Int) {
        combinations = combinations.toMutableList().apply { removeAt(index) }
    }

    // Преобразование комбинации в строку для сервера
    fun getCombinationText(comb: Combination): String {
        return when (comb.suit) {
            "бонус" -> "${comb.type} ${comb.suit} ${comb.value}"
            else -> "${comb.type} ${comb.element} ${comb.suit} ${comb.value}"
        }.trim()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ========== Ветры и правила ==========
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Правила:", style = MaterialTheme.typography.bodyLarge)
                RulesDropdownSelector(selected = rules, onSelect = { rules = it })
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Ваш ветер:", style = MaterialTheme.typography.bodyLarge)
                WindDropdownSelector(selected = playerWind, onSelect = { playerWind = it })
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Ветер раунда:", style = MaterialTheme.typography.bodyLarge)
                WindDropdownSelector(selected = roundWind, onSelect = { roundWind = it })
            }
        }

        // ========== Комбинации ==========
        Text("Комбинации", style = MaterialTheme.typography.titleMedium)

        // Отображаем все комбинации
        combinations.forEachIndexed { index, comb ->
            CombinationCard(
                combination = comb,
                onUpdate = { updateCombination(index, it) },
                onDelete = { removeCombination(index) }
            )
        }

        // Кнопка добавления
        Button(onClick = { addCombination() }) {
            Text("+ Добавить комбинацию")
        }

        // ========== Флаги ==========
        Text("Флаги", style = MaterialTheme.typography.titleMedium)
        FlagCheckboxes(
            options = listOf("чистая масть", "чистая масть с драконами и ветрами", "только драконы и ветра", "драконы, ветра, единицы и девятки"),
            selected = commonFlags,
            onSelectionChange = { commonFlags = it }
        )

        // ========== Маджонг ==========
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isMahjong, onCheckedChange = { isMahjong = it })
            Text("Маджонг (собран)")
        }

        if (isMahjong) {
            FlagCheckboxes(
                options = listOf(
                    "мизер",
                    "без последовательностей",
                    "все драконы",
                    "все ветра",
                    "предмаджонг со старта",
                    "завершение единственной возможной",
                    "завершение костью со стены",
                    "завершение свободной костью",
                    "завершение последней доступной",
                    "завершение ограблением открытого конга"
                ),
                selected = mahjongFlags,
                onSelectionChange = { mahjongFlags = it }
            )
        }

        // ========== Кнопка расчёта ==========
        Button(
            onClick = {
                isLoading = true
                val request = CalculationRequest(
                    playerWind = playerWind,
                    vipWind = roundWind,
                    combinationsOrTiles = combinations.map { getCombinationText(it) },
                    flags = (commonFlags + if (isMahjong) listOf("маджонг") + mahjongFlags else emptyList()).toList(),
                    rules = rules
                )
                viewModel.calculate(request) { response, err ->
                    isLoading = false
                    if (err != null) error = err
                    else result = response
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp))
            else Text("Рассчитать")
        }

        // ========== Результат ==========
        result?.let {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Очки: ${it.score}", style = MaterialTheme.typography.headlineMedium)
                    Text("Маджонг: ${if (it.isMahjong) "Да" else "Нет"}")
                    Text("Применённые флаги:")
                    it.appliedFlags.forEach { flag -> Text("• $flag") }
                }
            }
        }

        // ========== Ошибка ==========
        error?.let {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(it, modifier = Modifier.padding(16.dp))
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun WindDropdownSelector(
    selected: String,
    onSelect: (String) -> Unit
) {
    val options = listOf("east", "south", "west", "north")
    val displayNames = mapOf(
        "east" to "Восток",
        "south" to "Юг",
        "west" to "Запад",
        "north" to "Север"
    )

    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.width(140.dp)) {
        TextField(
            value = displayNames[selected] ?: selected,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        )

        // Прозрачный слой поверх TextField для обработки клика
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { expanded = true }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(140.dp)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(displayNames[option] ?: option) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun RulesDropdownSelector(
    selected: String,
    onSelect: (String) -> Unit
) {
    val options = listOf(
        "классика" to "Классика",
        "друзья" to "Света",
        "родители" to "Родители"
    )

    var expanded by remember { mutableStateOf(false) }

    val selectedDisplay = options.find { it.first == selected }?.second ?: selected

    Box(modifier = Modifier.width(200.dp)) {
        TextField(
            value = selectedDisplay,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        )

        // Прозрачный слой поверх TextField для обработки клика
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { expanded = true }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(200.dp)
        ) {
            options.forEach { (value, display) ->
                DropdownMenuItem(
                    text = { Text(display) },
                    onClick = {
                        onSelect(value)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun CombinationCard(
    combination: Combination,
    onUpdate: (Combination) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Тип (открытая/закрытая)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Открытая?", style = MaterialTheme.typography.bodyLarge)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = combination.type == "открытая",
                        onCheckedChange = { isChecked ->
                            onUpdate(combination.copy(type = if (isChecked) "открытая" else "закрытая"))
                        }
                    )
                    Text("Да")
                }
            }

            // Элемент
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Тип:", style = MaterialTheme.typography.bodyLarge)
                DropdownSelectorCompact(
                    options = listOf("пара", "тройка", "четверка", "последовательность три", "последовательность четыре", "одна"),
                    selected = combination.element,
                    onSelect = { onUpdate(combination.copy(element = it)) },
                    width = 340.dp
                )
            }

            // Масть (с вашими названиями)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Масть:", style = MaterialTheme.typography.bodyLarge)
                DropdownSelectorCompact(
                    options = listOf("символ", "дот", "бамбук", "дракон", "ветер", "бонус цветок", "бонус сезон"),
                    selected = combination.suit,
                    onSelect = { onUpdate(combination.copy(suit = it)) },
                    width = 200.dp
                )
            }

            // Значение (зависит от масти)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Значение:", style = MaterialTheme.typography.bodyLarge)
                val valueOptions = getValueOptions(combination.suit, combination.element)
                DropdownSelectorCompact(
                    options = valueOptions,
                    selected = if (valueOptions.contains(combination.value)) combination.value else valueOptions.firstOrNull() ?: "",
                    onSelect = { onUpdate(combination.copy(value = it)) },
                    width = 200.dp,
                    enabled = valueOptions.isNotEmpty()
                )
            }

            // Кнопка удаления
            Button(
                onClick = onDelete,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Удалить комбинацию")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownSelectorCompact(
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    width: Dp = 140.dp,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.width(width)) {
        // Поле ввода (только для отображения)
        TextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            enabled = enabled,
            singleLine = true
        )

        // Прозрачный слой поверх TextField для обработки клика
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(enabled = enabled) {
                    if (enabled) expanded = true
                }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(width)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

fun getValueOptions(suit: String, element: String): List<String> {
    return when (suit) {
        "дракон" -> listOf("красный", "зеленый", "белый")
        "ветер" -> listOf("восточный", "южный", "западный", "северный")
        "бонус цветок" -> (1..4).map { it.toString() }
        "бонус сезон" -> (1..4).map { it.toString() }
        else -> { // числовые масти: маны, пины, соусы
            when (element) {
                "последовательность три" -> (1..7).map { it.toString() }
                "последовательность четыре" -> (1..6).map { it.toString() }
                else -> (1..9).map { it.toString() } // пара, тройка, четверка, одиночная
            }
        }
    }
}

@Composable
fun FlagCheckboxes(
    options: List<String>,
    selected: Set<String>,
    onSelectionChange: (Set<String>) -> Unit
) {
    Column {
        options.forEach { option ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = option in selected,
                    onCheckedChange = { isChecked ->
                        val newSet = if (isChecked) selected + option else selected - option
                        onSelectionChange(newSet)
                    }
                )
                Text(option)
            }
        }
    }
}