package com.example.mylist.presentation.categories

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.mylist.BuildConfig
import com.example.mylist.core.domain.model.Category
import com.example.mylist.presentation.about.AboutActivity
import com.example.mylist.presentation.common.ListStateContent
import com.example.mylist.ui.theme.Palette
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    viewModel: CategoriesViewModel,
    onCategoryClick: (Category) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isAscending by viewModel.isAscending.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var categoryToEdit by remember { mutableStateOf<Category?>(null) }
    var isSearchVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Мои Списки",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White
                        )
                        if (BuildConfig.FULL_VERSION) {
                            Surface(
                                color = Color.White.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Text(
                                    text = "PRO",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Row {
                        val context = LocalContext.current
                        IconButton(onClick = {
                            context.startActivity(Intent(context, AboutActivity::class.java))
                        }) {
                            Icon(Icons.Default.Info, contentDescription = "About", tint = Color.White)
                        }
                        IconButton(onClick = { isSearchVisible = !isSearchVisible }) {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
                        }
                        IconButton(onClick = { viewModel.toggleSortOrder() }) {
                            Text(
                                text = if (isAscending) "А-Я" else "Я-А",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black
                                ),
                                color = Color.White
                            )
                        }
                    }
                }

                AnimatedVisibility(visible = isSearchVisible) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { viewModel.onSearchQueryChange(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        placeholder = { Text("Поиск категорий...", color = Color.Gray) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF1A1A1A),
                            unfocusedContainerColor = Color(0xFF1A1A1A),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color.White,
                            focusedIndicatorColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color.White,
                contentColor = Color.Black,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    Icons.Default.Add, 
                    contentDescription = "Add Category",
                    modifier = Modifier.size(30.dp)
                )
            }
        },
        containerColor = Color.Black
    ) { padding ->
        ListStateContent(
            state = uiState,
            onRetry = { viewModel.retry() },
            emptyTitle = "Пока нет категорий",
            emptyMessage = "Нажмите «+», чтобы создать первый список",
            modifier = Modifier.padding(padding)
        ) { categories ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(categories, key = { it.id }) { category ->
                    CategoryCard(
                        category = category,
                        onClick = { onCategoryClick(category) },
                        onEdit = { categoryToEdit = category },
                        onDelete = { viewModel.deleteCategory(category) }
                    )
                }
            }
        }

        if (showAddDialog) {
            AddCategoryDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { name, desc, color ->
                    viewModel.addCategory(name, desc, color)
                    showAddDialog = false
                }
            )
        }

        if (categoryToEdit != null) {
            AddCategoryDialog(
                onDismiss = { categoryToEdit = null },
                onAdd = { name, desc, color ->
                    viewModel.updateCategory(categoryToEdit!!.copy(name = name, description = desc, color = color))
                    categoryToEdit = null
                },
                initialCategory = categoryToEdit
            )
        }
    }
}

@Composable
fun CategoryCard(
    category: Category,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val baseColor = Color(category.color)
    val gradient = Brush.horizontalGradient(
        colors = listOf(
            baseColor.copy(alpha = 0.4f),
            baseColor.copy(alpha = 0.1f),
            Color.Transparent
        )
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .animateContentSize(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(modifier = Modifier.background(gradient)) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = category.name,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        if (!category.description.isNullOrBlank()) {
                            Text(
                                text = category.description!!,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.8f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "${category.itemCount}",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                        
                        var showMenu by remember { mutableStateOf(false) }
                        Box {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = Color.White)
                            }
                            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                                DropdownMenuItem(
                                    text = { Text("Изменить") },
                                    leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                                    onClick = { 
                                        onEdit()
                                        showMenu = false 
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Удалить", color = Color.Red) },
                                    leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red) },
                                    onClick = { 
                                        onDelete()
                                        showMenu = false 
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String?, Int) -> Unit,
    initialCategory: Category? = null
) {
    var name by remember { mutableStateOf(initialCategory?.name ?: "") }
    var description by remember { mutableStateOf(initialCategory?.description ?: "") }
    var selectedColor by remember { mutableStateOf(initialCategory?.color ?: Palette[0].toArgb()) }

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
                Text(
                    text = if (initialCategory == null) "Новая категория" else "Редактировать",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )

                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Название") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White,
                        focusedIndicatorColor = Color.White
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
                        cursorColor = Color.White,
                        focusedIndicatorColor = Color.White
                    )
                )

                Text("Цвет:", color = Color.White, style = MaterialTheme.typography.titleSmall)

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(44.dp),
                    modifier = Modifier.height(120.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(Palette) { color ->
                        val colorInt = color.toArgb()
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(color, RoundedCornerShape(12.dp))
                                .clickable { selectedColor = colorInt }
                                .padding(4.dp)
                        ) {
                            if (selectedColor == colorInt) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.White.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Отмена", color = Color.White.copy(alpha = 0.7f))
                    }
                    Button(
                        onClick = { if (name.isNotBlank()) onAdd(name, description, selectedColor) },
                        enabled = name.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black,
                            disabledContainerColor = Color.White.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (initialCategory == null) "Создать" else "Сохранить")
                    }
                }
            }
        }
    }
}