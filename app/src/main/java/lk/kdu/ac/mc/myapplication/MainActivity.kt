package lk.kdu.ac.mc.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.saveable.rememberSaveable
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToDoApp()
        }
    }
}

@Composable
fun ToDoApp() {
    var isDarkTheme by rememberSaveable { mutableStateOf(false) }

    ToDoAppTheme(
        darkTheme = isDarkTheme
    ) {
        Surface(modifier = Modifier.fillMaxSize()) {
            TaskListScreen(
                isDarkTheme = isDarkTheme,
                onThemeChange = { isDarkTheme = it }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToDoAppTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = Color(0xFFBB86FC),
            secondary = Color(0xFF03DAC6),
            tertiary = Color(0xFF3700B3),
            background = Color(0xFF121212),
            surface = Color(0xFF1E1E1E),
            onPrimary = Color.Black,
            onSecondary = Color.Black,
            onBackground = Color.White,
            onSurface = Color.White
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF6200EE),
            secondary = Color(0xFF03DAC6),
            tertiary = Color(0xFF3700B3),
            background = Color.White,
            surface = Color(0xFFFFFBFE),
            onPrimary = Color.White,
            onSecondary = Color.Black,
            onBackground = Color.Black,
            onSurface = Color.Black
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}

data class Task(
    val id: Int,
    val title: String,
    val isDone: Boolean,
    val priority: Priority = Priority.MEDIUM,
    val createdAt: Long = System.currentTimeMillis(),
    val dueDate: Long? = null  // Stored as epoch milliseconds
)

enum class Priority {
    LOW, MEDIUM, HIGH
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TaskListScreen(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    var tasks by remember {
        mutableStateOf(
            listOf(
                Task(
                    id = 1,
                    title = "Finish DataCrunch Forecasting model",
                    isDone = false,
                    priority = Priority.HIGH,
                    dueDate = LocalDate.now().plusDays(3).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                ),
                Task(
                    id = 2,
                    title = "Prepare for CodeX Stage 2 GitHub project",
                    isDone = false,
                    priority = Priority.HIGH,
                    dueDate = LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                ),
                Task(
                    id = 3,
                    title = "Create poster for ERIC Spark Workshop",
                    isDone = false,
                    priority = Priority.MEDIUM,
                    dueDate = LocalDate.now().plusDays(5).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                ),
                Task(
                    id = 4,
                    title = "Study for cybersecurity competition",
                    isDone = true,
                    priority = Priority.LOW
                ),
                Task(
                    id = 5,
                    title = "Submit WebDev assignment for KDU",
                    isDone = true,
                    priority = Priority.MEDIUM,
                    dueDate = LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                )
            )
        )
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showCalendarDialog by remember { mutableStateOf(false) }
    var currentEditTask by remember { mutableStateOf<Task?>(null) }
    var newTaskTitle by remember { mutableStateOf("") }
    var newTaskPriority by remember { mutableStateOf(Priority.MEDIUM) }
    var newTaskDueDate by remember { mutableStateOf<Long?>(null) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(1500)
        isLoading = false
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "📝 To-Do List",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = { onThemeChange(!isDarkTheme) }) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Rounded.LightMode else Icons.Rounded.DarkMode,
                            contentDescription = "Toggle Theme",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    IconButton(onClick = { showCalendarDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Calendar View",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    newTaskTitle = ""
                    newTaskPriority = Priority.MEDIUM
                    newTaskDueDate = null
                    showAddDialog = true
                },
                containerColor = MaterialTheme.colorScheme.tertiary
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Task",
                    tint = MaterialTheme.colorScheme.onTertiary
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            if (isLoading) {
                LoadingAnimation()
            } else if (tasks.isEmpty()) {
                AnimatedVisibility(
                    visible = tasks.isEmpty(),
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No tasks yet!\nClick the + button to add one.",
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                AnimatedVisibility(
                    visible = !isLoading && tasks.isNotEmpty(),
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically()
                ) {
                    LazyColumn(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(
                            items = tasks.sortedWith(
                                compareBy<Task> { it.dueDate ?: Long.MAX_VALUE }
                                    .thenBy { it.priority.ordinal }
                            ),
                            key = { _, task -> task.id }
                        ) { index, task ->
                            AnimatedVisibility(
                                visible = true,
                                enter = slideInVertically { it } + fadeIn(),
                                exit = slideOutVertically() + fadeOut()
                            ) {
                                TaskItem(
                                    task = task,
                                    onTaskClick = {
                                        tasks = tasks.toMutableList().apply {
                                            this[index] = task.copy(isDone = !task.isDone)
                                        }
                                    },
                                    onEditClick = {
                                        currentEditTask = task
                                        newTaskTitle = task.title
                                        newTaskPriority = task.priority
                                        newTaskDueDate = task.dueDate
                                        showEditDialog = true
                                    },
                                    onDeleteClick = {
                                        tasks = tasks.filter { it.id != task.id }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        TaskDialog(
            onDismissRequest = { showAddDialog = false },
            title = "Add New Task",
            taskTitle = newTaskTitle,
            onTaskTitleChange = { newTaskTitle = it },
            priority = newTaskPriority,
            onPriorityChange = { newTaskPriority = it },
            dueDate = newTaskDueDate?.let {
                Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
            },
            onDueDateChange = { date ->
                newTaskDueDate = date?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
            },
            onDismiss = { showAddDialog = false },
            onConfirm = {
                if (newTaskTitle.isNotBlank()) {
                    val newId = (tasks.maxOfOrNull { it.id } ?: 0) + 1
                    tasks = tasks + Task(
                        id = newId,
                        title = newTaskTitle,
                        isDone = false,
                        priority = newTaskPriority,
                        dueDate = newTaskDueDate
                    )
                    showAddDialog = false
                }
            }
        )
    }

    if (showEditDialog && currentEditTask != null) {
        TaskDialog(
            onDismissRequest = { showEditDialog = false },
            title = "Edit Task",
            taskTitle = newTaskTitle,
            onTaskTitleChange = { newTaskTitle = it },
            priority = newTaskPriority,
            onPriorityChange = { newTaskPriority = it },
            dueDate = newTaskDueDate?.let {
                Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
            },
            onDueDateChange = { date ->
                newTaskDueDate = date?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
            },
            onDismiss = { showEditDialog = false },
            onConfirm = {
                if (newTaskTitle.isNotBlank()) {
                    tasks = tasks.map { task ->
                        if (task.id == currentEditTask!!.id) {
                            task.copy(
                                title = newTaskTitle,
                                priority = newTaskPriority,
                                dueDate = newTaskDueDate
                            )
                        } else {
                            task
                        }
                    }
                    showEditDialog = false
                }
            }
        )
    }

    if (showCalendarDialog) {
        CalendarDialog(
            tasks = tasks,
            selectedDate = selectedDate,
            onDateSelected = { date ->
                selectedDate = date
            },
            onDismiss = { showCalendarDialog = false }
        )
    }
}

@Composable
fun LoadingAnimation() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val infiniteTransition = rememberInfiniteTransition()
        val alpha by infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        CircularProgressIndicator(
            modifier = Modifier.size(64.dp),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 6.dp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Loading your tasks...",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = alpha)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskItem(
    task: Task,
    onTaskClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val priorityColor = when (task.priority) {
        Priority.HIGH -> if (task.isDone) Color(0x66EF9A9A) else Color(0xFFFFCDD2)
        Priority.MEDIUM -> if (task.isDone) Color(0x66F5D69C) else Color(0xFFFFE0B2)
        Priority.LOW -> if (task.isDone) Color(0x66A5D6A7) else Color(0xFFC8E6C9)
    }

    val elevation by animateDpAsState(
        targetValue = if (task.isDone) 2.dp else 8.dp,
        animationSpec = tween(durationMillis = 300)
    )

    val dateFormatter = remember { DateTimeFormatter.ofPattern("MMM dd, yyyy") }
    val dueDate = task.dueDate?.let {
        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
    }
    val isOverdue = dueDate?.let { it < LocalDate.now() && !task.isDone } ?: false

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onTaskClick,
                onLongClick = onEditClick
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isOverdue) {
                Color(0x66FF5252) // Red tint for overdue tasks
            } else {
                priorityColor
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevation
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = task.title,
                    fontSize = 16.sp,
                    fontWeight = if (task.isDone) FontWeight.Normal else FontWeight.Medium,
                    textDecoration = if (task.isDone) TextDecoration.LineThrough else TextDecoration.None,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Row {
                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            dueDate?.let { date ->
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Due date",
                        tint = if (isOverdue) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = date.format(dateFormatter),
                        fontSize = 14.sp,
                        color = if (isOverdue) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        fontWeight = if (isOverdue) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDialog(
    onDismissRequest: () -> Unit,
    title: String,
    taskTitle: String,
    onTaskTitleChange: (String) -> Unit,
    priority: Priority,
    onPriorityChange: (Priority) -> Unit,
    dueDate: LocalDate?,
    onDueDateChange: (LocalDate?) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("MMM dd, yyyy") }

    Dialog(onDismissRequest = onDismissRequest) {
        AnimatedVisibility(
            visible = true,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    OutlinedTextField(
                        value = taskTitle,
                        onValueChange = onTaskTitleChange,
                        label = { Text("Task title") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Due date selector
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Due date",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = dueDate?.format(dateFormatter) ?: "No due date",
                            color = if (dueDate != null) MaterialTheme.colorScheme.onSurface
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            modifier = Modifier.weight(1f)
                        )
                        if (dueDate != null) {
                            IconButton(
                                onClick = { onDueDateChange(null) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear date",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }

                    if (showDatePicker) {
                        val datePickerState = rememberDatePickerState(
                            initialSelectedDateMillis = dueDate?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
                        )
                        DatePickerDialog(
                            onDismissRequest = { showDatePicker = false },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        datePickerState.selectedDateMillis?.let {
                                            val selectedDate = Instant.ofEpochMilli(it)
                                                .atZone(ZoneId.systemDefault())
                                                .toLocalDate()
                                            onDueDateChange(selectedDate)
                                        }
                                        showDatePicker = false
                                    }
                                ) {
                                    Text("OK")
                                }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = { showDatePicker = false }
                                ) {
                                    Text("Cancel")
                                }
                            }
                        ) {
                            DatePicker(state = datePickerState)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Priority:",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        PriorityChip(
                            text = "Low",
                            selected = priority == Priority.LOW,
                            onClick = { onPriorityChange(Priority.LOW) },
                            color = Color(0xFFC8E6C9)
                        )
                        PriorityChip(
                            text = "Medium",
                            selected = priority == Priority.MEDIUM,
                            onClick = { onPriorityChange(Priority.MEDIUM) },
                            color = Color(0xFFFFE0B2)
                        )
                        PriorityChip(
                            text = "High",
                            selected = priority == Priority.HIGH,
                            onClick = { onPriorityChange(Priority.HIGH) },
                            color = Color(0xFFFFCDD2)
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = onConfirm,
                            enabled = taskTitle.isNotBlank()
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarDialog(
    tasks: List<Task>,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("MMM yyyy") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Calendar View",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Month navigation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = { onDateSelected(selectedDate.minusMonths(1)) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "Previous month"
                        )
                    }
                    Text(
                        text = selectedDate.format(dateFormatter),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                    IconButton(
                        onClick = { onDateSelected(selectedDate.plusMonths(1)) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Next month"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Calendar grid
                val daysInMonth = selectedDate.lengthOfMonth()
                val firstDayOfMonth = selectedDate.withDayOfMonth(1)
                val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // Convert to 0-6 (Sun-Sat)

                Column {
                    // Weekday headers
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
                            Text(
                                text = day,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Calendar days
                    var dayCounter = 1
                    for (week in 0..5) {
                        if (dayCounter > daysInMonth) break

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            for (dayOfWeek in 0..6) {
                                if (week == 0 && dayOfWeek < firstDayOfWeek) {
                                    // Empty space before first day of month
                                    Box(modifier = Modifier.weight(1f))
                                } else if (dayCounter > daysInMonth) {
                                    // Empty space after last day of month
                                    Box(modifier = Modifier.weight(1f))
                                } else {
                                    val currentDay = dayCounter
                                    val date = selectedDate.withDayOfMonth(currentDay)
                                    val tasksForDay = tasks.filter { task ->
                                        task.dueDate?.let {
                                            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate() == date
                                        } ?: false
                                    }

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1f)
                                            .padding(4.dp)
                                            .background(
                                                color = when {
                                                    date == LocalDate.now() -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                                    tasksForDay.isNotEmpty() -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                                                    else -> Color.Transparent
                                                },
                                                shape = CircleShape
                                            )
                                            .clickable { onDateSelected(date) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = currentDay.toString(),
                                                color = when {
                                                    date == LocalDate.now() -> MaterialTheme.colorScheme.primary
                                                    tasksForDay.isNotEmpty() -> MaterialTheme.colorScheme.secondary
                                                    else -> MaterialTheme.colorScheme.onSurface
                                                },
                                                fontWeight = if (date == LocalDate.now()) FontWeight.Bold else FontWeight.Normal
                                            )
                                            if (tasksForDay.isNotEmpty()) {
                                                Text(
                                                    text = tasksForDay.size.toString(),
                                                    fontSize = 10.sp,
                                                    color = MaterialTheme.colorScheme.secondary,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                    dayCounter++
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tasks for selected date
                val selectedDateTasks = tasks.filter { task ->
                    task.dueDate?.let {
                        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate() == selectedDate
                    } ?: false
                }

                if (selectedDateTasks.isNotEmpty()) {
                    Text(
                        text = "Tasks for ${selectedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
                    ) {
                        items(selectedDateTasks.size) { index ->
                            val task = selectedDateTasks[index]
                            Text(
                                text = "• ${task.title}",
                                modifier = Modifier.padding(vertical = 4.dp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                } else {
                    Text(
                        text = "No tasks for ${selectedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Close")
                }
            }
        }
    }
}

@Composable
fun PriorityChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    color: Color
) {
    val animatedColor by animateColorAsState(
        targetValue = if (selected) color else color.copy(alpha = 0.3f),
        animationSpec = tween(durationMillis = 200)
    )

    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        color = animatedColor,
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent
        )
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}