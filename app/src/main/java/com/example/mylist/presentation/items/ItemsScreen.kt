package com.example.mylist.presentation.items

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.mylist.core.domain.model.ItemStatus
import com.example.mylist.core.domain.model.ListItem
import com.example.mylist.presentation.common.ListStateContent
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemsScreen(
    viewModel: ItemsViewModel,
    categoryName: String,
    categoryColor: Int,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val themeColor = Color(categoryColor)
    var selectedStatus by remember { mutableStateOf(ItemStatus.WANT) }
    var showAddDialog by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<ListItem?>(null) }
    var isSearchVisible by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.snackbarMessage.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .windowInsetsPadding(WindowInsets.statusBars)
            ) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = categoryName,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = { isSearchVisible = !isSearchVisible }) {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
                        }
                        IconButton(onClick = { viewModel.toggleSortOrder() }) {
                            val isAscending by viewModel.isAscending.collectAsState()
                            Text(
                                text = if (isAscending) "А-Я" else "Я-А",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black
                                ),
                                color = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                )

                AnimatedVisibility(visible = isSearchVisible) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { viewModel.onSearchQueryChange(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        placeholder = { Text("Поиск элементов...", color = Color.Gray) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF1A1A1A),
                            unfocusedContainerColor = Color(0xFF1A1A1A),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = themeColor,
                            focusedIndicatorColor = themeColor
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                }

                SecondaryTabRow(
                    selectedTabIndex = selectedStatus.ordinal,
                    containerColor = Color.Transparent,
                    contentColor = themeColor,
                    indicator = {
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(selectedStatus.ordinal),
                            color = themeColor
                        )
                    }
                ) {
                    ItemStatus.entries.forEach { status ->
                        Tab(
                            selected = selectedStatus == status,
                            onClick = { selectedStatus = status },
                            text = {
                                Text(
                                    when (status) {
                                        ItemStatus.WANT -> "Хочу"
                                        ItemStatus.IN_PROGRESS -> "В процессе"
                                        ItemStatus.DONE -> "Готово"
                                    },
                                    color = if (selectedStatus == status) themeColor else Color.Gray,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = themeColor,
                contentColor = Color.White,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    Icons.Default.Add, 
                    contentDescription = "Add Item",
                    modifier = Modifier.size(30.dp)
                )
            }
        },
        containerColor = Color.Black
    ) { padding ->
        ListStateContent(
            state = uiState,
            onRetry = { viewModel.retry() },
            emptyTitle = "Список пуст",
            emptyMessage = when (selectedStatus) {
                ItemStatus.WANT -> "Добавьте элементы, которые хотите посмотреть или прочитать"
                ItemStatus.IN_PROGRESS -> "Здесь появятся элементы в процессе"
                ItemStatus.DONE -> "Завершённые элементы будут отображаться здесь"
            },
            modifier = Modifier.padding(padding)
        ) { items ->
            val filteredItems = items.filter { it.status == selectedStatus }
            if (filteredItems.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Нет элементов в этой вкладке",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Нажмите «+», чтобы добавить",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredItems, key = { it.id }) { item ->
                        ItemCard(
                            item = item,
                            onClick = { itemToEdit = item }
                        )
                    }
                }
            }
        }

        if (showAddDialog) {
            AddItemDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { name, desc, status ->
                    viewModel.addItem(name, desc, status)
                    showAddDialog = false
                },
                initialStatus = selectedStatus,
                themeColor = themeColor
            )
        }

        if (itemToEdit != null) {
            AddItemDialog(
                onDismiss = { itemToEdit = null },
                onAdd = { name, desc, status ->
                    viewModel.updateItem(itemToEdit!!.copy(name = name, description = desc, status = status))
                    itemToEdit = null
                },
                onDelete = {
                    viewModel.deleteItem(itemToEdit!!)
                    itemToEdit = null
                },
                initialStatus = itemToEdit!!.status,
                initialItem = itemToEdit,
                themeColor = themeColor
            )
        }
    }
}

@Composable
fun ItemCard(
    item: ListItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            if (!item.description.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.description!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String?, ItemStatus) -> Unit,
    onDelete: (() -> Unit)? = null,
    initialStatus: ItemStatus,
    initialItem: ListItem? = null,
    themeColor: Color
) {
    var name by remember { mutableStateOf(initialItem?.name ?: "") }
    var description by remember { mutableStateOf(initialItem?.description ?: "") }
    var status by remember { mutableStateOf(initialStatus) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color(0xFF1E1E1E),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (initialItem == null) "Новый элемент" else "Редактировать",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Название") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = themeColor,
                        focusedIndicatorColor = themeColor
                    )
                )

                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Описание (опц.)") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = themeColor,
                        focusedIndicatorColor = themeColor
                    )
                )

                Text("Статус:", color = Color.White, style = MaterialTheme.typography.titleSmall)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ItemStatus.entries.forEach { s ->
                        FilterChip(
                            selected = status == s,
                            onClick = { status = s },
                            label = {
                                Text(when (s) {
                                    ItemStatus.WANT -> "Хочу"
                                    ItemStatus.IN_PROGRESS -> "В процессе"
                                    ItemStatus.DONE -> "Готово"
                                })
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = themeColor,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (onDelete != null) {
                    Button(
                        onClick = onDelete,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red.copy(alpha = 0.15f),
                            contentColor = Color.Red.copy(alpha = 0.8f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Удалить")
                    }
                }

                Button(
                    onClick = { if (name.isNotBlank()) onAdd(name, description, status) },
                    enabled = name.isNotBlank(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = themeColor,
                        contentColor = Color.White,
                        disabledContainerColor = themeColor.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (initialItem == null) "Добавить" else "Сохранить")
                }
            }
        }
    }
}