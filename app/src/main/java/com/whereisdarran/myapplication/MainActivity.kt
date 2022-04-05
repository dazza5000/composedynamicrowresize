@file:OptIn(ExperimentalSnapperApi::class)

package com.whereisdarran.myapplication

import android.graphics.Color.*
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.ColorInt
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import coil.compose.rememberImagePainter
import coil.decode.SvgDecoder
import com.whereisdarran.myapplication.ui.theme.MyApplicationTheme
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.SnapperFlingBehavior
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior

class MainActivity : ComponentActivity() {
    val largeCard =  BasicCardModel(
        title = "foo title, foo title, foo title, foo title, foo title, foo title, foo title, foo title, foo title, foo title, foo title, foo title, foo title, foo title, foo title, foo titleonnnnnnn",
        label = "foo label, foo label, foo label, foo label, foo label, foo label",
        bubbleText = "foo bubble text, " +
                "foo bubble text, foo bubble text, foo bubble text, foo bubble text, foo bubble text, \"foo bubble text, \" +\n" +
                "                \"foo bubble text, foo bubble text, foo bubble text, foo bubble text, foo bubble text,"
    )

    val mediumCard =  BasicCardModel(
        title = "foo title, foo title, foo title, foo title, foo title, foo title, foo title, foo title, foo title, foo title, foo title, foo title, foo title",
        label = "foo label, foo label, foo label, foo label, foo label, foo label",
        bubbleText = "foo bubble text, " +
                "foo bubble text, foo bubble text, foo bubble text, foo bubble text, foo bubble text, \"foo bubble text, \" +\n" +
                "                \"foo bubble text, foo bubble text, foo bubble text, foo bubble text, foo bubble text,"
    )


    val smallCard =  BasicCardModel(
        title = "foo title",
        label = "foo label",
        bubbleText = "foo bubble text"
    )

    val testObjects = listOf(
        smallCard,
        smallCard,
        smallCard,
        smallCard,
        smallCard,
        mediumCard,
        mediumCard,
        mediumCard,
        mediumCard,
        mediumCard,
        largeCard,
        largeCard,
        largeCard,
        largeCard,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column {
                        HomeCarouselView(carouselModel = testObjects, onAction = {})
                        Greeting("Android")
                    }
                }
            }
        }
    }
}

val spacingHorizontal = 20.dp


@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        Greeting("Android")
    }
}

@Composable
fun HomeCarouselView(carouselModel: List<BasicCardModel>, onAction: (action: Action) -> Unit) {
    val cardWidth = min(
        LocalConfiguration.current.screenWidthDp.dp - 40.dp,
        400.dp
    )
    val lazyListState = rememberLazyListState()
    val contentPadding = PaddingValues(16.dp, 16.dp)
    var titleHeightToSendDown by remember { mutableStateOf(0f) }
    LazyRow(
        state = lazyListState,
        contentPadding = contentPadding,
        flingBehavior = rememberRowSnapping(
            lazyListState = lazyListState,
            contentPadding = contentPadding
        ),
        horizontalArrangement = Arrangement.spacedBy(spacingHorizontal)
    ) {

        val largestTitleTextLength: BasicCardModel? = carouselModel.maxByOrNull { it.title?.length ?: 0 }

        val textLayout: (TextLayoutResult) -> Unit = {
            Log.d("darran", "darran layout $it")
            val textCharacterLength = it.layoutInput.text.length
            val textLayoutWidth =  it.multiParagraph.width

            val widthPerCharacter = textLayoutWidth / textCharacterLength * it.lineCount

            Log.d("darran", "width per character $widthPerCharacter")
            Log.d("darran", "lineCount ${it.lineCount}")

            val maxWidth = it.layoutInput.constraints.maxWidth

            val heightPerLine = it.multiParagraph.height / it.lineCount

            val totalWidthNeeded = (largestTitleTextLength?.title?.length ?: 1) * widthPerCharacter

            val linesRequired = totalWidthNeeded / maxWidth
            Log.d("darran", "linesRequired ${it.li")

            val titleHeight = heightPerLine * linesRequired

            Log.d("darran", "height calculated by textLayoutResult needed ${it.multiParagraph.height}")
            Log.d("darran", "height calculated by algo  needed $titleHeight")
            Log.d("darran", "height overflowhappened ${it.didOverflowHeight} ")
            Log.d("darran", "width overflowhappened ${it.didOverflowWidth} ")

            if (it.multiParagraph.height > titleHeightToSendDown) {
                titleHeightToSendDown = it.multiParagraph.height
            }

            if (titleHeight > titleHeightToSendDown) {
                titleHeightToSendDown = titleHeight
            }

            Log.d("darran", "title height to send down $titleHeightToSendDown")
        }

        items(carouselModel.size) { it ->
            when (val item = carouselModel[it]) {

                is BasicCardModel -> {
                    val cardTextColor = item.textColor?.toColor() ?: Color.Blue
                    val onClick = {
                        item.action?.run {
                            onAction(this)
                        }
                    }
                    BasicCardView(
                        modifier = Modifier
                            .width(
                                cardWidth
                            ),
                        Color(item.palette.getMediumColor()),
                        backgroundImage = item.imageUrl?.let {
                            ImageSource.Url(
                                it, placeholder = ImageSource.Color(item.palette.getDarkColor())
                            )
                        },
                        textColor = cardTextColor,
                        onTitleLayout = textLayout,
                        titleHeight = LocalDensity.current.run { titleHeightToSendDown.toInt().toDp().value },
                        label = item.label,
                        title = item.title,
                        bubbleText = item.bubbleText,
                        bubbleImageSource = item.bubbleImageUrl?.let { ImageSource.Url(it) },
                        bubbleTextColor = cardTextColor,
                        bubbleBackgroundColor = Color(item.palette.getDarkColor()),
                        onClick = if (item.action == null) {
                            null
                        } else {
                            onClick
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun rememberRowSnapping(
    lazyListState: LazyListState,
    contentPadding: PaddingValues
): SnapperFlingBehavior =
    rememberSnapperFlingBehavior(
        lazyListState = lazyListState,
        endContentPadding = contentPadding.calculateEndPadding(LayoutDirection.Ltr),
        snapIndex = { _, startIndex, targetIndex ->
            when {
                startIndex > targetIndex -> startIndex - 1
                startIndex < targetIndex -> startIndex + 1
                else -> targetIndex
            }
        }
    )

sealed class ImageSource {
    data class DrawableRes(@androidx.annotation.DrawableRes val resource: Int) : ImageSource()
    data class Drawable(val drawable: android.graphics.drawable.Drawable) : ImageSource()
    data class Color(@ColorInt val color: Int) : ImageSource()
    data class Url(val url: String, val placeholder: ImageSource? = null) : ImageSource()
}

data class BasicCardModel(
    val title: String?,
    val label: String?,
    val imageUrl: String? = "https://picsum.photos/536/354",
    val bubbleImageUrl: String? = "https://images.pexels.com/photos/1366942/pexels-photo-1366942.jpeg?cs=srgb&dl=pexels-rodolfo-clix-1366942.jpg&fm=jpg",
    val bubbleText: String?,
    val textColor: String? = null,
    val palette: ColorPalette = ColorPalette(),
    val action: Action? = null
)

data class ColorPalette(
    @JvmField val lightColor: String = MAGENTA.toString(),
    @JvmField val defaultColor: String = GREEN.toString(),
    @JvmField val mediumColor: String = BLUE.toString(),
    @JvmField val darkColor: String = YELLOW.toString()
) {

    @ColorInt
    fun getLightColor(): Int = cachedColors.getOrPut(lightColor) { lightColor.parseColor() }

    @ColorInt
    fun getDefaultColor(): Int = cachedColors.getOrPut(defaultColor) { defaultColor.parseColor() }

    @ColorInt
    fun getMediumColor(): Int = cachedColors.getOrPut(mediumColor) { mediumColor.parseColor() }

    @ColorInt
    fun getDarkColor(): Int = cachedColors.getOrPut(darkColor) { darkColor.parseColor() }

    companion object {
        private val cachedColors = mutableMapOf<String, Int>()
    }

}

@ColorInt
fun String?.parseColor(fallbackColor: Int = android.graphics.Color.TRANSPARENT): Int {
    this?.let { colorString ->
        return try {
            android.graphics.Color.parseColor(colorString)
        } catch (e: Exception) {
            fallbackColor
        }
    }
    return fallbackColor
}

sealed class Action {
    data class DeeplinkAction(
        val type: ActionOpenType,
        val value: Deeplink
    ) : Action()

    data class BrowserAction(
        val type: ActionOpenType,
        val value: Browser
    ) : Action()
}


data class Deeplink(val uri: String)


data class Browser(
    val uri: String,
    val isThirdParty: Boolean = false
)

enum class ActionOpenType {
    BROWSER,
    DEEP_LINK,
    UNKNOWN
}

fun CharSequence.toColor() = Color(android.graphics.Color.parseColor(this.toString()))

@Composable
fun FooCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius_card)),
        content = content,
        elevation = dimensionResource(id = R.dimen.elevation_card)
    )
}

@Composable
fun BasicCardView(
    modifier: Modifier,
    backgroundColor: Color,
    backgroundImage: ImageSource? = null,
    label: String? = null,
    title: String? = null,
    textColor: Color,
    onTitleLayout: (TextLayoutResult) -> Unit = {},
    titleHeight: Float = 0f,
    bubbleText: String? = null,
    bubbleTextColor: Color? = null,
    bubbleImageSource: ImageSource? = null,
    bubbleBackgroundColor: Color? = null,
    onClick: (() -> Unit?)? = null
) {

    FooCard(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = when (onClick) {
                    null -> null
                    else -> LocalIndication.current
                },
                enabled = onClick != null,
                onClick = {
                    onClick?.invoke()
                })
    ) {
        Column(
            modifier = Modifier
                .background(backgroundColor)
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(355 / 132f)
            )
            {
                backgroundImage?.run {
                    Image(
                        painter = getImagePainter(source = this),
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .matchParentSize()
                    )
                }
                bubbleText?.run {
                    BarBubble(
                        modifier = Modifier.padding(16.dp),
                        imageSource = bubbleImageSource,
                        text = bubbleText,
                        backgroundColor = bubbleBackgroundColor ?: Color.Black,
                        fontStyle = MaterialTheme.typography.caption.copy(
                            color = bubbleTextColor ?: Color.White
                        )
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                label?.run {
                    Text(
                        text = this,
                        modifier = Modifier.padding(bottom = 8.dp),
                        style = MaterialTheme.typography.caption,
                        color = textColor
                    )
                }
                title?.run {
                    Text(
                        modifier = Modifier.height(titleHeight.dp),
                        text = this,
                        style = MaterialTheme.typography.body1,
                        color = textColor,
                        onTextLayout = onTitleLayout
                    )
                }
            }
        }
    }
}


@Composable
fun getImagePainter(source: ImageSource): Painter {
    return when (source) {
        is ImageSource.Url -> {
            rememberImagePainter(
                source.url,
                builder = {
                    decoder(SvgDecoder(LocalContext.current))
                    source.placeholder?.let { placeholder ->
                        when (placeholder) {
                            is ImageSource.Drawable -> {
                                placeholder(placeholder.drawable)
                            }
                            is ImageSource.DrawableRes -> {
                                placeholder(placeholder.resource)
                            }
                            is ImageSource.Color -> {
                                placeholder(ColorDrawable(placeholder.color))
                            }
                            else -> {}
                        }
                    }
                })
        }
        is ImageSource.Drawable -> {
            rememberImagePainter(source.drawable)
        }
        is ImageSource.DrawableRes -> {
            painterResource(source.resource)
        }
        is ImageSource.Color -> {
            rememberImagePainter(ColorDrawable(source.color))
        }
    }
}


@Composable
fun BarBubble(
    modifier: Modifier = Modifier,
    imageSource: ImageSource? = null,
    backgroundColor: Color = MaterialTheme.colors.error,
    strokeColor: Color = backgroundColor,
    text: String,
    fontStyle: TextStyle = MaterialTheme.typography.caption
) {
    Row(
        modifier = modifier
            .border(color = strokeColor, width = Dp.Hairline, shape = RoundedCornerShape(6.dp))
            .background(color = backgroundColor, shape = RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.End,
    ) {
        imageSource?.run {
            Image(
                painter = getImagePainter(source = this),
                contentDescription = null,
                alignment = Alignment.CenterStart,
                modifier = Modifier
                    .padding(end = 6.dp)
                    .align(Alignment.CenterVertically)
                    .height(16.dp)
                    .width(16.dp)
            )
        }
        Text(style = fontStyle, text = text)
    }
}

//https://medium.com/@takahirom/understanding-the-jetpack-compose-layout-with-diagrams-1b7311765841
@Composable
fun HeightCalculator(
    onCalculateHeight: (Int) -> Unit,
    content: @Composable () -> Unit,
) = Layout(content = content) { measures, constraints ->

    val placeableList = measures.mapIndexed { _, measurable ->
        measurable.measure(constraints)
    }
    val height = maxOf(placeableList.sumOf { it.height }, constraints.minHeight)
    val width = maxOf(placeableList.maxOfOrNull { it.width } ?: 0, constraints.minWidth)
    val layout = layout(width, height) {
        var y = 0
        placeableList.forEach { placeable: Placeable ->
            placeable.placeRelative(x = 0, y = y)
            y += placeable.height
        }
        onCalculateHeight(y)
    }
    layout
}