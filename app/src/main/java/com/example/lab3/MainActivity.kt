package com.example.lab3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lab3.logic.Warehouse
import com.example.lab3.logic.Detail
import com.example.lab3.logic.Assembly
import com.example.lab3.logic.Mechanism
import com.example.lab3.ui.theme.Lab3Theme
import com.example.lab3.logic.Product


val warehouses = mutableMapOf<Warehouse, String>()

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab3Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    App()
                }
            }
        }
    }
}

@Composable
fun App() {
    var screen by remember { mutableStateOf("main") }
    var currentWarehouse by remember { mutableStateOf<Warehouse?>(null) }

    when (screen) {
        "main" -> MainScreen(
            loginClick = { screen = "login" },
            registerClick = { screen = "register" }
        )

        "login" -> LoginScreen(
            onBack = { screen = "main" },
            onLoginSuccess = {
                currentWarehouse = it
                screen = "menu"
            }
        )

        "register" -> RegisterScreen(
            onBack = { screen = "main" },
            onRegisterSuccess = {
                currentWarehouse = it
                screen = "menu"
            }
        )

        "menu" -> MenuScreen(
            currentWarehouse = currentWarehouse,
            onLogout = {
                currentWarehouse = null
                screen = "main"
            }
        )
    }
}

@Composable
fun MainScreen(loginClick: () -> Unit, registerClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = loginClick, modifier = Modifier.padding(4.dp)) {
            Text("Вхід", fontSize = 20.sp)
        }
        Button(onClick = registerClick, modifier = Modifier.padding(4.dp)) {
            Text("Реєстрація", fontSize = 20.sp)
        }
    }
}

@Composable
fun RegisterScreen(onBack: () -> Unit, onRegisterSuccess: (Warehouse) -> Unit) {
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Реєстрація", fontSize = 22.sp)
        OutlinedTextField(value = login, onValueChange = { login = it }, label = { Text("Логін") })
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            visualTransformation = PasswordVisualTransformation()
        )
        Text(text = message, color = MaterialTheme.colorScheme.primary)

        Button(onClick = {
            if (login.isBlank() || password.isBlank()) {
                message = "Заповніть усі поля!"
                return@Button
            }
            val exists = warehouses.keys.any { it.name == login }
            if (exists) {
                message = "Такий логін уже існує!"
            } else {
                val newWh = Warehouse(login)
                warehouses[newWh] = password
                onRegisterSuccess(newWh)
            }
        }, modifier = Modifier.padding(8.dp)) {
            Text("Підтвердити")
        }

        Button(onClick = onBack) { Text("Назад") }
    }
}

@Composable
fun LoginScreen(onBack: () -> Unit, onLoginSuccess: (Warehouse) -> Unit) {
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Вхід", fontSize = 22.sp)
        OutlinedTextField(value = login, onValueChange = { login = it }, label = { Text("Логін") })
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            visualTransformation = PasswordVisualTransformation()
        )
        Text(text = message, color = MaterialTheme.colorScheme.primary)

        Button(onClick = {
            val warehouse = warehouses.keys.find { it.name == login }
            if (warehouse != null && warehouses[warehouse] == password) {
                onLoginSuccess(warehouse)
            } else {
                message = "Неправильний логін або пароль!"
            }
        }, modifier = Modifier.padding(8.dp)) {
            Text("Увійти")
        }

        Button(onClick = onBack) { Text("Назад") }
    }
}

@Composable
fun MenuScreen(currentWarehouse: Warehouse?, onLogout: () -> Unit) {
    if (currentWarehouse == null) {
        Text("Помилка: склад не знайдено")
        return
    }

    var selectedTab by remember { mutableStateOf("Склад") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(20.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(15.dp)) {
            listOf("Склад", "Додати", "Видалити").forEach { tab ->
                Button(onClick = { selectedTab = tab }, modifier = Modifier.weight(1f)) {
                    Text(tab, fontSize = 15.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        when (selectedTab) {
            "Склад" -> WarehouseTab(currentWarehouse)
            "Додати" -> AddTab(currentWarehouse)
            "Видалити" -> DeleteTab(currentWarehouse)
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onLogout) { Text("Вийти") }
    }
}

@Composable
fun WarehouseTab(warehouse: Warehouse) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.padding(10.dp)
    ) {
        Text(
            "Cклад",
            fontSize = 30.sp,
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            textAlign = TextAlign.Center
        )

        // === Деталі ===
        Text("Деталі", fontSize = 20.sp)
        if (warehouse.allDetails.isEmpty()) {
            Text("• Немає", modifier = Modifier.padding(start = 10.dp))
        } else {
            warehouse.allDetails.forEach { detail ->
                var expanded by remember { mutableStateOf(false) }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = !expanded }
                        .padding(start = 10.dp, top = 4.dp, bottom = 4.dp)
                ) {
                    Text(detail.name, fontSize = 18.sp)

                    Text(
                        if (expanded) "−" else "+",
                        fontSize = 20.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )

                }

                if (expanded) {
                    Column(modifier = Modifier.padding(start = 26.dp)) {
                        Text("- Виробник: ${detail.manufacturer}")
                        Text("- Рік виготовлення: ${detail.year}")
                        Text("- Ціна: ${detail.price}")
                        Text("- Матеріал: ${detail.material}")
                    }
                }
            }
        }

        // === Вузли ===
        Text("Вузли", fontSize = 20.sp)
        if (warehouse.allAssemblies.isEmpty()) {
            Text("• Немає", modifier = Modifier.padding(start = 10.dp))
        } else {
            warehouse.allAssemblies.forEach { assembly ->
                var expanded by remember { mutableStateOf(false) }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = !expanded }
                        .padding(start = 10.dp, top = 4.dp, bottom = 4.dp)
                ) {
                    Text(assembly.name, fontSize = 18.sp)
                    Text(
                        if (expanded) "−" else "+",
                        fontSize = 20.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )

                }

                if (expanded) {
                    Column(modifier = Modifier.padding(start = 26.dp)) {
                        Text("- Виробник: ${assembly.manufacturer}")
                        Text("- Рік виготовлення: ${assembly.year}")
                        Text("- Ціна: ${assembly.price}")
                        Text(
                            "- Деталі: ${if (assembly.details.isEmpty()) "не вказано"
                            else assembly.details.joinToString(", ") { it.name }}"
                        )
                    }
                }
            }
        }

        // === Механізми ===
        Text("Механізми", fontSize = 20.sp)
        if (warehouse.allMechanisms.isEmpty()) {
            Text("• Немає", modifier = Modifier.padding(start = 10.dp))
        } else {
            warehouse.allMechanisms.forEach { mechanism ->
                var expanded by remember { mutableStateOf(false) }

                Row(

                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = !expanded }
                        .padding(start = 10.dp, top = 4.dp, bottom = 4.dp)
                ) {
                    Text(mechanism.name, fontSize = 18.sp)

                    Text(
                        if (expanded) "−" else "+",
                        fontSize = 20.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )

                }

                if (expanded) {
                    Column(modifier = Modifier.padding(start = 26.dp)) {
                        Text("- Виробник: ${mechanism.manufacturer}")
                        Text("- Рік виготовлення: ${mechanism.year}")
                        Text("- Ціна: ${mechanism.price}")
                        Text(
                            "- Вузли: ${if (mechanism.assemblies.isEmpty()) "не вказано"
                            else mechanism.assemblies.joinToString(", ") { it.name }}"
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun AddTab(warehouse: Warehouse) {
    var category by remember { mutableStateOf<String?>(null) }
    var name by remember { mutableStateOf("") }
    var manufacturer by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var material by remember { mutableStateOf("") }

    // --- для вузла: пошук деталей і вибір ---
    var searchQueryDetails by remember { mutableStateOf("") }
    var searchResultsDetails by remember { mutableStateOf(listOf<Detail>()) }
    var selectedDetails by remember { mutableStateOf(setOf<Detail>()) }

    // --- для механізму: пошук вузлів і вибір ---
    var searchQueryAssemblies by remember { mutableStateOf("") }
    var searchResultsAssemblies by remember { mutableStateOf(listOf<Assembly>()) }
    var selectedAssemblies by remember { mutableStateOf(setOf<Assembly>()) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)

    ) {
        Text("Додати", fontSize = 30.sp, modifier = Modifier.padding(8.dp))

        // Вибір типу
        if (category == null) {
            Text("Оберіть тип:", fontSize = 16.sp, modifier = Modifier.padding(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Деталь", "Вузол", "Механізм").forEach { type ->
                    Button(onClick = {
                        // при переході скидаємо попередні вибори
                        category = type
                        name = ""; manufacturer = ""; year = ""; price = ""; material = ""
                        searchQueryDetails = ""; searchResultsDetails = emptyList(); selectedDetails = emptySet()
                        searchQueryAssemblies = ""; searchResultsAssemblies = emptyList(); selectedAssemblies = emptySet()
                    }) {
                        Text(type)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

        } else {
            Text("Тип: $category", fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Назва") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = manufacturer,
                onValueChange = { manufacturer = it },
                label = { Text("Виробник") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = year,
                onValueChange = { year = it },
                label = { Text("Рік") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Ціна") },
                modifier = Modifier.fillMaxWidth()
            )

            if (category == "Деталь") {
                OutlinedTextField(
                    value = material,
                    onValueChange = { material = it },
                    label = { Text("Матеріал") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // --- Блок для Вузла: пошук деталей + чекбокси ---
            if (category == "Вузол") {
                Spacer(modifier = Modifier.height(12.dp))
                Text("Додайте деталі до вузла", fontSize = 16.sp, modifier = Modifier.fillMaxWidth())

                OutlinedTextField(
                    value = searchQueryDetails,
                    onValueChange = { searchQueryDetails = it },
                    label = { Text("Пошук деталей") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {
                        searchResultsDetails = warehouse.allDetails.filter {
                            it.name.contains(searchQueryDetails, ignoreCase = true)
                        }
                    }) {
                        Text("Пошук деталей")
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text("Вибрано: ${selectedDetails.size}", modifier = Modifier.align(Alignment.CenterVertically))
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (searchResultsDetails.isNotEmpty()) {
                    Column(modifier = Modifier.fillMaxWidth().padding(start = 8.dp)) {
                        searchResultsDetails.forEach { detail ->
                            val checked = selectedDetails.contains(detail)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Checkbox(
                                    checked = checked,
                                    onCheckedChange = { checkedNow ->
                                        selectedDetails = if (checkedNow) selectedDetails + detail else selectedDetails - detail
                                    }
                                )
                                Text(detail.name, modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                    }
                } else {
                    Text("(Пошук нічого не знайшов або порожній запит)", modifier = Modifier.padding(8.dp))
                }
            }

            // --- Блок для Механізму: пошук вузлів + чекбокси ---
            if (category == "Механізм") {
                Spacer(modifier = Modifier.height(12.dp))
                Text("Додайте вузли до механізму", fontSize = 16.sp, modifier = Modifier.fillMaxWidth())

                OutlinedTextField(
                    value = searchQueryAssemblies,
                    onValueChange = { searchQueryAssemblies = it },
                    label = { Text("Пошук вузлів") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {
                        searchResultsAssemblies = warehouse.allAssemblies.filter {
                            it.name.contains(searchQueryAssemblies, ignoreCase = true)
                        }
                    }) {
                        Text("Пошук вузлів")
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text("Вибрано: ${selectedAssemblies.size}", modifier = Modifier.align(Alignment.CenterVertically))
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (searchResultsAssemblies.isNotEmpty()) {
                    Column(modifier = Modifier.fillMaxWidth().padding(start = 8.dp)) {
                        searchResultsAssemblies.forEach { assembly ->
                            val checked = selectedAssemblies.contains(assembly)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Checkbox(
                                    checked = checked,
                                    onCheckedChange = { checkedNow ->
                                        selectedAssemblies = if (checkedNow) selectedAssemblies + assembly else selectedAssemblies - assembly
                                    }
                                )
                                Text(assembly.name, modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                    }
                } else {
                    Text("(Пошук нічого не знайшов або порожній запит)", modifier = Modifier.padding(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Кнопки Додати / Назад
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    try {
                        val y = year.toIntOrNull() ?: 2024
                        val p = price.toDoubleOrNull() ?: 0.0
                        when (category) {
                            "Деталь" -> {
                                warehouse.buy(
                                    Detail(name, manufacturer, y, p, material)
                                )
                            }

                            "Вузол" -> {
                                // створюємо вузол із вибраних деталей
                                val newAssembly = Assembly(name, manufacturer, y, p, selectedDetails.toList())
                                warehouse.buy(newAssembly)

                                // видаляємо використані деталі зі складу
                                warehouse.allDetails.removeAll(selectedDetails.toSet())
                            }

                            "Механізм" -> {
                                // створюємо механізм із вибраних вузлів
                                val newMechanism = Mechanism(name, manufacturer, y, p, selectedAssemblies.toList())
                                warehouse.buy(newMechanism)

                                // видаляємо використані вузли зі складу
                                warehouse.allAssemblies.removeAll(selectedAssemblies.toSet())
                            }
                        }

                        // очищення стану після додавання
                        name = ""; manufacturer = ""; year = ""; price = ""; material = ""
                        searchQueryDetails = ""; searchResultsDetails = emptyList(); selectedDetails = emptySet()
                        searchQueryAssemblies = ""; searchResultsAssemblies = emptyList(); selectedAssemblies = emptySet()
                        category = null

                    } catch (e: Exception) {
                        println("Помилка: ${e.message}")
                    }
                }) {
                    Text("Додати")
                }

                Button(onClick = {
                    // повернутися до вибору типу
                    category = null
                    name = ""; manufacturer = ""; year = ""; price = ""; material = ""
                    searchQueryDetails = ""; searchResultsDetails = emptyList(); selectedDetails = emptySet()
                    searchQueryAssemblies = ""; searchResultsAssemblies = emptyList(); selectedAssemblies = emptySet()
                }) {
                    Text("Назад")
                }
            }
        }
    }
}

@Composable
fun DeleteTab(warehouse: Warehouse) {
    var name by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf(listOf<Product>()) } // список объектов Product
    var selectedItems by remember { mutableStateOf(setOf<Product>()) } // множество выбранных объектов
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Видалити", fontSize = 30.sp, modifier = Modifier.padding(8.dp))

        // Поле для введення тексту пошуку
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Введіть назву для пошуку") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Кнопка пошуку
        Button(onClick = {
            val results = mutableListOf<Product>()

            results.addAll(warehouse.allDetails.filter { it.name.contains(name, ignoreCase = true) })
            results.addAll(warehouse.allAssemblies.filter { it.name.contains(name, ignoreCase = true) })
            results.addAll(warehouse.allMechanisms.filter { it.name.contains(name, ignoreCase = true) })

            searchResults = results
            selectedItems = emptySet()
            message = if (results.isEmpty()) "Нічого не знайдено" else ""
        }) {
            Text("Пошук")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Якщо є результати — показуємо список
        if (searchResults.isNotEmpty()) {
            Text("Знайдено:", fontSize = 18.sp, modifier = Modifier.padding(bottom = 8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                searchResults.forEach { product ->
                    val isChecked = selectedItems.contains(product)

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = {
                                selectedItems = if (isChecked)
                                    selectedItems - product
                                else
                                    selectedItems + product
                            }
                        )
                        val type = when (product) {
                            is Detail -> "Деталь"
                            is Assembly -> "Вузол"
                            is Mechanism -> "Механізм"
                            else -> "Невідомий тип"
                        }
                        Text("$type: ${product.name}", fontSize = 16.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Кнопка видалення вибраних елементів
            Button(
                onClick = {
                    if (selectedItems.isEmpty()) {
                        message = "Оберіть хоча б один елемент!"
                        return@Button
                    }

                    selectedItems.forEach { product ->
                        warehouse.sell(product)
                    }

                    message = "Видалено ${selectedItems.size} елемент(ів)"

                    // обновляем список найденных без удалённых
                    searchResults = searchResults.filterNot { it in selectedItems }
                    selectedItems = emptySet()
                },
                enabled = selectedItems.isNotEmpty()
            ) {
                Text("Видалити вибрані")
            }
        }

        // Повідомлення про результат
        if (message.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(message, color = MaterialTheme.colorScheme.primary)
        }
    }
}
