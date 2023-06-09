package io.github.dalinaum.bookfinder.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import io.github.dalinaum.bookfinder.entity.VolumeInfo
import io.github.dalinaum.bookfinder.entity.getThumbnail
import io.github.dalinaum.bookfinder.screen.composable.ErrorDialog
import io.github.dalinaum.bookfinder.screen.composable.Loading
import io.github.dalinaum.bookfinder.screen.composable.LoadingAnimation
import io.github.dalinaum.bookfinder.screen.composable.SearchField
import io.github.dalinaum.bookfinder.viewmodel.HomeViewModel
import io.github.dalinaum.bookfinder.viewmodel.HomeViewModel.Companion.PREFETCH_SIZE
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
                if (viewModel.volumeList.isNullOrEmpty()) {
                    item {
                        AnimatedVisibility(
                            visible = viewModel.currentQuery.isBlank()
                        ) {
                            Text(
                                text = "검색할 책을 입력해주세요.",
                                fontSize = 20.sp,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
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
                ?: -(PREFETCH_SIZE * 2)) >= (listState.layoutInfo.totalItemsCount - PREFETCH_SIZE)
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
                val painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(volumeInfo.imageLinks.getThumbnail())
                        .size(Size.ORIGINAL)
                        .build()
                )
                if (painter.state is AsyncImagePainter.State.Loading) {
                    Box(
                        modifier = Modifier
                            .width(70.dp)
                            .aspectRatio(1f)
                    ) {
                        LoadingAnimation(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                } else {
                    Image(
                        painter = painter,
                        contentDescription = "${volumeInfo.title}의 이미지",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .width(70.dp)
                            .align(CenterVertically)
                    )
                }
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
                .padding(
                    horizontal = 12.dp,
                    vertical = 0.dp
                )
                .fillMaxWidth()
                .height(1.dp)
        )
    }
}