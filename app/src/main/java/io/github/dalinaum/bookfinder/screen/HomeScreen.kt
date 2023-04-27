package io.github.dalinaum.bookfinder.screen

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import io.github.dalinaum.bookfinder.screen.composable.ErrorDialog
import io.github.dalinaum.bookfinder.screen.composable.Loading
import io.github.dalinaum.bookfinder.screen.composable.SearchField
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

                    is LoadStatus.ERROR -> {
                        val error = viewModel.loadStatus as LoadStatus.ERROR
                        item {
                            ErrorDialog(
                                message = error.exception.message ?: "에러가 났습니다."
                            )
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
