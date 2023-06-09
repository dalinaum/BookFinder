package io.github.dalinaum.bookfinder.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import io.github.dalinaum.bookfinder.entity.Item
import io.github.dalinaum.bookfinder.entity.getBigImage
import io.github.dalinaum.bookfinder.screen.composable.ErrorDialog
import io.github.dalinaum.bookfinder.screen.composable.Loading
import io.github.dalinaum.bookfinder.screen.composable.LoadingAnimation
import io.github.dalinaum.bookfinder.ui.theme.WColorLight
import io.github.dalinaum.bookfinder.viewmodel.DetailViewModel
import io.github.dalinaum.bookfinder.viewmodel.status.DetailResult
import kotlinx.coroutines.flow.Flow

@Composable
fun DetailScreen(
    navController: NavController,
    id: String,
    viewModel: DetailViewModel = hiltViewModel()
) {
    LaunchedEffect(id) {
        viewModel.id.value = id
    }

    val result by viewModel.volume.collectAsState()
    val title = if (result is DetailResult.Success) {
        val success = result as DetailResult.Success
        success.value.volumeInfo.title
    } else {
        "BookFinder"
    }

    Scaffold(
        topBar = {
            BookFinderTopBar(
                navController,
                title
            )
        }
    ) {
        when (result) {
            is DetailResult.Error -> {
                val error = result as DetailResult.Error
                val errorMessage = "$id 에서 에러가 발생했습니다.\n\n${error.exception.message}"
                ErrorDialog(
                    message = errorMessage,
                    onConfirmButtonClicked = {
                        navController.navigateUp()
                    }
                )
            }

            is DetailResult.Success -> {
                val item = (result as DetailResult.Success<Item>).value
                DetailSuccess(
                    navController = navController,
                    item = item
                )
            }

            DetailResult.InitialState -> {
                Loading()
            }
        }
    }
}

@Composable
private fun Flow<DetailResult<Item>>.collectAsState(
    lifecycle: Lifecycle = LocalLifecycleOwner.current.lifecycle
): State<DetailResult<Item>> = produceState<DetailResult<Item>>(
    initialValue = DetailResult.InitialState,
    key1 = lifecycle,
    key2 = this
) {
    lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
        this@collectAsState.collect {
            value = it
        }
    }
}

@Composable
private fun DetailSuccess(
    navController: NavController,
    item: Item
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
    ) {
        val painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(item.volumeInfo.imageLinks.getBigImage())
                .size(coil.size.Size.ORIGINAL)
                .build()
        )
        if (painter.state is AsyncImagePainter.State.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(1f)
            ) {
                LoadingAnimation(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
        Image(
            painter = painter,
            contentDescription = item.volumeInfo.description,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .background(WColorLight)
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
        )

        Spacer(
            modifier = Modifier.size(8.dp)
        )

        Column(
            modifier = Modifier.padding(4.dp)
        ) {
            val volumeInfo = item.volumeInfo
            val title = if (volumeInfo.subtitle?.isNotBlank() == true) {
                "${volumeInfo.title} (${volumeInfo.subtitle})"
            } else {
                volumeInfo.title
            }
            Text(
                text = title,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp, 0.dp)
            )
            if (!volumeInfo.authors.isNullOrEmpty()) {
                Spacer(
                    modifier = Modifier.size(8.dp)
                )
                Text(
                    text = "저자: ${volumeInfo.authors.joinToString()}",
                    fontSize = 28.sp,
                    modifier = Modifier.padding(16.dp, 0.dp)
                )
            }
            Spacer(
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = volumeInfo.description.removeHtml(),
                fontSize = 20.sp,
                lineHeight = 35.sp,
                modifier = Modifier.padding(16.dp, 0.dp)
            )
            Spacer(
                modifier = Modifier.size(24.dp)
            )
            Button(
                onClick = {
                    navController.navigateUp()
                }
            ) {
                Text(
                    text = "목록으로 돌아가기"
                )
            }
            Spacer(
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

fun String?.removeHtml(): String =
    this?.replace("<br>", "\n\n") ?: ""

@Composable
private fun BookFinderTopBar(
    navController: NavController,
    title: String = "BookFinder"
) {
    TopAppBar(
        navigationIcon = {
            IconButton(
                onClick = {
                    navController.navigateUp()
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "뒤로 가기"
                )
            }
        },
        title = {
            Text(
                text = title
            )
        }
    )
}