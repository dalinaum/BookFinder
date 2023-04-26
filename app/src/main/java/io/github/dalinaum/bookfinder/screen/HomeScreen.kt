package io.github.dalinaum.bookfinder.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import io.github.dalinaum.bookfinder.entity.VolumeInfo
import io.github.dalinaum.bookfinder.entity.getThumbnail
import io.github.dalinaum.bookfinder.ui.theme.WColorLight
import io.github.dalinaum.bookfinder.viewmodel.HomeViewModel
import io.github.dalinaum.bookfinder.viewmodel.status.LoadStatus

@Composable
fun HomeScreen(
    navController: NavController,
    listState: LazyListState,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val scaffoldState: ScaffoldState = rememberScaffoldState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "BookFinder")
                }
            )
        },
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState
    ) {
        Column {
            SearchField(
                query = viewModel.currentQuery,
                onQueryChanged = {
                    viewModel.currentQuery = it
                },
                onSearchButtonClicked = {
                    viewModel.loadData(0)
                }
            )

            LazyColumn(
                state = listState
            ) {
                val onItemRowClicked = { id: String ->
                    navController.navigate("Detail/$id")
                }
                items(
                    items = viewModel.volumeList,
                    key = { it.id }
                ) { item ->
                    ItemRow(
                        id = item.id,
                        volumeInfo = item.volumeInfo,
                        onItemRowClicked = onItemRowClicked
                    )
                }

                when (viewModel.loadStatus) {
                    LoadStatus.LOADING, LoadStatus.APPENDING -> {
                        item {
                            Loading()
                        }
                    }

                    LoadStatus.ERROR -> {
                        item {
                            Error()
                        }
                    }

                    else -> { // Do nothing.
                    }
                }
            }

            LoadMoreDataIfNeeded(
                viewModel = viewModel,
                listState = listState
            )
        }
    }
}

@Composable
private fun LoadMoreDataIfNeeded(
    viewModel: HomeViewModel,
    listState: LazyListState
) {
    val shouldLoadData by remember {
        derivedStateOf {
            viewModel.canPaginate && (listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                ?: -10) >= (listState.layoutInfo.totalItemsCount - 5)
        }
    }
    LaunchedEffect(shouldLoadData) {
        if (shouldLoadData && viewModel.loadStatus == LoadStatus.IDLE) {
            viewModel.loadData()
        }
    }
}

@Composable
private fun ItemRow(
    id: String,
    volumeInfo: VolumeInfo,
    onItemRowClicked: (String) -> Unit,
) {
    Column(
        Modifier.clickable {
            onItemRowClicked(id)
        }
    ) {
        Box(
            modifier = Modifier.padding(8.dp, 8.dp, 8.dp, 8.dp)
        ) {
            Row(
                modifier = Modifier.heightIn(min = 80.dp)
            ) {
                AsyncImage(
                    model = volumeInfo.imageLinks.getThumbnail(),
                    contentDescription = "${volumeInfo.title}의 이미지",
                    placeholder = ColorPainter(Color.LightGray),
                    modifier = Modifier
                        .width(70.dp)
                        .align(CenterVertically)
                )
                Column(
                    modifier = Modifier
                        .padding(4.dp, 8.dp, 32.dp, 8.dp)
                        .align(CenterVertically)
                ) {
                    val title = if (volumeInfo.subtitle?.isNotBlank() == true) {
                        "${volumeInfo.title} (${volumeInfo.subtitle})"
                    } else {
                        volumeInfo.title
                    }
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    if (!volumeInfo.authors.isNullOrEmpty()) {
                        Spacer(
                            modifier = Modifier.size(8.dp)
                        )
                        Text(
                            text = "저자: ${volumeInfo.authors.joinToString()}", fontSize = 14.sp
                        )
                    }
                }
            }
        }
        Divider(
            modifier = Modifier
                .padding(12.dp, 0.dp)
                .fillMaxWidth()
                .height(1.dp)
        )
    }
}

@Composable
private fun SearchField(
    query: String,
    onQueryChanged: (String) -> Unit,
    onSearchButtonClicked: () -> Unit
) {
    Surface(
        elevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .background(WColorLight)
                .padding(16.dp, 8.dp, 16.dp, 12.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChanged,
                modifier = Modifier
                    .align(CenterVertically)
                    .weight(1f),
                label = {
                    Text(
                        text = "검색어를 입력해주세요."
                    )
                },
                singleLine = true
            )
            IconButton(
                onClick = onSearchButtonClicked,
                enabled = query.isNotBlank(),
                modifier = Modifier.align(CenterVertically)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "검색"
                )
            }
        }
    }
}

@Composable
private fun Error() {
    Text(
        text = "에러가 발생했습니다. 다시 시도해주세요.",
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)
    )
}

@Composable
fun Loading() {
    CircularProgressIndicator(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)
    )
}