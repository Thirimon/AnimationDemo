package com.example.animationdemo

import android.annotation.SuppressLint
import android.view.VelocityTracker
import android.widget.Space
import androidx.compose.animation.Animatable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row


import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.animationdemo.ui.theme.Amber600
import com.example.animationdemo.ui.theme.Green
import com.example.animationdemo.ui.theme.GreenLight
import com.example.animationdemo.ui.theme.PaleDogwood
import com.example.animationdemo.ui.theme.Seashell

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

enum class TabPage{
    Home,Work
}

@Composable
fun Home() {
    // String resources.
    val allTasks = stringArrayResource(R.array.tasks)
    val allTopics = stringArrayResource(R.array.topics).toList()

    // The currently selected tab.
    var tabPage by remember { mutableStateOf(TabPage.Home) }

    // True if the whether data is currently loading.
    var weatherLoading by remember { mutableStateOf(false) }

    // Holds all the tasks currently shown on the task list.
    val tasks = remember { mutableStateListOf(*allTasks) }

    // Holds the topic that is currently expanded to show its body.
    var expandedTopic by remember { mutableStateOf<String?>(null) }

    // True if the message about the edit feature is shown.
    var editMessageShown by remember { mutableStateOf(false) }

    // Simulates loading weather data. This takes 3 seconds.
    suspend fun loadWeather() {
        if (!weatherLoading) {
            weatherLoading = true
            delay(3000L)
            weatherLoading = false
        }
    }

    // Shows the message about edit feature.
    suspend fun showEditMessage() {
        if (!editMessageShown) {
            editMessageShown = true
            delay(3000L)
            editMessageShown = false
        }
    }

    // Load the weather at the initial composition.
    LaunchedEffect(Unit) {
        loadWeather()
    }

    val lazyListState = rememberLazyListState()

    // The background color. The value is changed by the current tab.
    val backgroundColor by animateColorAsState(
        if (tabPage == TabPage.Home) Seashell else GreenLight,
        label = "background color"
    )

    // The coroutine scope for event handlers calling suspend functions.
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            HomeTabBar(
                backgroundColor = backgroundColor,
                tabPage = tabPage,
                onTabSelected = { tabPage = it }
            )
        },
        containerColor = backgroundColor,
        floatingActionButton = {
            HomeFloatingActionButton(
                extended = lazyListState.isScrollingUp(),
                onClick = {
                    coroutineScope.launch {
                        showEditMessage()
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(
            top = padding.calculateTopPadding(),
            start = padding.calculateLeftPadding(LayoutDirection.Ltr),
            end = padding.calculateEndPadding(LayoutDirection.Ltr)
        )) {
            LazyColumn(
                contentPadding = WindowInsets(16.dp, 32.dp, 16.dp,
                    padding.calculateBottomPadding() + 32.dp
                ).asPaddingValues(),
                state = lazyListState
            ) {
                // Weather
                item { Header(title = stringResource(R.string.weather)) }
                item { Spacer(modifier = Modifier.height(16.dp)) }
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shadowElevation = 2.dp
                    ) {
                        if (weatherLoading) {
                            LoadingRow()
                        } else {
                            WeatherRow(onRefresh = {
                                coroutineScope.launch {
                                    loadWeather()
                                }
                            })
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(32.dp)) }

                // Topics
                item { Header(title = stringResource(R.string.topics)) }
                item { Spacer(modifier = Modifier.height(16.dp)) }
                items(allTopics) { topic ->
                    TopicRow(
                        topic = topic,
                        expanded = expandedTopic == topic,
                        onClick = {
                            expandedTopic = if (expandedTopic == topic) null else topic
                        }
                    )
                }
                item { Spacer(modifier = Modifier.height(32.dp)) }

                // Tasks
                item { Header(title = stringResource(R.string.tasks)) }
                item { Spacer(modifier = Modifier.height(16.dp)) }
                if (tasks.isEmpty()) {
                    item {
                        TextButton(onClick = { tasks.clear(); tasks.addAll(allTasks) }) {
                            Text(stringResource(R.string.add_tasks))
                        }
                    }
                }
                items(tasks, key = { it }) { task ->
                    TaskRow(
                        task = task,
                        onRemove = { tasks.remove(task) }
                    )
                }
            }
            EditMessage(editMessageShown)
        }
    }
}

@Composable
private fun HomeFloatingActionButton(
    extended: Boolean,
    onClick: () -> Unit
) {
    // Use `FloatingActionButton` rather than `ExtendedFloatingActionButton` for full control on
    // how it should animate.
    FloatingActionButton(onClick = onClick) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null
            )
            // Toggle the visibility of the content with animation.
            AnimatedVisibility(visible = extended) {
                Text(
                    text = stringResource(R.string.edit),
                    modifier = Modifier
                        .padding(start = 8.dp, top = 3.dp)
                )
            }
        }
    }
}
@Composable
private fun EditMessage(shown: Boolean) {
    AnimatedVisibility(
        visible = shown,
        enter = slideInVertically(
            // Enters by sliding in from offset -fullHeight to 0.
            initialOffsetY = { fullHeight -> -fullHeight },
            animationSpec = tween(durationMillis = 150, easing = LinearOutSlowInEasing)
        ),
        exit = slideOutVertically(
            // Exits by sliding out from offset 0 to -fullHeight.
            targetOffsetY = { fullHeight -> -fullHeight },
            animationSpec = tween(durationMillis = 250, easing = FastOutLinearInEasing)
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.secondary,
            shadowElevation = 4.dp
        ) {
            Text(
                text = stringResource(R.string.edit_message),
                color = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
@Composable
private fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableIntStateOf(firstVisibleItemIndex)}
    var previousScrollOffset by remember(this) { mutableIntStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}
@Composable
private fun Header(
    title: String
) {
    Text(
        text = title,
        modifier = Modifier.semantics { heading() },
        style = MaterialTheme.typography.headlineLarge
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopicRow(topic: String, expanded: Boolean, onClick: () -> Unit) {
    TopicRowSpacer(visible = expanded)
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        shadowElevation = 2.dp,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                // This `Column` animates its size when its content changes.
                .animateContentSize()
        ) {
            Row {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = topic,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.take_me_home),
                    textAlign = TextAlign.Justify
                )
            }
        }
    }
    TopicRowSpacer(visible = expanded)
}
@Composable
fun TopicRowSpacer(visible: Boolean) {
    AnimatedVisibility(visible = visible) {
        Spacer(modifier = Modifier.height(8.dp))
    }
}
@Composable
private fun HomeTabBar(
    backgroundColor: Color,
    tabPage: TabPage,
    onTabSelected: (tabPage: TabPage) -> Unit
) {
    Column(modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))) {
        Spacer(Modifier.windowInsetsTopHeight(WindowInsets.safeDrawing))
        TabRow(
            selectedTabIndex = tabPage.ordinal,
            containerColor = backgroundColor,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            indicator = { tabPositions ->
                HomeTabIndicator(tabPositions, tabPage)
            }
        ) {
            HomeTab(
                icon = Icons.Default.Home,
                title = stringResource(R.string.home),
                onClick = { onTabSelected(TabPage.Home) }
            )
            HomeTab(
                icon = Icons.Default.AccountBox,
                title = stringResource(R.string.work),
                onClick = { onTabSelected(TabPage.Work) }
            )
        }
    }
}

@Composable
private fun HomeTabIndicator(
    tabPosition: List<TabPosition>,
    tabPage: TabPage
){
    val transition = updateTransition(
        tabPage,
        label = "Tab indicator"
    )
    val indicatorLeft by transition.animateDp(
        transitionSpec = {
            if (TabPage.Home isTransitioningTo TabPage.Work) {
                // Indicator moves to the right.
                // Low stiffness spring for the left edge so it moves slower than the right edge.
                spring(stiffness = Spring.StiffnessVeryLow)
            } else {
                // Indicator moves to the left.
                // Medium stiffness spring for the left edge so it moves faster than the right edge.
                spring(stiffness = Spring.StiffnessMedium)
            }
        },
        label = "Indicator left"
    ) { page ->
        tabPosition[page.ordinal].left
    }
    val indicatorRight by transition.animateDp(
        transitionSpec = {
            if (TabPage.Home isTransitioningTo TabPage.Work) {
                // Indicator moves to the right
                // Medium stiffness spring for the right edge so it moves faster than the left edge.
                spring(stiffness = Spring.StiffnessMedium)
            } else {
                // Indicator moves to the left.
                // Low stiffness spring for the right edge so it moves slower than the left edge.
                spring(stiffness = Spring.StiffnessVeryLow)
            }
        },
        label = "Indicator right"
    ) { page ->
        tabPosition[page.ordinal].right
    }
    val color by transition.animateColor(
        label = "Border color"
    ) { page ->
        if (page == TabPage.Home) PaleDogwood else Green
    }
 Box(
     Modifier
         .fillMaxSize()
         .wrapContentSize(align = Alignment.BottomStart)
         .offset(x = indicatorLeft)
         .width(indicatorRight - indicatorLeft)
         .padding(4.dp)
         .fillMaxSize()
         .border(BorderStroke(2.dp, color), RoundedCornerShape(4.dp))
 )
}

@Composable
fun HomeTab(
    icon: ImageVector,
    title:String,
    onClick: ()->Unit,
    modifier:Modifier=Modifier
){
    Row(
       modifier= Modifier
           .clickable(onClick = onClick)
           .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title)
    }
}
@Composable
private fun WeatherRow(
    onRefresh: () ->Unit
){
    Row (
        modifier= Modifier
            .heightIn(min = 64.dp)
            .padding(16.dp)
        , verticalAlignment = Alignment.CenterVertically
    ){
        Box(modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(Amber600))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = stringResource(id = R.string.temperature), fontSize = 24.sp)
        Spacer(modifier = Modifier.width(16.dp))
        IconButton(onClick = onRefresh) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
        }
    }
}
@Composable
fun LoadingRow(){
    //val alpha=1f
    val infiniteTransition= rememberInfiniteTransition(label = "infinite loading")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis=1000
                0.7f at 500
            },
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    Row(
        modifier = Modifier
            .heightIn(min = 64.dp)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.LightGray.copy(alpha = alpha))
        )
        Spacer(modifier = Modifier.width(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
                .background(Color.LightGray.copy(alpha = alpha))
        )
    }
}
@Composable
fun TaskRow(task:String,onRemove: () ->Unit){

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .swipeToDismiss(onRemove),
        shadowElevation = 2.dp
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)) {
            Icon(imageVector = Icons.Default.Check, contentDescription = null)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = task, style = MaterialTheme.typography.bodySmall)
        }
    }


}


@SuppressLint("SuspiciousIndentation")
private fun Modifier.swipeToDismiss(
    onDismissed: () ->Unit
):Modifier= composed{

    val offsetX=remember{ androidx.compose.animation.core.Animatable(0f) }


    pointerInput(Unit){
      val decay= splineBasedDecay<Float>(this)
        coroutineScope {
            while (true){
                val pointerId= awaitPointerEventScope { awaitFirstDown().id }
                offsetX.stop()
                val velocityTracker= androidx.compose.ui.input.pointer.util.VelocityTracker()
                awaitPointerEventScope {
                    horizontalDrag(pointerId){
                        change ->
                        val horizontalDragOffset=offsetX.value + change.positionChange().x
                        launch {
                            offsetX.snapTo(horizontalDragOffset)
                        }
                        velocityTracker.addPosition(change.uptimeMillis,change.position)
                        if(change.positionChange()!= Offset.Zero) change.consume()
                    }
                }
                val velocity=velocityTracker.calculateVelocity().x
                val targetOffsetX=decay.calculateTargetValue(offsetX.value,velocity)
                offsetX.updateBounds(
                    lowerBound = -size.width.toFloat(),
                    upperBound = size.width.toFloat()
                )
                launch {
                  if(targetOffsetX.absoluteValue <= size.width){
                      offsetX.animateTo(targetValue = 0f, initialVelocity = velocity)
                  }
                    else{
                        offsetX.animateDecay(velocity,decay)
                      onDismissed()
                    }
                }

            }
        }
    }.offset { IntOffset(offsetX.value.roundToInt(),0) }
}
